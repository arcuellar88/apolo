package apolo.queryrefinement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.spell.CompiledSpellChecker;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.TrainSpellChecker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Files;
import com.aliasi.util.Streams;

public class SpellingCorrection {
	
	NGramProcessLM lm;
    TokenizerFactory tokenizerFactory;
    TrainSpellChecker sc;
    CompiledSpellChecker checker;
    private static final int NGRAM_LENGTH = 5;
    static final double MATCH_WEIGHT = -0.0;
    static final double DELETE_WEIGHT = -4.0;
    static final double INSERT_WEIGHT = -1.0;
    static final double SUBSTITUTE_WEIGHT = -2.0;
    static final double TRANSPOSE_WEIGHT = -2.0;
    static final int MAX_HITS = 100;
    FixedWeightEditDistance fixedEdit;
    private String songsFile;
	private String artistsFile;
	private String releasesFile;
	private String modelFile;
    //private static final File MODEL_FILE = new File("SpellCheck.model");
    //private static final File songsFile = new File("songs.txt");
	//private static final File artistsFile = new File("artists.txt");
	//private static final File releasesFile = new File("releases.txt");
    
    public SpellingCorrection(){
    	lm = new NGramProcessLM(NGRAM_LENGTH);
    	fixedEdit = new FixedWeightEditDistance(MATCH_WEIGHT, DELETE_WEIGHT, INSERT_WEIGHT, SUBSTITUTE_WEIGHT,TRANSPOSE_WEIGHT);
    	tokenizerFactory = new com.aliasi.tokenizer.LowerCaseTokenizerFactory(IndoEuropeanTokenizerFactory.INSTANCE);
    	sc = new TrainSpellChecker(lm,fixedEdit,tokenizerFactory);
    	String path = System.getProperty("user.dir");
		String fullpath = path + File.separator + "src" + File.separator + "java" + File.separator + "apolo" + File.separator + "queryrefinement" + File.separator;
		this.songsFile = fullpath + "songs.txt";
		this.artistsFile = fullpath + "artists.txt";
		this.releasesFile = fullpath + "releases.txt";
		this.modelFile = fullpath + "SpellCheck.model";
		File MODEL_FILE = new File(this.modelFile);
    	//trainModel();
    	//writeModel(sc,MODEL_FILE);
    	checker = readModel(MODEL_FILE);
    	checker.setTokenizerFactory(tokenizerFactory);
    }
    
    private void trainModel(){
    	try{
    		File songsFile = new File(this.songsFile);
    		File artistsFile = new File(this.artistsFile);
    		File releasesFile = new File(this.releasesFile);
	    	String charSequence = "";
	    	//charSequence = Files.readFromFile(songsFile,"UTF-8");
	    	//sc.handle(charSequence);
	    	charSequence = Files.readFromFile(artistsFile,"UTF-8");
	    	sc.handle(charSequence);
	    	charSequence = Files.readFromFile(releasesFile,"UTF-8");
	    	sc.handle(charSequence);
	    	sc.train("artist", 500);
	    	sc.train("release", 500);
	    	sc.train("mode", 500);
	    	sc.train("tempo", 500);
	    	sc.train("loudness", 500);
	    	sc.train("key", 500);
	    	sc.train("duration", 500);
	    	sc.train("energy", 500);
    	}catch(Exception e){
    		System.out.println("Error opening files when training spelling correction model");
    	}
    	
    }
    
    private void writeModel(TrainSpellChecker sc, File MODEL_FILE){
    	try{

            // create object output stream from file
            FileOutputStream fileOut = new FileOutputStream(MODEL_FILE);
            BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
            ObjectOutputStream objOut = new ObjectOutputStream(bufOut);

            // write the spell checker to the file
            sc.compileTo(objOut);

            // close the resources
            Streams.closeQuietly(objOut);
            Streams.closeQuietly(bufOut);
            Streams.closeQuietly(fileOut);
        }catch(IOException e){
        	System.out.println("Error writing the spelling correction model to the file");
        }
    }
    
    private CompiledSpellChecker readModel(File file){
    	CompiledSpellChecker sc = null;
    	try{

            // create object input stream from file
            FileInputStream fileIn = new FileInputStream(file);
            BufferedInputStream bufIn = new BufferedInputStream(fileIn);
            ObjectInputStream objIn = new ObjectInputStream(bufIn);

            // read the spell checker
            sc = (CompiledSpellChecker) objIn.readObject();

            // close the resources and return result
            Streams.closeQuietly(objIn);
            Streams.closeQuietly(bufIn);
            Streams.closeQuietly(fileIn);
        }catch(Exception e){
        	System.out.println("Error when reading the spelling correction model file");
        }
    	return sc;
    }
    
    public String getSpellingSuggestions(String q){
    	String bestAlternative = checker.didYouMean(q);
    	//Iterator<ScoredObject<String>> so = checker.didYouMeanNBest(q);
    	
    	if (bestAlternative.equals(q)) {
            System.out.println("No spelling correction found.");
            bestAlternative = "";
        }
    	return bestAlternative;
    }
    /*
    public static void main(String a[]){
    	SpellingCorrection s = new SpellingCorrection();
    	String q = "Pearl Jam is an amrican artis, and some features are tmpo, mode, Queen is a brtish bnd and Cristal Starr Knighton";
    	String q2 = "Cristal Star Kighton sngs";
    	String sugg = s.getSpellingSuggestions(q);
    	System.out.println(sugg);
    	sugg = s.getSpellingSuggestions(q);
    	System.out.println(sugg);
    }*/

}
