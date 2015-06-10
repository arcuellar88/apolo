package apolo.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import apolo.msc.Global_Configuration;

/**
 * This is for referencing which fields we should index. We do not use this class for implementation 
 */

public class ApoloDocument {
	String documentID;
	String type = "";
	String timestamp;
	float luceneScore;
	
	/**
	 * PROTOTYPE FOR ARTIST
	 */
	
	IArtist iartist = null;
	
	String artistID; //ID from DW
	String artistMBID;
	String artistName;
	String artistType;
	String artistGender;
	String artistCountry;
	String artistContinent;
	double artistRating; //AVG rating
	String artistRatingType = "";
	String ratingTimestamp = "";
	int artistNumberOfRating;
	int artistNumberOfPositiveRating;
	int artistNumberOfNegativeRating;
	int artistNumberOfNeutralRating;
	
	
	/**
	 * PROTOTYPE FOR SONG
	 */
	
	ISong isong;
	String songYoutubeURL;
	String songThumbnail;
	
	//For song
	String songID; //ID from DW
	String songEchonestID;
	String songMBID;
	String songTitle;
	double songDuration; //In second
	double songTempo;
	double songLoudness;
	double songEnergy;
	double songHotness;
	double songDancebility;
	
	//Rating
	double songRating;
	String songRatingType;
	int songNumberOfRating;
	
	//Song country
	String songSimilarSongIds = "songID1,songID2,songID3"; //comma-separated string
	String songCountries = "UK,France,Belgium"; //comma-separated string
	String songContinents = "Asia,Europe"; //comma-separated string
	
	//Genre
	String songGenres = "Pop,Rock"; //delimiter-seprated string
	String songSubGenres; //delimiter-separated string
	
	//Label
	String songLabel = ""; //delimiter-separated string
	
	//Song date
	String songDate; //String in the lucene format(so that can search in range). Need to check again new version 5.1.0 if it natively support time
	int songMonth;
	int songYear;
	String songDayOfWeek;
	String songQuarter;
	String songDecade;
	
	//Song release
	String songReleaseID;
	String songReleaseName;
	String songReleaseType;
	String songReleaseMBID;
	
	//
	int songTotalPlayCount;
	
	//Song artist
	String songArtists = "Shakira,Bruno"; //delimiter-separted
	String songArtistsID;
	
	String songLyrics;
	
	/**
	 * PROTOTYPE FOR RELEASE
	 */
	
	IRelease irelease = null;
	
	String releaseID;
	String releaseMBID;
	String releaseName;
	String releaseType;
	String releaseSongs; //comma-separated
	String releaseSongIDs;
	String releaseArtist; //comma-separated //TODO check again?
	String releaseArtistID;
	
	/**
	 * Get delimiter separated fields from Lucene
	 * @param value
	 * @return
	 */
	
