package apolo.queryrefinement;

import java.util.*;
import java.io.*;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.dict.TrieDictionary;
import com.aliasi.io.FileLineReader;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class NER {

	private String query; /** Query that needs to be enhanced */
	private ArrayList<String> filenames;
	private ArrayList<String> clases;
	//another variable could be the Lucene index if we use it to get the entities
	//MapDictionary<String> dictionaryExact; /** Dictionary that contains the entities that will be recognized */
	ExactDictionaryChunker dictionaryChunkerExact;
	private static final double CHUNK_SCORE = 1.0;
	
	public NER(){
		this.query="";
		String path = System.getProperty("user.dir");
		String fullpath = path + File.separator + "src" + File.separator + "java" + File.separator + "apolo" + File.separator + "queryrefinement" + File.separator;
		this.filenames = new ArrayList<String>();
		this.filenames.add(fullpath + "artists.txt");
		this.filenames.add(fullpath + "releases.txt");
		this.clases = new ArrayList<String>();
		//this.filenames.add(fullpath + "songs.txt");
		this.clases.add("ARTIST");
		this.clases.add("RELEASE");
		//this.clases.add("SONG");
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
		this.filenames = new ArrayList<String>();
		this.filenames.add(fullpath + "artists.txt");
		this.filenames.add(fullpath + "releases.txt");
		//this.filenames.add(fullpath + "songs.txt");
		this.clases.add("ARTIST");
		this.clases.add("RELEASE");
		//this.clases.add("SONG");
		loadDictionaryFromDB();
	}
	
	public ArrayList<Annotation> annotateQuery(){
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		annotations = findNamedEntities();
		return annotations;
	}
	
	public ArrayList<Annotation> annotateQuery(String q){
		this.query = q;
		System.out.println("Start query annotation");
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		annotations = findNamedEntities();
		return annotations;
	}
	
	private ArrayList<Annotation> findNamedEntities(){
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		//First look if there are any " in the query, to look for exact matches
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
			Iterator<String> it = exactQueryElems.iterator();
			while(it.hasNext()){
				String text = it.next();
				Chunking chunking = dictionaryChunkerExact.chunk(text);
				for (Chunk chunk : chunking.chunkSet()) {
					int start = chunk.start();
		            int end = chunk.end();
					String phrase = text.substring(start,end);
					//System.out.println(phrase);
		            if(phrase.equalsIgnoreCase(text)){
			            String type = chunk.type();
		            	annotations.add(new Annotation(phrase,type,start,end,0,phrase.length()));
		            }
				}
			}
		}
		if(!approxQueryElems.isEmpty()){
	        Iterator<String> it = approxQueryElems.iterator();
	        while(it.hasNext()){
	        	String text = it.next();
	        	Chunking chunking = dictionaryChunkerExact.chunk(text);
	        	for (Chunk chunk : chunking.chunkSet()) {
	        		if(addAnnotation(chunk, chunking, text)){
		        		int start = chunk.start();
		                int end = chunk.end();
		                String type = chunk.type();
		                double distance = chunk.score();
		                String phrase = chunking.charSequence().subSequence(start,end).toString();
		                //System.out.println(phrase);
		                if(phrase.length()>1){
		                	annotations.add(new Annotation(phrase,type,start,end,distance,phrase.length()));
		                }
	        		}
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
		
		MapDictionary<String> dictionaryExact = new MapDictionary<String>();
		//TrieDictionary<String>dictionaryApprox = new TrieDictionary<String>();
		
		ArrayList<File> files = new ArrayList<File>();
		for(int i=0; i<this.filenames.size(); i++){
			files.add(new File(this.filenames.get(i)));
		}
		String[] lines = null;
		try{
			for(int m=0; m<files.size(); m++){
				lines = FileLineReader.readLineArray(files.get(m),"UTF-8");
				if(lines != null){
					for(String elem : lines){
						dictionaryExact.addEntry(new DictionaryEntry<String>(elem,"ARTIST",CHUNK_SCORE));
						//dictionaryApprox.addEntry(new DictionaryEntry<String>(name,"ARTIST"));
					}
				}
			}
		}catch(IOException ioe){
			System.out.println("ERROR: problem reading dictionary files on loadDictionary");
		}
		
	}
	
	/***
	 * Loads the songs, artists and releases to the dictionary.
	 * It uses the existing DB table to obtain all songs, artists and release names.
	 */
	private void loadDictionaryFromDB(){
		MapDictionary<String> dictionaryExact = new MapDictionary<String>();
		//this.dictionaryExact = new MapDictionary<String>();
		//this.dictionaryApprox = new TrieDictionary<String>();
		
		ArrayList<String> queries = new ArrayList<String>();
		queries.add("SELECT title FROM artists_apolo");
		queries.add("SELECT title FROM releases_apolo");
		//queries.add("SELECT title FROM songs_apolo");
		
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		for(int i=0; i<queries.size(); i++){
			try {
			    stmt = conn.createStatement();
			    rs = stmt.executeQuery(queries.get(i));
			    while(rs.next()){
			    	String val = rs.getString("title");
			    	val = val.substring(0,val.length()-1);
			    	dictionaryExact.addEntry(new DictionaryEntry<String>(val,this.clases.get(i),CHUNK_SCORE));
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
		
		System.out.println("finished loading dictionary");
		this.dictionaryChunkerExact = new ExactDictionaryChunker(dictionaryExact, IndoEuropeanTokenizerFactory.INSTANCE, true,false);
		System.out.println("finished chunckers");
		try {
			conn.close();
		} catch (SQLException e) {
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
	
	private boolean addAnnotation(Chunk chunk1, Chunking chunking, String t){
		boolean add=true;
		String c1 = getString(chunk1);
		for (Chunk chunk2 : chunking.chunkSet()) {
			String c2 = getString(chunk2);
			if(c2.contains(c1) && !c2.equals(c1)){
				add=false;
			}
		}
		return add;
	}
	
	private String getString(Chunk chunk){
		int start = chunk.start();
        int end = chunk.end();
		return this.query.substring(start,end);
	}
	
	/*
	public static void main(String args[]){
		String q2 = "\"Pearl Jam\" is an artist, Queen is an artist as well, crystal starr knighton is another artist, \"Silver Spoons\" & \"Broken Bones\" are multiple entities but \"Bohemian Rhapsody\" is a song";
		String q = "Crystal Starr Knighton \"Silver Spoons & Broken Bones\"";
		NER n = new NER();
		//n.trainModel();
		ArrayList<Annotation> nes;
		nes = n.annotateQuery(q);
		Iterator<Annotation> it = nes.iterator();
		System.out.println("\nQuery: "+q);
		while(it.hasNext()){
			Annotation a = it.next();
			System.out.println(a.getEntityValue() + "\t" + a.getEntityType());
		}
		
		nes = n.annotateQuery(q2);
		Iterator<Annotation> it2 = nes.iterator();
		System.out.println("\nQuery: "+q2);
		while(it2.hasNext()){
			Annotation a = it2.next();
			System.out.println(a.getEntityValue() + "\t" + a.getEntityType());
		}
	}
	 */
}
