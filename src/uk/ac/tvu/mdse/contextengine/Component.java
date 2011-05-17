/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class Component extends BroadcastReceiver implements Serializable{
	
	//Monitoring
	public static final String LOG_TAG = "CONTEXT";
	public static final boolean D = true;
	
	// Key names for context change
    public static final String CONTEXT_NAME = "context_name";
    public static final String CONTEXT_DATE = "context_date";
    public static final String CONTEXT_VALUE = "context_value";
    public static final long UPDATE= 3000;
    
    //BroadcastReceiver
    public BroadcastReceiver contextMonitor = null;    
    
	//Attributes
	public ContextEntity contextEntity;
	public ArrayList<Component> contexts;
	public String broadcastAction = "";
	

	public Context context; //android context to broadcast intent...INITIALISE!
	
	//Constructors
	public Component(Context c){		
		this.context = c;
		this.contextEntity = new ContextEntity(); //use parameters
		if (contexts==null)
			this.contexts = new ArrayList<Component>();
		this.broadcastAction = "uk.ac.tvu.mdse.contextengine."+this.contextEntity.name+".action.CONTEXT_CHANGED";
	//	setupContextMonitor();
	}
	
	public Component(ContextEntity contextEntity){		
		this.contextEntity = contextEntity;
		if (contexts==null)
			this.contexts = new ArrayList<Component>();
	}
	
	public Component(ArrayList contexts){		
	//	if (contexts==null)
	//		this.contexts = contexts;
	}
	
	public boolean registerComponent(Component c){
		int pos = contexts.indexOf(c);
		if (pos == -1) 
			return false;
		else{
			contexts.add(c);
			return true;
			}
	}
	
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
	
	public void sendNotification(String action){
		if(D) Log.d(contextEntity.name + LOG_TAG, contextEntity.name+" sendNotification");
		Intent intent = new Intent();
		//check the possibility to create custom actions!!!
	    intent.setAction(action); //might be better to use the name of the context
	    intent.putExtra(CONTEXT_NAME, this.contextEntity.name);
	    intent.putExtra(CONTEXT_DATE, this.contextEntity.getDateTimeString());
	    intent.putExtra(CONTEXT_VALUE, this.contextEntity.value);
	    try{
	    context.sendBroadcast(intent);
	    Log.v(contextEntity.name + LOG_TAG, "sent Notification");
	    }
	    catch(Exception e){
	    	Log.v(contextEntity.name + LOG_TAG,"not working");
	    }
	}	

	 //public abstract void setupContextMonitor();
	 

}
