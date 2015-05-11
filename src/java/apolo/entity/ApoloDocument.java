package apolo.entity;

/**
 * This is for referencing which fields we should index. We do not use this class for implementation 
 */

public class ApoloDocument {
	String documentId;
	String type = "artist/song/release";
	String timestamp;
	
	/**
	 * PROTOTYPE FOR ARTIST
	 */
	
	String artistId; //ID from DW
	String artistYahooId;
	String artistName;
	String artistType;
	String artistGender;
	String artistCountry;
	String artistContinent;
	double artistRating; //AVG rating
	String artistRating_type = "positive/neutral/negative";
	String ratingTimestamp = "";
	int artistNumberOfRating;
	int aritstNumberOfPositiveRating;
	int artistNumberOfNegativeRating;
	int artistNumberOfNeutralRating;
	
	
	/**
	 * PROTOTYPE FOR SONG
	 */
	
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
	String songGenres = "Pop,Rock"; //comma-seprated string
	String songSubGenres; //comma-separated string
	
	//
	String songGenreLinking = "";
	
	//Label
	String songLabel = ""; //comma-separated string
	
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
	
	
	//Song artist
	String songArtists = "Shakira,Bruno"; //commpa-separted
	String songArtistIDs;
	
	//Option
	String songArtistCountries; //comma-separated
	String songArtistTypes; //comma-separated
	String songArtistGenders; //comma-separated
	
	//How to index lyrics
	String songLyrics;
	
	//Should we include the playcount here?
	int songTotalPlayCount;
	
	/**
	 * PROTOTYPE FOR RELEASE
	 */
	
	String releaseID;
	String releaseName;
	String releaseType;
	String releaseSongs; //comma-separated
	String releaseSongIds;
	String releaseArtists; //comma-separated
}
