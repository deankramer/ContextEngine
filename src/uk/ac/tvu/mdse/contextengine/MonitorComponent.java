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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class MonitorComponent extends Component {

	private static final long serialVersionUID = -222900547936864703L;
	public static final String LOG_TAG = "MonitorComponent";
	public static final boolean D = true;

	// BroadcastReceiver
	public BroadcastReceiver contextMonitor = null;
	public String filterAction = "";
	public String monitoringData = "";
	public String monitoringKey;
	String[] values = new String[] { "ON", "OFF" };

	public MonitorComponent(String name, Context c) {
		super(name, c);
		if (D)
			Log.d(LOG_TAG, "constructor1");
		// default set of context values is ON&OFF
		// ContextValues cv = new ContextValues(values);
		// valuesSets.add(new ContextValues(values));
		// cv.contextInformation = "OFF";
		setupMonitor();
	}

	public MonitorComponent(String name, Context c, String action) {
		super(name, c);
		if (D)
			Log.d(LOG_TAG, "constructor2");
		this.filterAction = action;
		// valuesSets.add(new ContextValues(values));
		setupMonitor();
	}

	public MonitorComponent(String name, Context c, String action, String key) {
		super(name, c);
		if (D)
			Log.d(LOG_TAG, "constructor3");
		this.filterAction = action;
		this.monitoringKey = key;
		// valuesSets.add(new ContextValues(values));
		setupMonitor();
	}

	// implement receiver and specify the actions
	private void setupMonitor() {
		contextMonitor = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent in) {
				if (in.getAction().equals(filterAction)) {
					if (D)
						Log.d(LOG_TAG, "onReceive");
					Bundle data = in.getExtras();
					checkContext(data);
				}
			}
		};
		context.registerReceiver(contextMonitor, new IntentFilter(filterAction));
	}

	protected void checkContext(Bundle data) {
		if (D)
			Log.d(LOG_TAG, "checkContext");
		// check data
		// checkContextValue(data.toString());
		// evaluate by firing off the rules
		// set contextValue
		// sendNotification
		// if (data != null)
		// contextValue = data.getString(monitoringKey);
		// sendNotification();
	}

	// public String getContextValue(){
	// if (contextValue.equals("default"))
	// contextValue = monitoringKey;
	// return contextValue;
	// }

	public void stop() {
		if (D)
			Log.d(LOG_TAG, "stop");
		context.unregisterReceiver(contextMonitor);
	}
}