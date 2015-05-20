package apolo.queryrefinement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import apolo.msc.Global_Configuration;

import com.aliasi.io.FileLineReader;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.spell.CompiledSpellChecker;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.TrainSpellChecker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Files;
import com.aliasi.util.Streams;

public class SpellingCorrection {
	
    TrainSpellChecker sc;
    CompiledSpellChecker checker;
	private String modelFile;
	private ArrayList<String> filenames;
    //private static final File MODEL_FILE = new File("SpellCheck.model");
    //private static final File songsFile = new File("songs.txt");
	//private static final File artistsFile = new File("artists.txt");
	//private static final File releasesFile = new File("releases.txt");
    
    public SpellingCorrection(){
    	
    	int NGRAM_LENGTH = 5;
        double MATCH_WEIGHT = -0.0;
        double DELETE_WEIGHT = -4.0;
        double INSERT_WEIGHT = -1.0;
        double SUBSTITUTE_WEIGHT = -2.0;
        double TRANSPOSE_WEIGHT = -2.0;
        int MAX_HITS = 50;
    	NGramProcessLM lm = new NGramProcessLM(NGRAM_LENGTH);
    	FixedWeightEditDistance fixedEdit = new FixedWeightEditDistance(MATCH_WEIGHT, DELETE_WEIGHT, INSERT_WEIGHT, SUBSTITUTE_WEIGHT,TRANSPOSE_WEIGHT);
    	TokenizerFactory tokenizerFactory = new com.aliasi.tokenizer.LowerCaseTokenizerFactory(IndoEuropeanTokenizerFactory.INSTANCE);
    	//sc = new TrainSpellChecker(lm,fixedEdit,tokenizerFactory);
    	String path = System.getProperty("user.dir");
		//String fullpath = path + File.separator = + "src" + File.separator + "java" + File.separator + "apolo" + File.separator + "queryrefinement" + File.separator;
    	String fullpath = Global_Configuration.DATA_FOLDER + File.separator;
    	//this.filenames = new ArrayList<String>();
		//this.filenames.add(fullpath + "artists.txt");
		//this.filenames.add(fullpath + "releases.txt");
		//this.filenames.add(fullpath + "songs.txt");
    	//String fullpath = path + "/data/";
		this.modelFile = "SpellCheck.model";
		File MODEL_FILE = new File(fullpath + this.modelFile);
    	//trainModel();
    	//writeModel(sc,MODEL_FILE);
    	checker = readModel(MODEL_FILE);
    	checker.setTokenizerFactory(tokenizerFactory);
    }
    
    private void trainModel(){
    	try{    		
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
							sc.train(elem, 500);
							//sc.handle(elem);
						}
					}
				}
			}catch(IOException ioe){
				System.out.println("ERROR: problem reading dictionary files on training spell check model");
			}
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
    	System.out.println("Read spell checker model");
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
    
    public static void main(String a[]){
    	SpellingCorrection s = new SpellingCorrection();
    	String q = "Pearl Jam is an amrican artis, and some features are tmpo, mode, Queen is a brtish bnd and Cristal Starr Knighton";
    	String q2 = "Cristal Star Kighton sngs";
    	String sugg = s.getSpellingSuggestions(q);
    	System.out.println(sugg);
    	sugg = s.getSpellingSuggestions(q);
    	System.out.println(sugg);
    }

}
