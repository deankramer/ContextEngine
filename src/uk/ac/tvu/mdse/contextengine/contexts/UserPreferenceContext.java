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

package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.PreferenceChangeComponent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserPreferenceContext extends PreferenceChangeComponent {

	private static final long serialVersionUID = 2997863934263820784L;
	public static final String LOG_TAG = "UserPreferenceContext";
	public static final boolean D = true;
	private String preference;

	// for check box, ...boolean value
	public UserPreferenceContext(SharedPreferences pm, String pref, Context c) {
		super(pm, pref, PreferenceChangeComponent.PreferenceType.BOOLEAN, c);
		if (D)
			Log.d(LOG_TAG, "constructor");
		this.preference = pref;
	}

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		if (D)
			Log.d(LOG_TAG, "onSharedPreferenceChanged");
		// if (preference.equals(arg1))
		// sendNotification(arg1, true);
	}
}
