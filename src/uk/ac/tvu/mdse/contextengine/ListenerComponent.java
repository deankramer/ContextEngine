package uk.ac.tvu.mdse.contextengine;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @project ContextEngine
 * @date 16 Jun 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class ListenerComponent extends Component implements SensorEventListener{

	private static final long serialVersionUID = 14671122599992688L;

	protected SensorManager sensorManager;
	private Sensor theSensor;
	private int sensorType;
	private int delayType; 
	
	
	public ListenerComponent(String name, Context c, int sensorT, int delayT) {
		super(name, c);
		this.sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);		
		this.sensorType = sensorT;
		theSensor = sensorManager.getDefaultSensor(sensorType);
		this.delayType = delayT;
		sensorManager.registerListener(this, theSensor, delayType);
	}	

	public void setDelaytype(int delaytype) {
		this.delayType = delaytype;
		sensorManager.unregisterListener(this, theSensor);
		sensorManager.registerListener(this, theSensor, delayType);
	}

	public int getDelaytype() {
		return delayType;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	public void onSensorChanged(SensorEvent arg0) {		
		if (arg0.sensor.getType() == sensorType) 
			checkContext(arg0);				
	};
	
	//re-implement if context value depends on some values
	public void checkContext(SensorEvent data) {
		//check data		
		//fire off the rules
		//set contextValue	
		sendNotification();		
	}

	public void stop() {
		sensorManager.unregisterListener(this, theSensor);
		Log.v(contextName, "Stopping");
	}

}