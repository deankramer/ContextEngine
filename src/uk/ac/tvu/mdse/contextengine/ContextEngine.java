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

package uk.ac.tvu.mdse.contextengine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.contexts.LocationContext;
import uk.ac.tvu.mdse.contextengine.db.ContextDB;
import uk.ac.tvu.mdse.contextengine.db.ContextDBSQLite;
import uk.ac.tvu.mdse.contextengine.reasoning.ApplicationKey;
import uk.ac.tvu.mdse.contextengine.test.TestActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class ContextEngine extends Service {

	private static final String LOG_TAG = "ContextEngine";
	private boolean D = true;
	
	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.REMOTE_SERVICE";

	public static final String CONTEXT_INFORMATION = "context_information";
	public static final String CONTEXT_NAME = "context_name";
	public static final String CONTEXT_DATE = "context_date";
	public static final String CONTEXT_VALUE = "context_value";
	public static final String CONTEXT_APPLICATION_KEY = "context_location_key";	
	
	private NotificationManager mNM;
	private BroadcastReceiver contextMonitor;
	private IntentFilter filter;
	
	private LocationServices locationServices = null;
	
	private Context c;
	private ContextDB db;
	
	int defined = 0;
	
	//hold the info about running components
	private ArrayList<Component> activeContexts = new ArrayList<Component>();
	
	//holding info about subscribed apps
	private ArrayList<ApplicationKey> applicationKeys = new ArrayList<ApplicationKey>();
	
	//it is assumed that only one app define context
	//composition at the time - it needs to gets synchronised later on!
	ApplicationKey newAppKey;
	
	private ArrayList<LocationContext> locationContexts = new ArrayList<LocationContext>();
	LocationContext locationContext;
	
	//there are 2 approaches to deal with contexts, this variable serves
	//as a controller to switch between hashtable and rules approach
	//value = true - hashtable
	//value = false - rules

	/**
	 * This is a list of callbacks that have been registered with the service.
	 * Note that this is package scoped (instead of private) so that it can be
	 * accessed more efficiently from inner classes.
	 */
	final RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();

	int mValue = 0;

	// for notifications
	static int count = 0;
	static String order = "*";

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public ContextEngine getService() {
			return ContextEngine.this;
		}
	}

	@Override
	public void onCreate() {
		if (D)
			Log.d(LOG_TAG, "onCreate");
		
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		filter = new IntentFilter(
				"uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
		//copyDexFile();
		//setupContextMonitor();

		
		c = getApplicationContext();
		try{
		locationServices = new LocationServices(c);
		}catch(Exception e){
			if (D)
				Log.e(LOG_TAG, e.getLocalizedMessage());
		}
		// While this service is running, it will continually increment a
		// number. Send the first message that is used to perform the
		// increment.
		//mHandler.sendEmptyMessage(REPORT_MSG);
		
		// Manage list of all contexts registered with the engine and store the list in the database
		db = new ContextDBSQLite(c);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (D)
			Log.d(LOG_TAG, "onBind");

		if (IContextsDefinition.class.getName().equals(intent.getAction())) {
			Log.d(LOG_TAG, "bind-contextsBinder");
			return contextsBinder;
		}
		
		if (ISynchronousCommunication.class.getName().equals(intent.getAction())) {
			Log.d(LOG_TAG, "bind-SynchronousCommunication");
			return synchronousBinder;
		}

		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		this.stopSelf();
		return true;
	}

	public final IContextsDefinition.Stub contextsBinder = new IContextsDefinition.Stub() {
		
		public boolean registerApplicationKey(String key){
			if(newAppKey==null){
				newAppKey = new ApplicationKey(key);
				newAppKey.key = key;
				return true;
			}
				for(ApplicationKey k: applicationKeys){
				if(! (k.key.equalsIgnoreCase(key))){
					applicationKeys.add(newAppKey);
					newAppKey.key=key;
					return true;
				}	
			}
		    return false;
		}
		
		public boolean registerComponent(String componentName)
				throws RemoteException {
			
			//FOR LOCATION:
			if (componentName.equals("LocationContext")){
				if (locationServices == null)
					locationServices = new LocationServices(c);
			}
			
			if(activeContexts.isEmpty())
				return loadClass(componentName);
			else{
				for (Component ac: activeContexts){
					if (ac.contextName.equals(componentName)){
						Log.e(LOG_TAG, "Component already running!");						
						return false;
					}			
				}
				return loadClass(componentName);
			}
		}
		
		//***add context values to a component***
		public  boolean addContextValues(String componentName, String[] contextValues){	
			
			for (Component ac: activeContexts){
				if (ac.contextName.equals(componentName)){
					ac.setupNewValuesSet(newAppKey, contextValues);
					return true;
				}
			}
			Log.e(LOG_TAG, "Context not running!");
			return false;
		 }
		  
		  //***add a context value***
		public boolean addContextValue(String componentName, String contextValue){
			try{
				for (Component ac: activeContexts){
					if (ac.contextName.equals(componentName)){
						ac.addContextValue(newAppKey, contextValue);
						return true;
					}
				}
				Log.e(LOG_TAG, "Context not running!");
				return false;
			}catch(Exception e){
				Log.e(LOG_TAG, e.getLocalizedMessage());
				return false;
			}
		}
		  
		  //***add a specific context value described by two numeric coordinates (e.g.location)***
		public void addSpecificContextValue(String componentName, String contextValue, String numericData1, String numericData2){
			if (D)
				Log.d(LOG_TAG, "addSpecificContextValue");
			try{
				for (Component ac: activeContexts){
					if (ac.contextName.equals(componentName))
						ac.addSpecificContextValue(newAppKey, contextValue, Double.valueOf(numericData1), Double.valueOf(numericData2));	
				}		
			}catch(Exception e){
				Log.e(LOG_TAG, e.getLocalizedMessage());
			}
		}
		
		//***define higher context value - in case of numeric values specify range of values***  
		public void addRange(String componentName, String minValue, String maxValue, String contextValue){
			
			//look up for the component
			Component component = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(componentName))
					component = (Component) ac;				
			}		
			Log.d(LOG_TAG, "addRange" +  componentName);
			if (component!=null)
				component.addRange(newAppKey, Integer.valueOf(minValue), Integer.valueOf(maxValue), contextValue);
			
		}
		

		public boolean newComposite(String compositeName) throws RemoteException {				
			try{
				RuledCompositeComponent ruledComponent=null;
				if(activeContexts.isEmpty()){
					ruledComponent = new RuledCompositeComponent(compositeName, c);				
					activeContexts.add(ruledComponent);
					return true;
				}else{
					for (Component ac: activeContexts){
						if (ac.contextName.equals(compositeName)){
							Log.e(LOG_TAG, "Component already running!");
							return false;
						}			
					}
					
					ruledComponent = new RuledCompositeComponent(compositeName, c);				
					activeContexts.add(ruledComponent);
					return true;
				}			
			}
			catch(Exception e){
				Log.d(LOG_TAG, e.getLocalizedMessage());
				return false;
			}
		}

		public boolean addToComposite(String componentName, String compositeName)
				throws RemoteException {
				
			RuledCompositeComponent ruledComponent = null;
			Component component = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(compositeName))
					ruledComponent = (RuledCompositeComponent) ac;
				if (ac.contextName.equals(componentName))
					component = ac;	
			}
			
			if(component==null){
				if( ! (loadClass(componentName)) )
						return false;
			}
			if(ruledComponent==null){
				ruledComponent = new RuledCompositeComponent(compositeName, c);				
				activeContexts.add(ruledComponent);
			}
			ruledComponent.registerComponent(component);		
			if (D)
				Log.d(LOG_TAG, "Added" + componentName + " to " + compositeName);
			return true;
		}
		

		public void addRule(String componentName, String[] condition, String result){
			
			RuledCompositeComponent ruledComponent = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(componentName))
					ruledComponent = (RuledCompositeComponent) ac;		
			}	
			
				ruledComponent.addRule(condition, result);
				Log.d(LOG_TAG, "addRule" );
				
		}
		
		public boolean startComposite(String compositeName) throws RemoteException {
			RuledCompositeComponent ruledComponent = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(compositeName)){
					ruledComponent = (RuledCompositeComponent) ac;
					setupContextMonitor();
					ruledComponent.componentDefined();
					return true;
				}
			}
			Log.e(LOG_TAG, "Composite not active!");
			return false;
		}

		public void registerCallback(IRemoteServiceCallback cb) {
			if (cb != null)
				mCallbacks.register(cb);
		}

		public void unregisterCallback(IRemoteServiceCallback cb) {
			if (cb != null)
				mCallbacks.unregister(cb);
		}
	};
	
	public final ISynchronousCommunication.Stub synchronousBinder = new ISynchronousCommunication.Stub() {

		public ArrayList<String> getContextList() throws RemoteException {
			ArrayList<String> contextList = new ArrayList<String>();
			ArrayList<Component> contexts = new ArrayList<Component>();
			contexts = db.getAllContexts();
			for(Component c: contexts)
				contextList.add(c.contextName);
			return contextList;
		}

		public boolean getContextValue(String componentName)
				throws RemoteException {
			//firstly find if context exist in db!
			return db.getContextValue(componentName);			
		}
		
	};

	private void setupContextMonitor() {
		Log.v("value", "add contextMonitor");
		contextMonitor = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						"uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED")) {
					if (D)
						Log.d(LOG_TAG, "onReceive");
					Bundle bundle = intent.getExtras();
					
					//send broadcast to apps
					//sendBroadcastToApps(bundle);
					
					//show notification - just for testing
					String contextName = bundle
							.getString(Component.CONTEXT_NAME);
					String contextValue = bundle
							.getString(Component.CONTEXT_INFORMATION);
					ArrayList<String> appKey = bundle
							.getStringArrayList(Component.CONTEXT_APPLICATION_KEY);
						//showNotification(contextName+ " "+contextvalue);
					if (appKey.size()>0)
						Log.v(LOG_TAG, "onReceive:" + contextName + " " + contextValue + " " + appKey.get(0));
					else
						Log.v(LOG_TAG, "onReceive:" + contextName + " " + contextValue);
				}
			}

		};
		registerReceiver(contextMonitor, filter);
	}
	
	public void copyDexFile(){
		File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE),
		          "classes.dex");
		
		  BufferedInputStream bis = null;
		  OutputStream dexWriter = null;

		  final int BUF_SIZE = 8 * 1024;
		  try {
		      bis = new BufferedInputStream(getAssets().open("classes.dex"));
		      dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
		      byte[] buf = new byte[BUF_SIZE];
		      int len;
		      while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
		          dexWriter.write(buf, 0, len);
		      }
		      dexWriter.close();
		      bis.close();
		      
		  } catch (Exception e){
			  Log.e("Error", e.getStackTrace().toString());
		  }
	}

	protected boolean loadClass(String componentName) {
		final File optimizedDexOutputPath = getDir("outdex", Context.MODE_PRIVATE);
		File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE),
          "classes.dex");
		  DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
		                                         optimizedDexOutputPath.getAbsolutePath(),
		                                         null,
		                                         getClassLoader());
		  Class<?> contextClass = null;
		  Class<?>[] parameterTypes = {Context.class};
			String classpath = "uk.ac.tvu.mdse.contextengine.contexts.";
			  try {
			      // Load the Class
			      contextClass =
			          cl.loadClass(classpath.concat(componentName));
			      Constructor<?> contextConstructor = contextClass.getConstructor(parameterTypes);
			      
			      Component context = (Component) contextConstructor.newInstance(c);
			      activeContexts.add(context);
			      
			    //only to test - add key when default context values set = ON&OFF
//					if (context.valuesSets.size()==1)
//						context.valuesSets.get(0).keys.add(newAppKey);
						
					
			      return true;
			  } catch(ClassNotFoundException cnfe){
				  Log.e(LOG_TAG, "Component does not exist!");
				  return false;
			  }
			  catch (Exception e) {
				  //Log.e("Error", e.getStackTrace().toString());
				  e.printStackTrace();
				  return false;
			  } 
		
	}

	private void showNotification(String contextChange) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		if (D)
			Log.d(LOG_TAG, "showNotification");
		CharSequence contentTitle = "ContextEngine";
		CharSequence contentText = "Context Changed:" + contextChange;
		long when = System.currentTimeMillis();

		// this intent needs to be adapted, serves only for testing purposes!!!
		Intent i;
		i = new Intent(getBaseContext(), TestActivity.class);
		// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification notification = new Notification(R.drawable.stat_sample,
				contentText, when);
		PendingIntent contentIntent = PendingIntent.getActivity(
				ContextEngine.this, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		++count;
		mNM.notify(count, notification);
	}
	
	public void sendBroadcastToApps(Bundle bundle){
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME,bundle.getString(CONTEXT_NAME));
		if (!bundle.getString(CONTEXT_APPLICATION_KEY).equals(null))
			intent.putExtra(CONTEXT_APPLICATION_KEY, bundle.getString(CONTEXT_APPLICATION_KEY));
		intent.putExtra(CONTEXT_DATE, bundle.getString(CONTEXT_DATE));
		if (!bundle.getString(CONTEXT_VALUE).equals(null))
			intent.putExtra(CONTEXT_VALUE, bundle.getBoolean(CONTEXT_VALUE));
		if (!bundle.getString(CONTEXT_INFORMATION).equals(null))
			intent.putExtra(CONTEXT_INFORMATION, bundle.getString(CONTEXT_INFORMATION));
		try {
			c.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e("ContextEngine", "broadcasting to apps not working");
		}
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		// Cancel the persistent notification.
		mNM.cancel(R.string.local_service_started);

		for (Component ac: activeContexts){
			ac.stop();
			ac = null;
		}

		unregisterReceiver(contextMonitor);
		
		locationServices.stop();
		locationServices = null;
		
		// Unregister all callbacks.
		mCallbacks.kill();

		// Remove the next pending message to increment the counter, stopping
		// the increment loop.
		//mHandler.removeMessages(REPORT_MSG);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT)
				.show();

	}
}

