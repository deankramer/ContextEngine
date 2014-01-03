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

import java.util.Calendar;

import uk.ac.tvu.mdse.contextengine.reasoning.ContextValues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

public class PreferenceChangeComponent extends Component implements
		OnSharedPreferenceChangeListener {

	private static final long serialVersionUID = 560933927152794610L;
	// Monitoring
	private static final String LOG_TAG = "PreferenceChangeComponent";
	private static final boolean D = true;

	private SharedPreferences sharedPreferences;
	private String preference;

	public enum PreferenceType {
		STRING, INT, BOOLEAN
	};

	private PreferenceType prefType;

	private String preferenceValue;

	public PreferenceChangeComponent(SharedPreferences sp, String pref,
			PreferenceType prefT, Context c) {
		super(pref, c);
		if (D)
			Log.d(LOG_TAG, "constructor1");
		// might be option to work just with one preference a time, or register
		// a number of preferences
		preference = pref;
		sharedPreferences = sp;
		prefType = prefT;
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		contextInformation = "unknown";
	}

	public PreferenceChangeComponent(SharedPreferences sp, String pref,
			String prefT, Context c) {
		super(pref, c);
		if (D)
			Log.d(LOG_TAG, "constructor2");
		// might be option to work just with one preference a time, or register
		// a number of preferences
		preference = pref;
		sharedPreferences = sp;
		prefType = PreferenceType.valueOf(prefT);
		if (D)
			Log.d(LOG_TAG, "constructor2 prefType:" + prefType.toString());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		if (D)
			Log.d(LOG_TAG, "constructor2 ok");
		contextInformation = "unknown";
	}

	public PreferenceChangeComponent(SharedPreferences pm, Context c) {
		super("USER_PREFERENCE", c);
		if (D)
			Log.d(LOG_TAG, "constructor2");
		sharedPreferences = pm;
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		contextInformation = "unknown";
	}

	public void stop() {
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		if (D)
			Log.v(LOG_TAG, "Stopping");
	}

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		if (D)
			Log.v(LOG_TAG, "onSharedPreferenceChanged");
		if (preference.equals(arg1)) {
			switch (prefType) {
			case BOOLEAN:
				preferenceValue = String.valueOf(arg0.getBoolean(arg1, true));
				if (D)
					Log.v(LOG_TAG, "preferencetype BOOLEAN" + preferenceValue);
				checkContext(preferenceValue);
				break;
			case STRING:
				preferenceValue = arg0.getString(arg1, "none");
				if (D)
					Log.v(LOG_TAG, "preferencetype STRING" + preferenceValue);
				checkContext(preferenceValue);
				break;
			case INT:
				preferenceValue = String.valueOf(arg0.getInt(arg1, 0));
				if (D)
					Log.v(LOG_TAG, "preferencetype INT" + preferenceValue);
				checkContext(preferenceValue);
				break;
			default:
				preferenceValue = "none";
				checkContext(preferenceValue);
			}
		}
	}

	public void checkContext(String preferenceValue) {
		for (ContextValues cv : this.valuesSets) {
			if (cv.setNewContextInformation(preferenceValue))
				sendNotification(cv);
		}
	}
}
