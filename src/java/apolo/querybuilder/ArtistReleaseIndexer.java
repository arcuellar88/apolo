package apolo.querybuilder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

//import apolo.querybuilder.ArtistReleaseDataPreparation;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import apolo.msc.Global_Configuration;

public class ArtistReleaseIndexer {

	Analyzer analyzer;
	Directory indexDir;
	IndexWriter indexWriter = null;
	ArtistReleaseDataPreparation dp = null;

	/** Creates a new instance of Indexer */
	public ArtistReleaseIndexer(Directory indexDir, Analyzer analyzer) {
		this.analyzer = analyzer;
		this.indexDir = indexDir;
		dp = new ArtistReleaseDataPreparation();
	}

	/** Gets index writer */
	public IndexWriter getIndexWriter() throws IOException {
		if (indexWriter == null) {
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46,
					analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			config.setRAMBufferSizeMB(256.0);
			indexWriter = new IndexWriter(indexDir, config);
		}
		return indexWriter;
	}

	/** Closes index writer */
	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	/** Deletes all documents in the index */
	public void deleteIndex() throws IOException {
		if (indexWriter != null) {
			indexWriter.deleteAll();
		}
	}

	/** Adds a document (a artist) to the index */

	public void indexArtist(HashMap<String, String> artist) throws IOException {

		IndexWriter writer = getIndexWriter();
		Document doc = new Document();

		doc.add(new TextField("type", "artist", Field.Store.YES));
		doc.add(new TextField("timestamp", DateTools.dateToString(Calendar
				.getInstance().getTime(), Resolution.SECOND), Field.Store.YES));
		doc.add(new TextField("documentID", "artist_" + artist.get("artistID"),
				Field.Store.YES));

		doc.add(new TextField("artistID", artist.get("artistID"),
				Field.Store.YES));
		doc.add(new TextField("artistName", artist.get("artistName"),
				Field.Store.YES));
		doc.add(new TextField("artistGender", artist.get("artistGender"),
				Field.Store.YES));
		doc.add(new TextField("artistType", artist.get("artistType"),
				Field.Store.YES));
		doc.add(new TextField("artistCountry", artist.get("artistCountry"),
				Field.Store.YES));
		doc.add(new TextField("artistContinent", artist.get("artistContinent"),
				Field.Store.YES));

		if (!artist.get("artistRatingAVG").equals(""))
			doc.add(new DoubleField("artistRating", Double.parseDouble(artist.get("artistRatingAVG").replace(",", ".")), Field.Store.YES));
		else
			doc.add(new DoubleField("artistRating", Integer.MAX_VALUE, Field.Store.YES));

		if (!artist.get("artistRatingCount").equals(""))
			doc.add(new IntField("artistNumberOfRating", Integer.parseInt(artist.get("artistRatingCount")), Field.Store.YES));
		else
			doc.add(new IntField("artistNumberOfRating", Integer.MAX_VALUE, Field.Store.YES));

		if (!artist.get("artistP").equals(""))
			doc.add(new IntField("artistNumberOfPositiveRating", Integer.parseInt(artist.get("artistP")), Field.Store.YES));
		else
			doc.add(new IntField("artistNumberOfPositiveRating", Integer.MAX_VALUE, Field.Store.YES));

		if (!artist.get("artistNU").equals(""))
			doc.add(new IntField("artistNumberOfNeutralRating", Integer.parseInt(artist.get("artistNU")), Field.Store.YES));
		else
			doc.add(new IntField("artistNumberOfNeutralRating", Integer.MAX_VALUE, Field.Store.YES));

		if (!artist.get("artistN").equals(""))
			doc.add(new IntField("artistNumberOfNegativeRating", Integer.parseInt(artist.get("artistN")), Field.Store.YES));
		else
			doc.add(new IntField("artistNumberOfNegativeRating", Integer.MAX_VALUE, Field.Store.YES));

		if (!artist.get("artistMBID").equals(""))
			doc.add(new IntField("artistMBID", Integer.parseInt(artist.get("artistMBID")), Field.Store.YES));
		else
			doc.add(new IntField("artistMBID", Integer.MAX_VALUE, Field.Store.YES));

		writer.addDocument(doc);

	}

