package uk.ac.tvu.mdse.contextengine.contexts;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightContext extends Component implements SensorEventListener {

	// Attributes
	private static final long serialVersionUID = -8230486605325209599L;
	private SensorManager sensorm;
	private Sensor lightSensor;
	private static final double HIGH_LUM_VALUE = 180;
	private static final double MEDIUM_LUM_VALUE = 100;
	private int delaytype = SensorManager.SENSOR_DELAY_NORMAL;
	private int value = 0;

	public LightContext(SensorManager sm, Context c) {
		super("LIGHTCONTEXT", c);
		sensorm = sm;
		lightSensor = sensorm.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensorm.registerListener(this, lightSensor, delaytype);
	}

	public void setDelaytype(int delaytype) {
		this.delaytype = delaytype;
		sensorm.unregisterListener(this, lightSensor);
		sensorm.registerListener(this, lightSensor, delaytype);
	}

	public int getDelaytype() {
		return delaytype;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	public void onSensorChanged(SensorEvent arg0) {
		if (arg0.sensor.getType() == Sensor.TYPE_LIGHT) {
			double v = arg0.values[0];
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
		}
		;
	}

	public void stop() {
		sensorm.unregisterListener(this, lightSensor);
		Log.v(contextName, "Stopping");
	}

}
