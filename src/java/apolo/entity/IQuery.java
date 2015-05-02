package apolo.entity;

public interface IQuery {

	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------

	/**
	 * Set original query
	 * @param originalQuery
	 */
	public void setOriginalQuery(String originalQuery);
	
	/**
	 * Return the original query from the user
	 * @return String originalQuery
	 */
	public String getOriginalQuery();
	
	/**
	 * Finds the entities in the current query and saves them as a list within the query
	 */
	public void annotateQuery();
	
}
