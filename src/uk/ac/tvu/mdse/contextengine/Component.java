/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import uk.ac.tvu.mdse.contextengine.highLevelContext.ContextRange;
import uk.ac.tvu.mdse.contextengine.highLevelContext.Rule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class Component implements Serializable {

	// Attributes
	private static final long serialVersionUID = -4339043280287205031L;
	public static final String LOG_TAG = "CONTEXT";
	public static final boolean D = true;

	// Key names for context change
	public static final String CONTEXT_NAME = "context_name";
	public static final String CONTEXT_DATE = "context_date";
	public static final String CONTEXT_VALUE = "context_value";
	public static final String CONTEXT_INFORMATION = "context_information";
	
	//a set of valid context values
	public ArrayList<String> valuesSet = new ArrayList<String>();

	//to hold high level contexts with its range of values
	public ArrayList<ContextRange> contextRangeSet = new ArrayList<ContextRange>();
	
	// BroadcastReceiver
	public BroadcastReceiver contextMonitor = null;

	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED";
	public IntentFilter filter = new IntentFilter(CONTEXT_INTENT);
	public int contextId;
	public Context context;
	public String contextName;
	public boolean contextValue;
	public Calendar contextDate;
	public String contextInformation;
	
	// Constructors
	public Component(String name, Context c) {
		context = c;
		contextName = name;
		contextValue = false;		
	}
	
	public void sendNotification(){
		sendNotification(contextName, contextValue);
	}

	public void sendNotification(boolean value) {
		sendNotification(contextName, value);
	}

	public void sendNotification(String name, boolean value) {
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, name);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, value);
		intent.putExtra(CONTEXT_INFORMATION, contextInformation);
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}
	}
	
	public void sendNotification(String name, String contextInformation) {
		Intent intent = new Intent();

		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, name);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, contextValue);
		intent.putExtra(CONTEXT_INFORMATION, contextInformation);
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}
	}

	public boolean getContextValue(){
		return contextValue;
	}
	
	public String getContextInformation(){
		return contextInformation;
	}
	
	public String getContextInformation(double contextInput){
		for (ContextRange cr: contextRangeSet){
			if ((cr.maxValue>contextInput)&&(cr.minValue<contextInput))
				contextInformation = cr.contextHighValue;
		}
		return contextInformation;
	}
	
	//re-implement if context value depends on some values
	protected void checkContext(Bundle data) {
		//check data		
		//evaluate by firing off the rules - for the compoiste component
		//set contextValue	
		sendNotification();
	}
	
	protected boolean checkContextValue(String value){
		return (valuesSet.contains(value));
	}
	
	@Override
	public String toString() {
		return "Component [contextName=" + contextName + "]";
	}

	public String getDateToString() {
		StringBuffer date = new StringBuffer();
		date.append(contextDate.get(Calendar.DATE));
		date.append("-");
		date.append(contextDate.get(Calendar.MONTH) + 1);
		date.append("-");
		date.append(contextDate.get(Calendar.YEAR));
		return date.toString();
	}

	public String getTimeToString() {
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
	
	
	public boolean addRange(int minValue, int maxValue, String contextValue){		
		
//		if (checkRange(contextValue))
//			return false;
//		else{
			contextRangeSet.add(new ContextRange(minValue,maxValue,contextValue));		
			valuesSet.add(contextValue);
			return true;
		//}		
	}
	
	public boolean checkRange(String contextValue){
		boolean exist = false;
		for (ContextRange cr: contextRangeSet){
			if (cr.contextHighValue.equals(contextValue))
				exist = true;
		}	
		return exist;		
	}
	
	public void stop() {		
	}
}
