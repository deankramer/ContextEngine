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
	
	
	public SensorManager sensorm;
	public Sensor lightSensor;
	
	public LightContext(SensorManager sm, Context c){
		super(c, "LIGHT");
		Log.v("Status", "done super()");
		sensorm = sm;
		lightSensor = sensorm.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensorm.registerListener(this,
		          lightSensor,
		          SensorManager.SENSOR_DELAY_UI);
		
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType()==Sensor.TYPE_LIGHT){
			  Log.v("light", "Changed");
			  String contextName;
		      double value= arg0.values[0];
		      if(value < 100){
  				contextName="lightlevelLOW";
  				sendNotification("lightlevelLOW", true);
  				sendNotification("lightlevelMEDIUM", false);
  				sendNotification("lightlevelHIGH", false);
		      }else if (value < 180){
  				contextName="lightlevelMEDIUM";
		      	sendNotification("lightlevelLOW", false);
				sendNotification("lightlevelMEDIUM", true);
				sendNotification("lightlevelHIGH", false);
		      }else{
  				contextName="lightlevelHIGH";
  				sendNotification("lightlevelLOW", false);
				sendNotification("lightlevelMEDIUM", false);
				sendNotification("lightlevelHIGH", true);
		      }
		      //this.contextEntity.lastDateTime=Calendar.getInstance();
		   };
		
	}
	
	public void sendNotification(String name, boolean value){
		//	if(D) Log.d(contextEntity.name + LOG_TAG, contextEntity.name+" sendNotification");
			Intent intent = new Intent();
			//check the possibility to create custom actions!!!
		    intent.setAction(CONTEXT_INTENT); //might be better to use the name of the context
		    intent.putExtra(CONTEXT_NAME, name);
		    intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
		    intent.putExtra(CONTEXT_VALUE, value);
		    try{
		    context.sendBroadcast(intent);
		 //   Log.v(contextEntity.name + LOG_TAG, "sent Notification");
		    }
		    catch(Exception e){
		//    	Log.v(contextEntity.name + LOG_TAG,"not working");
		    }
		} 
	
	public void stop(){
		sensorm.unregisterListener(this, lightSensor);
		Log.v("LightContext", "Stopping");
	}

}