	public ArrayList<String> getSplittedFields(String value) {
		String[] tmp = value.trim().split(Global_Configuration.INDEX_DELIMITER);
		
		//Use set to remove duplication
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0 ; i < tmp.length; i++) {
			if (!tmp[i].trim().equals("")) {
				result.add(tmp[i].trim());
			}
		}
		return result;
	}
	
	public ArrayList<String> getUniqueSplittedFields(String value) {
		String[] tmp = value.trim().split(Global_Configuration.INDEX_DELIMITER);
		
		//Use set to remove duplication
		ArrayList<String> result = new ArrayList<String>();
		Set<String> setResult = new HashSet<String>();
		
		for(int i = 0 ; i < tmp.length; i++) {
			if (!tmp[i].trim().equals("")) {
				setResult.add(tmp[i].trim());
				
			}
		}
		result.addAll(setResult);
		return result;
	}
	
	public String getSongYoutubeURL() {
		return songYoutubeURL;
	}

	public void setSongYoutubeURL(String songYoutubeURL) {
		this.songYoutubeURL = songYoutubeURL;
	}

	public String getSongThumbnail() {
		return songThumbnail;
	}

	public void setSongThumbnail(String songThumbnail) {
		this.songThumbnail = songThumbnail;
	}

	public String getReleaseMBID() {
		return releaseMBID;
	}

	public void setReleaseMBID(String releaseMBID) {
		this.releaseMBID = releaseMBID;
	}

	public String getReleaseArtistID() {
		return releaseArtistID;
	}

	public void setReleaseArtistID(String releaseArtistID) {
		this.releaseArtistID = releaseArtistID;
	}

	public String getDocumentID() {
		return documentID;
	}
	public void setDocumentID(String documentId) {
		this.documentID = documentId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public IArtist getIartist() {
		return iartist;
	}
	public void setIartist(IArtist iartist) {
		this.iartist = iartist;
	}
	public String getArtistID() {
		return artistID;
	}
	public void setArtistID(String artistId) {
		this.artistID = artistId;
	}
	
	public String getArtistMBID() {
		return artistMBID;
	}
	public void setArtistMBID(String artistMBID) {
		this.artistMBID = artistMBID;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public String getArtistType() {
		return artistType;
	}
	public void setArtistType(String artistType) {
		this.artistType = artistType;
	}
	public String getArtistGender() {
		return artistGender;
	}
	public void setArtistGender(String artistGender) {
		this.artistGender = artistGender;
	}
	public String getArtistCountry() {
		return artistCountry;
	}
	public void setArtistCountry(String artistCountry) {
		this.artistCountry = artistCountry;
	}
	public String getArtistContinent() {
		return artistContinent;
	}
	public void setArtistContinent(String artistContinent) {
		this.artistContinent = artistContinent;
	}
	public double getArtistRating() {
		return artistRating;
	}
	public void setArtistRating(double artistRating) {
		this.artistRating = artistRating;
	}
	public String getArtistRatingType() {
		return artistRatingType;
	}
	public void setArtistRatingType(String artistRating_type) {
		this.artistRatingType = artistRating_type;
	}
	public String getRatingTimestamp() {
		return ratingTimestamp;
	}
	public void setRatingTimestamp(String ratingTimestamp) {
		this.ratingTimestamp = ratingTimestamp;
	}
	public int getArtistNumberOfRating() {
		return artistNumberOfRating;
	}
	public void setArtistNumberOfRating(int artistNumberOfRating) {
		this.artistNumberOfRating = artistNumberOfRating;
	}
	public int getArtistNumberOfPositiveRating() {
		return artistNumberOfPositiveRating;
	}
	public void setArtistNumberOfPositiveRating(int aritstNumberOfPositiveRating) {
		this.artistNumberOfPositiveRating = aritstNumberOfPositiveRating;
	}
	public int getArtistNumberOfNegativeRating() {
		return artistNumberOfNegativeRating;
	}
	public void setArtistNumberOfNegativeRating(int artistNumberOfNegativeRating) {
		this.artistNumberOfNegativeRating = artistNumberOfNegativeRating;
	}
	public int getArtistNumberOfNeutralRating() {
		return artistNumberOfNeutralRating;
	}
	public void setArtistNumberOfNeutralRating(int artistNumberOfNeutralRating) {
		this.artistNumberOfNeutralRating = artistNumberOfNeutralRating;
	}
	public ISong getIsong() {
		return isong;
	}
	public void setIsong(ISong isong) {
		this.isong = isong;
	}
	public String getSongID() {
		return songID;
	}
	public void setSongID(String songID) {
		this.songID = songID;
	}
	public String getSongEchonestID() {
		return songEchonestID;
	}
	public void setSongEchonestID(String songEchonestID) {
		this.songEchonestID = songEchonestID;
	}
	public String getSongMBID() {
		return songMBID;
	}
	public void setSongMBID(String songMBID) {
		this.songMBID = songMBID;
	}
	public String getSongTitle() {
		return songTitle;
	}
	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}
	public double getSongDuration() {
		return songDuration;
	}
	public void setSongDuration(double songDuration) {
		this.songDuration = songDuration;
	}
	public double getSongTempo() {
		return songTempo;
	}
	public void setSongTempo(double songTempo) {
		this.songTempo = songTempo;
	}
	public double getSongLoudness() {
		return songLoudness;
	}
	public void setSongLoudness(double songLoudness) {
		this.songLoudness = songLoudness;
	}
	public double getSongEnergy() {
		return songEnergy;
	}
	public void setSongEnergy(double songEnergy) {
		this.songEnergy = songEnergy;
	}
	public double getSongHotness() {
		return songHotness;
	}
	public void setSongHotness(double songHotness) {
		this.songHotness = songHotness;
	}
	public double getSongDancebility() {
		return songDancebility;
	}
	public void setSongDancebility(double songDancebility) {
		this.songDancebility = songDancebility;
	}
	public double getSongRating() {
		return songRating;
	}
	public void setSongRating(double songRating) {
		this.songRating = songRating;
	}
	public String getSongRatingType() {
		return songRatingType;
	}
	public void setSongRatingType(String songRatingType) {
		this.songRatingType = songRatingType;
	}
	public int getSongNumberOfRating() {
		return songNumberOfRating;
	}
	public void setSongNumberOfRating(int songNumberOfRating) {
		this.songNumberOfRating = songNumberOfRating;
	}
	public String getSongSimilarSongIds() {
		return songSimilarSongIds;
	}
	public void setSongSimilarSongIds(String songSimilarSongIds) {
		this.songSimilarSongIds = songSimilarSongIds;
	}
	public String getSongCountries() {
		return songCountries;
	}
	public void setSongCountries(String songCountries) {
		this.songCountries = songCountries;
	}
	public String getSongContinents() {
		return songContinents;
	}
	public void setSongContinents(String songContinents) {
		this.songContinents = songContinents;
	}
	public String getSongGenres() {
		return songGenres;
	}
	public void setSongGenres(String songGenres) {
		this.songGenres = songGenres;
	}
	public String getSongSubGenres() {
		return songSubGenres;
	}
	public void setSongSubGenres(String songSubGenres) {
		this.songSubGenres = songSubGenres;
	}
	public String getSongLabel() {
		return songLabel;
	}
	public void setSongLabel(String songLabel) {
		this.songLabel = songLabel;
	}
	public String getSongDate() {
		return songDate;
	}
	public void setSongDate(String songDate) {
		this.songDate = songDate;
	}
	public int getSongMonth() {
		return songMonth;
	}
	public void setSongMonth(int songMonth) {
		this.songMonth = songMonth;
	}
	public int getSongYear() {
		return songYear;
	}
	public void setSongYear(int songYear) {
		this.songYear = songYear;
	}
	public String getSongDayOfWeek() {
		return songDayOfWeek;
	}
	public void setSongDayOfWeek(String songDayOfWeek) {
		this.songDayOfWeek = songDayOfWeek;
	}
	public String getSongQuarter() {
		return songQuarter;
	}
	public void setSongQuarter(String songQuarter) {
		this.songQuarter = songQuarter;
	}
	public String getSongDecade() {
		return songDecade;
	}
	public void setSongDecade(String songDecade) {
		this.songDecade = songDecade;
	}
	public String getSongReleaseID() {
		return songReleaseID;
	}
	public void setSongReleaseID(String songReleaseID) {
		this.songReleaseID = songReleaseID;
	}
	public String getSongReleaseName() {
		return songReleaseName;
	}
	public void setSongReleaseName(String songReleaseName) {
		this.songReleaseName = songReleaseName;
	}
	public String getSongReleaseType() {
		return songReleaseType;
	}
	public void setSongReleaseType(String songReleaseType) {
		this.songReleaseType = songReleaseType;
	}
	public String getSongReleaseMBID() {
		return songReleaseMBID;
	}
	public void setSongReleaseMBID(String songReleaseMBID) {
		this.songReleaseMBID = songReleaseMBID;
	}
	public String getSongArtists() {
		return songArtists;
	}
	public void setSongArtists(String songArtists) {
		this.songArtists = songArtists;
	}
	public String getSongArtistsID() {
		return songArtistsID;
	}
	public void setSongArtistsID(String songArtistIDs) {
		this.songArtistsID = songArtistIDs;
	}
	public String getSongLyrics() {
		return songLyrics;
	}
	public void setSongLyrics(String songLyrics) {
		this.songLyrics = songLyrics;
	}
	public IRelease getIrelease() {
		return irelease;
	}
	public void setIrelease(IRelease irelease) {
		this.irelease = irelease;
	}
	public String getReleaseID() {
		return releaseID;
	}
	public void setReleaseID(String releaseID) {
		this.releaseID = releaseID;
	}
	public String getReleaseName() {
		return releaseName;
	}
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}
	public String getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}
	public String getReleaseSongs() {
		return releaseSongs;
	}
	public void setReleaseSongs(String releaseSongs) {
		this.releaseSongs = releaseSongs;
	}
	public String getReleaseSongIDs() {
		return releaseSongIDs;
	}
	public void setReleaseSongIDs(String releaseSongIds) {
		this.releaseSongIDs = releaseSongIds;
	}
	public String getReleaseArtist() {
		return releaseArtist;
	}
	public void setReleaseArtist(String releaseArtists) {
		this.releaseArtist = releaseArtists;
	}
	public float getLuceneScore() {
		return luceneScore;
	}
	public void setLuceneScore(float luceneScore) {
		this.luceneScore = luceneScore;
	}
	
	public int getSongTotalPlayCount() {
		return songTotalPlayCount;
	}

	public void setSongTotalPlayCount(int songTotalPlayCount) {
		this.songTotalPlayCount = songTotalPlayCount;
	}
}
