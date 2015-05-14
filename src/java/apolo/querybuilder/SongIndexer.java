package apolo.querybuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * To index song from extracted flat files
 */

public class SongIndexer implements IIndexer {
	private IndexWriter writer;
	private IndexWriterConfig config;
	private String indexDirectory;
	private String separator = "\t";
	private String fieldValueSeparator = "\\[::FVS::\\]";
	
	/**
	 * Constructor
	 * @param indexDirectory
	 * @throws IOException
	 */
	
	SongIndexer(String indexDirectory) throws IOException {
		this.indexDirectory = indexDirectory;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		config.setRAMBufferSizeMB(256.0);
	}
	
	/**
	 * Index a file. Each line is a document. Each field and value is separated by delimiter
	 * @param fileName
	 * @throws IOException
	 */
	
	public int indexFile(String fileName) throws IOException {
		System.out.println("Processing: " + fileName);
		long start = System.currentTimeMillis();
		int counter = 0;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			counter++;
			Document document = new Document();
			String[] tmp = line.split(separator);
			for(int i = 0 ; i < tmp.length; i++) {
				
				String[] fv = tmp[i].split(fieldValueSeparator);
				String field = fv[0].trim();
				String value = "";
				if (fv.length > 1) {
					value = fv[1];
				}
				value = value.replaceAll("\\u00a0", " ").trim();
				
				//Check int field
				if (checkIntValueField(field)) {
					if (value.length() <= 0) {
						//value is empty => set to MAX_INT
						document.add(new IntField(field, Integer.MAX_VALUE, Field.Store.YES));
					}
					else {
						document.add(new IntField(field, Integer.parseInt(value), Field.Store.YES));	
					}
				}
				//Check double field
				else if (checkDoubleValueField(field)) {
					if (value.length() <= 0) {
						document.add(new DoubleField(field, Integer.MAX_VALUE, Field.Store.YES));
					}
					else {
						document.add(new DoubleField(field, Double.parseDouble(value), Field.Store.YES));
					}
				}
				//Rest is string field
				else {
					document.add(new TextField(field, value, Field.Store.YES));
				}
			}
			
			writer.addDocument(document);
		}
		br.close();
		System.out.println("Files indexed: " + counter + " in " + (System.currentTimeMillis() - start)  * 1.0 / 1000.0 + " seconds");
		return counter;
	}
	
	/**
	 * Index a folder. Read each file and index
	 * @param folder
	 * @throws IOException 
	 */
	
	public int indexFolder(String folderPath) throws IOException {
		writer = new IndexWriter(FSDirectory.open(new File(indexDirectory)), config);
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		int totalFiles = 0;
		if (listOfFiles != null) {
			for(int i = 0 ; i < listOfFiles.length; i++) {
				File indexFile = listOfFiles[i];
				totalFiles += indexFile(indexFile.getAbsolutePath());
			}
		}
		writer.close();
		return totalFiles;
	}
	
	/**
	 * Check if a field is double field
	 * @param field
	 * @return
	 */
	
	boolean checkDoubleValueField(String field) {
		if (field.toLowerCase().equalsIgnoreCase("songDuration")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songTempo")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songLoudness")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songEnergy")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songHotness")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songDancebility")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songRating")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check if a field is integer field
	 * @param field
	 * @return
	 */
	
	boolean checkIntValueField(String field) {
		if (field.toLowerCase().equalsIgnoreCase("songNumberOfRating")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songMonth")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songYear")) {
			return true;
		} else if (field.toLowerCase().equalsIgnoreCase("songTotalPlayCount")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Test index
	 * @param args
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		String indexFolder = "indexfiles";
		String indexDirectory = "index";
		
		if (args.length > 1) {
			indexFolder = args[0];
			indexDirectory = args[1];
		}
		
		SongIndexer si = new SongIndexer(indexDirectory);
		System.out.println("Total files: " + si.indexFolder(indexFolder));
	}
}
