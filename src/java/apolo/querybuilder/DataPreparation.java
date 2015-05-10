package apolo.querybuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;




import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;

import apolo.msc.Global_Configuration;

public class DataPreparation {
	/**
	 * For song extraction
	 */
	public final String releaseFile = "data/release.csv";
	public final String songFile = "data/songs.csv";
	public final String songCountryFile = "data/song_country.csv";
	public final String songGenreFile = "data/song_genre.csv";
	public final String songOutputFile = "data/songsindex";
	public final String songArtistFile = "data/song_artist.csv";
	public final String songLyricsFile = "data/song_lyrics.csv";
	public final String separator = "\t";
	private final String fieldValueSeparator = "[::FIELD_VALUE::]";
	
	
	//Map in format <songId,value>
	private Map<String, String> songCountryMap = new HashMap<String, String>();
	private Map<String, String> songContinentMap = new HashMap<String, String>();
	
	private Map<String, String> songGenreMap = new HashMap<String, String>();
	private Map<String, String> songSubGenreMap = new HashMap<String, String>();
	
	private Map<String, String> songLabelMap = new HashMap<String, String>();
	
	//Map <releaseId,value>
	private Map<String, String> releaseNameMap = new HashMap<String, String>();
	private Map<String, String> releaseTypeMap = new HashMap<String, String>();
	private Map<String, String> releaseMBMap = new HashMap<String, String>();
	
	//Artist map
	private Map<String, String> songArtistIdMap = new HashMap<String, String>();
	private Map<String, String> songArtistNameMap = new HashMap<String, String>();
	
	//Lyric map
	private Map<String, String> songLyricsMap = new HashMap<String, String>();
	
