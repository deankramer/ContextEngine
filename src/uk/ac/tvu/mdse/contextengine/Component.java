/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.io.Serializable;
import java.util.ArrayList;

public class Component extends ContextEntity implements Serializable{

	//attributes
	public ArrayList<ContextEntity> contexts;	
	
	//constructors
	public Component(){
		super();
		if (contexts==null)
			this.contexts = new ArrayList<ContextEntity>();
	}
	
	public Component(ArrayList contexts){
		super();
		if (contexts==null)
			this.contexts = contexts;
	}
	
	public boolean registerContextEntity(ContextEntity c){
		int pos = contexts.indexOf(c);
		if (pos == -1) 
			return false;
		else{
			contexts.add(c);
			return true;
			}
	}
	
	public ArrayList getContextEntities(){
		return contexts;
	}
	
	public boolean isComposite(){
		if (contexts.size()>1) 
			return true;
		else
			return false;
	}
}
