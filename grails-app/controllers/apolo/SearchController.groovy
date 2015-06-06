package apolo

import java.io.File;

import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.BooleanClause.Occur;

import apolo.api.DBPediaHTTPXML;
import apolo.api.DPPediaHTTPSPARQL
import apolo.api.IDBpedia;
import apolo.api.MoomashAPI
import apolo.api.YouTubeAPI
import apolo.entity.ApoloDocument
import apolo.entity.Artist
import apolo.entity.IArtist
import apolo.entity.IRelease
import apolo.entity.ISong
import apolo.entity.Ranking
import apolo.entity.Release
import apolo.entity.Song
import apolo.msc.Global_Configuration;
import apolo.querybuilder.Searcher
import apolo.queryrefinement.Annotation;
import apolo.queryrefinement.Autocomplete
import apolo.queryrefinement.NER
import apolo.queryrefinement.SpellingCorrection
import apolo.recommender.Recommender
import grails.converters.JSON
import groovy.json.JsonSlurper

import org.apache.commons.codec.binary.Base64;

class SearchController extends BaseController {

    def index() { 		
		
		def isSearching = false
		def model = [:]
		
		String query = ""
		if (params.keyword != null) {
			query = params.keyword.trim()
		}
		
		String spellingCorrectedString = ""
		
		long start = System.currentTimeMillis()
		
		if (!query.equals("")) {
			model.searchingForArtist = false;
			//init NER module
			NER ner = servletContext.getAttribute("ner");
			SpellingCorrection spellChecker = servletContext.getAttribute("spellchecker")
			
			println "\n\nKEYWORD: " + query
			//Get spelling correction
			spellingCorrectedString = spellChecker.getSpellingSuggestions(query)
			println "SPELLING CORRECTION: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			if (spellingCorrectedString.equalsIgnoreCase(query)) {
				spellingCorrectedString = ""
			}
			
			isSearching = true
			
			IndexSearcher indexSearcher = servletContext.getAttribute("indexSearcher")
			
			//init searcher for song
			Searcher songSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
			songSearcher.addQuery("song", "type", Occur.MUST)
			songSearcher.setPage(1)
			songSearcher.setResultPerPage(10)
			
			//Searcher for release
			Searcher releaseSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
			releaseSearcher.addQuery("release", "type", Occur.MUST)
			releaseSearcher.setPage(1)
			releaseSearcher.setResultPerPage(10)
			
			//Searcher for artist
			Searcher artistSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
			artistSearcher.addQuery("artist", "type", Occur.MUST)
			artistSearcher.setPage(1)
			artistSearcher.setResultPerPage(10)
			
			println "INIT SEARCHERS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			boolean existSongAnno = false
			boolean existReleaseAnno = false
			boolean existArtistAnno = false
			
			//Get annotation
			ArrayList<Annotation> annotations = ner.annotateQuery(query)
			for(Annotation anno : annotations) {
				if (anno.getEntityType().equalsIgnoreCase("SONG")) {
					//Main search
					songSearcher.addQuery(anno.getEntityValue(), "songTitle", Occur.SHOULD, (float)2)
					
					//Additional information
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseSongs", Occur.SHOULD)
					
					existSongAnno = true
				}
				else if (anno.getEntityType().equalsIgnoreCase("RELEASE")) {
					//Main search
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseName", Occur.SHOULD, (float)2)
					
					//Additional information
					songSearcher.addQuery(anno.getEntityValue(), "songReleaseName", Occur.SHOULD)
					
					existReleaseAnno = true
				}
				else if (anno.getEntityType().equalsIgnoreCase("ARTIST")) {
					//Main search
					artistSearcher.addQuery(anno.getEntityValue(), "artistName", Occur.SHOULD, (float)2)
					
					//Additional information
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseArtists", Occur.SHOULD)
					
					existArtistAnno = true
				}
			}
			
			println "BUILD QUERY ANNOTATIONS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			//Check if annotation for song/artist/release exist, if not we need to relax the query criteria
			if (!existSongAnno) {
				songSearcher.addQuery(query, "songTitle", Occur.SHOULD, (float)1.5)
				songSearcher.addQuery(query, "songLabels", Occur.SHOULD)
				songSearcher.addQuery(query, "songLyrics", Occur.SHOULD)
				songSearcher.addQuery(query, "songCountries", Occur.SHOULD)
				songSearcher.addQuery(query, "songContinents", Occur.SHOULD)
			}
			
			if (!existReleaseAnno) {
				releaseSearcher.addQuery(query, "releaseName", Occur.SHOULD, (float)1.5)
				releaseSearcher.addQuery(query, "releaseType", Occur.SHOULD)
				releaseSearcher.addQuery(query, "releaseSongs", Occur.SHOULD)
				releaseSearcher.addQuery(query, "releaseArtists", Occur.SHOULD)
			}
			
			if (!existArtistAnno) {
				artistSearcher.addQuery(query, "artistName", Occur.SHOULD, (float)1.5)
				artistSearcher.addQuery(query, "artistType", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistGender", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistCountry", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistContinent", Occur.SHOULD)
			}
			
			println "BUILD QUERY NO ANNOTATIONS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			//Execute query and get result
			songSearcher.execute();
			ArrayList<ApoloDocument> songs = songSearcher.getResults()
			model.songs = songs
			
			println "RETRIEVE SONGS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			if (songs.size() > 0) {
				String youtubeURL = getYouTubeURL(songs.get(0))
				songs.get(0).setSongYoutubeURL(youtubeURL)
			}
			
			println "YOUTUBE API: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			releaseSearcher.execute();
			ArrayList<ApoloDocument> releases = releaseSearcher.getResults()
			model.releases = releases
			
			println "RETRIEVE RELEASES: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			artistSearcher.execute();
			ArrayList<ApoloDocument> artists = artistSearcher.getResults()
			model.artists = artists
			
			println "RETRIEVE ARTISTS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			//Get song of the first artist
			if (artists.size() > 0) {
				model.firstArtistSongs = getFirstArtistSongs(artists.get(0), 15)
				
				println "RETRIEVE SONGS FOR FIRST ARTIST: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
				start = System.currentTimeMillis()
				if (artists.get(0).getArtistName().trim().equalsIgnoreCase(query.trim())) {
					model.searchingForArtist = true;
				}
			}
			else {
				model.firstArtistSongs = new ArrayList<ApoloDocument>()
				model.recommendedArtists = new ArrayList<ApoloDocument>()
			}
			
			//Get additional information for  songs/releases/artists from DP pedia
			
			if (songs.size() > 0) {
				getDBPediaInfo(songs.get(0))
			}
			
			println "DBPEDIA SONG: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			if (releases.size() > 0) {
				getDBPediaInfo(releases.get(0))
			}
			
