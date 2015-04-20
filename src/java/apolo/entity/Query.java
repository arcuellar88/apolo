package apolo.entity;

public class Query implements IQuery{

	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------

	
	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------

	private String originalQuery;

	
	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------

	/**
	 * Constructor
	 * @param originalQuery
	 */
	public Query(String originalQuery) {
		super();
		this.originalQuery = originalQuery;
	}


	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------

	
	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}


	@Override
	public String getOriginalQuery() {
		return originalQuery;
	}
	
}
