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

import uk.ac.tvu.mdse.contextengine.ListenerComponent;
import uk.ac.tvu.mdse.contextengine.reasoning.ContextValues;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightContext extends ListenerComponent implements SensorEventListener {

	// Attributes
	private static final long serialVersionUID = -8230486605325209599L;
	public static final String LOG_TAG = "LightContext";
	public static final boolean D = true;
	
	//Will be kept incase of future "default" values.
	/*
	private static final double HIGH_LUM_VALUE = 180;
	private static final double MEDIUM_LUM_VALUE = 100;
	*/
	private String lastC;
	
	public LightContext(Context c) {
		
		super("LightContext", c, Sensor.TYPE_LIGHT,SensorManager.SENSOR_DELAY_NORMAL );		
		if (D) Log.d(LOG_TAG, "constructor");
		this.contextInformation = obtainContextInformation(sensorManager);
		Log.d("LightContext", this.contextInformation);
	}	
	
	protected String obtainContextInformation(SensorManager sm){
		if (D) Log.d(LOG_TAG, "obtainContextInformation");
		//should obtain real light
		//for demo using HIGH as the obtained one
		lastC="MEDIUM";
        return "MEDIUM";
	}

	public void checkContext(SensorEvent data) {
		if (D) Log.d(LOG_TAG, "checkContext");
		int v = (int) data.values[0];
		
		for (ContextValues cv: this.valuesSets){
			if (cv.setNewContextValue(v))
				sendNotification(cv);
		}		
		
//		double v  = data.values[0];
		//send context value - 2nd approach
//		String highContext = this.getContextInformation(v);
//		Log.d("LightContext", "newValue" + v +" "+highContext);
//		if(!contextInformation.equalsIgnoreCase(lastC)){
//			lastC=highContext;
//			sendNotification();
//			Log.d("LightContext", "newValue set ".concat(contextInformation));
//		}
	}
}
