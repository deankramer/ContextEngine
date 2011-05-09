package uk.ac.tvu.mdse.contextengine;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends ListActivity{
	
	
	private BroadcastReceiver contextMonitor;
	private uk.ac.tvu.mdse.contextengine.LocationContext locationContext;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
             setupContextMonitor();
             LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
             locationContext = new LocationContext(locationManager);
             
         }
         
         
         private void setupContextMonitor() {
        	 Log.v("value", "add contextMonitor");
       	contextMonitor = new BroadcastReceiver() {
            	@Override 
             	public void onReceive(Context context,Intent intent) {
            		Log.v("value", "getting action");
             			if (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.location.action.CONTEXT_CHANGED")) {
             				Log.v("value", "got action");
     	       			Bundle bundle = intent.getExtras();
     	     			String changeName = intent.getExtras().getString(Component.CONTEXT_NAME);
     	        			String changeDateTime = intent.getExtras().getString(Component.CONTEXT_DATE);//(Calendar) intent.getExtras().get(ContextEntity.CONTEXT_DATE);
     	        			String changeValue = intent.getExtras().getString(Component.CONTEXT_VALUE);
     	        			Log.v("value", changeValue);
     	        			//check rules what happens if child context changed & get corresponding state for this context
     	        			//e.g. if received a notification about wifi (is off) --> data connectivity (this component) is off too 
     	        			//persist new state in the dbs 
     	        			 //to all components dependent on this component
     	      		}
             		}        		  	
             	
             };
             IntentFilter filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.location.action.CONTEXT_CHANGED");
             Log.v("value", "register receiver");
             registerReceiver(contextMonitor, filter);
         	
         	
         	
         }
	

}
