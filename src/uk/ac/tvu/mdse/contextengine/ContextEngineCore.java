/*
 * Copyright (C) 2014 The Context Engine Project
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
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import uk.ac.tvu.mdse.contextengine.PreferenceChangeComponent;
import uk.ac.tvu.mdse.contextengine.contexts.LocationContextTest;
import uk.ac.tvu.mdse.contextengine.db.ContextDB;
import uk.ac.tvu.mdse.contextengine.db.ContextDBImpl;
import uk.ac.tvu.mdse.contextengine.parser.ContextsParser;
import uk.ac.tvu.mdse.contextengine.parser.ParserHandler;
import uk.ac.tvu.mdse.contextengine.reasoning.ApplicationKey;
import uk.ac.tvu.mdse.contextengine.test.TestActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ContextEngineCore {

	protected Context context;
	protected static final String LOG_TAG = "ContextEngine";
	protected static final boolean D = true;
	// public String USER_CLASSPATH = null;

	private final HashMap<String, Component> activeContexts = new HashMap<String, Component>();

	// holding info about subscribed apps
	private final ArrayList<ApplicationKey> applicationKeys = new ArrayList<ApplicationKey>();

	// it is assumed that only one app define context
	// composition at the time - it needs to gets synchronised later on!
	ApplicationKey newAppKey;
	boolean set = false;

	// added because multiple messages received from one broadcast
	String lastContextName = "";
	String lastContextValue = "";

	// private ArrayList<LocationContextTest> locationContexts = new
	// ArrayList<LocationContextTest>();
	LocationContextTest locationContext;
	private LocationServices locationServices = null;
	private SharedPreferences sp;

	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.REMOTE_SERVICE";

	public static final String CONTEXT_INFORMATION = "context_information";
	public static final String CONTEXT_NAME = "context_name";
	public static final String CONTEXT_DATE = "context_date";
	public static final String CONTEXT_APPLICATION_KEY = "context_application_key";
	// public static final String DEFAULT_CLASSPATH =
	// "uk.ac.uwl.mdse.adspl.contextengine.contexts.";

	protected BroadcastReceiver contextMonitor;
	private ContextEngineService ces = null;
	private ContextDB contextDB;
	protected IntentFilter filter;
	int mValue = 0;

	// for notifications
	static int count = 0;
	static String order = "*";

	private NotificationManager mNM;

	public ContextEngineCore(Context c) {
		context = c;
		mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		filter = new IntentFilter(
				"uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");

		sp = PreferenceManager.getDefaultSharedPreferences(c);
		contextDB = new ContextDBImpl(c);
		setupContextMonitor();
		// try{
		// locationServices = new LocationServices(c);
		// }catch(Exception e){
		// if (D)
		// Log.e(LOG_TAG, e.getLocalizedMessage());
		// }
	}
	
	public List<String> getContextList(String appkey) {
		return contextDB.getUsableContextList(appkey);
	}
	
	public String getContextValue(String appkey, String componentname) {
		
		Component context = this.activeContexts.get(componentname);
		
		if (context != null) {
			return context.getContextInformation(appkey);
		}
		
		return "";
	}
	
	public boolean isComponentDeployed(String appkey, String componentname) {
		List<String> component = contextDB.getLoadComponentInfo(appkey, componentname);
		if (component != null)
			return true;
		else
			return false;
	}

	public boolean newPreferenceComponent(String appKey, String preferenceName,
			String preferenceType) {
		PreferenceChangeComponent preferenceContext = new PreferenceChangeComponent(
				sp, preferenceName, preferenceType, context);
		activeContexts.put(preferenceName, preferenceContext);
		Log.e(LOG_TAG, "newPreferenceComponent added");
		return true;
	}

	// public boolean registerContextPathI(String path) {
	// this.USER_CLASSPATH = path;
	// return true;
	// }

	public boolean registerAppKey(String key) {
		// if(newAppKey==null){
		// newAppKey = new ApplicationKey(key);
		// newAppKey.key = key;
		// return true;
		// }
		// for(ApplicationKey k: applicationKeys){
		// if(! (k.key.equalsIgnoreCase(key))){
		// applicationKeys.add(newAppKey);
		// newAppKey.key=key;
		// return true;
		// }
		// }
		// return false;
		newAppKey = new ApplicationKey(key);
		applicationKeys.add(newAppKey);
		Log.d(LOG_TAG, "registerAppKey " + newAppKey.key);
		return true;

	}

	public boolean newComponent(String appKey, String componentName) {
		// FOR LOCATION:
		// if (componentName.equals("LocationContext")) {
		// if (locationServices == null)
		// locationServices = new LocationServices(context);
		// }

		Component context = activeContexts.get(componentName);

		if (context != null) {
			Log.e(LOG_TAG, "Component already running!");
			return false;
		} else {
			return loadClass(appKey, componentName);
		}

	}

	private ApplicationKey getApplicationKey(String appKey) {

		ApplicationKey appkey = null;
		for (ApplicationKey ak : this.applicationKeys) {
			if (ak.key.equalsIgnoreCase(appKey)) {
				appkey = ak;
			}
		}

		return appkey;
	}

	public boolean newContextValues(String appKey, String componentName,
			String[] contextValues) {

		Component context = activeContexts.get(componentName);

		if (context == null) {
			newComponent(appKey, componentName);
			newContextValues(appKey, componentName, contextValues);
		} else {
			context.setupNewValuesSet(getApplicationKey(appKey), contextValues);
			return true;
		}
		return false;
	}

	public boolean newContextValue(String appKey, String componentName,
			String contextValue) {
		try {

			Component context = activeContexts.get(componentName);

			if (context == null) {
				newComponent(appKey, componentName);
				newContextValue(appKey, componentName, contextValue);
			} else {
				// need to revisit newAppKey stuff...
				context.addContextValue(newAppKey, contextValue);
			}

			return true;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getLocalizedMessage());
			return false;
		}
	}

	public void newSpecificContextValue(String appKey, String componentName,
			String contextValue, String numericData1, String numericData2) {
		if (D)
			Log.d(LOG_TAG, "addSpecificContextValue");
		try {
			Component context = activeContexts.get(componentName);

			if (context == null) {
				newComponent(appKey, componentName);
				newSpecificContextValue(appKey, componentName, contextValue,
						numericData1, numericData2);
			} else {
				// need to revisit newAppKey stuff...
				context.addSpecificContextValue(newAppKey, contextValue,
						Double.valueOf(numericData1),
						Double.valueOf(numericData2));
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getLocalizedMessage());
		}
	}

	public void removeContextValueSet(String appKey, String componentName) {
		ApplicationKey key = null;

		for (ApplicationKey k : this.applicationKeys) {
			if (k.key.equalsIgnoreCase(appKey)) {
				key = k;
			}
		}

		// look up for the composite if created
		Component context = activeContexts.get(componentName);

		if ((context != null) && (key != null)) {
			context.removeValuesSet(key);

			if (context.getValuesSetCount() < 1) {
				activeContexts.remove(componentName);
				context.stop();
			}
		}
	}

	public void newRange(String appKey, String componentName, String minValue,
			String maxValue, String contextValue) {
		// look up for the component
		Component context = activeContexts.get(componentName);

		Log.d(LOG_TAG, "addRange" + componentName);

		if (context == null) {
			newComponent(appKey, componentName);
			newRange(appKey, componentName, minValue, maxValue, contextValue);

		} else {
			context.addRange(newAppKey, Long.parseLong(minValue),
					Long.parseLong(maxValue), contextValue);
		}
	}

	public boolean addComposite(String compositeName) {
		try {

			CompositeComponent compositeContext = (CompositeComponent) activeContexts
					.get(compositeName);

			if (compositeContext != null) {
				Log.e(LOG_TAG, "Component already running!");
				// create new content values set
				return false;
			} else {
				compositeContext = new CompositeComponent(compositeName,
						context);
				activeContexts.put(compositeName, compositeContext);
				return true;
			}

		} catch (Exception e) {
			Log.d(LOG_TAG, e.getLocalizedMessage());
			return false;
		}
	}

	public boolean addToCompositeM(String appKey, String componentName,
			String compositeName) {

		CompositeComponent compositeContext = (CompositeComponent) activeContexts
				.get(compositeName);
		Component context = activeContexts.get(componentName);

		if (context == null) {
			if (!(loadClass(appKey, componentName)))
				return false;
		}
		if (compositeContext == null) {
			compositeContext = new CompositeComponent(compositeName,
					this.context);
			activeContexts.put(compositeName, compositeContext);
		}
		compositeContext.registerComponent(context);
		if (D)
			Log.d(LOG_TAG, "Added" + componentName + " to " + compositeName);
		return true;
	}

	public void newRule(String componentName, String[] condition, String result) {

		CompositeComponent compositeContext = (CompositeComponent) activeContexts
				.get(componentName);

		if (compositeContext != null) {
			compositeContext.addRule(condition, result);
			Log.d(LOG_TAG, "addRule");
		}

	}

	public void componentDefined(String name) {

		Component context = activeContexts.get(name);

		if (context != null) {
			context.componentDefined(newAppKey);
		}
	}

	public boolean compositeReady(String compositeName) {
		CompositeComponent compositeContext = (CompositeComponent) activeContexts
				.get(compositeName);

		if (compositeContext != null) {
			if (set == false) {
				setupContextMonitor();
				set = true;
			}

			compositeContext.componentDefined(newAppKey);
			return true;
		} else {
			Log.e(LOG_TAG, "Composite not active!");
			return false;
		}

	}

	public void setupContextMonitor() {
		Log.v("value", "add contextMonitor");
		contextMonitor = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						"uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED")) {
					if (D)
						Log.d(LOG_TAG, "onReceive");
					Bundle bundle = intent.getExtras();

					// show notification - just for testing
					String contextName = bundle
							.getString(Component.CONTEXT_NAME);
					String contextValue = bundle
							.getString(Component.CONTEXT_INFORMATION);
					// ArrayList<String> appKey = bundle
					// .getStringArrayList(Component.CONTEXT_APPLICATION_KEY);
					if (lastContextName.equals(contextName)
							&& lastContextValue.equals(contextValue))
						Log.v("value", "again the same msg");
					else
						// showNotification(contextName+ " "+contextValue);
						sendBroadcastToApps(bundle);
				}
			}

		};
		context.registerReceiver(contextMonitor, filter);
	}

	protected void runXML(String path) {
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ path);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			ParserHandler ph = new ParserHandler();
			ph.setContextEngine(this);
			xr.setContentHandler(ph);

			xr.parse(new InputSource(new InputStreamReader(new FileInputStream(
					file))));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void runXMLParser(String path) {
		try {
			Log.d(LOG_TAG, "runWorkflowXML");
			ContextsParser wcp = new ContextsParser();
			wcp.readXMLfile(context, path, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void copyDexFile(String appKey, final String newDex,
			String[] contexts, String packageName, int permission) {
		File dexInternalStoragePath = new File(context.getDir("dex",
				Context.MODE_PRIVATE), newDex);
		// File newDexFile = new File(context.getExternalFilesDir(null),
		// newDex);
		String celoc = Environment.getExternalStorageDirectory()
				+ "/Android/data/uk.ac.tvu.mdse.contextengine/files/";
		File newDexFile = new File(celoc, newDex);
		BufferedInputStream bis = null;
		OutputStream dexWriter = null;

		final int BUF_SIZE = 8 * 1024;
		try {
			bis = new BufferedInputStream(new FileInputStream(newDexFile));
			dexWriter = new BufferedOutputStream(new FileOutputStream(
					dexInternalStoragePath));
			byte[] buf = new byte[BUF_SIZE];
			int len;
			while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
				dexWriter.write(buf, 0, len);
			}
			dexWriter.close();
			bis.close();
			Log.v(LOG_TAG, "copied dex file");
			for (String c : contexts) {
				this.contextDB.insertComponent(packageName, c, appKey,
						permission, newDex);
			}

		} catch (Exception e) {
			Log.e("Error", e.getStackTrace().toString());
		}
	}

	protected boolean loadClass(String appId, String componentName) {
		List<String> componentInfo = this.contextDB.getLoadComponentInfo(appId,
				componentName);

		if (componentInfo.size() > 0) {
			return loadClass(componentName, componentInfo.get(0),
					componentInfo.get(1));
		}

		return false;

	}

	private boolean loadClass(String componentName, String dex,
			String packagename) {
		final File optimizedDexOutputPath = context.getDir("outdex",
				Context.MODE_PRIVATE);
		File dexInternalStoragePath;
		// if (dex == null)
		// dexInternalStoragePath = new File(context.getDir("dex",
		// Context.MODE_PRIVATE),
		// "classes.dex");
		// else
		dexInternalStoragePath = new File(context.getDir("dex",
				Context.MODE_PRIVATE), dex);

		DexClassLoader cl = new DexClassLoader(
				dexInternalStoragePath.getAbsolutePath(),
				optimizedDexOutputPath.getAbsolutePath(), null,
				context.getClassLoader());
		Class<?> contextClass = null;
		Class<?>[] parameterTypes = { Context.class };

		try {
			// Load the Class
			contextClass = cl
					.loadClass(packagename.concat("." + componentName));
			Constructor<?> contextConstructor = contextClass
					.getConstructor(parameterTypes);

			Component context = (Component) contextConstructor
					.newInstance(this.context);

			activeContexts.put(componentName, context);

			return true;
		} catch (ClassNotFoundException cnfe) {
			Log.e(LOG_TAG, "Component does not exist!");
			return false;
		} catch (Exception e) {
			// Log.e("Error", e.getStackTrace().toString());
			e.printStackTrace();
			return false;
		}

	}

	// used originally to test broadcasting
	// instead of sending message to app, the message is displayed as
	// notification
	@SuppressWarnings("deprecation")
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
		i = new Intent(((ContextWrapper) context).getBaseContext(),
				TestActivity.class);
		// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.stat_sample,
				contentText, when);
		PendingIntent contentIntent = PendingIntent.getActivity(ces, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(ces, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		++count;
		mNM.notify(count, notification);
	}

	public void sendBroadcastToApps(Bundle bundle) {

		for (Component c : activeContexts.values()) {
			if ((c.contextName.equals("NoOfReviewsUP"))
					|| (c.contextName.equals("RatingUP")))
				Log.d(LOG_TAG, c.contextName + ": 2");
			else
				Log.d(LOG_TAG, c.contextName + ": "
						+ c.valuesSets.get(0).contextInformation);
		}

		Log.d(LOG_TAG, "broadcasting to apps" + bundle.getString(CONTEXT_NAME)
				+ bundle.getString(CONTEXT_INFORMATION));

		Intent intent = new Intent();
		try {
			intent.setAction(CONTEXT_INTENT);
			intent.putExtra(CONTEXT_NAME, bundle.getString(CONTEXT_NAME));
			intent.putExtra(CONTEXT_APPLICATION_KEY,
					bundle.getString(CONTEXT_APPLICATION_KEY));
			intent.putExtra(CONTEXT_DATE, bundle.getString(CONTEXT_DATE));
			if (!bundle.getString(CONTEXT_INFORMATION).equals(null))
				intent.putExtra(CONTEXT_INFORMATION,
						bundle.getString(CONTEXT_INFORMATION));

			// c.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e("ContextEngine", "broadcasting to apps not working");
		}
	}

	public void stop() {
		Log.d(LOG_TAG, "onDestroy");

		// Cancel the persistent notification.
		mNM.cancel(R.string.local_service_started);

		for (Component context : activeContexts.values()) {
			context.stop();
			context = null;
		}

		context.unregisterReceiver(contextMonitor);

		if (locationServices != null) {
			locationServices.stop();
			locationServices = null;
		}
		this.contextDB.closeDB();

	}

}