	public static void main(String[] args) throws IOException {
		
		DataPreparation dp = new DataPreparation();
		
		long start = System.currentTimeMillis();
		
		dp.readSongLocationMap();
		System.out.println("Location loaded: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		dp.readReleaseMap();
		System.out.println("Release loaded: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		dp.readSongGenreMap();
		System.out.println("Genre loaded: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		dp.readSongLabelMap();
		System.out.println("Label loaded: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		dp.readSongArtistMap();
		System.out.println("Artist loaded: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		dp.readSongLyricsMap();
		System.out.println("Lyrics loaded: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		dp.generateSongFile();
		System.out.println("Finish processing: " + (System.currentTimeMillis() - start));
	}
	
	/**
	 * Get field name and corresponding index in a text file
	 * @return
	 */
	
	public Map<String, Integer> getFieldValueMap() {
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();
	
		fieldValueMap.put("songID", 0);
		fieldValueMap.put("songTitle", 5);
		fieldValueMap.put("songEchonestID", 6);
		fieldValueMap.put("songMBID", 7);
		
		fieldValueMap.put("songDuration", 10);
		fieldValueMap.put("songTempo", 11);
		fieldValueMap.put("songLoudness", 12);
		fieldValueMap.put("songEnergy", 13);
		fieldValueMap.put("songHotness", 14);
		fieldValueMap.put("songDancebility", 15);
		
		fieldValueMap.put("songRating", 16);
		fieldValueMap.put("songNumberOfRating", 17);
		
		//Date
		fieldValueMap.put("songDate", 22);
		fieldValueMap.put("songMonth", 23);
		fieldValueMap.put("songYear", 24);
		fieldValueMap.put("songDayOfWeek", 25);
		fieldValueMap.put("songQuarter", 26);
		fieldValueMap.put("songDecade", 27);
		
		//Release
		fieldValueMap.put("releaseID", 4);
		
		return fieldValueMap;
	}
	
	public void generateSongFile() throws IOException {
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(songFile));
	    String line;
	    
	    int counter = 0;
	    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(songOutputFile + "_" + counter + ".csv"), 131072);
	    while ((line = br.readLine()) != null) {
	    	counter++;
	    	if (counter == 1) {
	    		//Ignore first row
	    		continue;
	    	}
	       // process the line.
	    	String[] tmp = line.split(separator);
	    	String outputLine = "";
	    	
	    	Map<String, Integer> fieldValueMap = getFieldValueMap();
	    	
	    	String songID = tmp[fieldValueMap.get("songID")];
	    	
	    	//Add type/timestamp
	    	outputLine += "documentID" + fieldValueSeparator + "song_" + songID;
	    	outputLine += "\ttimestamp" + fieldValueSeparator + DateTools.dateToString(Calendar.getInstance().getTime(), Resolution.SECOND);
	    	outputLine += "\ttype" + fieldValueSeparator + "song";
	    	
	    	for(Map.Entry<String, Integer> entry : fieldValueMap.entrySet()) {
	    		if (!outputLine.equalsIgnoreCase("")) {
	    			outputLine += "\t";
	    		}
	    		outputLine += entry.getKey() + fieldValueSeparator + tmp[entry.getValue()];
	    	}
	    	
	    	
	    	//Add date field to search
	    	String date = tmp[fieldValueMap.get("songDate")];
	    	if (!date.equalsIgnoreCase("")) {
	    		String month = tmp[fieldValueMap.get("songMonth")];
	    		String year = tmp[fieldValueMap.get("songYear")];
	    		outputLine += "\tdateSearch" + fieldValueSeparator + getDateString(date, month, year);
	    	}
	    	else {
	    		outputLine += "\tdateSearch" + fieldValueSeparator + "";
	    	}
	    	
	    	//Add countries
	    	if (songCountryMap.containsKey(songID)) {
	    		outputLine += "\tsongCountries" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songCountryMap.get(songID) +  Global_Configuration.INDEX_SEPARATOR;
	    	}
	    	else {
	    		outputLine += "\tsongCountries" + fieldValueSeparator + "";
	    	}
	    	
	    	
	    	//Add continent
	    	if (songContinentMap.containsKey(songID)) {
	    		outputLine += "\tsongContinents" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songContinentMap.get(songID) + Global_Configuration.INDEX_SEPARATOR;
	    	}
	    	else {
	    		outputLine += "\tsongContinents" + fieldValueSeparator + "";
	    	}
	    	
	    	//add Release
	    	String releaseID = tmp[fieldValueMap.get("releaseID")];
	    	if (releaseNameMap.containsKey(releaseID)) {
	    		outputLine += "\treleaseName" + fieldValueSeparator + releaseNameMap.get(releaseID);
	    		outputLine += "\treleaseType" + fieldValueSeparator + releaseTypeMap.get(releaseID);
	    		outputLine += "\treleaseMBID" + fieldValueSeparator + releaseMBMap.get(releaseID);
	    	}
	    	else {
	    		outputLine += "\treleaseName" + fieldValueSeparator + "";
	    		outputLine += "\treleaseType" + fieldValueSeparator + "";
	    		outputLine += "\treleaseMBID" + fieldValueSeparator + "";
	    	}
	    	
	    	//Add genre
	    	if (songGenreMap.containsKey(songID)) {
	    		outputLine += "\tsongGenres" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songGenreMap.get(songID) + Global_Configuration.INDEX_SEPARATOR;
	    		outputLine += "\tsongSubGenres" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songSubGenreMap.get(songID) + Global_Configuration.INDEX_SEPARATOR;
	    	}
	    	else {
	    		outputLine += "\tsongGenres" + fieldValueSeparator + "";
	    		outputLine += "\tsongSubGenres" + fieldValueSeparator + "";
	    	}
	    	
	    	//Add label
	    	if (songLabelMap.containsKey(songID)) {
	    		outputLine += "\tsongLabels" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songLabelMap.get(songID) + Global_Configuration.INDEX_SEPARATOR;
	    	}
	    	else {
	    		outputLine += "\tsongLabels" + fieldValueSeparator + "";
	    	}
	    	
	    	//add artist
	    	if (songArtistIdMap.containsKey(songID)) {
	    		outputLine += "\tsongArtistsID" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songArtistIdMap.get(songID) + Global_Configuration.INDEX_SEPARATOR;
	    		outputLine += "\tsongArtists" + fieldValueSeparator + Global_Configuration.INDEX_SEPARATOR + songArtistNameMap.get(songID) + Global_Configuration.INDEX_SEPARATOR;
	    	}
	    	else {
	    		outputLine += "\tsongArtistsID" + fieldValueSeparator + "";
	    		outputLine += "\tsongArtists" + fieldValueSeparator + "";
	    	}
	    	
	    	//add lyrics
	    	
	    	if (songLyricsMap.containsKey(songID)) {
	    		outputLine += "\tsonglyrics" + fieldValueSeparator + songLyricsMap.get(songID);
	    	}
	    	else {
	    		outputLine += "\tsonglyrics" + fieldValueSeparator + "";
	    	}
	    	
	    	fileWriter.write(outputLine + "\n");
	    	
	    	if (counter % 1000000 == 0) {
	    		fileWriter.flush();
	    		fileWriter.close();
	    		fileWriter = new BufferedWriter(new FileWriter(songOutputFile + "_" + counter + ".csv"), 131072);
	    	}
	    }
	    
	    fileWriter.close();
	}
	
	/**
	 * Read songCountryMap and songContinentMap
	 * @throws IOException 
	 */
	
	public void readSongLocationMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(songCountryFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] tmp = line.split(separator);
	    	String songId = tmp[0];
	    	String country = tmp[2];
	    	String continent = tmp[3];
	    	
	    	if (songCountryMap.containsKey(songId)) {
	    		//String newCountryValue = songCountryMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + country;
	    		//String newContinentValue = songContinentMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + continent;
	    		
	    		songCountryMap.put(songId, songCountryMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + country);
	    		songContinentMap.put(songId, songContinentMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + continent);
	    	}
	    	else {
	    		songCountryMap.put(songId, country);
	    		songContinentMap.put(songId, continent);
	    	}
	    }
	    br.close();
	}
	
	/**
	 * Read artist map
	 * @throws IOException
	 */
	
	public void readSongArtistMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(songArtistFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] tmp = line.split(separator);
	    	String songId = tmp[0];
	    	String artistId = tmp[1];
	    	String artistName = tmp[2];
	    	
	    	if (songArtistIdMap.containsKey(songId)) {
	    		//String newArtistIdValue = songArtistIdMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + artistId;
	    		//String newArtistNameValue = songArtistNameMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + artistName;
	    		
	    		songArtistIdMap.put(songId, songArtistIdMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + artistId);
	    		songArtistNameMap.put(songId, songArtistNameMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + artistName);
	    	}
	    	else {
	    		songArtistIdMap.put(songId, artistId);
	    		songArtistNameMap.put(songId, artistName);
	    	}
	    }
	    br.close();
	}
	
