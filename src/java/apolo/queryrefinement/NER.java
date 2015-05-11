package apolo.queryrefinement;

import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.ApproxDictionaryChunker;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.dict.TrieDictionary;
import com.aliasi.io.FileLineReader;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.WeightedEditDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class NER {

	private String query; /** Query that needs to be enhanced */
	private String songsFile;
	private String artistsFile;
	private String releasesFile;
	//another variable could be the Lucene index if we use it to get the entities
	MapDictionary<String> dictionaryExact; /** Dictionary that contains the entities that will be recognized */
	TrieDictionary<String> dictionaryApprox;
	private static final double CHUNK_SCORE = 1.0;
	
	public NER(){
		this.query="";
		String path = System.getProperty("user.dir");
		String fullpath = path + File.separator + "src" + File.separator + "java" + File.separator + "apolo" + File.separator + "queryrefinement" + File.separator;
		this.songsFile = fullpath + "songs.txt";
		this.artistsFile = fullpath + "artists.txt";
		this.releasesFile = fullpath + "releases.txt";
		dictionaryExact = new MapDictionary<String>();
		dictionaryApprox = new TrieDictionary<String>();
		
		try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }
		
		loadDictionaryFromDB();
	}
	
	public NER(String query){
		this.query=query;
		String path = System.getProperty("user.dir");
		String fullpath = path + File.separator + "src" + File.separator + "java" + File.separator + "apolo" + File.separator + "queryrefinement" + File.separator;
		this.songsFile = fullpath + "songs.txt";
		this.artistsFile = fullpath + "artists.txt";
		this.releasesFile = fullpath + "releases.txt";
		dictionaryExact = new MapDictionary<String>();
		dictionaryApprox = new TrieDictionary<String>();
		loadDictionaryFromDB();
	}
	
	public ArrayList<Annotation> annotateQuery(){
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		annotations = findNamedEntities();
		return annotations;
	}
	
	public ArrayList<Annotation> annotateQuery(String q){
		this.query = q;
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		annotations = findNamedEntities();
		return annotations;
	}
	
	private ArrayList<Annotation> findNamedEntities(){
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		//First look if there are any " in the query, to look for exact matches
		boolean findExactMatch = false;
		ArrayList<String> exactQueryElems = new ArrayList<String>(); //this list won't have any elements if the query doesn't have any exact match requests (defined with "...")
										 //But if the query has exact match requests, then it will have one element per string within quotes
		ArrayList<String> approxQueryElems = new ArrayList<String>(); //this list will have only one element if there are no quotes within the query
																	//if there are quotes in the query then it will have one element per string outside of quotes
		String elems[] = this.query.split("\"");
		for(int i=0; i<elems.length; i++){
			if(i%2==0){
				approxQueryElems.add(elems[i]);
			}else{
				exactQueryElems.add(elems[i]);
			}
		}
		if(!exactQueryElems.isEmpty()){
			ExactDictionaryChunker dictionaryChunkerExact = new ExactDictionaryChunker(dictionaryExact, IndoEuropeanTokenizerFactory.INSTANCE, true,false);
			Iterator<String> it = exactQueryElems.iterator();
			while(it.hasNext()){
				String text = it.next();
				Chunking chunking = dictionaryChunkerExact.chunk(text);
				for (Chunk chunk : chunking.chunkSet()) {
					int start = chunk.start();
		            int end = chunk.end();
		            String type = chunk.type();
		            double score = chunk.score();
		            String phrase = text.substring(start,end);
		            annotations.add(new Annotation(phrase,type,start,end,0,phrase.length()));
				}
			}
		}
		if(!approxQueryElems.isEmpty()){
	        WeightedEditDistance editDistance = new FixedWeightEditDistance(0,-1,-1,-1,Double.NaN);
	        double maxDistance = 3.0;
	        ApproxDictionaryChunker dictionaryChunkerApprox = new ApproxDictionaryChunker(dictionaryApprox,IndoEuropeanTokenizerFactory.INSTANCE, editDistance,maxDistance);
	        Iterator<String> it = approxQueryElems.iterator();
	        while(it.hasNext()){
	        	String text = it.next();
	        	Chunking chunking = dictionaryChunkerApprox.chunk(text);
	        	for (Chunk chunk : chunking.chunkSet()) {
	        		int start = chunk.start();
	                int end = chunk.end();
	                String type = chunk.type();
	                double distance = chunk.score();
	                String phrase = chunking.charSequence().subSequence(start,end).toString();
	                annotations.add(new Annotation(phrase,type,start,end,distance,phrase.length()));
	                
	        	}
	        }
		}
		
		return annotations;
	}
	
	
	
	
	/***
	 * Loads the songs, artists and releases to the dictionary.
	 * It uses text files extracted from the DW containing all the information for songs, artists and releases
	 * It assumes the ID attributes is the first one of every line and the title or name comes second
	 */
	private void loadDictionaryFromFile(){
		File songsFile = new File(this.songsFile);
		File artistsFile = new File(this.artistsFile);
		File releasesFile = new File(this.releasesFile);
		String[] songs = null;
		String[] artists = null;
		String[] releases = null;
		try{
			//songs = FileLineReader.readLineArray(songsFile,"UTF-8");
			artists = FileLineReader.readLineArray(artistsFile,"UTF-8");
			releases = FileLineReader.readLineArray(releasesFile,"UTF-8");
		}catch(IOException ioe){
			System.out.println("ERROR: problem reading dictionary files on loadDictionary");
		}
		
		//*** ASSUMING TAB SEPARATED FILES AND THE NAMES ARE LOCATED IN THE SECOND VALUE ON THE FILES
		
		//add all the song titles to the dictionary
		if(songs != null){
			for(String song : songs){
				//int f = song.indexOf("\t");
				//int i = song.indexOf("\t", f);
	            //if (i < 0) continue;
	            //String title = song.substring(f+1,i);
				String title = song;
				dictionaryExact.addEntry(new DictionaryEntry<String>(title,"SONG",CHUNK_SCORE));
				dictionaryApprox.addEntry(new DictionaryEntry<String>(title,"SONG"));
			}
		}
		
		//add all the artist names to the dictionary
		if(artists != null){
			for(String artist : artists){
				//int f = artist.indexOf("\t");
				//int i = artist.indexOf("\t", f);
	            //if (i < 0) continue;
	            //String name = artist.substring(f+1,i);
				String name = artist;
				dictionaryExact.addEntry(new DictionaryEntry<String>(name,"ARTIST",CHUNK_SCORE));
				dictionaryApprox.addEntry(new DictionaryEntry<String>(name,"ARTIST"));
			}
		}
		
		//add all the release names to the dictionary
		if(releases != null){
			for(String release : releases){
				//int f = release.indexOf("\t");
				//int i = release.indexOf("\t", f);
	            //if (i < 0) continue;
	            //String name = release.substring(f+1,i);
				String name = release;
				dictionaryExact.addEntry(new DictionaryEntry<String>(name,"RELEASE",CHUNK_SCORE));
				dictionaryApprox.addEntry(new DictionaryEntry<String>(name,"RELEASE"));
			}
		}
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
		    	dictionaryExact.addEntry(new DictionaryEntry<String>(val,"ARTIST",CHUNK_SCORE));
				dictionaryApprox.addEntry(new DictionaryEntry<String>(val,"ARTIST"));
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
		    	dictionaryExact.addEntry(new DictionaryEntry<String>(val,"RELEASE",CHUNK_SCORE));
				dictionaryApprox.addEntry(new DictionaryEntry<String>(val,"RELEASE"));
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
	
	public static void main(String args[]){
		String q = "Pearl Jam is an artist, Queen is an artist as well, Crystal Starr Knighton is another artist, \"One Night At The Opra\" is a release but \"Bohemian Rhapsody\" is a song";
		NER n = new NER();
		ArrayList<Annotation> nes = n.annotateQuery(q);
		Iterator<Annotation> it = nes.iterator();
		System.out.println("Query: "+q);
		while(it.hasNext()){
			Annotation a = it.next();
			System.out.println(a.getEntityValue() + "\t" + a.getEntityType());
		}
	}

}
