package uk.ac.tvu.mdse.contextengine;

import uk.ac.tvu.mdse.contextengine.IContextsDefinition;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ContextEngineService extends Service {

	private static final String LOG_TAG = "ContextEngine";
	ContextEngineCore cec;

	@Override
	public void onCreate() {
		cec = new ContextEngineCore(getApplicationContext());
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (IContextsDefinition.class.getName().equals(intent.getAction())) {
			Log.d(LOG_TAG, "bind-contextsBinder");
			return contextsBinder;
		}
		return null;
	}

	public final IContextsDefinition.Stub contextsBinder = new IContextsDefinition.Stub() {

		public boolean registerApplicationKey(String key) {
			return cec.registerAppKey(key);
		}

		public boolean registerComponent(String appKey, String componentName)
				throws RemoteException {
			return cec.newComponent(appKey, componentName);
		}

		// ***add context values to a component***
		public boolean addContextValues(String appKey, String componentName,
				String[] contextValues) {
			return cec.newContextValues(appKey, componentName, contextValues);
		}

		// ***add a context value***
		public boolean addContextValue(String appKey, String componentName,
				String contextValue) {
			return cec.newContextValue(appKey, componentName, contextValue);
		}

		// ***add a specific context value described by two numeric coordinates
		// (e.g.location)***
		public void addSpecificContextValue(String appKey,
				String componentName, String contextValue, String numericData1,
				String numericData2) {
			cec.newSpecificContextValue(appKey, componentName, contextValue,
					numericData1, numericData2);
		}

		// ***define higher context value - in case of numeric values specify
		// range of values***
		public void addRange(String appKey, String componentName,
				String minValue, String maxValue, String contextValue) {
			cec.newRange(appKey, componentName, minValue, maxValue,
					contextValue);
		}

		public boolean newComposite(String appKey, String compositeName)
				throws RemoteException {
			return cec.addComposite(compositeName);
		}

		public boolean addToComposite(String appKey, String componentName,
				String compositeName) throws RemoteException {
			return cec.addToCompositeM(appKey, componentName, compositeName);
		}

		public void addRule(String componentName, String[] condition,
				String result) {
			cec.newRule(componentName, condition, result);
		}

		public boolean startComposite(String compositeName)
				throws RemoteException {
			return cec.compositeReady(compositeName);
		}

		public void setupContexts(String path) throws RemoteException {
			cec.runXML(path);
		}

		@Override
		public void copyDexFile(String appKey, String newDex,
				String[] contexts, String packageName, int permission)
				throws RemoteException {
			cec.copyDexFile(appKey, newDex, contexts, packageName, permission);

		}
	};

	@Override
	public void onDestroy() {
		cec.stop();
	}
}
