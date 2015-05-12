package apolo.entity;

import java.util.Date;

public interface ISong {

	
	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------
		
	public int getSong_id();
	public void setSong_id(int song_id);
	public String getTitle();
	public void setTitle(String title);
	public String getRating();
	public void setRating(String rating);
	public int getRatingType();
	public void setRatingType(int ratingType);
	public Date getReleaseDate();
	public void setReleaseDate(Date releaseDate);
	public double getDuration();
	public void setDuration(double duration);
	public String getTempo();
	public void setTempo(String tempo);
	public String getMode();
	public void setMode(String mode);
	public String getKey();
	public void setKey(String key);
	public String getHotness();
	public void setHotness(String hotness);
	public String getDanceability();
	public void setDanceability(String danceability);
	public double getKey_value();
	public void setKey_value(double key_value);
	public double getMode_value();
	public void setMode_value(double mode_value);
	public void setDescription(String description);
	public String getDescription();
	public void setURI(String textContent);
	public String getURI();
	
}
