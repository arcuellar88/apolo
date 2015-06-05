package apolo.entity;

public interface IRelease {

	
	// --------------------------------------------------------
	// Getters & Setters
	// --------------------------------------------------------
	public String getName();
	public String getType();
	public void setName(String name);
	public void setType(String type);
	public void setDescription(String description);
	public String getDescription();
	public void setURI(String textContent);
	public String getURI();
	public String getThumbnail();
	public void setThumbnail(String thumbnail);
	
}
