package apolo.queryrefinement;

import java.util.*;

public interface INER {
	
	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------

	/**
	 * Get named entities in the query that was input by the user
	 * Returns a list of Annotations. The list is empty if none is found
	 * @param original query
	 */
	public ArrayList<Annotation> getNamedEntities(String query);
	

	
}
