package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import uk.ac.tvu.mdse.contextengine.highLevelContext.Rule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
		setupMonitor();
	}

//	public RuledCompositeComponent(String name, Context c, ArrayList<String> pc,
//			ArrayList<String> nc) {
//		super(name, c);
//		positivecontexts = new Hashtable<String, Boolean>();
//		setupMonitor();
//		for (String cn : pc)
//			registerComponent(cn, false);
//		for (String cn : nc)
//			registerComponent(cn, true);
//	}

	private void setupMonitor() {
		// TODO Auto-generated method stub
		contextMonitor = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent in) {
				// TODO Auto-generated method stub
				String context = in.getExtras().getString(CONTEXT_NAME);
				boolean value = in.getExtras().getBoolean(CONTEXT_VALUE);
					checkContext();				
			}
		};
		context.registerReceiver(contextMonitor, filter);

	}

	public void checkContext(){
		String compositeContextValue  = fireRules();
		if (!(compositeContextValue.equals(null)||compositeContextValue.equals(this.contextInformation))){
			this.contextInformation = compositeContextValue;
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
		if (!checkRule(conditions, statement))
			rules.add(new Rule(conditions, statement));
	}
	
	public boolean checkRule(String[] conditions, String statement){
		Rule rule = new Rule(conditions, statement);
		return rules.contains(rule);
	}
	
	public void addRules(ArrayList<Rule> newRules){
		for(Rule r: newRules)
			addRule(r.ifCondition,r.thenStatement);
	}
	
	public String fireRules(){
		String[] componentContexts = new String[components.size()];
		int i =0;
		for (Component c: components){
			componentContexts[i++] = c.contextInformation;
		}
		
		String thenStatement = "";
		for(Rule r: rules)
			thenStatement = r.fireRule(componentContexts);
		
		if(thenStatement.equals(null)&&thenStatement.trim().equals(""))
			return null;
		else
			return thenStatement;
	}
	
	//add a new component to composite
	public void registerComponent(Component c) {
	
		if (!checkComponent(c))
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
