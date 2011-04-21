package uk.ac.tvu.mdse.contextengine;

/**
 * @project ContextEngine
 * @date 21 Apr 2011
 * @author Anna Kocurova
 */

public interface ContextDB {

	boolean addContext(ContextEntity c);
	boolean removeContext(int id);
	boolean updateContext(ContextEntity c);
	ContextEntity getContext(int id);
	
}
