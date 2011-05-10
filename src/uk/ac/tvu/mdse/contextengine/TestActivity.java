package uk.ac.tvu.mdse.contextengine;

import uk.ac.tvu.mdse.contextengine.contexts.LightContext;
import android.R.color;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class TestActivity extends ListActivity{
	
	
	private BroadcastReceiver contextMonitor;
	private uk.ac.tvu.mdse.contextengine.contexts.LocationContext locationContext;
	private LightContext lightcontext;
	private IntentFilter filter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //String[] menu = new String[] {
    	///		"Testing Context"
    	//};
      //  setListAdapter(new ArrayAdapter<String>(this, R.layout.main, menu));  
       //      filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.location.action.CONTEXT_CHANGED");
        filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.light.action.CONTEXT_CHANGED");
             setupContextMonitor();
             SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
             lightcontext = new LightContext(sm, this.getApplicationContext());
             
           //  LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
           //  locationContext = new LocationContext(locationManager, this.getApplicationContext());
             
         }
         
         
         private void setupContextMonitor() {
        	 Log.v("value", "add contextMonitor");
       	contextMonitor = new BroadcastReceiver() {
            	@Override 
             	public void onReceive(Context context,Intent intent) {
            		Log.v("value", "getting action");
             			if (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.light.action.CONTEXT_CHANGED")) {
             				Log.v("value", "got action");
             				Bundle bundle = intent.getExtras();
             				String changeName = intent.getExtras().getString(Component.CONTEXT_NAME);
     	        			String changeDateTime = intent.getExtras().getString(Component.CONTEXT_DATE);//(Calendar) intent.getExtras().get(ContextEntity.CONTEXT_DATE);
     	        			String changeValue = intent.getExtras().getString(Component.CONTEXT_VALUE);
     	        			if(Double.parseDouble(changeValue)> 50){
     	        				getListView().setBackgroundResource(color.black);
     	        				
     	        			}else
     	        				getListView().setBackgroundResource(color.white);
     	        			Log.v("value", changeValue);
     	        			//check rules what happens if child context changed & get corresponding state for this context
     	        			//e.g. if received a notification about wifi (is off) --> data connectivity (this component) is off too 
     	        			//persist new state in the dbs 
     	        			 //to all components dependent on this component
     	      		}
             		}        		  	
             	
             };
             registerReceiver(contextMonitor, filter);
             Log.v("value", "register receiver");
             
         	
         	
         	
         }
	

}
