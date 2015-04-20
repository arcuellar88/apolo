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


	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	
	
}
