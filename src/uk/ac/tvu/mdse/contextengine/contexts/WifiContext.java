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
import android.os.Bundle;
import android.util.Log;

public class WifiContext extends MonitorComponent {

	private static final long serialVersionUID = -6833408704101539915L;
	public static final String LOG_TAG = "WifiContext";
	public static final boolean D = true;
	private WifiManager wm;
	Context c;

	public WifiContext(Context c) {
		super("WifiContext", c, "android.net.wifi.WIFI_STATE_CHANGED");
		if (D) Log.d(LOG_TAG, "constructor");
		this.c=c;
		this.wm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);					
//		this.contextInformation = obtainContextInformation();
//		this.valuesSets.get(0).contextInformation = this.contextInformation;		
//		Log.d("WifiContext", this.contextInformation);		
	}
	
	protected String obtainContextInformation(){
		if (D) Log.d(LOG_TAG, "obtainContextInformation");
		Boolean wifiEnabled = wm.isWifiEnabled();		
		return (wifiEnabled) ? "ON" : "OFF";
	}

	public void checkContext(Bundle data) {
		if (D) Log.d(LOG_TAG, "checkContext");
		wm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);		
		Boolean wifiEnabled = wm.isWifiEnabled();		
		String wifiStatus = "";		
		if (wifiEnabled)
			wifiStatus = "ON" ;
		else
			wifiStatus = "OFF" ;				
		for (ContextValues cv: this.valuesSets){
			if (cv.setNewContextInformation(wifiStatus))
				sendNotification(cv);
		}

	}
}
