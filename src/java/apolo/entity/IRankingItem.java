package apolo.entity;

public interface IRankingItem {

	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	/**
	 * Return the type of the ranking Item= artist, release or song
	 * @return
	 */
	public String getItemType();
	
	
	// --------------------------------------------------------
	// Recommender
	// --------------------------------------------------------

	/**
	 * Similarity: Applicable for recommendation ranking
	 * @return
	 */
	public double getSimilarity();
	public void setSimilarity(double sim);


	public void setItemType(String type);
	
}
