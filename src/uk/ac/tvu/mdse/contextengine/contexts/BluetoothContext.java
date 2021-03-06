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

import uk.ac.tvu.mdse.contextengine.MonitorComponent;
import uk.ac.tvu.mdse.contextengine.reasoning.ContextValues;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class BluetoothContext extends MonitorComponent {

	private static final long serialVersionUID = -8852296839608708684L;
	public static final String LOG_TAG = "BluetoothContext";
	public static final boolean D = true;

	BluetoothAdapter bluetoothAdapter;
	static NetworkInfo netInfo;

	public BluetoothContext(Context c) {
		super("BluetoothContext", c,
				"android.bluetooth.adapter.action.STATE_CHANGED",
				"bluetoothAdapter.getState()");
		if (D)
			Log.d(LOG_TAG, "constructor");
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// this.contextInformation = obtainContextInformation();
		// this.contextInformation = "ON";
		// Log.d("BluetoothContext", this.contextInformation);
	}

	protected String obtainContextInformation() {
		if (D)
			Log.d(LOG_TAG, "obtainContextInformation");
		int bluetoothValue = bluetoothAdapter.getState();

		// For some reason the statement below does not work, so using if
		// statement for now
		// return (bluetoothValue == BluetoothAdapter.STATE_ON) ? "ON" : "OFF";
		if (bluetoothValue == BluetoothAdapter.STATE_ON)
			return "ON";
		else if (bluetoothValue == BluetoothAdapter.STATE_OFF)
			return "OFF";
		else
			return "NOBODY KNOWS";
		// return String.valueOf(bluetoothValue);
	}

	protected void checkContext(Bundle data) {
		if (D)
			Log.d(LOG_TAG, "checkContext");
		// check data
		// evaluate by firing off the rules
		// set contextValue
		String bluetoothValue = (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) ? "ON"
				: "OFF";

		for (ContextValues cv : this.valuesSets) {
			if (cv.setNewContextInformation(bluetoothValue))
				sendNotification(cv);
		}

	}
}
