package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import uk.ac.tvu.mdse.contextengine.highLevelContext.Rule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @project ContextEngine
 * @date 28 Jul 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class RuledCompositeComponent extends Component implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5268211078370074986L;

	public ArrayList<Component> components = new ArrayList<Component>();
	
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	
	//to make easier if AND or OR can be applied
	public enum Expression {ALL, ANY}; 
	
	public Expression simplerRule;

	public RuledCompositeComponent(String name, Context c) {
		super(name, c);
		this.contextInformation = "default";
		
	}
	
	public void componentDefined(){
		setupMonitor();
	}

	private void setupMonitor() {
		// TODO Auto-generated method stub
		contextMonitor = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent in) {
				// TODO Auto-generated method stub
//				String context = in.getExtras().getString(CONTEXT_NAME);
//				boolean value = in.getExtras().getBoolean(CONTEXT_VALUE);
				checkContext();				
			}
		};
		context.registerReceiver(contextMonitor, filter);

	}

	public void checkContext(){
		String compositeContextValue  = fireRules(); //"ON";//
		if (!compositeContextValue.equals(this.contextInformation))	{		//((compositeContextValue.equals(null)||compositeContextValue.equals(this.contextInformation)))){
			this.contextInformation = compositeContextValue;
			Log.d("Rule", contextInformation);
			sendNotification();
		}			
	}
	
	//ALL or ANY
	public boolean addSimplerRule(String s){
		try{
			this.simplerRule = Expression.valueOf(s);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public void addRule(String[] conditions, String statement){
		//if (!checkRule(conditions, statement))
		Rule r = new Rule(conditions, statement);
			rules.add(r);
			Log.d("Rule", r.toString());
	}
	
	public boolean checkRule(String[] conditions, String statement){
		//this is not good
		Rule rule = new Rule(conditions, statement);
		return rules.contains(rule);
	}
	
	public void addRules(ArrayList<Rule> newRules){
		for(Rule r: newRules)
			addRule(r.ifCondition,r.thenStatement);
	}
	
	public String fireRules(){
		String[] componentContexts = new String[components.size()];
		Log.d("fireRules", String.valueOf(componentContexts.length));
		int i =0;
		for (Component c: components){
			componentContexts[i] = c.getContextInformation();			
			Log.d(LOG_TAG, "fireRules" +  componentContexts[i]);
			i++;
		}
		String thenStatement = "";
		if (!componentContexts.equals(null)){			
			for(Rule r: rules)
				thenStatement = r.fireRule(componentContexts);
		}
		
		if(thenStatement.equals(null)||thenStatement.trim().equals(""))
			return "default";
		else
			return thenStatement;
	}
	
	//add a new component to composite
	public void registerComponent(Component c) {
	
		//if (!checkComponent(c))
			components.add(c);
	}
	
	//check whether the component has been already added
	public boolean checkComponent(Component c){
		return components.contains(c);
	}

	public void registerComponents(ArrayList<Component> componentList) {

		for (Component c: componentList)
			if (!checkComponent(c))
				components.add(c);
				
	}

	public boolean unregisterComponent(Component c) {

		if (checkComponent(c))
			return components.remove(c);
		else
			return false;
	}

	public boolean isComposite() {
		if (components.size() > 1)
			return true;
		else
			return false;
	}
	
	public int getComponentsNo(){
		return this.components.size();
	}
}
