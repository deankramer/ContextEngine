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

import android.location.Location;
import android.util.Log;

import uk.ac.tvu.mdse.contextengine.highLevelContext.ContextRange;
import uk.ac.tvu.mdse.contextengine.highLevelContext.LocationIdentifier;

public class ContextValues {

	//a set of applications listening to the context values
	public ArrayList<ApplicationKey> keys = new ArrayList<ApplicationKey>();

	//a set of valid context values
	public ArrayList<String> valuesSet = new ArrayList<String>();

	//to hold high level contexts with its range of values
	public ArrayList<ContextRange> contextRangeSet = new ArrayList<ContextRange>();

	//to hold high level contexts with its range of values
	public ArrayList<LocationIdentifier> locationsSet = new ArrayList<LocationIdentifier>();
	
	public ContextValues(String[] values){
		for (String newValue: values)
			addValue(newValue);
	}
	
	public ContextValues(ApplicationKey key, String[] values){
		for (String newValue: values)
			addValue(newValue);
		keys.add(key);
	}


	public boolean addRange(int minValue, int maxValue, String contextValue){		

		if (checkRange(contextValue))
			return false;
		else{
		Log.v("ContextValues", "havent crashed yet");
		contextRangeSet.add(new ContextRange(minValue,maxValue,contextValue));		
		valuesSet.add(contextValue);
		return true;
		}	
	}
	
	public boolean checkRange(String contextValue){
		boolean exist = false;
		for (ContextRange cr: contextRangeSet){
			if (cr.contextHighValue.equals(contextValue))
				exist = true;
		}	
		return exist;		
	}

	public void addLocation(String identifier, double latitude, double longitude ){
		Log.v("ContextValues", "addLocation " + identifier + " " +latitude+ " " +longitude);
		locationsSet.add(new LocationIdentifier(identifier, latitude, longitude));
		valuesSet.add(identifier);
	}
	
	public String getContextInformation(double contextInput){
		String contextInformation = "";
		if (!contextRangeSet.isEmpty())
			for (ContextRange cr: contextRangeSet){
				if ((cr.maxValue>contextInput)&&(cr.minValue<contextInput))
					contextInformation = cr.contextHighValue;
			}
		return contextInformation;
	}
	
	public boolean addValue(String contextValue){
		
		//check if the value already exists in the set
		if (valuesSet.contains(contextValue))
			return false;
		else {
			valuesSet.add(contextValue);
			return true;
		}		
	}
	
	public void addValues(String[] values){		
		for (String newValue: values)
			addValue(newValue);
	}
	
	public void setupValues(String[] values){
		valuesSet.removeAll(valuesSet.subList(0, valuesSet.size()-1));
		addValues(values);
	}	
	
	protected boolean checkContextValue(String value){
		return (valuesSet.contains(value));
	}
}
