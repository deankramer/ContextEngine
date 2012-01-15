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

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import uk.ac.tvu.mdse.contextengine.highLevelContext.ContextRange;
import uk.ac.tvu.mdse.contextengine.reasoning.ApplicationKey;
import uk.ac.tvu.mdse.contextengine.reasoning.ContextValues;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class Component implements Serializable {

	// Attributes
	private static final long serialVersionUID = -4339043280287205031L;
	public static final String LOG_TAG = "Component";
	public static final boolean D = true;
	
	//current state of the application/object (Android specific)
	public Context context;
	
	// Each application listens only to particular location context determined by its key
	public static final String CONTEXT_APPLICATION_KEY = "context_application_key";

	// Key names for context change
	public static final String CONTEXT_NAME = "context_name";
	public static final String CONTEXT_DATE = "context_date";
	public static final String CONTEXT_VALUE = "context_value";
	public static final String CONTEXT_INFORMATION = "context_information";
	
	//a number of context values sets can be specified for each context
	public ArrayList<ContextValues> valuesSets = new ArrayList<ContextValues>();
	
	// BroadcastReceiver - (listening to messages broadcasted by other components)
	public BroadcastReceiver contextMonitor = null;

	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED";
	public IntentFilter filter = new IntentFilter(CONTEXT_INTENT);
	
	//for database use
	public int contextId;
	
	public String contextName;
	public boolean contextValue;
	public Calendar contextDate;
	public String contextInformation;
	
	String[] values = new String[]{"ON","OFF"};
	
	// Constructors
	public Component(String name, Context c) {
		if (D) Log.d(LOG_TAG, "constructor");
		context = c;
		contextName = name;
		contextValue = false;	
		valuesSets.add(new ContextValues(values));
		valuesSets.get(0).contextInformation = "OFF";
	}
	
	//send notification for particular context values and application keys set
	public void sendNotification(ContextValues contextValues) {
		if (D) Log.d(LOG_TAG, "sendNotification(ContextValues)");
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, contextName);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, contextValue);
		intent.putExtra(CONTEXT_INFORMATION, contextValues.contextInformation);
		if (D) Log.d(LOG_TAG, "sendNotification(ContextValues).contextInformation:" +contextValues.contextInformation);
		intent.putExtra(CONTEXT_APPLICATION_KEY, contextValues.getKeysList());
		if (D) Log.d(LOG_TAG, "sendNotification(ContextValues)-keylist0:"+contextValues.getKeysList());
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}
	}

	public boolean getContextValue(){
		if (D) Log.d(LOG_TAG, "getContextValue");
		return contextValue;
	}
		
	//if a call from composite context
		public String getContextInformation(String appKey){
			if (D) Log.d(LOG_TAG, "getContextInformation");
			String contextInfo="";
			//if (contextInformation.trim().equals("") || contextInformation == null){
				for (ContextValues cv: valuesSets){
					for (ApplicationKey ak: cv.keys){
					if (ak.key.equals(appKey)){
						contextInfo = cv.contextInformation;
						if (D) Log.v(LOG_TAG, "contextname:"+contextName);
						if (D) Log.v(LOG_TAG, "getContextInformation:"+contextInfo);
					}
					}
				}
			//}
			return contextInfo;
		}
	

