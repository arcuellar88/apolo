package apolo.entity;

public class Release extends RankingItem implements IRelease{

	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------

	/**
	 * Name of the release
	 */
	private String name;
	
	/**
	 * Type of the release: Album, Single, etc.
	 */
	private String type;
	
	
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