	/**
	 * Read song lyrics map
	 * @throws IOException
	 */
	
	public void readSongLyricsMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(songLyricsFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] tmp = line.split(separator);
	    	
	    	if (!songLyricsMap.containsKey(tmp[0])) {
	    		songLyricsMap.put(tmp[0], tmp[2]);
	    	}
	    }
	    br.close();
	}
	
	/**
	 * Read the release
	 * @throws IOException
	 */
	
	public void readReleaseMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(releaseFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] tmp = line.split(separator);
	    	String releaseId = tmp[0];
	    	
	    	//String releaseName = tmp[1];
	    	//String releaseType = tmp[2];
	    	//String releaseMBID = tmp[3];
	    	
	    	if (!releaseNameMap.containsKey(releaseId)) {
	    		//Add to release map
	    		releaseNameMap.put(releaseId, tmp[1]);
	    		releaseTypeMap.put(releaseId, tmp[2]);
	    		releaseMBMap.put(releaseId, tmp[3]);
	    	}
	    }
	    br.close();
	}
	
	/**
	 * Read song genre
	 * @throws IOException 
	 */
	
	public void readSongGenreMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(songGenreFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] tmp = line.split(separator);
	    	String songId = tmp[0];
	    	String genre = tmp[2];
	    	String subGenre = tmp[4];
	    	
	    	if (songGenreMap.containsKey(songId)) {
	    		//String newGenreValue = songGenreMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + genre;
	    		//String newSubGenreValue = songSubGenreMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + subGenre;
	    		
	    		songGenreMap.put(songId, songGenreMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + genre);
	    		songSubGenreMap.put(songId, songSubGenreMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + subGenre);
	    	}
	    	else {
	    		songGenreMap.put(songId, genre);
	    		songSubGenreMap.put(songId, subGenre);
	    	}
	    }
	    br.close();
	}
	
	/**
	 * Read label
	 * @throws IOException
	 */
	
	public void readSongLabelMap() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(songGenreFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	    	String[] tmp = line.split(separator);
	    	String songId = tmp[0];
	    	String label = tmp[2];
	    	
	    	if (songLabelMap.containsKey(songId)) {
	    		//String newLabelValue = songLabelMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + label;
	    		
	    		songLabelMap.put(songId, songLabelMap.get(songId) + Global_Configuration.INDEX_SEPARATOR + label);
	    	}
	    	else {
	    		songLabelMap.put(songId, label);
	    	}
	    }
	    br.close();
	}
	
	/**
	 * Get Lucene resolution string for index
	 * @param date: in form "dd/mm/yy(yy)"
	 * @param month
	 * @param year
	 * @return
	 */
	
	public String getDateString(String date, String month, String year) {
		String[] tmp = date.split("/");
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(tmp[0]));
		return DateTools.dateToString(c.getTime(), Resolution.DAY);
	}
}
