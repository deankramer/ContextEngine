package uk.ac.tvu.mdse.contextengine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * @project ContextEngine
 * @date 16 Jun 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class MonitorComponent extends Component{

	private static final long serialVersionUID = -222900547936864703L;

    //BroadcastReceiver
    public BroadcastReceiver contextMonitor = null;    
    public String filterAction = "";
    public String monitoringData = "";
    public String monitoringKey;
    
	public MonitorComponent(String name, Context c){		
		super(name, c);		
		setupMonitor();
	}
	
	public MonitorComponent(String name, Context c, String action){		
		super(name, c);	
		this.filterAction = action;			
		setupMonitor();
	}
	
	
	public MonitorComponent(String name, Context c, String action, String key){		
		super(name, c);	
		this.filterAction = action;		
		this.monitoringKey = key;
		setupMonitor();
	}
	

	//implement receiver and specify the actions	
	private void setupMonitor() {
		contextMonitor = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent in) {				
                Bundle data =  in.getExtras();
                checkContext(data);
            }			
		};
		context.registerReceiver(contextMonitor, new IntentFilter(filterAction));
	}
	
	protected void checkContext(Bundle data) {
		//check data		
		//evaluate by firing off the rules
		//set contextValue
		//sendNotification
//		if (data != null)
//			contextValue = data.getString(monitoringKey);
		sendNotification();		
	}
	
//	public String getContextValue(){
//		if (contextValue.equals("default"))
//			contextValue = monitoringKey;
//		return contextValue;
//	}
	
	public void stop() {
		context.unregisterReceiver(contextMonitor);
	}
}