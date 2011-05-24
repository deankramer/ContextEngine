package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.Calendar;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightContext extends Component implements SensorEventListener{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8230486605325209599L;
	public SensorManager sensorm;
	public Sensor lightSensor;
	public static final double HIGH_LUM_VALUE = 180;
	public static final double MEDIUM_LUM_VALUE = 100;
	private int delaytype= SensorManager.SENSOR_DELAY_UI;
	
	public LightContext(SensorManager sm, Context c){
		super("LIGHTCONTEXT", c);
		sensorm = sm;
		lightSensor = sensorm.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensorm.registerListener(this,
		          lightSensor,
		          delaytype);
	}

	public void setDelaytype(int delaytype) {
		this.delaytype = delaytype;
		sensorm.unregisterListener(this, lightSensor);
		sensorm.registerListener(this,
		          lightSensor,
		          delaytype);
	}

	public int getDelaytype() {
		return delaytype;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType()==Sensor.TYPE_LIGHT){
		      double value= arg0.values[0];
		      if(value >= HIGH_LUM_VALUE){
	  			  sendNotification("lightlevelLOW", false);
			      sendNotification("lightlevelMEDIUM", false);
				  sendNotification("lightlevelHIGH", true); 
		      }else if (value >= MEDIUM_LUM_VALUE){
		    	  sendNotification("lightlevelLOW", false);
				  sendNotification("lightlevelMEDIUM", true);
				  sendNotification("lightlevelHIGH", false); 
		      }else{
		    	  sendNotification("lightlevelLOW", true);
	  			  sendNotification("lightlevelMEDIUM", false);
	  			  sendNotification("lightlevelHIGH", false);
		      }
		   };
	}
	
	public void sendNotification(String name, boolean value){
		Intent intent = new Intent();
		
		intent.setAction(CONTEXT_INTENT);
		intent.putExtra(CONTEXT_NAME, name);
		intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		intent.putExtra(CONTEXT_VALUE, value);
		try{
		    context.sendBroadcast(intent);
		}catch(Exception e){
		    Log.e(contextName,"not working");
		}
	} 
	
	public void stop(){
		sensorm.unregisterListener(this, lightSensor);
		Log.v(contextName, "Stopping");
	}

}

