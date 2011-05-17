package uk.ac.tvu.mdse.contextengine;

import uk.ac.tvu.mdse.contextengine.contexts.LightLevelContext;
import android.R.color;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends ListActivity{
	
	
	private BroadcastReceiver contextMonitor;
	private uk.ac.tvu.mdse.contextengine.contexts.LocationContext locationContext;
	private LightLevelContext lightlevelcontext;
	private IntentFilter filter;
	private IntentFilter filter1;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //String[] menu = new String[] {
    	///		"Testing Context"
    	//};
      //  setListAdapter(new ArrayAdapter<String>(this, R.layout.main, menu));  
       //      filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.location.action.CONTEXT_CHANGED");
        filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.lightlevel.action.CONTEXT_CHANGED");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightlevelcontext = new LightLevelContext(sm, this.getApplicationContext());
             setupContextMonitor();
             
             //lightcontext = new LightContext(sm);
               
           //  LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
           //  locationContext = new LocationContext(locationManager, this.getApplicationContext());
             
         }
         
         
         private void setupContextMonitor() {
        	 Log.v("value", "add contextMonitor");
       	contextMonitor = new BroadcastReceiver() {
            	@Override 
             	public void onReceive(Context context,Intent intent) {
            		Log.v("value", "getting action");
             			if (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.lightlevel.action.CONTEXT_CHANGED")) {
             				Log.v("value", "got action");
             				Bundle bundle = intent.getExtras();
             				String changeName = bundle.getString(Component.CONTEXT_NAME);
     	        			String changeDateTime = bundle.getString(Component.CONTEXT_DATE);//(Calendar) intent.getExtras().get(ContextEntity.CONTEXT_DATE);
     	        			String value = bundle.getString(Component.CONTEXT_VALUE);
     	        			if(value.equalsIgnoreCase("HIGH"))
     	        				getListView().setBackgroundResource(color.black);
     	        			else if(value.equalsIgnoreCase("MEDIUM"))
     	        				getListView().setBackgroundResource(color.darker_gray);
     	        			else
     	        				getListView().setBackgroundResource(color.white);
     	        			Log.v("value", value);
     	        			//check rules what happens if child context changed & get corresponding state for this context
     	        			//e.g. if received a notification about wifi (is off) --> data connectivity (this component) is off too 
     	        			//persist new state in the dbs 
     	        			 //to all components dependent on this component
     	      		}
             		}        		  	
             	
             };
             registerReceiver(lightlevelcontext, lightlevelcontext.filter);
             registerReceiver(contextMonitor, filter);
             Log.v("value", "register receiver");
             
         	
         	
         	
         }
	

}
