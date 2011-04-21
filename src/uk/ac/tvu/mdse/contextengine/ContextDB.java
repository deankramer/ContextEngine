/**
 * @project ContextEngine
 * @date 21 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine;

public interface ContextDB {

	boolean addContext(ContextEntity c);
	boolean removeContext(int id);
	boolean updateContext(ContextEntity c);
	ContextEntity getContext(int id);
	
}
