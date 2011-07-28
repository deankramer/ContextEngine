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
		if (condition.equals(ifCondition))
			return thenStatement;
		else
			return null;
	}
}