//	public String getContextInformation(double v) {
//		if (D) Log.d(LOG_TAG, "getContextInformation(v)");
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	//re-implement if context value depends on some values
	protected void checkContext(Bundle data) {
		if (D) Log.d(LOG_TAG, "checkContext");
		//check data		
		//evaluate by firing off the rules - for the composite component
		//set contextValue	
//		sendNotification();
	}

	@Override
	public String toString() {
		return "Component [contextName=" + contextName + "]";
	}

	public String getDateToString() {
		if (D) Log.d(LOG_TAG, "getDateToString");
		StringBuffer date = new StringBuffer();
		date.append(contextDate.get(Calendar.DATE));
		date.append("-");
		date.append(contextDate.get(Calendar.MONTH) + 1);
		date.append("-");
		date.append(contextDate.get(Calendar.YEAR));
		return date.toString();
	}

	public String getTimeToString() {
		if (D) Log.d(LOG_TAG, "getTimeToString");
		int mMinute = contextDate.get(Calendar.MINUTE);
		int mHour = contextDate.get(Calendar.HOUR_OF_DAY);

		String min = "";
		if (mMinute == 0)
			min = Integer.toString(mMinute) + "0";
		else if (mMinute < 10)
			min = "0" + Integer.toString(mMinute);
		else
			min = Integer.toString(mMinute);
		return mHour + ":" + min;
	}

	public String getDateTimeString() {
		if (D) Log.d(LOG_TAG, "getDateTimeString");
		StringBuffer date = new StringBuffer();
		date.append(contextDate.get(Calendar.DATE));
		date.append("-");
		date.append(contextDate.get(Calendar.MONTH) + 1);
		date.append("-");
		date.append(contextDate.get(Calendar.YEAR));
		date.append(" ");
		date.append(contextDate.get(Calendar.HOUR_OF_DAY));
		date.append(":");
		date.append(contextDate.get(Calendar.MINUTE));
		date.append(":");
		date.append(contextDate.get(Calendar.SECOND));
		return date.toString();
	}
	
	public void setupNewValuesSet(ApplicationKey key, String[] values){
		if (D) Log.d(LOG_TAG, "setupNewValuesSet");
		//TO DO:
		//check whether such a set already exist
		//if yes, just add app key to it!
		
		//if not,create new one:
		valuesSets.add(new ContextValues(key, values));		
	}	

	public void addContextValue(ApplicationKey appKey, String newContextValue) {
		if (D) Log.d(LOG_TAG, "addContextValue");
		boolean keyExists = false;
		for (ContextValues cv: valuesSets){
			if (cv.keys.contains(appKey)){
				cv.addValue(newContextValue);
				keyExists = true;
				if (D) Log.d(LOG_TAG, "addSpecificContextValue true key exist");
			}
		}		
		if (!keyExists){
			ContextValues newSet = new ContextValues();			
			newSet.keys.add(appKey);
			newSet.valuesSet.add(newContextValue);
			valuesSets.add(newSet);
			if (D) Log.d(LOG_TAG, "addSpecificContextValue false key exist");
		}
	}
	
	public void addSpecificContextValue(ApplicationKey appKey,
			String newContextValue, Double value1, Double value2) {	
		if (D) Log.d(LOG_TAG, "addSpecificContextValue");
		boolean keyExists = false;
		for (ContextValues cv: valuesSets){
			if (cv.keys.contains(appKey)){
				cv.addLocation(newContextValue, value1, value2);
				keyExists = true;
				if (D) Log.d(LOG_TAG, "addSpecificContextValue true key exist");
			}
		}
		if (!keyExists){
			ContextValues newSet = new ContextValues();			
			newSet.keys.add(appKey);
			newSet.addLocation(newContextValue, value1, value2);
			valuesSets.add(newSet);
			if (D) Log.d(LOG_TAG, "addSpecificContextValue false key exist");
		}
	}	
	
	public void addRange(ApplicationKey appKey, Integer minValue,
			Integer maxValue, String newContextValue) {
		if (D) Log.d(LOG_TAG, "addRange");
		boolean keyExists = false;
		if (D) Log.d(LOG_TAG, "values sets size:"+valuesSets.size());
		if (D) Log.d(LOG_TAG, "values set 0 keys size:"+valuesSets.get(0).keys.size());
		if (D) Log.d(LOG_TAG, "values set 0 values size:"+valuesSets.get(0).valuesSet.size());
		for (ContextValues cv: valuesSets){
			if (cv.keys.contains(appKey)){
				cv.addRange(minValue, maxValue, newContextValue);
				keyExists = true;
				if (D) Log.d(LOG_TAG, "addSpecificContextValue true key exist");
			}
		}
		if (!keyExists){
			ContextValues newSet = new ContextValues();			
			newSet.keys.add(appKey);
			newSet.addRange(minValue, maxValue, newContextValue);
			valuesSets.add(newSet);
			if (D) Log.d(LOG_TAG, "addSpecificContextValue false key exist");
		}
	}
	
	public void addAppKey(ApplicationKey appKey){
		boolean keyExists = false;
		for (ContextValues cv: valuesSets){
			if (cv.keys.contains(appKey)){
				keyExists = true;
			}
		}
		if (!keyExists)			
			valuesSets.get(0).keys.add(appKey);
		if (D) Log.d(LOG_TAG, "addAppKey:" + String.valueOf(keyExists));
	}
	
	public boolean existKey(ApplicationKey appKey){
		for (ContextValues cv: valuesSets){
			if (cv.keys.contains(appKey)){				
				return true;
			}
		}
		return false;				
	}
		
	public void componentDefined(){
	}
	
	public void stop() {	
		if (D) Log.d(LOG_TAG, "stop");
	}
	
	public void onLocationChangedManually(Double latitude, Double longitude) {
	
	}
}

