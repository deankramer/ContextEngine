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
	BluetoothAdapter bluetoothAdapter;
	static NetworkInfo netInfo;


	public BluetoothContext(Context c) {
		super("BluetoothContext", c, "android.bluetooth.adapter.action.STATE_CHANGED", "bluetoothAdapter.getState()" );	
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.contextInformation = obtainContextInformation();
		//this.contextInformation = "ON";
		Log.d("BluetoothContext", this.contextInformation);
	}
	
	protected String obtainContextInformation(){
		int bluetoothValue = bluetoothAdapter.getState();
		return (bluetoothValue == BluetoothAdapter.STATE_ON) ? "ON" : "OFF";
	}
	
	protected void checkContext(Bundle data) {
		//check data		
		//evaluate by firing off the rules
		//set contextValue	
		String bluetoothValue = (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) ? "ON" : "OFF";

		for (ContextValues cv: this.valuesSets){
			if (cv.setNewContextInformation(bluetoothValue))
				sendNotification(cv);
		}
		
//		int bluetoothValue = bluetoothAdapter.getState();
//		//send context information - 2nd approach
//		if ((bluetoothValue == BluetoothAdapter.STATE_ON)&&(!contextInformation.equals("ON"))) {
//			contextInformation = "ON";
//			sendNotification();
//		} 
//		if ((bluetoothValue == BluetoothAdapter.STATE_OFF)&&(!contextInformation.equals("OFF"))) {
//			contextInformation = "OFF";
//			sendNotification();
//		}	
		
//		if (bluetoothValue == BluetoothAdapter.STATE_ON) {
//		sendNotification("bluetoothON", true);
//		sendNotification("bluetoothOFF", false);
//	} else {
//		sendNotification("bluetoothON", false);
//		sendNotification("bluetoothOFF", true);
//	}
	}
}
