package apolo.entity;

public abstract class RankingItem implements IRankingItem {
	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------
	public final static String TYPE_ARTIST="Artist";
	public final static String TYPE_SONG="Song";
	public final static String TYPE_RELEASE="Release";
	
	
	// --------------------------------------------------------
	// Attribute
	// --------------------------------------------------------

	private String type;
	private double similarity;

	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------
	@Override
	public String getItemType() {
		return type;
	}
	public void setItemType(String type)
	{
		this.type=type;
	}


	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public double getSimilarity() {
		return similarity;
	}

	@Override
	public void setSimilarity(double sim) {
		similarity=sim;
		
	}
	
}
