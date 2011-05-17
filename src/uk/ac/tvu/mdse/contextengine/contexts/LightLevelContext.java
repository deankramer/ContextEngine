package uk.ac.tvu.mdse.contextengine.contexts;

import java.util.Calendar;

import uk.ac.tvu.mdse.contextengine.Component;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class LightLevelContext extends Component {
	
	public static final String CUSTOM_INTENT = "uk.ac.tvu.mdse.contextengine.lightlevel.action.CONTEXT_CHANGED";
	public IntentFilter filter;
	
	
	public LightLevelContext(SensorManager sm, Context c){
		super(c);
		this.contextEntity.name="lightLevel";
		this.contextEntity.lastDateTime= Calendar.getInstance();
		registerComponent(new LightContext(sm, c));
		Log.v("LightLevel", "LightLevel is ready");
		filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.light.action.CONTEXT_CHANGED");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
			if (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.light.action.CONTEXT_CHANGED")) {
    			Log.d( LOG_TAG, "contextMonitor");
    			Bundle bundle = intent.getExtras();
    			double changeValue = Double.parseDouble(bundle.getString(Component.CONTEXT_VALUE));
    			  //CONTEXT RULE
    			if(changeValue < 100)
    				contextEntity.value="LOW";
    			else if (changeValue < 180)
    				contextEntity.value="MEDIUM";
    			else
    				contextEntity.value="HIGH";
    			Log.v("LightLevel", "LightLevel is" + contextEntity.value);
    			contextEntity.lastDateTime=Calendar.getInstance();
    			
    			sendNotification(CUSTOM_INTENT); 
    		}
	}


}


//hello
