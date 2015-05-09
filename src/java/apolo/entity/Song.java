package apolo.entity;

import java.util.Date;

public class Song extends RankingItem implements ISong{

	
	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------

	private int song_id;
	private String title;
	private String rating;
	private int ratingType;
	private Date releaseDate;
	
	//Features
	
	private double duration;
	private String tempo;
	private String mode;
	private String key;
	private String hotness;
	private String danceability;
	private double key_value;
	private double mode_value;
	
	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------
	public Song(int song_id, String title, String rating, int ratingType,
			Date releaseDate, double duration, String tempo, String mode,
			String key, String hotness, String danceability, double key_value,
			double mode_value) {
		super();
		this.song_id = song_id;
		this.title = title;
		this.rating = rating;
		this.ratingType = ratingType;
		this.releaseDate = releaseDate;
		this.duration = duration;
		this.tempo = tempo;
		this.mode = mode;
		this.key = key;
		this.hotness = hotness;
		this.danceability = danceability;
		this.key_value = key_value;
		this.mode_value = mode_value;
	}
	
	// --------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------
	public int getSong_id() {
		return song_id;
	}
	
	public void setSong_id(int song_id) {
		this.song_id = song_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public int getRatingType() {
		return ratingType;
	}
	public void setRatingType(int ratingType) {
		this.ratingType = ratingType;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getTempo() {
		return tempo;
	}
	public void setTempo(String tempo) {
		this.tempo = tempo;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getHotness() {
		return hotness;
	}
	public void setHotness(String hotness) {
		this.hotness = hotness;
	}
	public String getDanceability() {
		return danceability;
	}
	public void setDanceability(String danceability) {
		this.danceability = danceability;
	}
	public double getKey_value() {
		return key_value;
	}
	public void setKey_value(double key_value) {
		this.key_value = key_value;
	}
	public double getMode_value() {
		return mode_value;
	}
	public void setMode_value(double mode_value) {
		this.mode_value = mode_value;
	}
	
}
