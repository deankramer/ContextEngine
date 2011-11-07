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
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiContext extends MonitorComponent {

	private static final long serialVersionUID = -6833408704101539915L;
	public static final String LOG_TAG = "WifiContext";
	public static final boolean D = true;
	private WifiManager wm;

	public WifiContext(Context c) {
		super("WifiContext", c, "android.net.wifi.WIFI_STATE_CHANGED");
		if (D) Log.d(LOG_TAG, "constructor");
		this.wm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);					
		this.contextInformation = obtainContextInformation();
		this.valuesSets.get(0).contextInformation = this.contextInformation;
		//this.contextInformation = "ON";
		Log.d("WifiContext", this.contextInformation);
	}
	
	protected String obtainContextInformation(){
		if (D) Log.d(LOG_TAG, "obtainContextInformation");
		Boolean wifiEnabled = wm.isWifiEnabled();
		return (wifiEnabled) ? "ON" : "OFF";
	}

	protected void checkContext() {
		if (D) Log.d(LOG_TAG, "checkContext");
		String wifiEnabled = wm.isWifiEnabled() ? "ON" : "OFF" ;		
		
		for (ContextValues cv: this.valuesSets){
			if (cv.setNewContextInformation(wifiEnabled))
				sendNotification(cv);
		}

//		Boolean wifiEnabled = wm.isWifiEnabled() ;	
//		//send context value - 2nd approach
//		if (wifiEnabled & (!contextInformation.equals("ON"))) {			
//			contextInformation = "ON";
//		} else if ((!wifiEnabled) & (!contextInformation.equals("OFF"))) {			
//			contextInformation = "OFF";
//		}
//		sendNotification();
		
//		if (wifiEnabled & (!contextValue)) {
//		sendNotification("wifiON", true);
//		contextValue = true;
//	} else if ((!wifiEnabled) & (contextValue)) {
//		sendNotification("wifiON", false);
//		contextValue = false;
//	}

	}
}
