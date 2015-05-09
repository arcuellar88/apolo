package apolo.recommender;

import apolo.entity.*;

public interface IRecommender {

	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	/**
	 * Get recommended artists
	 * @param a
	 * @return IRanking, a ranking of recommendations
	 */
	public IRanking getRecommendation(IArtist a,int numRecommendations);
	
}
