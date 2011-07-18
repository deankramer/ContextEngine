/**
 * @project ContextEngine
 * @date 21 Apr 2011
 * @author Dean Kramer & Anna Kocurova
 */

package uk.ac.tvu.mdse.contextengine.db;

import java.util.ArrayList;

import uk.ac.tvu.mdse.contextengine.Component;

public interface ContextDB {

	boolean addContext(Component c);

	boolean removeContext(int id);

	boolean updateContext(Component c);

	Component getContext(int id);
	
	boolean getContextValue(String name);
	
	ArrayList<Component> getAllContexts();

}
