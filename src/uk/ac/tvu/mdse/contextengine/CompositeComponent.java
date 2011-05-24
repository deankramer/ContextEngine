package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CompositeComponent extends Component implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -493221379660180723L;
	//Attributes
	public Hashtable<String, Boolean> contexts;
	
	public CompositeComponent(String name, Context c){
		super(name, c);
		contexts = new Hashtable<String, Boolean>();
		setupMonitor();
		
	}
	
	public CompositeComponent(String name, Context c, ArrayList<String> contextList){
		super(name, c);
		contexts = new Hashtable<String, Boolean>();
		setupMonitor();
		for(String cn : contextList){
			registerComponent(cn);
		}
		
	}

	private void setupMonitor() {
		// TODO Auto-generated method stub
		contextMonitor = new BroadcastReceiver(){
			
		@Override
		public void onReceive(Context c, Intent in) {
			// TODO Auto-generated method stub
			String context = in.getExtras().getString(CONTEXT_NAME);
			boolean value = in.getExtras().getBoolean(CONTEXT_VALUE);
			if(contexts.containsKey(context)){
					contexts.put(context, value);
					checkContext();	
			}	
		}};
		context.registerReceiver(contextMonitor, filter);
		
	}
	
	public boolean registerComponent(String c){
		if ( ! contexts.containsKey(c)){	
			contexts.put(c, false);
			return true;		
		}else
			return false;
	}
	
	public boolean unregisterComponent(String c){
		
		if (contexts.containsKey(c)){
			contexts.remove(c);
			return true;
		}else
			return false;	
	}
	
	public boolean isComposite(){
		if (contexts.size()>1) 
			return true;
		else
			return false;
	}
	
	public void checkContext(){
		if ( ! contexts.containsValue(false) && contextValue==false){
			sendNotification(true);
			contextValue=true;
		}
		else if (contexts.containsValue(false) && contextValue==true){
			sendNotification(false);
			contextValue=false;
		}
	}
	
	public void stop(){
		context.unregisterReceiver(contextMonitor);
		Log.v(contextName, "Stopping");
	}
	
}

