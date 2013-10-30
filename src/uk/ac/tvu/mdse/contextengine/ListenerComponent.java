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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ListenerComponent extends Component implements SensorEventListener {

	private static final long serialVersionUID = 14671122599992688L;
	public static final String LOG_TAG = "ListenerComponent";
	public static final boolean D = false;

	protected SensorManager sensorManager;
	private Sensor theSensor;
	private int sensorType;
	private int delayType;

	public ListenerComponent(String name, Context c, int sensorT, int delayT) {
		super(name, c);
		if (D)
			Log.d(LOG_TAG, "constructor");
		this.sensorManager = (SensorManager) c
				.getSystemService(Context.SENSOR_SERVICE);
		this.sensorType = sensorT;
		theSensor = sensorManager.getDefaultSensor(sensorType);
		this.delayType = delayT;
		sensorManager.registerListener(this, theSensor, delayType);
	}

	public void setDelaytype(int delaytype) {
		if (D)
			Log.d(LOG_TAG, "setDelaytype");
		this.delayType = delaytype;
		sensorManager.unregisterListener(this, theSensor);
		sensorManager.registerListener(this, theSensor, delayType);
	}

	public int getDelaytype() {
		if (D)
			Log.d(LOG_TAG, "getDelaytype");
		return delayType;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		if (D)
			Log.d(LOG_TAG, "onAccuracyChanged");
	}

	public void onSensorChanged(SensorEvent arg0) {
		if (D)
			Log.d(LOG_TAG, "onSensorChanged");
		if (arg0.sensor.getType() == sensorType)
			checkContext(arg0);
	};

	// re-implement if context value depends on some values
	public void checkContext(SensorEvent data) {
		if (D)
			Log.d(LOG_TAG, "checkContext");
		// check data
		// fire off the rules
		// set contextValue
		// sendNotification();
	}

	public void stop() {
		if (D)
			Log.d(LOG_TAG, "stop");
		sensorManager.unregisterListener(this, theSensor);
	}
}