//REMOVED FROM CONTEXT DEFINITION:
//public void addLocationComponent(String key) throws RemoteException {
//	
//	if (locationServices == null)
//		locationServices = new LocationServices(c);
//	
//	locationContext = new LocationContext(c, key, locationServices);
//	locationContexts.add(locationContext);
//	activeContexts.add(locationContext);
//	if (D)
//		Log.d(LOG_TAG, "onaddLocationComponent -success");
//	
//}
//
//public void addLocation(String locationKey, String identifier, String latitude,
//		String longitude) throws RemoteException {
//	if (D)
//		Log.d(LOG_TAG, "addLocation -success");
//	try{
//	for (LocationContext lc: locationContexts){
//		if (lc.key.equals(locationKey)){
//			lc.addLocation(identifier, Double.valueOf(latitude), Double.valueOf(longitude));
//		}
//	}			
//	}catch(Exception e){
//		Log.e(LOG_TAG, e.getLocalizedMessage());
//	}
//}

//USED TO TEST CONNECTION TO APP:
//private static final int REPORT_MSG = 1;
//
///**
// * Our Handler used to execute operations on the main thread. This is used
// * to schedule increments of our value.
// */
//private final Handler mHandler = new Handler() {
//	@Override
//	public void handleMessage(Message msg) {
//		switch (msg.what) {
//
//		// It is time to bump the value!
//		case REPORT_MSG: {
//			// Up it goes.
////			int value = ++value;
//			Bundle bundle = msg.getData();
//			String contextInfo = bundle
//			.getString(Component.CONTEXT_NAME);
//			// Broadcast to all clients the new value.
//			final int N = mCallbacks.beginBroadcast();
//			for (int i = 0; i < N; i++) {
//				try {
//					mCallbacks.getBroadcastItem(i).valueChanged(contextInfo);
//				} catch (RemoteException e) {
//					// The RemoteCallbackList will take care of removing
//					// the dead object for us.
//				}
//			}
//			mCallbacks.finishBroadcast();
//
//			// Repeat every 1 second.
////			sendMessageDelayed(obtainMessage(REPORT_MSG), 1 * 1000);
//		}
//			break;
//		default:
//			super.handleMessage(msg);
//		}
//	}
//};