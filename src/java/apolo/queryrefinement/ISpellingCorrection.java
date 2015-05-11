package apolo.queryrefinement;

import java.util.ArrayList;

public interface ISpellingCorrection {
	
	/**
	 * Get spelling suggestions for the query that was input. Returns the "corrected" query
	 * @param originalQuery
	 */
	public String getSpellingSuggestions(String query);

}
