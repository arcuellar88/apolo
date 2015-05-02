package apolo.queryrefinement;

import java.util.*;
import java.io.*;

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

public class NER {

	private String query; /** Query that needs to be enhanced */
	private String songsFile;
	private String artistsFile;
	private String releasesFile;
	//another variable could be the Lucene index if we use it to get the entities
	MapDictionary<String> dictionaryExact; /** Dictionary that contains the entities that will be recognized */
	TrieDictionary<String> dictionaryApprox;
	private static final double CHUNK_SCORE = 1.0;
	
	
	public NER(String query){
		this.query=query;
		this.songsFile = "songs.txt";
		this.artistsFile = "artists.txt";
		this.releasesFile = "releases.txt";
		dictionaryExact = new MapDictionary<String>();
		dictionaryApprox = new TrieDictionary<String>();
	}
	
	public ArrayList<Annotation> annotateQuery(){
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
	private void loadDictionary(){
		File songsFile = new File(this.songsFile);
		File artistsFile = new File(this.artistsFile);
		File releasesFile = new File(this.releasesFile);
		String[] songs = null;
		String[] artists = null;
		String[] releases = null;
		try{
			songs = FileLineReader.readLineArray(songsFile,"ISO-8859-1");
			artists = FileLineReader.readLineArray(songsFile,"ISO-8859-1");
			releases = FileLineReader.readLineArray(songsFile,"ISO-8859-1");
		}catch(IOException ioe){
			System.out.println("ERROR: problem reading dictionary files on loadDictionary");
		}
		
		//*** ASSUMING TAB SEPARATED FILES AND THE NAMES ARE LOCATED IN THE SECOND VALUE ON THE FILES
		
		//add all the song titles to the dictionary
		for(String song : songs){
			int f = song.indexOf("\t");
			int i = song.indexOf("\t", f);
            if (i < 0) continue;
            String title = song.substring(f+1,i);
			dictionaryExact.addEntry(new DictionaryEntry<String>(title,"SONG",CHUNK_SCORE));
			dictionaryApprox.addEntry(new DictionaryEntry<String>(title,"SONG"));
		}
		
		//add all the artist names to the dictionary
		for(String artist : artists){
			int f = artist.indexOf("\t");
			int i = artist.indexOf("\t", f);
            if (i < 0) continue;
            String name = artist.substring(f+1,i);
			dictionaryExact.addEntry(new DictionaryEntry<String>(name,"ARTIST",CHUNK_SCORE));
			dictionaryApprox.addEntry(new DictionaryEntry<String>(name,"ARTIST"));
		}
		
		//add all the release names to the dictionary
		for(String release : releases){
			int f = release.indexOf("\t");
			int i = release.indexOf("\t", f);
            if (i < 0) continue;
            String name = release.substring(f+1,i);
			dictionaryExact.addEntry(new DictionaryEntry<String>(name,"RELEASE",CHUNK_SCORE));
			dictionaryApprox.addEntry(new DictionaryEntry<String>(name,"RELEASE"));
		}
	}
	
	/***
	 * Loads the songs, artists and releases to the dictionary.
	 * It uses the existing Lucene index to obtain all songs, artists and release names.
	 */
	private void loadDictionaryFromLucene(){
		
	}

}
