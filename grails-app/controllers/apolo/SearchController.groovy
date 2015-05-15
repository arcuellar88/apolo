package apolo

import org.apache.lucene.search.BooleanClause.Occur;

import apolo.api.DBPediaHTTPXML;
import apolo.api.IDBpedia;
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

class SearchController extends BaseController {

    def index() { 		
		
		//init NER module
		NER ner = servletContext.getAttribute("ner");
		SpellingCorrection spellChecker = servletContext.getAttribute("spellchecker")
		
		def isSearching = false
		def model = [:]
		
		String query = ""
		if (params.keyword != null) {
			query = params.keyword.trim()
		}
		
		String spellingCorrectedString = ""
		
		if (!query.equals("")) {
			
			//Get spelling correction
			spellingCorrectedString = spellChecker.getSpellingSuggestions(query)
			
			if (spellingCorrectedString.equalsIgnoreCase(query)) {
				spellingCorrectedString = ""
			}
			
			isSearching = true
			
			//init searcher for song
			Searcher songSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY)
			songSearcher.addQuery("song", "type", Occur.MUST)
			songSearcher.setPage(1)
			songSearcher.setResultPerPage(10)
			
			//Searcher for release
			Searcher releaseSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY)
			releaseSearcher.addQuery("release", "type", Occur.MUST)
			releaseSearcher.setPage(1)
			releaseSearcher.setResultPerPage(10)
			
			//Searcher for artist
			Searcher artistSearcher = new Searcher(Global_Configuration.INDEX_DIRECTORY)
			artistSearcher.addQuery("artist", "type", Occur.MUST)
			artistSearcher.setPage(1)
			artistSearcher.setResultPerPage(10)
			
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
			
			//Execute query and get result
			songSearcher.execute();
			ArrayList<ApoloDocument> songs = songSearcher.getResults()
			model.songs = songs
			
			if (songs.size() > 0) {
				String youtubeURL = getYouTubeURL(songs.get(0))
				songs.get(0).setSongYoutubeURL(youtubeURL)
			}
			
			releaseSearcher.execute();
			ArrayList<ApoloDocument> releases = releaseSearcher.getResults()
			model.releases = releases
			
			artistSearcher.execute();
			ArrayList<ApoloDocument> artists = artistSearcher.getResults()
			model.artists = artists
			
			//Get song of the first artist
			if (artists.size() > 0) {
				model.firstArtistSongs = getFirstArtistSongs(artists.get(0))
				model.recommendedArtists = getRecommendation(artists.get(0))
			}
			else {
				model.firstArtistSongs = new ArrayList<ApoloDocument>()
				model.recommendedArtists = new ArrayList<ApoloDocument>()
			}
			
			long start = System.currentTimeMillis();
			//Get additional information for  songs/releases/artists from DP pedia
			IDBpedia dbpediaClient = new DBPediaHTTPXML();
			
			//Song
			for(ApoloDocument song : songs) {
				
				try {
					ISong isong = new Song()
					
					isong.setTitle(song.getSongTitle())
					//TODO: Set more information to get correct information
					
					dbpediaClient.getAdditionalInformationSong(isong)
					song.setIsong(isong)
					
					//TODO Next Phase: To update the index with information crawl from DBpedia
					
					//Only get for the first one
				} catch (Exception e) {
					e.printStackTrace()
				}
				break
			}
			
			//Release
			for(ApoloDocument release : releases) {
				try {
					IRelease irelease = new Release()
					
					irelease.setName(release.getReleaseName())
					irelease.setType(release.getReleaseType())
					//TODO: Set more information to get correct information
					
					dbpediaClient.getAdditionalInformationRelease(irelease)
					release.setIrelease(irelease)
					
					//TODO Next Phase: To update the index with information crawl from DBpedia
					
					//Only get for the first one
				} catch (Exception e) {
					e.printStackTrace()
				}
				break
			}
			
			//Artist
			for(ApoloDocument artist : artists) {
				try {
					IArtist iartist = new Artist()
					
					iartist.setGender(artist.getArtistGender())
					iartist.setName(artist.getArtistName())
					//TODO: Set more information to get correct information
					
					dbpediaClient.getAdditionalInformationArtist(iartist)
					artist.setIartist(iartist)
					
					//TODO Next Phase: To update the index with information crawl from DBpedia
					
					//Only get for the first one
				} catch (Exception e) {
					e.printStackTrace()
				}
				break
			}
		}
		
		model.spellingCorrectedString = spellingCorrectedString
		model.isSearching = isSearching
		
		render (view : "search" , layout : "main" , model : model);
	}
	
	def getEntity() {
		
		def model = [:]
		String data = ""
		String name = ""
		
		String entityID = params.entityID
		
		Searcher searcher = new Searcher(Global_Configuration.INDEX_DIRECTORY)
		searcher.setPage(1)
		searcher.setResultPerPage(10)
		searcher.addQuery(entityID.trim(), "documentID", Occur.MUST)
		
		searcher.execute()
		
		ApoloDocument entity = searcher.getResults().get(0)
		
		IDBpedia dbpediaClient = new DBPediaHTTPXML();
		
		if (entity.getType().equalsIgnoreCase("song")) {
			//Song
			
			try {
				ISong isong = new Song()
				
				isong.setTitle(entity.getSongTitle())
				dbpediaClient.getAdditionalInformationSong(isong)
				
				entity.setIsong(isong)
			} catch (Exception e) {
				e.printStackTrace()
			}
			
			//Youtube link
			String youtubeURL = getYouTubeURL(entity)
			entity.setSongYoutubeURL(youtubeURL)
			
			name = entity.getSongTitle()
			model.song = entity
			data = g.render(template : "/template/first-song" , model : model);
		}
		else if (entity.getType().equalsIgnoreCase("artist")) {
			//Artist
			IArtist iartist = new Artist()
			
			iartist.setGender(entity.getArtistGender())
			iartist.setName(entity.getArtistName())
			
			dbpediaClient.getAdditionalInformationArtist(iartist)
			entity.setIartist(iartist)
			
			
			model.firstArtistSongs = getFirstArtistSongs(entity)
			model.recommendedArtists = getRecommendation(entity)
			
			name = entity.getArtistName()
			model.artist = entity
			data = g.render(template : "/template/first-artist" , model : model);
			
		} 
		else if (entity.getType().equalsIgnoreCase("release")) {
			//Release
			IRelease irelease = new Release()
			
			irelease.setName(entity.getReleaseName())
			irelease.setType(entity.getReleaseType())
			
			dbpediaClient.getAdditionalInformationRelease(irelease)
			entity.setIrelease(irelease)
			
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
	 * Get songs of first artist
	 * @param artist
	 * @return
	 */
	
	private ArrayList<ApoloDocument> getFirstArtistSongs(ApoloDocument artist) {
		Searcher searcher = new Searcher(Global_Configuration.INDEX_DIRECTORY)
		searcher.addQuery(artist.getArtistID(), "songArtistsID", Occur.MUST)
		searcher.addQuery("song", "type", Occur.MUST)
		searcher.execute()
		return searcher.getResults()
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
			ApoloDocument newItem = new ApoloDocument()
			newItem.setType("artist")
			newItem.setArtistID(rartists.getItems().get(i).getItemId() + "")
			newItem.setArtistName(rartists.getItems().get(i).getItemName())
			newItem.setDocumentID("artist_" + rartists.getItems().get(i).getItemId())
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
		if (song.getSongTitle().length() <= 10 && !song.getSongArtists().equals("")) {
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
}
