package uk.ac.tvu.mdse.contextengine.highLevelContext;

/**
 * @project ContextEngine
 * @date 27 Jul 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class ContextRange {

	public int minValue;
	public int maxValue;
	public String contextHighValue;
	
	public ContextRange(int mv, int mxv, String value){
		this.minValue = mv;
		this.maxValue = mxv;
		this.contextHighValue = value;
	}
	
	public String getContextHighValue(int currentValue){
		return ((currentValue<maxValue)&&(currentValue>=minValue)) ? contextHighValue : null;
	}
}
