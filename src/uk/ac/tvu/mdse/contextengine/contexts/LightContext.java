package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.ListenerComponent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightContext extends ListenerComponent implements SensorEventListener {

	// Attributes
	private static final long serialVersionUID = -8230486605325209599L;
	private static final double HIGH_LUM_VALUE = 180;
	private static final double MEDIUM_LUM_VALUE = 100;

	private int value = 0;
	
	public LightContext(SensorManager sm, Context c) {
		super("LIGHTCONTEXT", c, sm, Sensor.TYPE_LIGHT,SensorManager.SENSOR_DELAY_NORMAL );		
	}	

	public void checkContext(SensorEvent data) {
		double v = data.values[0];
		if ((v >= HIGH_LUM_VALUE) && (value != 3)) {
			value = 3;
			sendNotification("lightlevelLOW", false);
			sendNotification("lightlevelMEDIUM", false);
			sendNotification("lightlevelHIGH", true);
		} else if ((v >= MEDIUM_LUM_VALUE) && (value != 2)) {
			value = 2;
			sendNotification("lightlevelLOW", false);
			sendNotification("lightlevelMEDIUM", true);
			sendNotification("lightlevelHIGH", false);
		} else if ((v < MEDIUM_LUM_VALUE) && (value != 1)) {
			value = 1;
			sendNotification("lightlevelLOW", true);
			sendNotification("lightlevelMEDIUM", false);
			sendNotification("lightlevelHIGH", false);
		}	
		
		//send context value - 2nd approach
//		double v = arg0.values[0];
//		if ((v >= HIGH_LUM_VALUE) && (!contextValue.equals("HIGH"))) {				
//			contextValue = "HIGH";
//			sendNotification();
//		} else if ((v >= MEDIUM_LUM_VALUE) && (!contextValue.equals("MEDIUM"))) {
//			contextValue = "MEDIUM";
//			sendNotification();
//		} else if ((v < MEDIUM_LUM_VALUE) && (!contextValue.equals("LOW"))) {				
//			contextValue = "LOW";
//			sendNotification();
//		}
	}
}
