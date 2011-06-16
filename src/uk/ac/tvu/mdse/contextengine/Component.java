/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.Calendar;

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

	// BroadcastReceiver
	public BroadcastReceiver contextMonitor = null;

	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED";
	public IntentFilter filter = new IntentFilter(CONTEXT_INTENT);
	public int contextId;
	public Context context;
	public String contextName;
	public boolean contextValue;
	public Calendar contextDate;

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
		try {
			context.sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(contextName, "not working");
		}
	}

	public boolean getContextValue(){
		return contextValue;
	}
	
	//re-implement if context value depends on some values
	protected void checkContext(Bundle data) {
		//check data		
		//evaluate by firing off the rules
		//set contextValue	
		sendNotification();
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
	
	public void stop() {		
	}
}
