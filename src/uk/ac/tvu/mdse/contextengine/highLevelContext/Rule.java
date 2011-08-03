package uk.ac.tvu.mdse.contextengine.highLevelContext;

import android.util.Log;

/**
 * @project ContextEngine
 * @date 28 Jul 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class Rule {

	public String[] ifCondition;
	public String thenStatement;
	
	public Rule(String[] condition, String statement){
		this.ifCondition = condition;
		this.thenStatement = statement;
		Log.d("Rule", String.valueOf(ifCondition.length)+" "+ifCondition[0]);
	}
	
	public boolean fireRule(String[] condition){
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
		StringBuffer strbuf = new StringBuffer();
		for (String str : ifCondition)
			strbuf.append(str);
		strbuf.append(thenStatement);
		return strbuf.toString();
	}
}
