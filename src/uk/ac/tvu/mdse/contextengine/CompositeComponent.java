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
import java.util.StringTokenizer;

import uk.ac.tvu.mdse.contextengine.Component;
import uk.ac.tvu.mdse.contextengine.highLevelContext.Rule;
import uk.ac.tvu.mdse.contextengine.reasoning.ApplicationKey;
import uk.ac.tvu.mdse.contextengine.reasoning.ContextValues;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @project ContextEngine
 * @date 28 Jul 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class CompositeComponent extends Component implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5268211078370074986L;
	public static final String LOG_TAG = "CompositeComponent";
	public static final boolean D = true;

	public ArrayList<Component> components = new ArrayList<Component>();

	public ArrayList<Rule> rules = new ArrayList<Rule>();

	// a set of valid context values
	// public ArrayList<String> valuesSet = new ArrayList<String>();

	ArrayList<String> broadcastedKeys = new ArrayList<String>();

	public CompositeComponent(String name, Context c) {
		super(name, c);
		if (D)
			Log.d(LOG_TAG, "constructor " + name);
	}

	public void componentDefined(ApplicationKey appKey) {
		if (D)
			Log.d(LOG_TAG, "componentDefined " + contextName);
		for (ContextValues cv : valuesSets) {
			if (cv.keys.contains(appKey)) {
				cv.contextInformation = cv.valuesSet.get(0);
			}
		}
		setupMonitor();
	}

	private void setupMonitor() {

		contextMonitor = new BroadcastReceiver() {

			@Override
			public void onReceive(Context c, Intent in) {
				if (D)
					Log.d(LOG_TAG, "onReceive" + contextName);

				String context = in.getExtras().getString(CONTEXT_NAME);

				for (Component comp : components) {
					if (comp.contextName.equals(context)) {
						String keys = in.getExtras().getString(
								CONTEXT_APPLICATION_KEY);
						if (D)
							Log.d(LOG_TAG, "onReceivekeys" + keys);

						if (keys.contains(",")) {
							if (D)
								Log.d(LOG_TAG, "onReceivecontains");
							StringTokenizer st = new StringTokenizer(keys, ",");
							while (st.hasMoreTokens()) {
								String key = st.nextToken();
								if (!key.equals(""))
									broadcastedKeys.add(key);
								if (D)
									Log.d(LOG_TAG, "onReceivecontainskey" + key);
							}
						} else {
							broadcastedKeys.add(keys);
						}
						if (D)
							Log.d(LOG_TAG, "onReceive keysNo:"
									+ broadcastedKeys.size() + " "
									+ broadcastedKeys.get(0));
						for (ContextValues cv : valuesSets) {
							for (String key : cv.getKeysList())
								if (key.equals(broadcastedKeys.get(0))) {
									if (D)
										Log.d(LOG_TAG, "checkContext" + key);
									checkContext(cv);
								}
						}

					}

					broadcastedKeys.clear();
				}
			}
		};
		context.registerReceiver(contextMonitor, filter);

	}

	public void checkContext(ContextValues cv) {
		String compositeContextValue = fireRules();
		if (D)
			Log.d(LOG_TAG, "compositeRec: " + contextName
					+ compositeContextValue);
		if (cv.setNewContextInformation(compositeContextValue))
			sendNotification(cv);
	}

	public void addRule(String[] conditions, String statement) {
		if (D)
			Log.d(LOG_TAG, "addRule " + contextName);
		// if (!checkRule(conditions, statement))
		Rule r = new Rule(conditions, statement);
		rules.add(r);
		Log.d("Rule", r.toString());
	}

	public boolean checkRule(String[] conditions, String statement) {
		if (D)
			Log.d(LOG_TAG, "checkRule " + contextName);
		// this is not good
		Rule rule = new Rule(conditions, statement);
		return rules.contains(rule);
	}

	public void addRules(ArrayList<Rule> newRules) {
		if (D)
			Log.d(LOG_TAG, "addRules " + contextName);
		for (Rule r : newRules)
			addRule(r.ifCondition, r.thenStatement);
	}

	public String fireRules() {
		if (D)
			Log.d(LOG_TAG, "fireRules " + contextName);
		String[] componentContexts = new String[components.size()];
		Log.d("fireRules", String.valueOf(componentContexts.length));

		int i = 0;
		for (Component c : components) {
			componentContexts[i] = c.getContextInformation(broadcastedKeys
					.get(0));
			Log.d(LOG_TAG, "fireRules" + c.contextName);
			Log.d(LOG_TAG, "fireRules" + componentContexts[i]);
			i++;
		}

		String thenStatement = "OFF";
		if (!componentContexts.equals(null)) {
			for (Rule r : rules) {
				if (r.fireRule(componentContexts))
					thenStatement = r.thenStatement;
				Log.d(LOG_TAG, "fireRules" + thenStatement);
			}
		}

		Log.d(LOG_TAG, "fireRules" + thenStatement);
		return thenStatement;
	}

	// add a new component to composite
	public void registerComponent(Component c) {
		if (D)
			Log.d(LOG_TAG, "registerComponent " + c.contextName + " to: "
					+ contextName);
		// if (!checkComponent(c))
		components.add(c);
	}

	// check whether the component has been already added
	public boolean checkComponent(Component c) {
		if (D)
			Log.d(LOG_TAG, "checkComponent in " + contextName);
		return components.contains(c);
	}

	public void registerComponents(ArrayList<Component> componentList) {
		if (D)
			Log.d(LOG_TAG, "registerComponents");
		for (Component c : componentList)
			if (!checkComponent(c))
				components.add(c);
	}

	public boolean unregisterComponent(Component c) {
		if (D)
			Log.d(LOG_TAG, "unregisterComponent");
		if (checkComponent(c))
			return components.remove(c);
		else
			return false;
	}

	public boolean isComposite() {
		if (D)
			Log.d(LOG_TAG, "isComposite");
		if (components.size() > 1)
			return true;
		else
			return false;
	}

	public int getComponentsNo() {
		if (D)
			Log.d(LOG_TAG, "getComponentsNo");
		return this.components.size();
	}

	// public String[] getKeysList(){
	// if (D) Log.d(LOG_TAG, "getKeysList");
	// String[] keysList = new String[valuesSets.get(0).keys.size()];
	// int i=0;
	// for (ApplicationKey appKey: valuesSets.get(0).keys){
	// keysList[i] = appKey.key;
	// i++;
	// }
	// return keysList;
	// }

	// public String[] getKeysList(){
	// if (D) Log.d(LOG_TAG, "getKeysList");
	// String[] keysList = new String[keys.size()];
	// int i=0;
	// for (ApplicationKey appKey: keys){
	// keysList[i] = appKey.key;
	// i++;
	// }
	// return keysList;
	// }
}
