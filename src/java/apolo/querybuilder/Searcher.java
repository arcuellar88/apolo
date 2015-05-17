package apolo.querybuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import apolo.entity.ApoloDocument;

public class Searcher implements ISearcher {
	private final int maxInteger = 20;
	private final int maxTopDocument = 1<<20;
	private String indexDirectory;
	private Analyzer analyzer;
	private IndexSearcher indexSearcher;
	private BooleanQuery booleanQuery;
	private ArrayList<ScoreDoc> scoreDocs;
	private int totalResult = 0;
	private int page = 1;
	private int resultPerPage = maxInteger;
	
	/**
	 * Constructor
	 * @param indexDirectory the directory where indexed data is stored
	 * @throws IOException
	 */
	
	public Searcher(String indexDirectory) throws IOException {
		this.indexDirectory = indexDirectory;
		this.analyzer = new StandardAnalyzer(Version.LUCENE_46);
		try {
			this.indexSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(this.indexDirectory))));
		} catch (IndexNotFoundException e) {
			//e.printStackTrace();
		}
		resetParameters();
	}
	
	/**
	 * Reset each parameter to its default value
	 */
	
	public void resetParameters() {
		scoreDocs = null;
		booleanQuery = new BooleanQuery();
	}
	
	/**
	 * Add query boosting score
	 * @param keyword
	 * @param searchField
	 * @param occur
	 * @param boostingScore
	 * @throws Exception
	 */
	
	public void addQuery(String keyword , String searchField, Occur occur, float boostingScore) throws Exception {
		String escapeKeyword =  QueryParser.escape(keyword);
		Query query = new QueryParser(Version.LUCENE_46, searchField, analyzer).parse(escapeKeyword.toLowerCase());
		query.setBoost(boostingScore);
		booleanQuery.add(query, occur);
	}
	
	/**
	 * Add a particular query to the current query
	 * @param keyword
	 * @param searchField
	 * @param occur
	 * @throws ParseException
	 */
	
	public void addQuery(String keyword , String searchField, Occur occur) throws Exception {
		String escapeKeyword =  QueryParser.escape(keyword);
		Query query = new QueryParser(Version.LUCENE_46, searchField, analyzer).parse(escapeKeyword.toLowerCase());
		booleanQuery.add(query, occur);
	}
	
	public int getPage() {
		return page;
	}

	/**
	 * Add a query to search multiple keywords
	 * @param keywords
	 * @param searchField
	 * @param occur
	 * @throws ParseException
	 */
	
	public void addQuery(ArrayList<String> keywords , String searchField, Occur occur) throws Exception {
		for (String keyword : keywords) {
			addQuery(keyword, searchField, occur);
		}
	}
	
	/**
	 * Reset the boolean query. After resetting, the booleanQuery contains no query
	 */
	
	public void resetQuery() {
		page = 1;
		resultPerPage = Integer.MAX_VALUE;
		totalResult = 0;
		booleanQuery = new BooleanQuery();
		if (scoreDocs != null) {
			scoreDocs.clear();
		}
	}
	
	/**
	 * Execute query
	 * @throws IOException
	 * @throws ParseException 
	 */
	
	public void execute() throws IOException, ParseException {
		ScoreDoc[] hits;
		
		if (page == 1 && resultPerPage == maxInteger) {
			//If no pagination is requested
			hits = indexSearcher.search(booleanQuery, null , Integer.MAX_VALUE).scoreDocs;
			totalResult = hits.length;
		}
		else {
			//If pagination is requested
			TopScoreDocCollector collector = TopScoreDocCollector.create(maxTopDocument, true);
			indexSearcher.search(booleanQuery, collector);
			int startIndex = (page - 1) * resultPerPage;
			if (booleanQuery == null) {
				System.out.println("NULL BOOLEAN QUERY");
			}
			if (collector == null) {
				System.out.println("NULL COLLECTOR");
			}
			hits = collector.topDocs(startIndex, resultPerPage).scoreDocs;
			//Never this code to get total hit before collector.topDocs().scoreDocs. It will resulted in empty hits 
			totalResult = collector.topDocs().totalHits;
		}
		
		scoreDocs = new ArrayList<ScoreDoc>(Arrays.asList(hits));
	}

	/**
	 * Get result
	 * @return
	 * @throws IOException
	 */
	
	public ArrayList<ApoloDocument> getResults() throws IOException {
		ArrayList<ApoloDocument> documents = new ArrayList<ApoloDocument>();
		
		if (scoreDocs != null) {
			for(ScoreDoc hit : scoreDocs) {
				//Create customized document
				Document hitDoc = indexSearcher.doc(hit.doc);
				ApoloDocument document = getApoloDocument(hitDoc);
				document.setLuceneScore(hit.score);
				documents.add(document);
			}
		}
		
		return documents;
	}
	
	/**
	 * Convert lucene document to song
	 * @param doc
	 * @return
	 */
	
	public ApoloDocument getSongDocument(Document doc) {
		ApoloDocument song = new ApoloDocument();
		
		//For song
		song.setSongID(doc.get("songID"));
		song.setSongEchonestID(doc.get("songEchonestID"));
		song.setSongMBID(doc.get("songMBID"));
		song.setSongTitle(doc.get("songTitle"));
		song.setSongDuration(Double.parseDouble(doc.get("songDuration")));
		song.setSongTempo(Double.parseDouble(doc.get("songTempo")));
		song.setSongLoudness(Double.parseDouble(doc.get("songLoudness")));
		song.setSongEnergy(Double.parseDouble(doc.get("songEnergy")));
		song.setSongHotness(Double.parseDouble(doc.get("songHotness")));
		song.setSongDancebility(Double.parseDouble(doc.get("songDancebility")));
		
		//Rating
		song.setSongRating(Double.parseDouble(doc.get("songRating")));
		if (song.getSongRating() <= 100 && song.getSongRating() >= 80) {
			song.setSongRatingType("Positve");
		} 
		else if (song.getSongRating() >= 40) {
			song.setSongRatingType("Neutral");
		}
		else {
			song.setSongRatingType("Negative");
		}
		
		song.setSongNumberOfRating(Integer.parseInt(doc.get("songNumberOfRating")));
		
		//Song country
		song.setSongSimilarSongIds(doc.get("songSimilarSongIds"));
		song.setSongCountries(doc.get("songCountries"));
		song.setSongContinents(doc.get("songContinents"));
		
		//Genre
		song.setSongGenres(doc.get("songGenres"));
		song.setSongSubGenres(doc.get("songSubGenres"));
		
		//Label
		song.setSongLabel(doc.get("songLabel"));
		
		//Song date
		song.setSongDate(doc.get("songDate"));
		song.setSongMonth(Integer.parseInt(doc.get("songMonth")));
		song.setSongYear(Integer.parseInt(doc.get("songYear")));
		song.setSongDayOfWeek(doc.get("songDayOfWeek"));
		song.setSongQuarter(doc.get("songQuarter"));
		song.setSongDecade(doc.get("songDecade"));
		
		//Song release
		song.setSongReleaseID(doc.get("songReleaseID"));
		song.setSongReleaseName(doc.get("songReleaseName"));
		song.setSongReleaseType(doc.get("songReleaseType"));
		song.setSongReleaseMBID(doc.get("songReleaseMBID"));
		
		//Song artist
		song.setSongArtists(doc.get("songArtists"));
		song.setSongArtistsID(doc.get("songArtistsID"));
		
		//How to index lyrics
		song.setSongLyrics(doc.get("songLyrics"));
		
		//Should we include the playcount here?
		/*
		if (doc.get("songTotalPlayCount") != null) {
			song.setSongTotalPlayCount(Integer.parseInt(doc.get("songTotalPlayCount")));
		}
		else {
			song.setSongTotalPlayCount(Integer.MAX_VALUE);
		}
		*/
		
		return song;
	}
	
	/**
	 * Convert lucene document to release document
	 * @param doc
	 * @return
	 */
	
	public ApoloDocument getReleaseDocument(Document doc) {
		ApoloDocument release = new ApoloDocument();
		
		release.setReleaseID(doc.get("releaseID"));
		release.setReleaseName(doc.get("releaseName"));
		release.setReleaseType(doc.get("releaseType"));
		release.setReleaseSongs(doc.get("releaseSongs"));
		release.setReleaseSongIDs(doc.get("releaseSongIDs"));
		release.setReleaseArtist(doc.get("releaseArtist"));
		release.setReleaseArtistID(doc.get("releaseArtistID"));
		
		return release;
	}
	
	/**
	 * Convert lucene document to artist document
	 * @param doc
	 * @return
	 */
	
	public ApoloDocument getArtistDocument(Document doc) {
		ApoloDocument artist = new ApoloDocument();
		
		artist.setArtistID(doc.get("artistID"));
		artist.setArtistMBID(doc.get("artistMBID"));
		artist.setArtistName(doc.get("artistName"));
		artist.setArtistType(doc.get("artistType"));
		artist.setArtistGender(doc.get("artistGender"));
		artist.setArtistCountry(doc.get("artistCountry"));
		artist.setArtistContinent(doc.get("artistContinent"));
		Double artistRating = Double.parseDouble(doc.get("artistRating"));
		
		if (artistRating != null) {
			artist.setArtistRating(artistRating);
		}
		else {
			artist.setArtistRating(Integer.MAX_VALUE);
		}
		
		artist.setArtistRatingType(doc.get("artistRatingType"));
		
		if (artist.getArtistRating() <= 100 && artist.getArtistRating() >= 80) {
			artist.setArtistRatingType("Positve");
		} 
		else if (artist.getArtistRating() >= 40) {
			artist.setArtistRatingType("Neutral");
		}
		else {
			artist.setArtistRatingType("Negative");
		}
		
		Integer artistNumberOfRating = Integer.parseInt(doc.get("artistNumberOfRating"));
		if (artistNumberOfRating != null) {
			artist.setArtistNumberOfRating(artistNumberOfRating);
		}
		else {
			artist.setArtistNumberOfRating(Integer.MAX_VALUE);
		}
		
		Integer artistNumberOfPositiveRating = Integer.parseInt(doc.get("artistNumberOfPositiveRating"));
		if (artistNumberOfPositiveRating != null) {
			artist.setArtistNumberOfPositiveRating(artistNumberOfPositiveRating);
		}
		else {
			artist.setArtistNumberOfPositiveRating(Integer.MAX_VALUE);
		}
		
		Integer artistNumberOfNegativeRating = Integer.parseInt(doc.get("artistNumberOfNegativeRating"));
		if (artistNumberOfNegativeRating != null) {
			artist.setArtistNumberOfNegativeRating(artistNumberOfNegativeRating);
		}
		else {
			artist.setArtistNumberOfNegativeRating(Integer.MAX_VALUE);
		}
		
		Integer artistNumberOfNeutralRating = Integer.parseInt(doc.get("artistNumberOfNeutralRating"));
		if (artistNumberOfNeutralRating != null) {
			artist.setArtistNumberOfNeutralRating(artistNumberOfNeutralRating);
		}
		else {
			artist.setArtistNumberOfNeutralRating(Integer.MAX_VALUE);
		}
		
		return artist;
	}
	
	/**
	 * Convert a lucene document into apolo document
	 * @param hitDoc
	 * @return
	 */
	
	public ApoloDocument getApoloDocument(Document hitDoc) {
		ApoloDocument document = null;
		
		if (hitDoc.get("type").equalsIgnoreCase("song")) {
			//Song
			document = getSongDocument(hitDoc);
		} 
		else if (hitDoc.get("type").equalsIgnoreCase("release")) {
			//Release
			document = getReleaseDocument(hitDoc);
		}
		else if (hitDoc.get("type").equalsIgnoreCase("artist")) {
			//Release
			document = getArtistDocument(hitDoc);
		}
		
		document.setDocumentID(hitDoc.get("documentID"));
		document.setTimestamp(hitDoc.get("timestamp"));
		document.setType(hitDoc.get("type"));
		
		return document;
	}

	public String getIndexDirectory() {
		return indexDirectory;
	}

	public void setIndexDirectory(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public IndexSearcher getIndexSearcher() {
		return indexSearcher;
	}

	public void setIndexSearcher(IndexSearcher indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	public BooleanQuery getBooleanQuery() {
		return booleanQuery;
	}

	public void setBooleanQuery(BooleanQuery booleanQuery) {
		this.booleanQuery = booleanQuery;
	}

	public ArrayList<ScoreDoc> getScoreDocs() {
		return scoreDocs;
	}

	public void setScoreDocs(ArrayList<ScoreDoc> scoreDocs) {
		this.scoreDocs = scoreDocs;
	}

	public int getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public int getMaxInteger() {
		return maxInteger;
	}

	public int getMaxTopDocument() {
		return maxTopDocument;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
