package apolo

import java.io.File;
import java.util.ArrayList;

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
			songSearcher.addQuery(false, "song", "type", Occur.MUST)
			songSearcher.setPage(1)
			songSearcher.setResultPerPage(30)
			
			//Searcher for release
			Searcher releaseSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
			releaseSearcher.addQuery(false, "release", "type", Occur.MUST)
			releaseSearcher.setPage(1)
			releaseSearcher.setResultPerPage(15)
			
			//Searcher for artist
			Searcher artistSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY, indexSearcher)
			artistSearcher.addQuery(false, "artist", "type", Occur.MUST)
			artistSearcher.setPage(1)
			artistSearcher.setResultPerPage(15)
			
			println "INIT SEARCHERS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			boolean existSongAnno = false
			boolean existReleaseAnno = false
			boolean existArtistAnno = false
			
			//Get annotation
			ArrayList<Annotation> annotations = getAnnotations(query)
			println "ANNOTATION SIZE: " + annotations.size()
			
			String stringSongs = "";
			String stringArtists = "";
			String stringReleases = "";
			String stringYears = "";
			
			for(Annotation anno : annotations) {
				//println anno.getEntityType() + " " + anno.getEntityValue()
				if (anno.getEntityType().equalsIgnoreCase("SONG")) {
					
					if (stringSongs.size() > 0) {
						stringSongs += " OR";
					}
					stringSongs += " \"" + anno.getEntityValue() + "\"";
					
					/*
					//Main search
					songSearcher.addQuery(anno.getEntityValue(), "songTitle", Occur.SHOULD, (float)10)
					
					//Additional information
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseSongs", Occur.SHOULD)
					*/
					existSongAnno = true
				}
				else if (anno.getEntityType().equalsIgnoreCase("RELEASE")) {
					if (stringReleases.size() > 0) {
						stringReleases += " OR";
					}
					stringReleases += " \"" + anno.getEntityValue() + "\"";
					
					/*
					//Main search
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseName", Occur.SHOULD, (float)2)
					
					//Additional information
					//songSearcher.addQuery(anno.getEntityValue(), "songReleaseName", Occur.SHOULD)
					*/
					existReleaseAnno = true
				}
				else if (anno.getEntityType().equalsIgnoreCase("ARTIST")) {
					
					if (stringArtists.size() > 0) {
						stringArtists += " OR";
					}
					stringArtists += " \"" + anno.getEntityValue() + "\"";
					
					/*
					//Main search
					artistSearcher.addQuery(anno.getEntityValue(), "artistName", Occur.SHOULD, (float)2)
					
					//Additional information
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseArtists", Occur.SHOULD)
					
					//Additional information for song
					println "ADD song of artist: " + anno.getEntityValue()
					songSearcher.addQuery("\"" + anno.getEntityValue() + "\"", "songArtists", Occur.MUST)
					*/
					
					existArtistAnno = true
				} else if (anno.getEntityType().equalsIgnoreCase("YEAR")) {
					if (stringYears.size() > 0) {
						stringYears += " OR";
					}
					stringYears += " \"" + anno.getEntityValue() + "\"";
				}
			}
			
			stringSongs = stringSongs.trim()
			stringReleases = stringReleases.trim()
			stringArtists = stringArtists.trim()
			stringYears = stringYears.trim()
			
			//println "String Songs: " + stringSongs
			//println "String Releases: " + stringReleases
			//println "String Artists: " + stringArtists
			//println "String Years: " + stringYears
			
			if (stringSongs.size() > 0) {
				songSearcher.addQuery(false, stringSongs, "songTitle", Occur.SHOULD, (float)2)
				releaseSearcher.addQuery(false, stringSongs, "releaseSongs", Occur.SHOULD)
			}
			
			if (stringReleases.size() > 0) {
				releaseSearcher.addQuery(false, stringReleases, "releaseName", Occur.MUST)
			}
			
			if (stringArtists.size() > 0) {
				artistSearcher.addQuery(false, stringArtists, "artistName", Occur.MUST, (float)2)
				songSearcher.addQuery(false, stringArtists, "songArtists" , Occur.SHOULD, (float)5)
			}
			
			if (stringYears.size() > 0) {
				songSearcher.addQuery(false, stringYears, "songYear" , Occur.SHOULD, (float)10)
			}
			
			println "BUILD QUERY ANNOTATIONS: " + ((System.currentTimeMillis() - start)) * 1.0 / 1000
			start = System.currentTimeMillis()
			
			//Check if annotation for song/artist/release exist, if not we need to relax the query criteria
			if (!existSongAnno) {
				println "ADD SONG TITLE: " + query
				songSearcher.addQuery(true, "\"" + query + "\"", "songTitle", Occur.MUST, (float)10)
				//songSearcher.addQuery("\"" + query + "\"", "songTitle", Occur.MUST, (float)10)
				songSearcher.addQuery(true, query, "songGenres", Occur.SHOULD)
				//songSearcher.addQuery(query, "songLyrics", Occur.SHOULD)
				songSearcher.addQuery(true, query, "songCountries", Occur.SHOULD)
				songSearcher.addQuery(true, query, "songContinents", Occur.SHOULD)
			}
			
			if (!existReleaseAnno) {
				releaseSearcher.addQuery(true, "\"" + query + "\"", "releaseName", Occur.SHOULD, (float)3)
				releaseSearcher.addQuery(true, query, "releaseType", Occur.SHOULD)
				releaseSearcher.addQuery(true, query, "releaseSongs", Occur.SHOULD)
				releaseSearcher.addQuery(true, query, "releaseArtists", Occur.SHOULD)
			}
			
			if (!existArtistAnno) {
				artistSearcher.addQuery(true, "\"" + query + "\"", "artistName", Occur.SHOULD, (float)3)
				//artistSearcher.addQuery("\"" + query + "\"", "artistName", Occur.MUST, (float)1.5)
				artistSearcher.addQuery(true, query, "artistType", Occur.SHOULD)
				artistSearcher.addQuery(true, query, "artistGender", Occur.SHOULD)
				artistSearcher.addQuery(true, query, "artistCountry", Occur.SHOULD)
				artistSearcher.addQuery(true, query, "artistContinent", Occur.SHOULD)
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
	
	public ArrayList<Annotation> getAnnotations(String query) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>()
		NER ner = servletContext.getAttribute("ner");
		query = query.trim();
		String[] terms = query.split("\\s+");
		for (int i = 0 ; i < terms.length; i++) {
			for(int j = i ; j <  terms.length; j++) {
				String candidate = "";
				for(int t = i; t <= j; t++) {
					candidate += terms[t] + " "
				}
				candidate = candidate.trim()
				println "\"" + candidate + "\""
				if (candidate.length() >= 0) {
					ArrayList<Annotation> tmpAnno = ner.annotateQuery("\"" + candidate + "\"");
					if (tmpAnno.size() > 0) {
						annotations.addAll(tmpAnno)
					}
				}
			}
		}
		
		Map<String, String> checkUniqueArtists = new HashMap<String, String>();
		Map<String, String> checkUniqueSongs = new HashMap<String, String>();
		Map<String, String> checkUniqueReleases = new HashMap<String, String>();
		Map<String, String> checkUniqueYears = new HashMap<String, String>();
		
		//Only get annotation with value exist in query
		ArrayList<Annotation> finalAnnotations = new ArrayList<Annotation>()
		String tmpQuery = query.trim().toLowerCase();
		for(int i = 0; i < annotations.size(); i++ ) {
			Annotation an = annotations.get(i);
			String v = an.entityValue.toLowerCase();
			if (tmpQuery.indexOf(v) >= 0) {
				
				//Check unique song
				if (an.getEntityType().equalsIgnoreCase("SONG")) {
					if (checkUniqueSongs.containsKey(an.getEntityValue())) {
						continue;
					}
					checkUniqueSongs.put(an.getEntityValue(), an.getEntityType())
				}
				
				//Check unique artist
				if (an.getEntityType().equalsIgnoreCase("ARTIST")) {
					if (checkUniqueArtists.containsKey(an.getEntityValue())) {
						continue;
					}
					checkUniqueArtists.put(an.getEntityValue(), an.getEntityType())
				}
				
				//Check unique release
				if (an.getEntityType().equalsIgnoreCase("RELEASE")) {
					if (checkUniqueReleases.containsKey(an.getEntityValue())) {
						continue;
					}
					checkUniqueReleases.put(an.getEntityValue(), an.getEntityType())
				}
				
				//Chekc unique year
				if (an.getEntityType().equalsIgnoreCase("YEAR")) {
					if (checkUniqueYears.containsKey(an.getEntityValue())) {
						continue;
					}
					checkUniqueYears.put(an.getEntityValue(), an.getEntityType())
				}
				finalAnnotations.add(an);
			}
		}
		return finalAnnotations
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
		searcher.addQuery(true, entityID.trim(), "documentID", Occur.MUST)
		
		searcher.execute()
		
		ApoloDocument entity = searcher.getResults().get(0)
		
		getDBPediaInfo(entity)
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
		def model = [:]
		ApoloDocument artist = new ApoloDocument();
		artist.setArtistID(artistID);
		model.firstArtistSongs = getFirstArtistSongs(artist, 10000)
		def data = g.render(template : "/template/first-artist-songs" , model : model);
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
		searcher.addQuery(true, artist.getArtistID(), "songArtistsID", Occur.MUST)
		searcher.addQuery(true, "song", "type", Occur.MUST)
		searcher.setPage(1)
		searcher.setResultPerPage(limit)
		searcher.execute()
		
		Set<String> songTitles = new HashSet<String>();
		ArrayList<ApoloDocument> results = searcher.getResults()
		ArrayList<ApoloDocument> finalResults = new ArrayList<ApoloDocument>();
		
		for(ApoloDocument adocument : results) {
			if (!songTitles.contains(adocument.getSongTitle())) {
				songTitles.add(adocument.getSongTitle());
				finalResults.add(adocument);
			}
		}
		
		Collections.sort(finalResults, new Comparator<ApoloDocument>() {
			@Override public int compare(final ApoloDocument d1, final ApoloDocument d2) {
			  if (d1.getSongTitle() > d2.getSongTitle()) {
				return 1;
			  } else if (d1.getSongTitle() < d2.getSongTitle()) {
				return -1;
			  }
			  return 0;
			}
		});
		
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
		try {
			model.recommendedArtists = getRecommendation(document)
		} catch (Exception e) {
		model.recommendedArtists = ""
		}
		
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
		Collections.sort(suggestions)
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
