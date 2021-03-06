package apolo.entity;

import java.util.Collection;

public class Artist extends RankingItem implements IArtist {

	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------
	
	
	private int artist_id;
	private String name;
	private String type;
	private String gender;
	private String country;
	private String description;
	private String thumbnail;
	private String URI;
	
	//DBPedia
	
	
	
	


	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	/**
	 * 
	 * @param artist_id
	 * @param name
	 * @param type
	 * @param gender
	 * @param country
	 */
	public Artist(int artist_id, String name, String type, String gender,
			String country) {
		super();
		this.artist_id = artist_id;
		this.name = name;
		this.type = type;
		this.gender = gender;
		this.country = country;
	}
	
	
	/**
	 * 
	 */
	public Artist() {
		// Empty Artists
	}

	
	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------


	public int getArtist_id() {
		return artist_id;
	}
	
	public void setArtist_id(int artist_id) {
		this.artist_id = artist_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	
	@Override
	public Collection<IRelease> getReleases() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString()
	{
		return artist_id+" "+name+" "+type;
	}
	
	
	public String getThumbnail() {
		return thumbnail;
	}


	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
}
