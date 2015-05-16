package apolo.entity;

import java.util.Collection;

public interface IArtist {

	
	
	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------
	public int getArtist_id();
	public void setArtist_id(int artist_id);
	public String getName();
	public void setName(String name);
	public String getType();
	public void setType(String type);
	public String getGender();
	public void setGender(String gender);
	public String getCountry();
	public void setCountry(String country);
	public String getThumbnail();
	public void setThumbnail(String thumbnail);
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------
	/**
	 * Get the releases of an artist
	 * @return
	 */
	public Collection<IRelease>getReleases();
	public void setDescription(String description);
	public String getDescription();
	public void setURI(String textContent);
	public String getURI();
	
}
