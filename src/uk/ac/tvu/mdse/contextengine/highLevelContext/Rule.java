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

package uk.ac.tvu.mdse.contextengine.highLevelContext;

import android.util.Log;

public class Rule {
	
	public static final String LOG_TAG = "Rule";
	public static final boolean D = true;

	public String[] ifCondition;
	public String thenStatement;
	
	public Rule(String[] condition, String statement){
		if (D) Log.d(LOG_TAG, "constructor");
		this.ifCondition = condition;
		this.thenStatement = statement;
		Log.d("Rule", String.valueOf(ifCondition.length)+" "+ifCondition[0]);
	}
	
	public boolean fireRule(String[] condition){
		if (D) Log.d(LOG_TAG, "fireRule");
		int i=0;
		int j =0;
		boolean match = false;
		while (i<ifCondition.length){
			if (condition[i].equals(ifCondition[i])){
				j++;
				if (j == ifCondition.length)
					match = true;
				Log.d("Rule", "fire rule" +String.valueOf(j));
			}
			i++;
		}
		Log.d("Rule", "fire rule" +String.valueOf(match));
		return match;
	}
	
	public String toString(){
		if (D) Log.d(LOG_TAG, "toString");
		StringBuffer strbuf = new StringBuffer();
		for (String str : ifCondition)
			strbuf.append(str);
		strbuf.append(thenStatement);
		return strbuf.toString();
	}
}
