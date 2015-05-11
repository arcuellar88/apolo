package apolo.queryrefinement;

import java.util.ArrayList;

public interface IAutocomplete {
	
	/**
	 * Get the list of suggested auto completions for the query that was input by the user.
	 * Returns a list of Strings that are suggested for auto complete. The list is empty if none is found
	 * @param input corresponding to the part of the query that can be checked for auto completion suggestions
	 */
	public ArrayList<String> getCompletionsList(String input);

}
