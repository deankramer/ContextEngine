package uk.ac.tvu.mdse.contextengine;

import uk.ac.tvu.mdse.contextengine.contexts.LightContext;
import android.R.color;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends ListActivity{
	
	
	private BroadcastReceiver contextMonitor;
	private IntentFilter filter;
	private LightContext lightcontext;
	private CompositeComponent sync;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //String[] menu = new String[] {
    	///		"Testing Context"
    	//};
      //  setListAdapter(new ArrayAdapter<String>(this, R.layout.main, menu));  
       //      filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.location.action.CONTEXT_CHANGED");
        filter = new IntentFilter("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //onnectivityManager wifi = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //wifi = new ConnectionContext(wifi, getApplicationContext());
        setupContextMonitor();
             
        lightcontext = new LightContext(sm, getApplicationContext());
        sync= new CompositeComponent(getApplicationContext(), "datasync_ON");
        sync.registerComponent("lightlevelHIGH");
        
        
               
           //  LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
           //  locationContext = new LocationContext(locationManager, this.getApplicationContext());
             
         }
         
         
         private void setupContextMonitor() {
        	 Log.v("value", "add contextMonitor");
       	contextMonitor = new BroadcastReceiver() {
            	@Override 
             	public void onReceive(Context context,Intent intent) {
            		Log.v("value", "getting action");
             			if (intent.getAction().equals("uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED")) {
             				Log.v("value", "got action");
             				Bundle bundle = intent.getExtras();
             				String changeName = bundle.getString(Component.CONTEXT_NAME);
     	        			String changeDateTime = bundle.getString(Component.CONTEXT_DATE);//(Calendar) intent.getExtras().get(ContextEntity.CONTEXT_DATE);
     	        			boolean currentcontext = bundle.getBoolean(Component.CONTEXT_VALUE);
     	        			if(changeName.equalsIgnoreCase("datasync_ON") &&( currentcontext ) )
     	        				getListView().setBackgroundResource(color.black);
     	        			else if (changeName.equalsIgnoreCase("datasync_ON") &&( !currentcontext ) )
     	        				getListView().setBackgroundResource(color.white);
     	     
     	     				
     	        			Log.v("value", changeName + " = " + currentcontext);
     	      		}
             		}        		  	
             	
             };
             registerReceiver(contextMonitor, filter);
             Log.v("value", "register receiver");     	
         }
         
         @Override
         public void onStop(){
        	 lightcontext.stop();
        	 sync.stop();
        	 unregisterReceiver(contextMonitor);
        	 super.onStop();
         }
}
