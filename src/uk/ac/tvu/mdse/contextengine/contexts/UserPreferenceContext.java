/**
 * @project ContextEngine
 * @date 30 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.util.Log;

public class UserPreferenceContext extends Component implements OnSharedPreferenceChangeListener{

	private static final long serialVersionUID = 560933927152794610L;
	//Monitoring
	private static final String LOG_TAG = "UserPreferenceContext";
	private static final boolean D = true;
	
	private SharedPreferences sharedPreferences;
	private String preference;

    public UserPreferenceContext(SharedPreferences pm, String pref, Context c){
		super("USERPREFERENCECONTEXT", c);
		//might be option to work just with one preference a time, or register a number of preferences
		preference = pref;
		sharedPreferences = pm;
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);		
	}	
    
    public UserPreferenceContext(SharedPreferences pm, Context c){
		super("USERPREFERENCECONTEXT", c);
		sharedPreferences = pm;
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);		
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (D) Log.v(LOG_TAG, "onPreferenceChange");
		//if (preference.equals(this.preference))
		sendNotification(CONTEXT_NAME,true);		
		return true;
	}
	
	public void stop(){
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);		
		if (D) Log.v(LOG_TAG, "Stopping");
	}


	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		if (D) Log.v(LOG_TAG, "onPreferenceChange");
		if (preference==null)
			sendNotification(arg1,true);
		else if (preference.equals(arg1))
			sendNotification(arg1,true);
	}
}