//public String getContextInformation(){
//if (D) Log.d(LOG_TAG, "getContextInformation");
//
//if (contextInformation.trim().equals("") || contextInformation == null){
//	for (ContextValues cv: valuesSets){
//		if (valuesSets.size()==1)
//			contextInformation = valuesSets.get(0).contextInformation;
//	}
//}
//return contextInformation;
//}

//if a call from composite context
//public String getContextInformation(ApplicationKey appKey){
//if (D) Log.d(LOG_TAG, "getContextInformation");
//String contextInfo="";
////if (contextInformation.trim().equals("") || contextInformation == null){
//	for (ContextValues cv: valuesSets){
//		if (cv.keys.contains(appKey)){
//			contextInfo = cv.contextInformation;
//			if (D) Log.v(LOG_TAG, "contextname:"+contextName);
//			if (D) Log.v(LOG_TAG, "getContextInformation:"+contextInfo);
//		}
//	}
////}
//return contextInfo;
//}
	
//public void sendNotification(){
//sendNotification(contextName, contextValue);
//}
//
//public void sendNotification(boolean value) {
//sendNotification(contextName, value);
//}
//
//public void sendNotification(String name, boolean value) {
//if (D) Log.d(LOG_TAG, "sendNotification(name,value)");
//Intent intent = new Intent();
//
//intent.setAction(CONTEXT_INTENT);
//intent.putExtra(CONTEXT_NAME, name);
//intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
//intent.putExtra(CONTEXT_VALUE, value);
//intent.putExtra(CONTEXT_INFORMATION, contextInformation);
//try {
//	context.sendBroadcast(intent);
//} catch (Exception e) {
//	Log.e(contextName, "not working");
//}
//}	
//
//public void sendNotification(String name, String contextInformation, String[] keys) {
//if (D) Log.d(LOG_TAG, "sendNotification(name,contextInformation,keys)");
//Intent intent = new Intent();
//
//intent.setAction(CONTEXT_INTENT);
//intent.putExtra(CONTEXT_NAME, name);
//intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
//intent.putExtra(CONTEXT_VALUE, contextValue);
//intent.putExtra(CONTEXT_INFORMATION, contextInformation);
//intent.putExtra(CONTEXT_APPLICATION_KEY, keys);
//if (D) Log.d(LOG_TAG, "sendNotification(ContextValues)-keylist0:"+keys);
//
//try {
//	context.sendBroadcast(intent);
//} catch (Exception e) {
//	Log.e(contextName, "not working");
//}
//}
//
//public void sendNotification(String[] contextInformation, String[] keys) {
//if (D) Log.d(LOG_TAG, "sendNotification(contextInformation,keys)");
//Intent intent = new Intent();
//
//intent.setAction(CONTEXT_INTENT);
//intent.putExtra(CONTEXT_NAME, contextName);
//intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
//intent.putExtra(CONTEXT_VALUE, contextValue);
//intent.putExtra(CONTEXT_INFORMATION, contextInformation);
//intent.putExtra(CONTEXT_APPLICATION_KEY, keys);
//if (D) Log.d(LOG_TAG, "sendNotification(ContextValues)-keylist0:"+keys);
//
//try {
//	context.sendBroadcast(intent);
//} catch (Exception e) {
//	Log.e(contextName, "not working");
//}
//}
//
//public void sendNotification(String name, String contextInformation) {
//if (D) Log.d(LOG_TAG, "sendNotification(name,contextInformation)");
//Intent intent = new Intent();
//
//intent.setAction(CONTEXT_INTENT);
//intent.putExtra(CONTEXT_NAME, name);
//intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
//intent.putExtra(CONTEXT_VALUE, contextValue);
//intent.putExtra(CONTEXT_INFORMATION, contextInformation);
//
//try {
//	context.sendBroadcast(intent);
//} catch (Exception e) {
//	Log.e(contextName, "not working");
//}
//}
