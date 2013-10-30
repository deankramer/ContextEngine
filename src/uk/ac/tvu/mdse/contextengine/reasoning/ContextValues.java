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

package uk.ac.tvu.mdse.contextengine.reasoning;

import java.util.ArrayList;

import android.util.Log;

import uk.ac.tvu.mdse.contextengine.highLevelContext.ContextRange;
import uk.ac.tvu.mdse.contextengine.highLevelContext.LocationIdentifier;
import uk.ac.tvu.mdse.contextengine.reasoning.ApplicationKey;

public class ContextValues {

	public static final String LOG_TAG = "ContextValues";
	public static final boolean D = true;

	// current contextInformation of this context value set
	public String contextInformation = "";

	// list of app keys to know which applications are interested in
	// this particular contextValues set
	public ArrayList<ApplicationKey> keys = new ArrayList<ApplicationKey>();

	// a set of valid context values
	public ArrayList<String> valuesSet = new ArrayList<String>();

	// to hold high level contexts with its range of values
	public ArrayList<ContextRange> contextRangeSet = new ArrayList<ContextRange>();

	// to hold high level contexts with its range of values
	public ArrayList<LocationIdentifier> locationsSet = new ArrayList<LocationIdentifier>();

	public ContextValues(String[] values) {
		if (D)
			Log.d(LOG_TAG, "constructor1");
		for (String newValue : values)
			addValue(newValue);
	}

	public ContextValues(ApplicationKey key, String[] values) {
		if (D)
			Log.d(LOG_TAG, "constructor2");
		for (String newValue : values)
			addValue(newValue);
		keys.add(key);
		this.contextInformation = values[0];
	}

	public ContextValues(ApplicationKey key) {
		if (D)
			Log.d(LOG_TAG, "constructor3");
		keys.add(key);
	}

	public ContextValues() {
		if (D)
			Log.d(LOG_TAG, "constructor4");
	}

	public boolean setNewContextInformation(String newContextInformation) {
		if (D)
			Log.d(LOG_TAG, "setNewContextInformation" + newContextInformation);
		// only inform&change it if different from previous context value
		if (!(contextInformation.equals(newContextInformation))) {
			contextInformation = newContextInformation;
			return true;
		} else
			return false;
	}

	public boolean setNewContextValue(long newContextValue) {
		if (D)
			Log.d(LOG_TAG, "setNewContextValue");
		boolean newInformation = false;

		for (ContextRange cr : contextRangeSet)
			if (cr.getContextHighValue(newContextValue) != null)
				if (setNewContextInformation(cr
						.getContextHighValue(newContextValue)))
					newInformation = true;
		return newInformation;
	}

	public boolean addRange(long minValue, long maxValue, String contextValue) {
		if (D)
			Log.d(LOG_TAG, "addRange");
		if (checkRange(contextValue))
			return false;
		else {
			Log.v("ContextValues", "havent crashed yet");
			contextRangeSet.add(new ContextRange(minValue, maxValue,
					contextValue));
			valuesSet.add(contextValue);

			// set the new value as context information
			// this needs to be changed and obtain default value
			// it would require a method to define the whole range of values
			this.contextInformation = contextValue;

			// check values set so far
			for (int i = 0; i < valuesSet.size(); i++) {
				if (D)
					Log.v(LOG_TAG, "addRange values" + valuesSet.get(i));
			}

			return true;
		}
	}

	public boolean checkRange(String contextValue) {
		if (D)
			Log.d(LOG_TAG, "checkRange");
		boolean exist = false;
		for (ContextRange cr : contextRangeSet) {
			if (cr.contextHighValue.equals(contextValue))
				exist = true;
		}
		return exist;
	}

	public void addLocation(String identifier, double latitude, double longitude) {
		if (D)
			Log.d(LOG_TAG, "addLocation " + identifier + " " + latitude + " "
					+ longitude);
		locationsSet
				.add(new LocationIdentifier(identifier, latitude, longitude));
		valuesSet.add(identifier);
	}

	public String getContextInformation(double contextInput) {
		if (D)
			Log.d(LOG_TAG, "getContextInformation");
		String contextInformation = "";
		if (!contextRangeSet.isEmpty())
			for (ContextRange cr : contextRangeSet) {
				if ((cr.maxValue > contextInput)
						&& (cr.minValue < contextInput))
					contextInformation = cr.contextHighValue;
			}
		return contextInformation;
	}

	public boolean addValue(String contextValue) {
		if (D)
			Log.d(LOG_TAG, "addValue");
		// check if the value already exists in the set
		if (valuesSet.contains(contextValue))
			return false;
		else {
			valuesSet.add(contextValue);
			this.contextInformation = contextValue;
			if (D)
				Log.d(LOG_TAG, "addValue added" + contextValue);
			return true;
		}
	}

	public void addValues(String[] values) {
		if (D)
			Log.d(LOG_TAG, "addValues");
		for (String newValue : values)
			addValue(newValue);
	}

	public void setupValues(String[] values) {
		if (D)
			Log.d(LOG_TAG, "setupValues");
		valuesSet.removeAll(valuesSet.subList(0, valuesSet.size() - 1));
		addValues(values);
	}

	protected boolean checkContextValue(String value) {
		if (D)
			Log.d(LOG_TAG, "checkContextValue");
		return (valuesSet.contains(value));
	}

	public ArrayList<String> getKeysList() {
		if (D)
			Log.d(LOG_TAG, "getKeysList");
		ArrayList<String> keysList = new ArrayList<String>();
		for (ApplicationKey appKey : keys) {
			keysList.add(appKey.key);
		}
		return keysList;
	}
}
