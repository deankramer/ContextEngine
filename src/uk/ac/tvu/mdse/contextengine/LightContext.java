package uk.ac.tvu.mdse.contextengine;

import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightContext extends Component implements SensorEventListener{
	
	public SensorManager sensorm;
	public Sensor myLightSensor;
	LightContext(SensorManager sm, Context c){
		super();
		Log.v("Status", "done super()");
		this.context = c;
		sensorm = sm;
		myLightSensor = sensorm.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensorm.registerListener(this,
		          myLightSensor,
		          SensorManager.SENSOR_DELAY_FASTEST);
		this.contextEntity.name = "light";
		
	}

	@Override
	public void getContextValue() {
		// TODO Auto-generated method stub
		
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType()==Sensor.TYPE_LIGHT){
		     
		      this.contextEntity.value=String.valueOf(arg0.values[0]);
		      this.contextEntity.lastDateTime=Calendar.getInstance();
		      sendNotification();
		   };
		
	}

}

