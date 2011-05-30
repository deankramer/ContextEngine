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
	public Hashtable<String, Boolean> positivecontexts;
	
	public CompositeComponent(String name, Context c){
		super(name, c);
		positivecontexts = new Hashtable<String, Boolean>();
		setupMonitor();
		
	}
	
	public CompositeComponent(String name, Context c, ArrayList<String> contextList){
		super(name, c);
		positivecontexts = new Hashtable<String, Boolean>();
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
			if(positivecontexts.containsKey(context)){
					positivecontexts.put(context, value);
					checkContext();	
			}	
		}};
		context.registerReceiver(contextMonitor, filter);
		
	}
	
	public boolean registerComponent(String c){
		if ( ! positivecontexts.containsKey(c)){	
			positivecontexts.put(c, false);
			return true;		
		}else
			return false;
	}
	
	public boolean unregisterComponent(String c){
		
		if (positivecontexts.containsKey(c)){
			positivecontexts.remove(c);
			return true;
		}else
			return false;	
	}
	
	public boolean isComposite(){
		if (positivecontexts.size()>1) 
			return true;
		else
			return false;
	}
	
	public void checkContext(){
		if ( ! positivecontexts.containsValue(false) && contextValue==false){
			sendNotification(true);
			contextValue=true;
		}
		else if (positivecontexts.containsValue(false) && contextValue==true){
			sendNotification(false);
			contextValue=false;
		}
	}
	
	public void stop(){
		context.unregisterReceiver(contextMonitor);
		Log.v(contextName, "Stopping");
	}
	
}

