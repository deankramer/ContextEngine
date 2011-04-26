/**
 * @project ContextEngine
 * @date 26 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

import java.util.Calendar;

public class Event {

	//attributes	
	public String name;
	
	
	//constructors
	public Event(){
		this.name = "unknown";
	}
	
	public Event(String name){
		this.name = name;
	}
}
