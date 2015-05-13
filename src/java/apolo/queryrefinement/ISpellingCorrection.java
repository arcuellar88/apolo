package apolo.queryrefinement;

import java.util.ArrayList;

public interface ISpellingCorrection {
	
	/**
	 * Get spelling suggestions for the query that was input by the user.
	 * Returns a String with the "corrected" query. The String is empty if no correction was found
	 * @param original query
	 */
	public String getSpellingSuggestions(String query);

}
