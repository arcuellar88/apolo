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
	
	private String description;
	private String URI;
	
	public Release() {
		// Empty Artists
	}
	
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
	
	public void setDescription(String description)
	{
		this.description=description;
	}
	public String getDescription()
	{
		return description;
	}
	
	public String getURI() {
		return URI;
	}


	public void setURI(String uRI) {
		URI = uRI;
	}
	
}
