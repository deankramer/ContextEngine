/**
 * @project ContextEngine
 * @date 26 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.util.ArrayList;
import java.util.List;

import uk.ac.tvu.mdse.contextengine.contexts.BluetoothContext;
import uk.ac.tvu.mdse.contextengine.contexts.LightContext;
import uk.ac.tvu.mdse.contextengine.contexts.UserPreferenceContext;
import uk.ac.tvu.mdse.contextengine.contexts.WifiContext;
import uk.ac.tvu.mdse.contextengine.db.ContextDB;
import uk.ac.tvu.mdse.contextengine.db.ContextDBSQLite;
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
import android.net.wifi.WifiManager;
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

public class ContextEngine extends Service {

	private static final String LOG_TAG = "ContextEngine";
	private boolean D = true;

	private NotificationManager mNM;
	private BroadcastReceiver contextMonitor;
	private IntentFilter filter;
	private LightContext lightcontext;
	private CompositeComponent sync;
	private BluetoothContext bluetoothContext;
	private PreferenceChangeComponent uc1;
	private PreferenceChangeComponent uc2;
	private SensorManager sm;
	
	private ContextDB db;
	
	//there are 2 approaches to deal with contexts, this variable serves
	//as a controller to switch between Anna's and Dean's approach
	//value = true - Dean
	//value = false - Anna
	private boolean controlVariable = false;

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
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		filter = new IntentFilter(
				"uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
		setupContextMonitor();

		// While this service is running, it will continually increment a
		// number. Send the first message that is used to perform the
		// increment.
		mHandler.sendEmptyMessage(REPORT_MSG);
		
		// Manage list of all contexts registered with the engine and store the list in the database
		db = new ContextDBSQLite(getApplicationContext());
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

		public void newComposite(String compositeName) throws RemoteException {

			if (controlVariable){
				Context c = getApplicationContext();
				bluetoothContext = new BluetoothContext(BluetoothAdapter.getDefaultAdapter(),c);
	
				// listen to these particular preferences change
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
				String pref1 = "remember_pwd";
				String pref2 = "method";
				uc1 = new PreferenceChangeComponent(sp, pref1,PreferenceChangeComponent.PreferenceType.BOOLEAN, c);
				uc2 = new PreferenceChangeComponent(sp, pref2, PreferenceChangeComponent.PreferenceType.STRING, c);
				// lightcontext = new LightContext(sm, getApplicationContext());
				CompositeComponent cc = new CompositeComponent("testComposite",	c);
				WifiContext wc = new WifiContext(
						(WifiManager) getSystemService(Context.WIFI_SERVICE), c);
				// ArrayList<String> eithers = new ArrayList<String>();
				// eithers.add("remember_pwd");
				// eithers.add("bluetoothON");
				cc.registerComponent("bluetoothOn", true);
				cc.registerComponent("wifiOn", false);
				// cc.registerComponent("bluetoothON", false);
				// or listen to any preference change
				// uc = new UserPreferenceContext(sp, getApplicationContext());
				// lightcontext = new LightContext(sm, getApplicationContext());
				if (D)
					Log.d(LOG_TAG, "Dean's approach");
			}
			else{
				Context c = getApplicationContext();
				WifiContext wifiContext = new WifiContext(
						(WifiManager) getSystemService(Context.WIFI_SERVICE), c);
				
				bluetoothContext = new BluetoothContext(BluetoothAdapter.getDefaultAdapter(),c);
				
				lightcontext = new LightContext(sm, getApplicationContext());
				lightcontext.addRange(0.00, 100.00, "LOW");
				lightcontext.addRange(100.01, 180.00, "MEDIUM");
				lightcontext.addRange(180.01, 250.00, "HIGH");
				
				
				RuledCompositeComponent rcc = new RuledCompositeComponent("test1", c);
				
				rcc.registerComponent(wifiContext);
				rcc.registerComponent(bluetoothContext);
				rcc.registerComponent(lightcontext);
				
				rcc.addRule(new String[]{"ON","ON","MEDIUM"}, "ON");
				rcc.addRule(new String[]{"ON","OFF","HIGH"}, "ON");		
				
				
				
				if (D)
					Log.d(LOG_TAG, "Anna's approach");
			}

		}

		public void registerComponent(String componentName, String compositeName)
				throws RemoteException {
			// lightcontext = new LightContext(sm, getApplicationContext());
			// sync.registerComponent(componentName);
			// showNotification("light changed");
			if (D)
				Log.d(LOG_TAG, "registerComponent");
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
					String changeName = bundle
							.getString(Component.CONTEXT_NAME);
					boolean currentcontext = bundle
							.getBoolean(Component.CONTEXT_VALUE);
					String contextvalue = bundle
							.getString(PreferenceChangeComponent.CONTEXT_VALUE);
					// if(changeName.equalsIgnoreCase("datasync_ON") &&(
					// currentcontext ) )
					// getListView().setBackgroundResource(color.black);
					// else if (changeName.equalsIgnoreCase("datasync_ON") &&(
					// !currentcontext ) )
					// getListView().setBackgroundResource(color.white);
					if (contextvalue == null)
						showNotification(changeName+ " "+currentcontext);
					else
						showNotification(changeName+ " "+contextvalue);
				}
			}

		};
		registerReceiver(contextMonitor, filter);
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

	private static final int REPORT_MSG = 1;

	/**
	 * Our Handler used to execute operations on the main thread. This is used
	 * to schedule increments of our value.
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			// It is time to bump the value!
			case REPORT_MSG: {
				// Up it goes.
				int value = ++mValue;

				// Broadcast to all clients the new value.
				final int N = mCallbacks.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						mCallbacks.getBroadcastItem(i).valueChanged(value);
					} catch (RemoteException e) {
						// The RemoteCallbackList will take care of removing
						// the dead object for us.
					}
				}
				mCallbacks.finishBroadcast();

				// Repeat every 1 second.
				sendMessageDelayed(obtainMessage(REPORT_MSG), 1 * 1000);
			}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		// Cancel the persistent notification.
		mNM.cancel(R.string.local_service_started);
		// lightcontext.stop();
		// sync.stop();
		bluetoothContext.stop();
		bluetoothContext = null;
		uc1.stop();
		uc1 = null;
		uc2.stop();
		uc2 = null;
		// lightcontext= null;
		// sync=null;
		unregisterReceiver(contextMonitor);

		// Unregister all callbacks.
		mCallbacks.kill();

		// Remove the next pending message to increment the counter, stopping
		// the increment loop.
		mHandler.removeMessages(REPORT_MSG);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT)
				.show();

	}

}