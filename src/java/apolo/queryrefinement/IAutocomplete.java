package apolo.queryrefinement;

import java.util.ArrayList;

public interface IAutocomplete {
	
	/**
	 * Get the list of suggested auto completions for the input
	 * @param input corresponding to the part of the query that can be checked for auto completion suggestions
	 */
	public ArrayList<String> getCompletionsList(String input);

}
