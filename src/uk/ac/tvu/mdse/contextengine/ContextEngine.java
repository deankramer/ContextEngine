/**
 * @project ContextEngine
 * @date 26 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.contexts.BluetoothContext;
import uk.ac.tvu.mdse.contextengine.contexts.LightContext;
import uk.ac.tvu.mdse.contextengine.contexts.UserPreferenceContext;
import uk.ac.tvu.mdse.contextengine.test.TestActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ContextEngine extends Service{
	
	private static final String LOG_TAG = "ContextEngine";
	private boolean D = true;
	
	private NotificationManager mNM;
	private BroadcastReceiver contextMonitor;
	private IntentFilter filter;
	private LightContext lightcontext;
	private CompositeComponent sync;
	private BluetoothContext bc;
	private UserPreferenceContext uc;
	private SensorManager sm;
	
	/**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    final RemoteCallbackList<IRemoteServiceCallback> mCallbacks
            = new RemoteCallbackList<IRemoteServiceCallback>();

    int mValue = 0;

	
	//for notifications
	static int count = 0;
	static String order = "*";
	
	// This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder(); 
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	public ContextEngine getService() {
            return ContextEngine.this;
        }
    }
	
	
    @Override
    public void onCreate() {    
    	if (D) Log.d( LOG_TAG, "onCreate" );
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
        setupContextMonitor();
        
        // While this service is running, it will continually increment a
        // number.  Send the first message that is used to perform the
        // increment.
        mHandler.sendEmptyMessage(REPORT_MSG);

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
		if (D) Log.d( LOG_TAG, "onBind" );	
		
		 if (IContextsDefinition.class.getName().equals(intent.getAction())) {
    		 Log.d( LOG_TAG, "bind-contextsBinder" );
             return contextsBinder;
         }
		
		 return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent){
		this.stopSelf();	
		return true;
	}
	
	public final IContextsDefinition.Stub contextsBinder = new IContextsDefinition.Stub() {

		public void newComposite(String compositeName)
				throws RemoteException {
			 //sync = new CompositeComponent(compositeName, getApplicationContext());
			 bc = new BluetoothContext(BluetoothAdapter.getDefaultAdapter(), getApplicationContext());
			 
			 //listen to this particular preference change
			 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
			 String pref = "remember_pwd";
			 uc = new UserPreferenceContext(sp, pref, getApplicationContext());
			 //lightcontext = new LightContext(sm, getApplicationContext());
			 CompositeComponent cc = new CompositeComponent("testComposite", getApplicationContext());
			 
			 ArrayList<String> eithers = new ArrayList<String>();
			 eithers.add("remember_pwd");
			 eithers.add("bluetoothON");
			 cc.registerComponent(eithers, true);
			 //cc.registerComponent("bluetoothON", false);
			 //or listen to any preference change
			 //uc = new UserPreferenceContext(sp, getApplicationContext());			
			 //lightcontext = new LightContext(sm, getApplicationContext());			
			 if (D) Log.d( LOG_TAG, "newComposite" );	
			
		}

		public void registerComponent(String componentName, String compositeName)
				throws RemoteException {
			//lightcontext = new LightContext(sm, getApplicationContext());
			//sync.registerComponent(componentName);
			//showNotification("light changed");
			if (D) Log.d( LOG_TAG, "registerComponent" );	
		}     
		
		public void registerCallback(IRemoteServiceCallback cb) {
            if (cb != null) mCallbacks.register(cb);
        }
        public void unregisterCallback(IRemoteServiceCallback cb) {
            if (cb != null) mCallbacks.unregister(cb);
        }

    };
    
    private void setupContextMonitor() {
   	 Log.v("value", "add contextMonitor");
  	contextMonitor = new BroadcastReceiver() {
       	@Override 
        	public void onReceive(Context context,Intent intent) {
        			if (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED")) {
        				if (D) Log.d( LOG_TAG, "onReceive" );
        				Bundle bundle = intent.getExtras();
        				String changeName = bundle.getString(Component.CONTEXT_NAME);
	        			boolean currentcontext = bundle.getBoolean(Component.CONTEXT_VALUE);
//	        			if(changeName.equalsIgnoreCase("datasync_ON") &&( currentcontext ) )
//	        				getListView().setBackgroundResource(color.black);
//	        			else if (changeName.equalsIgnoreCase("datasync_ON") &&( !currentcontext ) )
//	        				getListView().setBackgroundResource(color.white);
	        			showNotification(changeName);
	      		}
        		}        		  	
        	
        };
        registerReceiver(contextMonitor, filter);  	
    }
    
    
    private void showNotification(String contextChange) {
		// In this sample, we'll use the same text for the ticker and the expanded notification
    	if (D) Log.d( LOG_TAG, "showNotification");
		CharSequence contentTitle = "ContextEngine";
		CharSequence contentText = "Context Changed:" + contextChange;
		long when = System.currentTimeMillis();
		
		//this intent needs to be adapted, serves only for testing purposes!!!
		Intent i;		
		i = new Intent(getBaseContext(),TestActivity.class);
		//i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Notification notification = new Notification(R.drawable.stat_sample,contentText,when);
		PendingIntent contentIntent = PendingIntent.getActivity(ContextEngine.this, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		notification.flags |= notification.FLAG_AUTO_CANCEL;
		
		// Send the notification.
		// We use a layout id because it is a unique number.  We use it later to cancel.
		++count;
		mNM.notify(count, notification);
	}
    
    private static final int REPORT_MSG = 1;

    /**
     * Our Handler used to execute operations on the main thread.  This is used
     * to schedule increments of our value.
     */
    private final Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {

                // It is time to bump the value!
                case REPORT_MSG: {
                    // Up it goes.
                    int value = ++mValue;

                    // Broadcast to all clients the new value.
                    final int N = mCallbacks.beginBroadcast();
                    for (int i=0; i<N; i++) {
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value);
                        } catch (RemoteException e) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                    }
                    mCallbacks.finishBroadcast();

                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(REPORT_MSG), 1*1000);
                } break;
                default:
                    super.handleMessage(msg);
            }
        }
    };



	 @Override
	    public void onDestroy() {
		    super.onDestroy();
	    	Log.d( LOG_TAG, "onDestroy" );
	        // Cancel the persistent notification.
	        mNM.cancel(R.string.local_service_started);	
	        //lightcontext.stop();
	    	//sync.stop();
	    	uc.stop();
	    	uc=null;
	    	//lightcontext= null;
	    	//sync=null;
	    	unregisterReceiver(contextMonitor);	  
	    	
	    	// Unregister all callbacks.
	        mCallbacks.kill();
	        
	        // Remove the next pending message to increment the counter, stopping
	        // the increment loop.
	        mHandler.removeMessages(REPORT_MSG);


	        // Tell the user we stopped.
	        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	        
	    }
	
}