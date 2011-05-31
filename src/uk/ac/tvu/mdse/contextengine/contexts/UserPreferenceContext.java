/**
 * @project ContextEngine
 * @date 30 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.Calendar;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class UserPreferenceContext extends Component implements OnPreferenceChangeListener{

	private static final long serialVersionUID = 560933927152794610L;
	//Monitoring
	private static final String LOG_TAG = "UserPreferenceContext";
	private static final boolean D = true;
	
	private PreferenceManager preferencem;
	private SharedPreferences sharedPreferences;
	private Preference preference;
	
	public static final String CONTEXT_NAME = "context_name";
    public static final String CONTEXT_DATE = "context_date";
    public static final String CONTEXT_VALUE = "context_value";
    
    public UserPreferenceContext(PreferenceManager pm, Preference pref, Context c){
		super("USERPREFERENCECONTEXT", c);
		//might be option to work just with one preference a time, ot register a number of preferences
		preference = pref;
		preferencem = pm;
		sharedPreferences = pm.getSharedPreferences();
		//preference = pm.findPreference(CONTEXT_NAME);
		preference.setOnPreferenceChangeListener(this);
		
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (D) Log.v(LOG_TAG, "onPreferenceChange");
		//if (preference.equals(this.preference))
		sendNotification(CONTEXT_NAME,true);		
		return true;
	}
	
	public void sendNotification(String name, boolean value){
		Intent intent = new Intent();
		
		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, name);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, value);
		try{
		    context.sendBroadcast(intent);
		}catch(Exception e){
			if (D) Log.e(LOG_TAG, "not working");
		}
	} 
	
	public void registerIntent(Context context){
		IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
    	if (D) Log.v(LOG_TAG, "registerIntent");
	}
	
	public void stop(){
		this.stop(); //?
		if (D) Log.v(LOG_TAG, "Stopping");
	}
}