	/** Adds a document (a release) to the index */

	public void indexRelease(HashMap<String, String> release)
			throws IOException {

		IndexWriter writer = getIndexWriter();
		Document doc = new Document();

		// System.out.println(release.get("releaseID"));
		// System.out.println(release.get("releaseName"));

		doc.add(new TextField("type", "release", Field.Store.YES));
		doc.add(new TextField("timestamp", DateTools.dateToString(Calendar.getInstance().getTime(), Resolution.SECOND), Field.Store.YES));
		doc.add(new TextField("documentID", "release_" + release.get("releaseID"), Field.Store.YES));

		doc.add(new TextField("releaseID", release.get("releaseID"), Field.Store.YES));
		doc.add(new TextField("releaseName", release.get("releaseName"), Field.Store.YES));
		doc.add(new TextField("releaseType", release.get("releaseType"), Field.Store.YES));

		if (release.get("releaseMBID") != null)
			doc.add(new IntField("releaseMBID", Integer.parseInt(release.get("releaseMBID")), Field.Store.YES));
		else
			doc.add(new DoubleField("releaseMBID", Integer.MAX_VALUE,Field.Store.YES));

		if (release.get("releaseArtist") != null)
			doc.add(new TextField("releaseArtist", release.get("releaseArtist"), Field.Store.YES));
		else
			doc.add(new TextField("releaseArtist", "", Field.Store.YES));

		if (release.get("releaseArtistID") != null)
			doc.add(new TextField("releaseArtistID", release.get("releaseArtistID"), Field.Store.YES));
		else
			doc.add(new TextField("releaseArtistID", "", Field.Store.YES));

		if (release.get("releaseSong") != null && !release.get("releaseSong").equals(""))
			doc.add(new TextField("releaseSongs", Global_Configuration.INDEX_SEPARATOR + release.get("releaseSong") + Global_Configuration.INDEX_SEPARATOR,	Field.Store.YES));
		else
			doc.add(new TextField("releaseSongs", "", Field.Store.YES));

		if (release.get("releaseSongID") != null && !release.get("releaseSongID").equals(""))
			doc.add(new TextField("releaseSongIDs", Global_Configuration.INDEX_SEPARATOR + release.get("releaseSongID") + Global_Configuration.INDEX_SEPARATOR, Field.Store.YES));
		else
			doc.add(new TextField("releaseSongIDs", "", Field.Store.YES));

		writer.addDocument(doc);

	}

	/** Rebuilds index */
	public int rebuildIndexArtist() throws IOException {
		// Index
		dp.readArtist();
		int counter = 0;
		for (HashMap<String, String> artist : dp.artistList) {
			indexArtist(artist);
			counter++;
		}
		return counter;
	}

	/** Rebuilds index */
	public int rebuildIndexRelease() throws IOException {
		// Index
		dp.readRelease();

		int counter = 0;
		for (HashMap<String, String> release : dp.releaseList) {
			indexRelease(release);
			counter++;
		}
		return counter;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		try {

			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

			String indexDirectory = "index";
			if (args.length > 0) {
				indexDirectory = args[0];
			}

			Directory indexFolder = FSDirectory.open(new File(indexDirectory));

			ArtistReleaseIndexer indexer = new ArtistReleaseIndexer(indexFolder, analyzer);

			long start = System.currentTimeMillis();
			int numberOfRelease = indexer.rebuildIndexRelease();
			System.out.println(numberOfRelease + " releases indexed in: " + ((System.currentTimeMillis() - start) * 1.0 / 1000.0) + " seconds");

			start = System.currentTimeMillis();
			int numberOfArtist = indexer.rebuildIndexArtist();
			System.out.println(numberOfArtist + " artists indexed in: " + ((System.currentTimeMillis() - start) * 1.0/1000.0) + " seconds");

			indexer.closeIndexWriter();

		} catch (Exception e) {
			System.out.println("Exception caught.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}