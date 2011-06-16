/**
 * @project ContextEngine
 * @date 30 May 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.PreferenceChangeComponent;
import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferenceContext extends PreferenceChangeComponent{	

	private static final long serialVersionUID = 2997863934263820784L;
	private String preference;

	//for check box, ...boolean value
	public UserPreferenceContext(SharedPreferences pm, String pref, Context c) {
		super(pm, pref, PreferenceChangeComponent.PreferenceType.BOOLEAN, c);	
		this.preference = pref;
	}	

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {		
		if (preference.equals(arg1))
			sendNotification(arg1, true);
	}
}