			println "DBPEDIA RELEASE: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			if (artists.size() > 0) {
				getDBPediaInfo(artists.get(0))
			} 
			
			println "DBPEDIA ARTIST: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
		}
		
		model.spellingCorrectedString = spellingCorrectedString
		model.isSearching = isSearching
		
		render (view : "search" , layout : "main" , model : model);
		
		println "RENDER VIEW: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
		start = System.currentTimeMillis()
	}
	
	def getEntity() {
		
		IndexSearcher indexSearcher = servletContext.getAttribute("indexSearcher")
		
		def model = [:]
		String data = ""
		String name = ""
		
		String entityID = params.entityID
		
		Searcher searcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
		searcher.setPage(1)
		searcher.setResultPerPage(10)
		searcher.addQuery(entityID.trim(), "documentID", Occur.MUST)
		
		searcher.execute()
		
		ApoloDocument entity = searcher.getResults().get(0)
		
		getDBPediaInfo(entity)
		print "TYPEEEEEEEEEEEE: " + entity.getType();
		if (entity.getType().equalsIgnoreCase("song")) {
			//Youtube link
			String youtubeURL = getYouTubeURL(entity)
			entity.setSongYoutubeURL(youtubeURL)
			
			name = entity.getSongTitle()
			model.song = entity
			data = g.render(template : "/template/first-song" , model : model);
		}
		else if (entity.getType().equalsIgnoreCase("artist")) {
			
			model.firstArtistSongs = getFirstArtistSongs(entity, 15)
			model.recommendedArtists = getRecommendation(entity)
			
			name = entity.getArtistName()
			model.artist = entity
			data = g.render(template : "/template/first-artist" , model : model);
			
		} 
		else if (entity.getType().equalsIgnoreCase("release")) {
			
			model.releaseSongs = entity.getSplittedFields(entity.releaseSongs);
			model.releaseSongIDs = entity.getSplittedFields(entity.releaseSongIDs);
			
			name = entity.getReleaseName()
			model.release = entity
			data = g.render(template : "/template/first-release" , model : model);
		}
		
		def json = [entityName : name, data : data]
		render json as JSON
	}
	
	/**
	 * Get all songs of an artist
	 * @return
	 */
	
	public getAllArtistSong() {
		String artistID = params.artistID
		model = [:]
		ApoloDocument artist = new ApoloDocument();
		artist.setArtistID(artistID);
		model.firstArtistSongs = getFirstArtistSongs(artist, 100000)
		data = g.render(template : "/template/_first-artist-songs" , model : model);
		def json = [data : data]
		render json as JSON
	}
	
	
	/**
	 * Get songs of first artist
	 * @param artist
	 * @return
	 */
	
	private ArrayList<ApoloDocument> getFirstArtistSongs(ApoloDocument artist, int limit) {
		
		IndexSearcher indexSearcher = servletContext.getAttribute("indexSearcher")
		
		Searcher searcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
		searcher.addQuery(artist.getArtistID(), "songArtistsID", Occur.MUST)
		searcher.addQuery("song", "type", Occur.MUST)
		searcher.setPage(1)
		searcher.setResultPerPage(limit)
		searcher.execute()
		
		Set<String> songTitles = new HashSet<String>();
		ArrayList<ApoloDocument> results = searcher.getResults()
		
		for(ApoloDocument adocument : results) {
			songTitles.add(adocument.getSongTitle());
		}
		
		ArrayList<ApoloDocument> finalResults = new ArrayList<ApoloDocument>();
		finalResults.addAll(songTitles);
		return finalResults;
	}
	
	/**
	 * Get artist recommender
	 * @param document
	 * @return
	 */
	
	private ArrayList<ApoloDocument> getRecommendation(ApoloDocument document) {
		ArrayList<ApoloDocument> recommendedItems = new ArrayList<ApoloDocument>()
		Recommender recommender = servletContext.getAttribute("recommender")
		
		IArtist artist = new Artist()
		artist.setArtist_id(Integer.parseInt(document.getArtistID()))
		
		Ranking rartists = recommender.getRecommendation(artist, 10)
		
		for(int i = 0 ; i < rartists.getItems().size(); i++) {
			Artist a = (Artist)rartists.getItems().get(i);
			ApoloDocument newItem = new ApoloDocument()
			newItem.setType("artist")
			newItem.setArtistID(a.getArtist_id() + "")
			newItem.setArtistName(a.getName())
			newItem.setDocumentID("artist_" + a.getArtist_id())
			recommendedItems.add(newItem)
		}
		
		return recommendedItems;
	}
	
	/**
	 * Search youtube for video
	 * @param song
	 * @return
	 */
	
	private String getYouTubeURL(ApoloDocument song) {
		String query = song.getSongTitle()
		
		//add artist if possible
		if (song.getSongTitle().length() <= 1<<20 && !song.getSongArtists().equals("")) {
			ArrayList<String> artistNames = song.getSplittedFields(song.getSongArtists())
			for(String artistName : artistNames) {
				query += " " + artistName
			}
		}
		
		YouTubeAPI youtube = new YouTubeAPI()
		String youtubeJSON = youtube.query(query)
		
		if (youtubeJSON == null || youtubeJSON.length() <= 0) {
			return ""
		} 
		
		def slurper = new JsonSlurper()
		def result = slurper.parseText(youtubeJSON)
		
		try {
		
		if (result.items.size() > 0) {
			String videoID = result.items[0].id.videoId
			return "https://www.youtube.com/embed/" + videoID
		}
		
		} catch (Exception e) {
			e.printStackTrace()
			return ""
		}
	}
	
	/**
	 * Get DBpedia 
	 * @param document
	 * @return
	 */
	
	private getDBPediaInfo(ApoloDocument document) {
		
		IDBpedia dbpediaClient = new DBPediaHTTPXML();
		IDBpedia dbpediaSPARQLClient = new DPPediaHTTPSPARQL();
		
		if (document.getType().equalsIgnoreCase("song")) {
			ISong isong = new Song()
			
			isong.setTitle(dbpediaSPARQLClient.capitalizeString(document.getSongTitle()))
			dbpediaSPARQLClient.getAdditionalInformationSong(isong)
			
			document.setIsong(isong)
		} else if (document.getType().equalsIgnoreCase("artist")) {
		
			//Artist
			IArtist iartist = new Artist()
			
			iartist.setGender(document.getArtistGender())
			iartist.setName(document.getArtistName())
			
			dbpediaSPARQLClient.getAdditionalInformationArtist(iartist)
			document.setIartist(iartist)
		
		} else if (document.getType().equalsIgnoreCase("release")) {
			//Release
			IRelease irelease = new Release()
			irelease.setName(dbpediaSPARQLClient.capitalizeString(document.getReleaseName()))
			irelease.setType(document.getReleaseType())
			
			dbpediaSPARQLClient.getAdditionalInformationRelease(irelease)
			document.setIrelease(irelease)
		}
	}
	
	/**
	 * Get recommendation
	 * @return
	 */
	
	def getRecommendationArtist() {
		
		long start = System.currentTimeMillis()
		
		def model = [:]
		String artistID = params.artistID
		ApoloDocument document = new ApoloDocument()
		document.setArtistID(artistID)
		
		model.recommendedArtists = getRecommendation(document)
		
		println "RETRIEVE RECOMMENDATION FOR FIRST ARTIST: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
		start = System.currentTimeMillis()
		
		render(template : "/template/rartists" , model : model)
	}
	
	/**
	 * Get options for autocomplete
	 * @return
	 */
	
	def getSuggestion() {
		String query = params.keyword
		Autocomplete autoComplete = servletContext.getAttribute("autocomplete");
		
		ArrayList<String> suggestions = autoComplete.getCompletionsList(query)
		def result = [keyword : query , suggestions : suggestions]
		render result as JSON
	}
	
	/**
	 * Upload song recording to server
	 * @return
	 */
	
	def upload() {
		
		String songName = ""
		
		try {
			
			String data = params.data
			String name = session.id
			
			int secondRecorded = (int) (Double.parseDouble(params.secondRecorded))
			
			data = data.replace("data:audio/wav;base64,", "")
			data = data.replace(" ", "+")
			byte[] bytes = data.getBytes()
			byte[] valueDecoded = Base64.decodeBase64(bytes)
			
			def fileName = Global_Configuration.TEMP_FOLDER + "/" + name + ".wav"
			
			FileOutputStream os = new FileOutputStream(new File(fileName))
			os.write(valueDecoded)
			os.close();
			
			if (secondRecorded >= 30) {
				songName = getSongName(fileName, 30)
				println "SONG NAME 30: " + songName
			}
			
			if (songName == null && secondRecorded >= 20) {
				songName = getSongName(fileName, 20)
			}
			println "SONG NAME 20: " + getSongName(fileName, 20)
			
			if (songName == null && secondRecorded >= 15) {
				songName = getSongName(fileName, 15)		
			}
			println "SONG NAME 15: " + getSongName(fileName, 15)
			
			if (songName == null) {
				songName = ""				
			}
			
			File file  = new File(fileName)
			file.delete()
			
		} catch (Exception e) {
			println "Exception"
			e.printStackTrace();
		}
		
		def model = [songName : songName]
		render model as JSON
	}
	
	/**
	 * Detect song name by Moomash API
	 * @param fileName
	 * @param length
	 * @return
	 */
	
	def getSongName(fileName, length) {
		MoomashAPI mmapi = new MoomashAPI()
		def code = mmapi.getCode(fileName, length)
		if (code != null) {
			def songName = mmapi.query(code)
			if (songName != null) {
				return songName
			}
			else {
				return null
			}
		}
	}
}
