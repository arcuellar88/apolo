package apolo.queryrefinement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

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
		this.songsFile = "songs.txt";
		this.artistsFile = "artists.txt";
		this.releasesFile = "releases.txt";
		this.dictionary = new HashMap<String,Integer>(15000000);
		loadDictionary();
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
	
	private void loadDictionary(){
		File songsFile = new File(this.songsFile);
		File artistsFile = new File(this.artistsFile);
		File releasesFile = new File(this.releasesFile);
		String[] songs = null;
		String[] artists = null;
		String[] releases = null;
		try{
			songs = FileLineReader.readLineArray(songsFile,"ISO-8859-1");
			artists = FileLineReader.readLineArray(artistsFile,"ISO-8859-1");
			releases = FileLineReader.readLineArray(releasesFile,"ISO-8859-1");
		}catch(IOException ioe){
			System.out.println("ERROR: problem reading dictionary files on loadDictionary");
		}
		
		//add all the song titles to the dictionary
		for(String song : songs){
			int f = song.indexOf("\t");
			int i = song.indexOf("\t", f);
            if (i < 0) continue;
            String title = song.substring(f+1,i);
			dictionary.put(title, 0); //0 is for SONG
		}
		
		//add all the artist names to the dictionary
		for(String artist : artists){
			int f = artist.indexOf("\t");
			int i = artist.indexOf("\t", f);
            if (i < 0) continue;
            String name = artist.substring(f+1,i);
			dictionary.put(name, 1); //1 is for ARTIST
		}
		
		//add all the release names to the dictionary
		for(String release : releases){
			int f = release.indexOf("\t");
			int i = release.indexOf("\t", f);
            if (i < 0) continue;
            String name = release.substring(f+1,i);
			dictionary.put(name, 2); //2 is for RELEASE
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

}
