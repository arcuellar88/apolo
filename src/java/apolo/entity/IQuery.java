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
	
}
