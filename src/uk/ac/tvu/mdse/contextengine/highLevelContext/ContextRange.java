package uk.ac.tvu.mdse.contextengine.highLevelContext;

/**
 * @project ContextEngine
 * @date 27 Jul 2011
 * @author Dean Kramer & Anna Kocurova
 */

public class ContextRange {

	public Double minValue;
	public Double maxValue;
	public String contextHighValue;
	
	public ContextRange(Double mv, Double mxv, String value){
		this.minValue = mv;
		this.maxValue = mxv;
		this.contextHighValue = value;
	}
	
	public String getContextHighValue(Double currentValue){
		return ((currentValue<=maxValue)&&(currentValue>=minValue)) ? contextHighValue : null;
	}
}
