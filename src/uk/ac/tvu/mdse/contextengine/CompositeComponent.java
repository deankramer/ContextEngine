package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.Hashtable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CompositeComponent extends Component implements Serializable{
	
	public boolean value;
	
	public Hashtable<String, Boolean> contexts;
	
	public CompositeComponent(Context c, String contextName){
		super(c, contextName);
		contexts = new Hashtable<String, Boolean>();
		setupMonitor();
		
	}

	private void setupMonitor() {
		// TODO Auto-generated method stub
		contextMonitor = new BroadcastReceiver(){
			
		@Override
		public void onReceive(Context c, Intent in) {
			// TODO Auto-generated method stub
			String context = in.getExtras().getString(CONTEXT_NAME);
			boolean value = in.getExtras().getBoolean(CONTEXT_VALUE);
			Log.v(contextName, context + " is " + value);
			if(contexts.containsKey(context)){
					contexts.put(context, value);
					checkContext();	
			}	
		}};
		context.registerReceiver(contextMonitor, filter);
		
	}
	
	public boolean registerComponent(String c){
		//int pos = contexts.containsKey(c);
	//	if (pos == -1) 
	//		return false;
	//	else{
			contexts.put(c, false);
			return true;
	//		}
	}
	
	public boolean isComposite(){
		if (contexts.size()>1) 
			return true;
		else
			return false;
	}
	
	public void checkContext(){
		Log.v(contextName, "Checking Context");
		if ( ! contexts.containsValue(false) && value==false){
			sendNotification(true);
			value=true;
		}
		else if (contexts.containsValue(false) && value==true){
			sendNotification(false);
			value=false;
		}
	}
	
	public void stop(){
		context.unregisterReceiver(contextMonitor);
		Log.v(contextName, "Stopping");
	}
	
	//public 

}


//hello
