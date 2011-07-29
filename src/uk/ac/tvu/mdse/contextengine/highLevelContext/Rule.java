package uk.ac.tvu.mdse.contextengine.highLevelContext;

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
	}
	
	public String fireRule(String[] condition){
		int i=0;
		boolean match = false;
		if (condition[i].equals(ifCondition[i])){
			i++;
			if (i == ifCondition.length-1)
				match = true;
		}
			
		if (match)
			return thenStatement;
		else
			return "OFF";
	}
}
