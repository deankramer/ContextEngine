package uk.ac.tvu.mdse.contextengine;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

/**
 * @project ContextEngine
 * @date 16 Jun 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class PreferenceChangeComponent extends Component implements
		OnSharedPreferenceChangeListener {

	private static final long serialVersionUID = 560933927152794610L;
	// Monitoring
	private static final String LOG_TAG = "UserPreferenceContext";
	private static final boolean D = true;

	private SharedPreferences sharedPreferences;
	private String preference;
	public enum PreferenceType {STRING, INT, BOOLEAN};
	private PreferenceType prefType;
	
	private String preferenceValue;

	public PreferenceChangeComponent(SharedPreferences sp, String pref, PreferenceType prefT, Context c) {
		super(pref, c);
		// might be option to work just with one preference a time, or register
		// a number of preferences
		preference = pref;
		sharedPreferences = sp;
		prefType = prefT;
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	public PreferenceChangeComponent(SharedPreferences pm, Context c) {
		super("USER_PREFERENCE", c);
		sharedPreferences = pm;
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

//	public boolean onPreferenceChange(Preference preference, Object newValue) {
//		if (D) Log.v(LOG_TAG, "onPreferenceChange");
//		preferenceValue = newValue.toString();
//		sendNotification();
//		return true;
//	}

	public void stop() {
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		if (D) Log.v(LOG_TAG, "Stopping");
	}

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		if (D) Log.v(LOG_TAG, "onPreferenceChange");
		if (preference.equals(arg1)){
			switch (prefType){
			case BOOLEAN:
				preferenceValue = String.valueOf(arg0.getBoolean(arg1, true));
				sendNotification(preferenceValue);
				break;
			case STRING:
				preferenceValue = arg0.getString(arg1, "none");
				sendNotification(preferenceValue);
				break;
			case INT:
				preferenceValue = String.valueOf(arg0.getInt(arg1, 0));
				sendNotification(preferenceValue);
				break;
			default:
				preferenceValue = "none";
				sendNotification(preferenceValue);
			}			
		}			
	}
	
	public void sendNotification(String preferenceValue) {
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, contextName);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, preferenceValue);
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}
	}
}
