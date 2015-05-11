package apolo.queryrefinement;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import com.aliasi.dict.DictionaryEntry;
import com.aliasi.io.FileLineReader;
import com.aliasi.spell.AutoCompleter;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.util.ScoredObject;

public class Autocomplete {
	
	private String songsFile;
	private String artistsFile;
	private String releasesFile;
	private Map<String,Integer> dictionary;
	private double matchWeight;
	private double insertWeight;
	private double substituteWeight;
	private double deleteWeight;
	private double transposeWeight;
	private FixedWeightEditDistance editDistance;
	private int maxResults;
	private int maxQueueSize;
	private double minScore;
	private AutoCompleter completer;
	
	public Autocomplete(){
		String path = System.getProperty("user.dir");
		String fullpath = path + File.separator + "src" + File.separator + "java" + File.separator + "apolo" + File.separator + "queryrefinement" + File.separator;
		this.songsFile = fullpath + "songs.txt";
		this.artistsFile = fullpath + "artists.txt";
		this.releasesFile = fullpath + "releases.txt";
		this.dictionary = new HashMap<String,Integer>(15000000);
		loadDictionaryFromDB();
		this.matchWeight = 0.0;
        this.insertWeight = -10.0;
        this.substituteWeight = -10.0;
        this.deleteWeight = -10.0;
        this.transposeWeight = Double.NEGATIVE_INFINITY;
        this.editDistance = new FixedWeightEditDistance(matchWeight,deleteWeight,insertWeight,substituteWeight,transposeWeight);
        this.maxResults = 5;
        this.maxQueueSize = 10000;
        this.minScore = -25.0;
        completer = new AutoCompleter(dictionary, editDistance, maxResults, maxQueueSize, minScore);
	}
	
	private void loadDictionaryFromFile(){
		File songsFile = new File(this.songsFile);
		File artistsFile = new File(this.artistsFile);
		File releasesFile = new File(this.releasesFile);
		String[] songs = null;
		String[] artists = null;
		String[] releases = null;
		try{
			songs = FileLineReader.readLineArray(songsFile,"UTF-8");
			artists = FileLineReader.readLineArray(artistsFile,"UTF-8");
			releases = FileLineReader.readLineArray(releasesFile,"UTF-8");
		}catch(IOException ioe){
			System.out.println("ERROR: problem reading dictionary files on loadDictionary");
		}
		
		//add all the song titles to the dictionary
		if(songs != null){
			for(String song : songs){
				int f = song.indexOf("\t");
				int i = song.indexOf("\t", f);
	            if (i < 0) continue;
	            String title = song.substring(f+1,i);
				dictionary.put(title, 0); //0 is for SONG
			}
		}
		
		//add all the artist names to the dictionary
		if(artists != null){
			for(String artist : artists){
				int f = artist.indexOf("\t");
				int i = artist.indexOf("\t", f);
	            if (i < 0) continue;
	            String name = artist.substring(f+1,i);
				dictionary.put(name, 1); //1 is for ARTIST
			}
		}
		
		//add all the release names to the dictionary
		if(releases != null){
			for(String release : releases){
				int f = release.indexOf("\t");
				int i = release.indexOf("\t", f);
	            if (i < 0) continue;
	            String name = release.substring(f+1,i);
				dictionary.put(name, 2); //2 is for RELEASE
			}
		}
	}
	
	public ArrayList<String> getCompletionsList(String input){
		SortedSet<ScoredObject<String>> completions = completer.complete(input);
		ArrayList<String> completionsList = new ArrayList<String>();
		for (ScoredObject<String> so : completions){
			completionsList.add(so.getObject());
		}
		return completionsList;
	}
	
	/***
	 * Loads the songs, artists and releases to the dictionary.
	 * It uses the existing DB table to obtain all songs, artists and release names.
	 */
	private void loadDictionaryFromDB(){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		/** artists **/
		try {
		    stmt = conn.createStatement();
		    rs = stmt.executeQuery("SELECT title FROM artists_apolo");
		    while(rs.next()){
		    	String val = rs.getString("title");
		    	val = val.substring(0,val.length()-1);
		    	dictionary.put(val, 1); //1 is for ARTIST
		    }
		}
		catch (SQLException ex){
		}
		finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore

		        rs = null;
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore

		        stmt = null;
		    }
		}
		
		
		/** releases **/
		try {
		    stmt = conn.createStatement();
		    rs = stmt.executeQuery("SELECT title FROM releases_apolo");
		    while(rs.next()){
		    	String val = rs.getString("title");
		    	val = val.substring(0,val.length()-1);
		    	dictionary.put(val, 2); //2 is for RELEASE
		    }
		}
		catch (SQLException ex){
		}
		finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore

		        rs = null;
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore

		        stmt = null;
		    }
		}
	}
	
	public Connection getConnection(){
		Connection conn = null;
		try {
		    conn =
		       DriverManager.getConnection("jdbc:mysql://localhost/apolo?" +
		                                   "user=root&password=");
		} catch (SQLException ex) {
		}
		return conn;
	}

}
