/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class Component implements Serializable{
	
	//Monitoring
	private static final String LOG_TAG = "CONTEXT_ENTITY";
	private static final boolean D = true;
	
	// Key names for context change
    public static final String CONTEXT_NAME = "context_name";
    public static final String CONTEXT_DATE = "context_date";
    public static final String CONTEXT_VALUE = "context_value";
    public static final long UPDATE= 3000;
    
    //BroadcastReceiver
    private BroadcastReceiver contextMonitor = null;    
    
	//Attributes
	public ContextEntity contextEntity;
	//public ArrayList<Component> contexts;
	public String broadcastAction = "";
	public static final String CUSTOM_INTENT = "uk.ac.tvu.mdse.contextengine.light.action.CONTEXT_CHANGED";

	public Context context; //android context to broadcast intent...INITIALISE!
	
	//Constructors
	public Component(){		
		this.contextEntity = new ContextEntity(); //use parameters
	//	if (contexts==null)
	//		this.contexts = new ArrayList<Component>();
		this.broadcastAction = "uk.ac.tvu.mdse.contextengine."+this.contextEntity.name+".action.CONTEXT_CHANGED";
	//	setupContextMonitor();
	}
	
	public Component(ContextEntity contextEntity){		
		this.contextEntity = contextEntity;
//		if (contexts==null)
	//		this.contexts = new ArrayList<Component>();
	}
	
	public Component(ArrayList contexts){		
	//	if (contexts==null)
	//		this.contexts = contexts;
	}
	
	//public boolean registerComponent(Component c){
	//	int pos = contexts.indexOf(c);
	//	if (pos == -1) 
	//		return false;
	//	else{
	//		contexts.add(c);
	//		return true;
	//		}
//	}
	
	public ContextEntity getContextEntity(){
		return contextEntity;
	}
	
	//public ArrayList getContexts(){
	//	return contexts;
	//}
	
	//public boolean isComposite(){
//		if (contexts.size()>1) 
	//		return true;
	//	else
	//		return false;
	//}
	
	public void sendNotification(){
		if(D) Log.d(LOG_TAG, "sendNotification");
		Intent intent = new Intent();
		//check the possibility to create custom actions!!!
	    intent.setAction(CUSTOM_INTENT); //might be better to use the name of the context
	    intent.putExtra(CONTEXT_NAME, this.contextEntity.name);
	    intent.putExtra(CONTEXT_DATE, this.contextEntity.getDateTimeString());
	    intent.putExtra(CONTEXT_VALUE, this.contextEntity.value);
	    try{
	    context.sendBroadcast(intent);
	    Log.v(LOG_TAG, "sent Notification");
	    }
	    catch(Exception e){
	    	Log.v(LOG_TAG,"not working");
	    }
	}	
/*
	 private void setupContextMonitor() {
			contextMonitor = new BroadcastReceiver() {
	        	@Override 
	        	public void onReceive(Context context,Intent intent) {
	        		for(Component c: contexts){
	        			if ((!c.broadcastAction.equals(null))&&(intent.getAction().equals(c.broadcastAction))) {
		        			Log.d( LOG_TAG, "contextMonitor");
		        			Bundle bundle = intent.getExtras();
		        			String changeName = intent.getExtras().getString(Component.CONTEXT_NAME);
		        			String changeDateTime = intent.getExtras().getString(Component.CONTEXT_DATE);//(Calendar) intent.getExtras().get(ContextEntity.CONTEXT_DATE);
		        			String changeValue = intent.getExtras().getString(Component.CONTEXT_VALUE);
		        			//check rules what happens if child context changed & get corresponding state for this context
		        			//e.g. if received a notification about wifi (is off) --> data connectivity (this component) is off too 
		        			//persist new state in the dbs 
		        			sendNotification(); //to all components dependent on this component
		        		}
	        		}        		  	
	        	}
	        };

	    }
	 */
	 
	 public abstract void getContextValue();

}
