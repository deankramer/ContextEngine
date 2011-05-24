/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;

public abstract class Component implements Serializable{
	
	//Monitoring
	public static final String LOG_TAG = "CONTEXT";
	public static final boolean D = true;
	
	// Key names for context change
    public static final String CONTEXT_NAME = "context_name";
    public static final String CONTEXT_DATE = "context_date";
    public static final String CONTEXT_VALUE = "context_value";
    //public static final long UPDATE= 3000;
    
    //BroadcastReceiver
    public BroadcastReceiver contextMonitor = null;    
    
	//Attributes
	//public ContextEntity contextEntity;
	//public ArrayList<Component> contexts;
	//public String broadcastAction = "";
	
	public static final String CONTEXT_INTENT = "uk.ac.tvu.mdse.contextengine.CONTEXT_CHANGED";
	public IntentFilter filter = new IntentFilter(CONTEXT_INTENT);
	public Context context;
	public String contextName;
	public boolean contextValue;
	
	//Constructors
	public Component(Context c, String name){		
		this.context = c;
		this.contextName = name;
		this.contextValue = false;
		//this.contextEntity = new ContextEntity(); //use parameters
		//if (contexts==null)
		//	this.contexts = new ArrayList<Component>();
		
		//this.broadcastAction = "uk.ac.tvu.mdse.contextengine."+this.contextEntity.name+".action.CONTEXT_CHANGED";
	//	setupContextMonitor();
	}
	
	public void sendNotification(boolean value){
	//	if(D) Log.d(contextEntity.name + LOG_TAG, contextEntity.name+" sendNotification");
		Intent intent = new Intent();
		//check the possibility to create custom actions!!!
	    intent.setAction(CONTEXT_INTENT); //might be better to use the name of the context
	    intent.putExtra(CONTEXT_NAME, this.contextName);
	    intent.putExtra(CONTEXT_DATE, Calendar.getInstance().toString());
	    intent.putExtra(CONTEXT_VALUE, value);
	    try{
	    context.sendBroadcast(intent);
	    Log.v(this.contextName, "sent Notification");
	    }
	    catch(Exception e){
	//    	Log.v(contextEntity.name + LOG_TAG,"not working");
	    }
	}	
}
