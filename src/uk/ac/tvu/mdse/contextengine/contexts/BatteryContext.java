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
import android.content.Context;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

public class BatteryContext extends MonitorComponent{
	
	private static final double HIGH_BAT_VALUE = 80;
	private static final double MEDIUM_BAT_VALUE = 30;
	int v = -1;
	int value = 0;
	private static final long serialVersionUID = -7034400776638700530L;
	public static final String LOG_TAG = "BatteryContext";
	public static final boolean D = true;
	
	public BatteryContext(Context c) {
		super("BatteryContext", c, "Intent.ACTION_BATTERY_CHANGED", "BatteryManager.EXTRA_STATUS");	
		if (D) Log.d(LOG_TAG, "constructor");
//		this.addRange(0, 30, "LOW");
//		this.addRange(31, 80, "MEDIUM");
//		this.addRange(81, 200, "HIGH"); 
		try{
			this.valuesSets.remove(0);
			this.valuesSets.add(new ContextValues());
		}
		catch (Exception e){
			if (D) Log.e(LOG_TAG, "constructor - values sets problem");
		}
		this.contextInformation = obtainContextInformation();
	}	
	
	protected String obtainContextInformation(){
		if (D) Log.d(LOG_TAG, "obtainContextInformation");
        /*
		BatteryManager bm = new BatteryManager();
		
		int rawlevel = Integer.parseInt(BatteryManager.EXTRA_LEVEL);
        int scale = Integer.parseInt(BatteryManager.EXTRA_SCALE);
        
        if (rawlevel >= 0 && scale > 0) 
            v = (rawlevel * 100) / scale; 
       */
		
		//set as default
		int v=50;
		
        return this.valuesSets.get(0).contextInformation;
	}
	
	protected void checkContext(Bundle data) {
		if (D) Log.d(LOG_TAG, "checkContext");
		int rawlevel = data.getInt(BatteryManager.EXTRA_LEVEL, -1);
        int scale = data.getInt(BatteryManager.EXTRA_SCALE, -1);
        
        if (rawlevel >= 0 && scale > 0) {
            v = (rawlevel * 100) / scale; 
            
            //go through all context sets and send notification 
            //only for sets in which info changed
        	for (ContextValues cv: this.valuesSets){
    			if (cv.setNewContextValue(v))
    				sendNotification(cv);
    		}
        	
//            String highContext = this.getContextInformation(v);
//    		if ((highContext.equals("HIGH")) && (!contextInformation.equals("HIGH"))) {				
//    			contextInformation = "HIGH";
//    			sendNotification();
//    		} else if ((highContext.equals("MEDIUM")) && (!contextInformation.equals("MEDIUM"))) {
//    			contextInformation = "MEDIUM";
//    			sendNotification();
//    		} else if ((highContext.equals("LOW")) && (!contextInformation.equals("LOW"))) {				
//    			contextInformation = "LOW";
//    			sendNotification();
//    		}
        }
	}	
}

