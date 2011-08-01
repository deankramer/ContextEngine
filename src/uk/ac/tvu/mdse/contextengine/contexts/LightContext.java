package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.ListenerComponent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightContext extends ListenerComponent implements SensorEventListener {

	// Attributes
	private static final long serialVersionUID = -8230486605325209599L;
	
	//Will be kept incase of future "default" values.
	/*
	private static final double HIGH_LUM_VALUE = 180;
	private static final double MEDIUM_LUM_VALUE = 100;
	*/
	private String lastC;
	
	public LightContext(Context c) {
		
		super("LightContext", c, Sensor.TYPE_LIGHT,SensorManager.SENSOR_DELAY_NORMAL );		
		this.contextInformation = obtainContextInformation(sensorManager);
		Log.d("LightContext", this.contextInformation);
	}	
	
	protected String obtainContextInformation(SensorManager sm){
		        
		//should obtain real light
		//for demo using HIGH as the obtained one
		lastC="MEDIUM";
        return "MEDIUM";
	}

	public void checkContext(SensorEvent data) {
		double v = data.values[0];
		
		//send context value - 2nd approach
		String highContext = this.getContextInformation(v);
		Log.d("LightContext", "newValue" + v +" "+highContext);
		if(!contextInformation.equalsIgnoreCase(lastC)){
			lastC=highContext;
			sendNotification();
			Log.d("LightContext", "newValue set ".concat(contextInformation));
		}
	}
}
