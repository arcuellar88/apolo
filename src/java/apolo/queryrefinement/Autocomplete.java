package apolo.queryrefinement;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import apolo.msc.Global_Configuration;

import com.aliasi.io.FileLineReader;
import com.aliasi.spell.AutoCompleter;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.util.ScoredObject;

public class Autocomplete {
	
	private String songsFile;
	private String artistsFile;
	private String releasesFile;
	//private Map<String,Integer> dictionary;
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
		Map<String,Integer> dictionary = loadDictionaryFromDB();
		//loadDictionaryFromFile();
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
	
	private Map<String,Integer> loadDictionaryFromFile(){
		Map<String,Integer> dictionary = new HashMap<String,Integer>(1000000);
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
		return dictionary;
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
	private Map<String, Integer> loadDictionaryFromDB(){
		Map<String,Integer> dictionary = new HashMap<String,Integer>(1000000);
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
		    	dictionary.put(val, 2); //1 is for ARTIST
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
		
		System.out.println("finished artists");
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
		System.out.println("finished releases");
		/** songs **/
		/*
		try {
			System.out.println("starting songs");
		    stmt = conn.createStatement();
		    rs = stmt.executeQuery("SELECT title FROM songs_apolo");
		    System.out.println("finished query");
		    while(rs.next()){
		    	String val = rs.getString("title");
		    	val = val.substring(0,val.length()-1);
		    	dictionary.put(val, 2); //1 is for SONG
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
		*/
		return dictionary;
	}
	
	public Connection getConnection(){
		Connection conn = null;
		try {
		    conn =
		       DriverManager.getConnection("jdbc:mysql://localhost/" +
		    		   					    Global_Configuration.MYSQL_DB + "?" +
		                                   "user=" + Global_Configuration.MYSQL_USER + 
		                                   "&password=" + Global_Configuration.MYSQL_PWD);
		} catch (SQLException ex) {
		}
		return conn;
	}
	
	/*
	public static void main(String a[]){
		System.out.println("Starting Autocomplete");
		Autocomplete ac = new Autocomplete();
		System.out.println("Autocomplete instance created");
		
		/*String q2 = "Ten Years";
		String q = "One Nigh";
		String q3 = "Pearl";
		ArrayList<String> comp;
		System.out.println("load finished");
		comp = ac.getCompletionsList(q);
		Iterator<String> it = comp.iterator();
		System.out.println("\n\nQuery: "+q);
		while(it.hasNext()){
			String rs = it.next();
			System.out.println(rs);
		}
		
		ArrayList<String> comp2 = ac.getCompletionsList(q2);
		Iterator<String> it2 = comp2.iterator();
		System.out.println("\n\nQuery: "+q2);
		while(it2.hasNext()){
			String rs = it2.next();
			System.out.println(rs);
		}
		
		ArrayList<String> comp3 = ac.getCompletionsList(q3);
		Iterator<String> it3 = comp3.iterator();
		System.out.println("\n\nQuery: "+q3);
		while(it3.hasNext()){
			String rs = it3.next();
			System.out.println(rs);
		}
	}
	*/
}
