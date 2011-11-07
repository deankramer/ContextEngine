/*
 * Copyright (C) 2011 The Context Engine Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.highLevelContext.Rule;
import uk.ac.tvu.mdse.contextengine.reasoning.ApplicationKey;
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
	public static final String LOG_TAG = "RuledCompositeComponent";
	public static final boolean D = true;

	public ArrayList<Component> components = new ArrayList<Component>();
	
	public ArrayList<Rule> rules = new ArrayList<Rule>();
	
	//a set of valid context values
	public ArrayList<String> valuesSet = new ArrayList<String>();
	
	//a set of applications listening to the context values
	public ArrayList<ApplicationKey> keys = new ArrayList<ApplicationKey>();
	
	//to make easier if AND or OR can be applied
	public enum Expression {ALL, ANY}; 
	
	public Expression simplerRule;

	public RuledCompositeComponent(String name, Context c) {
		super(name, c);
		if (D) Log.d(LOG_TAG, "constructor");
		this.contextInformation = "default";		
	}
	
	public void componentDefined(){
		if (D) Log.d(LOG_TAG, "componentDefined");
		setupMonitor();
	}

	private void setupMonitor() {
		// TODO Auto-generated method stub
		contextMonitor = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent in) {
				if (D) Log.d(LOG_TAG, "onReceive");
				// TODO Auto-generated method stub
//				String context = in.getExtras().getString(CONTEXT_NAME);
//				boolean value = in.getExtras().getBoolean(CONTEXT_VALUE);
//				in.getExtras().getStringArrayList(CONTEXT_APPLICATION_KEY);
				checkContext();				
			}
		};
		context.registerReceiver(contextMonitor, filter);

	}

	public void checkContext(){
		if (D) Log.d(LOG_TAG, "checkContext");
		String compositeContextValue  = fireRules(); //"ON";//
		if (!compositeContextValue.equals(this.contextInformation))	{		//((compositeContextValue.equals(null)||compositeContextValue.equals(this.contextInformation)))){
			this.contextInformation = compositeContextValue;
			Log.d("checkContext", contextInformation);
			sendNotification(this.contextName,this.contextInformation,getKeysList());
		}			
	}
	
	//ALL or ANY
	public boolean addSimplerRule(String s){
		if (D) Log.d(LOG_TAG, "addSimplerRule");
		try{
			this.simplerRule = Expression.valueOf(s);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public void addRule(String[] conditions, String statement){
		if (D) Log.d(LOG_TAG, "addRule");
		//if (!checkRule(conditions, statement))
		Rule r = new Rule(conditions, statement);
			rules.add(r);
			Log.d("Rule", r.toString());
	}
	
	public boolean checkRule(String[] conditions, String statement){
		if (D) Log.d(LOG_TAG, "checkRule");
		//this is not good
		Rule rule = new Rule(conditions, statement);
		return rules.contains(rule);
	}
	
	public void addRules(ArrayList<Rule> newRules){
		if (D) Log.d(LOG_TAG, "addRules");
		for(Rule r: newRules)
			addRule(r.ifCondition,r.thenStatement);
	}
	
	public String fireRules(){
		if (D) Log.d(LOG_TAG, "fireRules");
		if (D) Log.v(LOG_TAG, "fireRules keys size"+keys.size());
		String[] componentContexts = new String[components.size()];
		Log.d("fireRules", String.valueOf(componentContexts.length));
		//for each application key check value
		for(ApplicationKey appKey: keys){
			int i =0;
			for (Component c: components){
				componentContexts[i] = c.getContextInformation(appKey);			
				Log.d(LOG_TAG, "fireRules" +  componentContexts[i]);
				i++;
			}
		}
		
		String thenStatement = "OFF";
		if (!componentContexts.equals(null)){			
			for(Rule r: rules){
				if(r.fireRule(componentContexts))
					thenStatement = r.thenStatement;
					Log.d(LOG_TAG, "fireRules" +  thenStatement);
			}
		}
		
//		if(thenStatement.equals(null)||thenStatement.trim().equals(""))
//			return "default";
//		else
		Log.d(LOG_TAG, "fireRules" +  thenStatement);
			return thenStatement;
	}
	
	//add a new component to composite
	public void registerComponent(Component c) {
		if (D) Log.d(LOG_TAG, "registerComponent");
		//if (!checkComponent(c))
			components.add(c);
	}
	
	//check whether the component has been already added
	public boolean checkComponent(Component c){
		if (D) Log.d(LOG_TAG, "checkComponent");
		return components.contains(c);
	}

	public void registerComponents(ArrayList<Component> componentList) {
		if (D) Log.d(LOG_TAG, "registerComponents");
		for (Component c: componentList)
			if (!checkComponent(c))
				components.add(c);
				
	}

	public boolean unregisterComponent(Component c) {
		if (D) Log.d(LOG_TAG, "unregisterComponent");
		if (checkComponent(c))
			return components.remove(c);
		else
			return false;
	}

	public boolean isComposite() {
		if (D) Log.d(LOG_TAG, "isComposite");
		if (components.size() > 1)
			return true;
		else
			return false;
	}
	
	public int getComponentsNo(){
		if (D) Log.d(LOG_TAG, "getComponentsNo");
		return this.components.size();
	}
	
	public void addAppKey(ApplicationKey appKey){
		keys.add(appKey);
	}
	
	public String[] getKeysList(){
		if (D) Log.d(LOG_TAG, "getKeysList");
		String[] keysList = new String[keys.size()];
		int i=0;
		for (ApplicationKey appKey: keys){
			keysList[i] = appKey.key;
			i++;
		}
		return keysList;
	}
}
