/**
 * @project ContextEngine
 * @date 26 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.db.ContextDB;
import uk.ac.tvu.mdse.contextengine.db.ContextDBSQLite;
import uk.ac.tvu.mdse.contextengine.test.TestActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class ContextEngine extends Service {

	private static final String LOG_TAG = "ContextEngine";
	private boolean D = true;

	public static final String CONTEXT_INFORMATION = "context_information";
	
	private NotificationManager mNM;
	private BroadcastReceiver contextMonitor;
	private IntentFilter filter;
	private PreferenceChangeComponent uc1;
	private PreferenceChangeComponent uc2;	
	
	private CompositeComponent sync;
	private RuledCompositeComponent ruledCC;	
	
	private Context c;
	private ContextDB db;
	
	int defined = 0;
	
	private ArrayList<Component> activeContexts = new ArrayList<Component>();
	
	//there are 2 approaches to deal with contexts, this variable serves
	//as a controller to switch between hashtable and rules approach
	//value = true - hashtable
	//value = false - rules
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
		filter = new IntentFilter(
				"uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
		copyDexFile();
		//setupContextMonitor();

		c = getApplicationContext();
		
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

		public void newComposite(String compositeName) throws RemoteException {

			if (controlVariable){
				Context c = getApplicationContext();
				//bluetoothContext = new BluetoothContext(BluetoothAdapter.getDefaultAdapter(),c);
	
				// listen to these particular preferences change
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
				String pref1 = "remember_pwd";
				String pref2 = "method";
				uc1 = new PreferenceChangeComponent(sp, pref1,PreferenceChangeComponent.PreferenceType.BOOLEAN, c);
				uc2 = new PreferenceChangeComponent(sp, pref2, PreferenceChangeComponent.PreferenceType.STRING, c);
				// lightcontext = new LightContext(sm, getApplicationContext());
				CompositeComponent cc = new CompositeComponent("testComposite",	c);
				//WifiContext wc = new WifiContext(
				//		(WifiManager) getSystemService(Context.WIFI_SERVICE), c);
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
					Log.d(LOG_TAG, "Hashtable approach");
			}
			else{				
				
				ruledCC = new RuledCompositeComponent(compositeName, c);
				
				
				activeContexts.add(ruledCC);
				//db.addContext(ruledCC);
				try{				
				//wifiContext = new WifiContext(c);
				//bluetoothContext = new BluetoothContext(c);
				//lightcontext = new LightContext(getApplicationContext()); 
				}
				catch(Exception e){
					Log.d(LOG_TAG, e.getLocalizedMessage());
				}
				if (D)
					Log.d(LOG_TAG, "Ruled approach");
			}

		}

		public void registerComponent(String componentName, String compositeName)
				throws RemoteException {
			
			loadClass(componentName);
			
			RuledCompositeComponent ruledComponent = null;
			Component component = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(compositeName))
					ruledComponent = (RuledCompositeComponent) ac;
				if (ac.contextName.equals(componentName))
					component = ac;			
			}		
			
			if ((ruledComponent!=null)&&(component!=null)){
				ruledComponent.registerComponent(component);	
				Log.d(LOG_TAG, component.contextName);
			}			
			
			if (D)
				Log.d(LOG_TAG, "registerComponent");
		}
		
		public void addRange(String componentName, String minValue, String maxValue, String contextValue){
			
			//look up for the component
			ListenerComponent component = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(componentName))
					component = (ListenerComponent) ac;				
			}		
			Log.d(LOG_TAG, "addRange" +  componentName);
			if (component!=null)
				component.addRange(Integer.valueOf(minValue), Integer.valueOf(maxValue), contextValue);
			
		}
		
		public void addRule(String componentName, String[] condition, String result){
//			rcc.addRule(new String[]{"ON","ON","MEDIUM"}, "ON");
//			rcc.addRule(new String[]{"ON","OFF","HIGH"}, "ON");	
			
			RuledCompositeComponent ruledComponent = null;
			
			//look up for the composite if created
			for (Component ac: activeContexts){
				if (ac.contextName.equals(componentName))
					ruledComponent = (RuledCompositeComponent) ac;		
			}	
			
			//if ((ruledComponent!=null)&&(ruledComponent.getComponentsNo() == condition.length))
				ruledComponent.addRule(condition, result);
				//ruledComponent.fireRules();
				Log.d(LOG_TAG, "addRule" );
				defined++;
				if (defined == 2){
					setupContextMonitor();
					ruledComponent.componentDefined();
				}
				
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
//					Message msg = new Message();
//					msg.setData(bundle);
//					mHandler.sendMessage(msg);
					
					String changeName = bundle
							.getString(Component.CONTEXT_NAME);
//					boolean currentcontext = bundle
//							.getBoolean(Component.CONTEXT_VALUE);
					String contextvalue = bundle
							.getString(Component.CONTEXT_INFORMATION);
					// if(changeName.equalsIgnoreCase("datasync_ON") &&(
					// currentcontext ) )
					// getListView().setBackgroundResource(color.black);
					// else if (changeName.equalsIgnoreCase("datasync_ON") &&(
					// !currentcontext ) )
					// getListView().setBackgroundResource(color.white);
//					if (contextvalue == null)
//						showNotification(changeName+ " "+currentcontext);
//					else
						showNotification(changeName+ " "+contextvalue);
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

	protected void loadClass(String componentName) {
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
			
			  } catch (Exception e) {
				  Log.e("Error", e.getStackTrace().toString());
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

//	private static final int REPORT_MSG = 1;
//
//	/**
//	 * Our Handler used to execute operations on the main thread. This is used
//	 * to schedule increments of our value.
//	 */
//	private final Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//
//			// It is time to bump the value!
//			case REPORT_MSG: {
//				// Up it goes.
////				int value = ++value;
//				Bundle bundle = msg.getData();
//				String contextInfo = bundle
//				.getString(Component.CONTEXT_NAME);
//				// Broadcast to all clients the new value.
//				final int N = mCallbacks.beginBroadcast();
//				for (int i = 0; i < N; i++) {
//					try {
//						mCallbacks.getBroadcastItem(i).valueChanged(contextInfo);
//					} catch (RemoteException e) {
//						// The RemoteCallbackList will take care of removing
//						// the dead object for us.
//					}
//				}
//				mCallbacks.finishBroadcast();
//
//				// Repeat every 1 second.
////				sendMessageDelayed(obtainMessage(REPORT_MSG), 1 * 1000);
//			}
//				break;
//			default:
//				super.handleMessage(msg);
//			}
//		}
//	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		// Cancel the persistent notification.
		mNM.cancel(R.string.local_service_started);
		// lightcontext.stop();
		// sync.stop();
		for (Component ac: activeContexts){
			ac.stop();
			ac = null;
		}
//		bluetoothContext.stop();
//		bluetoothContext = null;
//		uc1.stop();
//		uc1 = null;
//		uc2.stop();
//		uc2 = null;
		// lightcontext= null;
		// sync=null;
		unregisterReceiver(contextMonitor);

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