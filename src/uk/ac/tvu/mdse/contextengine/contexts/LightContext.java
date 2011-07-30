package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.ListenerComponent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.util.Log;

public class LightContext extends ListenerComponent implements SensorEventListener {

	// Attributes
	private static final long serialVersionUID = -8230486605325209599L;
	private static final double HIGH_LUM_VALUE = 180;
	private static final double MEDIUM_LUM_VALUE = 100;

	private int value = 0;
	
	public LightContext(SensorManager sm, Context c) {
		super("LIGHTCONTEXT", c, sm, Sensor.TYPE_LIGHT,SensorManager.SENSOR_DELAY_NORMAL );		
		this.contextInformation = obtainContextInformation(sm);
		Log.d("LightContext", this.contextInformation);
	}	
	
	protected String obtainContextInformation(SensorManager sm){
		        
		//should obtain real light
		//for demo using HIGH as the obtained one
        return "MEDIUM";
	}

	public void checkContext(SensorEvent data) {
		double v = data.values[0];
//		if ((v >= HIGH_LUM_VALUE) && (value != 3)) {
//			value = 3;
//			sendNotification("lightlevelLOW", false);
//			sendNotification("lightlevelMEDIUM", false);
//			sendNotification("lightlevelHIGH", true);
//		} else if ((v >= MEDIUM_LUM_VALUE) && (value != 2)) {
//			value = 2;
//			sendNotification("lightlevelLOW", false);
//			sendNotification("lightlevelMEDIUM", true);
//			sendNotification("lightlevelHIGH", false);
//		} else if ((v < MEDIUM_LUM_VALUE) && (value != 1)) {
//			value = 1;
//			sendNotification("lightlevelLOW", true);
//			sendNotification("lightlevelMEDIUM", false);
//			sendNotification("lightlevelHIGH", false);
//		}	
		
		//send context value - 2nd approach
		String highContext = this.getContextInformation(v);
		Log.d("LightContext", "newValue" + v +" "+highContext);
		if (highContext.equals("HIGH")){// && (!contextInformation.equals("HIGH"))) {				
			contextInformation = "HIGH";
			sendNotification();
			Log.d("LightContext", "newValue set HIGH" );
		} else if (highContext.equals("MEDIUM")){// && (!contextInformation.equals("MEDIUM"))) {
			contextInformation = "MEDIUM";
			sendNotification();
			Log.d("LightContext", "newValue set medium");
		} else if (highContext.equals("LOW")){// && (!contextInformation.equals("LOW"))) {				
			contextInformation = "LOW";
			sendNotification();
			Log.d("LightContext", "newValue set low");
		}
	}
}
