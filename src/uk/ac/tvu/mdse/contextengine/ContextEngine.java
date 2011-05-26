/**
 * @project ContextEngine
 * @date 26 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class ContextEngine extends Service{
	
	private static final String LOG_TAG = "CAWEFA_SERVER";
	private boolean D = true;
	
	private NotificationManager mNM;
	
	private CompositeComponent sync;
	
	
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
	
	public final IContextsDefinition.Stub contextsBinder = new IContextsDefinition.Stub() {

		@Override
		public void newComposite(String compositeName)
				throws RemoteException {
			 sync = new CompositeComponent(compositeName, getApplicationContext());
		     sync.registerComponent("lightlevelHIGH");
			
		}

		@Override
		public void registerComponent(String componentName, String compositeName)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
       
    };
    
    private void showNotification(String contextChange) {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_started);
		
		CharSequence contentTitle = "ContextEngine";
		CharSequence contentText = "Context Changed:" + contextChange;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(R.drawable.stat_sample,text,when);
		
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		long[] vibrate = {0,100,200,300};
		notification.vibrate = vibrate;
		
		notification.flags |= notification.FLAG_AUTO_CANCEL;
		
		// Send the notification.
		// We use a layout id because it is a unique number.  We use it later to cancel.
		mNM.notify(R.string.local_service_started, notification);
	}

	 @Override
	    public void onDestroy() {
	    	Log.d( LOG_TAG, "onDestroy" );
	        // Cancel the persistent notification.
	        mNM.cancel(R.string.local_service_started);	     
	        // Tell the user we stopped.
	        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	    }

	public void newComposite(String string) {
		// TODO Auto-generated method stub
		
	}
}



