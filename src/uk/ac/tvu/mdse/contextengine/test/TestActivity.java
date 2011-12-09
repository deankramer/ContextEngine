/*
 * Copyright (C) 2011 The Context Engine Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.tvu.mdse.contextengine.test;

import uk.ac.tvu.mdse.contextengine.IContextsDefinition;
import uk.ac.tvu.mdse.contextengine.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*
 * This activity has been originally created in another Android project
 * with aim to test Context Definition of Context Engine.
 * It is placed here for demonstration purposes.
 * The code enables binding to context engine and 
 * needs to be used in a context-aware application!!!
 */
public class TestActivity extends Activity{
	
	private static final String LOG_TAG = "TestActivity";
	private boolean D = true;
	
	
	public static final String CONTEXT_INFORMATION = "context_information";
	public static final String CONTEXT_NAME = "context_name";
	public static final String CONTEXT_DATE = "context_date";
	public static final String CONTEXT_VALUE = "context_value";
	public static final String CONTEXT_APPLICATION_KEY = "context_application_key";	
	IContextsDefinition contextService;
	private boolean started=false;
	private boolean mIsBound= false;
	
	//app key defined
	private String thisAppKey = "1111";

	BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
	private BroadcastReceiver contextMonitor;
	
	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.REMOTE_SERVICE";
	public IntentFilter filter = new IntentFilter(CONTEXT_INTENT);
	
	TextView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView((R.layout.testactivity));
			
		view = (TextView)findViewById(R.id.viewbox);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			try {
				contextService = IContextsDefinition.Stub.asInterface(service);
				
				try {
					if (D) Log.d( LOG_TAG, "onServiceConnected" );					
					
					contextService.registerApplicationKey(thisAppKey);
					
					contextService.registerComponent("WifiContext");
					contextService.registerComponent("BluetoothContext");
					contextService.registerComponent("BatteryContext");
					
					contextService.addRange("BatteryContext", "0", "30", "LOW");
					contextService.addRange("BatteryContext", "31", "80", "MEDIUM");
					contextService.addRange("BatteryContext", "81", "100", "HIGH");					
					
					contextService.newComposite("DATASYNC");
					
					contextService.addToComposite("WifiContext","DATASYNC");
					contextService.addToComposite("BluetoothContext","DATASYNC");
					contextService.addToComposite("BatteryContext","DATASYNC");
					
					Log.e("status", "adding rules");
					contextService.addRule("DATASYNC", new String[]{"ON","ON","MEDIUM"}, "ON");
					contextService.addRule("DATASYNC", new String[]{"OFF","ON","HIGH"}, "ON");
					contextService.addRule("DATASYNC", new String[]{"ON","OFF","HIGH"}, "ON");
					contextService.startComposite("DATASYNC");
				
					setupContextMonitor();
					doUnbindService(); 
				} catch (RemoteException e) {

					e.printStackTrace();
				}

			} catch (NotFoundException e) {

				e.printStackTrace();
			}      	

		}

		public void onServiceDisconnected(ComponentName className) {			
			if (D) Log.d( LOG_TAG, "onServiceDisconnected" );    		  
			
			contextService = null;
			
		}
	};

	void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).

		bindService(new Intent(IContextsDefinition.class.getName()), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {     
		
		if (mConnection != null)
			unbindService(mConnection);		
		
		mIsBound = false;
		
	}


	private void setupContextMonitor() {
		Log.v("value", "add contextMonitor");
		contextMonitor = new BroadcastReceiver() {
			@Override 
			public void onReceive(Context context,Intent intent) {
				//check whether broadcast include the app key
				try{
					Bundle bundle = intent.getExtras();	            				
					if (!bundle.getStringArray(CONTEXT_APPLICATION_KEY).equals(null)){
						String[] keys = bundle.getStringArray(CONTEXT_APPLICATION_KEY);
						
						//check whether the broadcast is for the app
						//by checking the app key
						boolean contains = false;
						for (int i = 0; i<keys.length;i++)
							if (keys[i].equals(thisAppKey))
									contains = true;
						if (contains) {

							if (!bundle.getString(CONTEXT_NAME).equals(null) && !bundle.getString(CONTEXT_INFORMATION).equals(null)){
								view.setText("changeName context: " + bundle.getString(CONTEXT_NAME) + " " + bundle.getString(CONTEXT_INFORMATION));
								if (D) Log.v( LOG_TAG, "changeName context: " + bundle.getString(CONTEXT_NAME) + " " + bundle.getString(CONTEXT_INFORMATION));
							}

							else
								if (D) Log.v( LOG_TAG, "changeName context: is null");
						}
					}
				}
						catch (Exception ex){
							if (D) Log.e( LOG_TAG, "error:" + ex.getLocalizedMessage());
						}
					}
		};
		registerReceiver(contextMonitor, filter);  	
	}
	     
	@Override
	public void onStop(){
		super.onStop();
	}

	//update message
	private void updateServiceStatus() {
		String startStatus = started ? "subscribed" : "not subscribed";
		String statusText = "Server status: "+
		startStatus;
		TextView t = (TextView)findViewById( R.id.servicestatus );
		t.setText( statusText );	  
	}	

	public void subscribe(View v){
		if( started ) {
			Toast.makeText(TestActivity.this, "Already subscribed", Toast.LENGTH_SHORT).show();
		}
		else{
			try {
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("uk.ac.tvu.mdse.contextengine.IContextsDefinition");
				startService(serviceIntent);
				started = true;
				updateServiceStatus();   
				doBindService();           
				
			}
			catch (Exception e){
				Toast.makeText(TestActivity.this, "Error is " + e, Toast.LENGTH_LONG).show();
			}     	
		}
	}

	public void unsubscribe(View v){
		if( !started ) {
			Toast.makeText(TestActivity.this, "Not subscribed to service", Toast.LENGTH_SHORT).show();
		} else {

			try{
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("uk.ac.tvu.mdse.contextengine.IContextsDefinition");
				stopService(serviceIntent);		
				started = false;
				updateServiceStatus();
				doUnbindService(); 
			}
			catch (Exception e){
				Toast.makeText(TestActivity.this, "Error is " + e, Toast.LENGTH_LONG).show();
			}	
		}
	}

	//only to manually enable/disable Bluetooth
	//-->to test correct broadcasting
	public void bluetoothChange(View v){
		try{
			if (bt.isEnabled()){
				bt.disable();				
			}
			else{
				bt.enable();				
			}
		}
		catch (Exception e){
			Toast.makeText(TestActivity.this, "Error is " + e, Toast.LENGTH_LONG).show();
		}	
	}
}

