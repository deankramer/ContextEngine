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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import uk.ac.tvu.mdse.contextengine.contexts.LocationContext;
import uk.ac.tvu.mdse.contextengine.db.ContextDB;
import uk.ac.tvu.mdse.contextengine.db.ContextDBSQLite;
import uk.ac.tvu.mdse.contextengine.parser.ParserHandler;
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
import android.os.Environment;
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
	public static final String CONTEXT_APPLICATION_KEY = "context_application_key";	
	
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

		setupContextMonitor();
		
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
			return registerAppKey(key);
		}
		
		public boolean registerComponent(String componentName)
				throws RemoteException {
			return newComponent(componentName);
		}
		
		//***add context values to a component***
		public  boolean addContextValues(String componentName, String[] contextValues){	
			return newContextValues(componentName, contextValues);
		 }
		  
		  //***add a context value***
		public boolean addContextValue(String componentName, String contextValue){
			return newContextValue(componentName, contextValue);
		}
		  
		  //***add a specific context value described by two numeric coordinates (e.g.location)***
		public void addSpecificContextValue(String componentName, String contextValue, String numericData1, String numericData2){
			newSpecificContextValue(componentName, contextValue, numericData1, numericData2);
		}
		
		//***define higher context value - in case of numeric values specify range of values***  
		public void addRange(String componentName, String minValue, String maxValue, String contextValue){
			newRange(componentName, minValue, maxValue, contextValue);
		}
		
		public boolean newComposite(String compositeName) throws RemoteException {				
			return addComposite(compositeName);
		}

		public boolean addToComposite(String componentName, String compositeName)
				throws RemoteException {
			return addToCompositeM(componentName, compositeName);
		}
		
		public void addRule(String componentName, String[] condition, String result){
			newRule(componentName, condition, result);
		}
		
		public boolean startComposite(String compositeName) throws RemoteException {
			return compositeReady(compositeName);
		}

		public void registerCallback(IRemoteServiceCallback cb) {
			if (cb != null)
				mCallbacks.register(cb);
		}

		public void unregisterCallback(IRemoteServiceCallback cb) {
			if (cb != null)
				mCallbacks.unregister(cb);
		}

		public void setupContexts(String path) throws RemoteException {
			runXML(path);
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
//					String contextName = bundle
//							.getString(Component.CONTEXT_NAME);
//					String contextValue = bundle
//							.getString(Component.CONTEXT_INFORMATION);
//					ArrayList<String> appKey = bundle
//							.getStringArrayList(Component.CONTEXT_APPLICATION_KEY);
//						//showNotification(contextName+ " "+contextvalue);
//					if (appKey.size()>0)
//						Log.v(LOG_TAG, "onReceive:" + contextName + " " + contextValue + " " + appKey.get(0));
//					else
//						Log.v(LOG_TAG, "onReceive:" + contextName + " " + contextValue);
					sendBroadcastToApps(bundle);
				}
			}

		};
		registerReceiver(contextMonitor, filter);
	}
	
	protected void runXML(String path) {
		try{
			File file = new File(Environment.getExternalStorageDirectory() + path);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			ParserHandler ph = new ParserHandler();
			ph.setContextEngine(this);
			xr.setContentHandler(ph);
			
			xr.parse(new InputSource(new InputStreamReader(new FileInputStream(file))));
		} catch(MalformedURLException e){
			e.printStackTrace();
		} catch(ParserConfigurationException e){
			e.printStackTrace();
		} catch(SAXException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
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
		
		Log.d(LOG_TAG, "broadcasting to apps");
		
		Intent intent = new Intent();
		try {
		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME,bundle.getString(CONTEXT_NAME));
		Log.d(LOG_TAG, "CONTEXT_NAME:" + bundle.getString(CONTEXT_NAME));
		if (!bundle.getStringArray(CONTEXT_APPLICATION_KEY).equals(null)){
			Log.d(LOG_TAG, "broadcasting to apps-getting app key");
			intent.putExtra(CONTEXT_APPLICATION_KEY, bundle.getStringArray(CONTEXT_APPLICATION_KEY));}
		intent.putExtra(CONTEXT_DATE, bundle.getString(CONTEXT_DATE));
//		if (!bundle.getBoolean(CONTEXT_VALUE).equals(null))
//			intent.putExtra(CONTEXT_VALUE, bundle.getBoolean(CONTEXT_VALUE));
		if (!bundle.getString(CONTEXT_INFORMATION).equals(null))
			intent.putExtra(CONTEXT_INFORMATION, bundle.getString(CONTEXT_INFORMATION));
		Log.d(LOG_TAG, "CONTEXT_INFORMATION:" + bundle.getString(CONTEXT_INFORMATION));
			c.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e("ContextEngine", "broadcasting to apps not working");
		}
	}
	
	public boolean registerAppKey(String key){
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
	
	public boolean newComponent(String componentName){
		//FOR LOCATION:
		if (componentName.equals("LocationContext")){
			if (locationServices == null)
				locationServices = new LocationServices(c);
		}
		
		if(activeContexts.isEmpty()){
			return loadClass(componentName);
		}
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
	
	public boolean newContextValues(String componentName, String[] contextValues){
		for (Component ac: activeContexts){
			if (ac.contextName.equals(componentName)){
				ac.setupNewValuesSet(newAppKey, contextValues);
				return true;
			}
		}
		Log.e(LOG_TAG, "Context not running!");
		return false;
	}
	
	public boolean newContextValue(String componentName, String contextValue){
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
	
	public void newSpecificContextValue(String componentName, String contextValue, String numericData1, String numericData2){
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
	
	public void newRange(String componentName, String minValue, String maxValue, String contextValue){
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
	
	public boolean addComposite(String compositeName){
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
						//create new content values set
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
	
	public boolean addToCompositeM(String componentName, String compositeName){
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
	
	public void newRule(String componentName, String[] condition, String result){
		
		RuledCompositeComponent ruledComponent = null;
		
		//look up for the composite if created
		for (Component ac: activeContexts){
			if (ac.contextName.equals(componentName))
				ruledComponent = (RuledCompositeComponent) ac;		
		}	
		
		ruledComponent.addRule(condition, result);
		Log.d(LOG_TAG, "addRule" );
	}
	
	public boolean compositeReady(String compositeName){
		RuledCompositeComponent ruledComponent = null;
		
		//look up for the composite if created
		for (Component ac: activeContexts){
			if (ac.contextName.equals(compositeName)){
				ruledComponent = (RuledCompositeComponent) ac;
				ruledComponent.addAppKey(newAppKey);
				
				//check all contexts  whether app key added:
				for (Component c: ruledComponent.components)
				{					
					if (c.valuesSets.size()==1){
						c.addAppKey(newAppKey);
					}
						
				}
					
				setupContextMonitor();
				ruledComponent.componentDefined();
				return true;
			}
		}
		Log.e(LOG_TAG, "Composite not active!");
		return false;
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
		
		if (locationServices!=null){		
			locationServices.stop();
			locationServices = null;
		}
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
