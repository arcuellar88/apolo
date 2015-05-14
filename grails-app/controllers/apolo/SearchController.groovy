package apolo

import org.apache.lucene.search.BooleanClause.Occur;

import apolo.api.DBPediaHTTPXML;
import apolo.api.IDBpedia;
import apolo.entity.ApoloDocument
import apolo.entity.Artist
import apolo.entity.IArtist
import apolo.entity.IRelease
import apolo.entity.ISong
import apolo.entity.Release
import apolo.entity.Song
import apolo.msc.Global_Configuration;
import apolo.querybuilder.Searcher
import apolo.queryrefinement.Annotation;
import apolo.queryrefinement.Autocomplete
import apolo.queryrefinement.NER
import apolo.queryrefinement.SpellingCorrection
import grails.converters.JSON

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
		
		//Get spellinig correction
		String spellingCorrectedString = spellChecker.getSpellingSuggestions(query)
		if (spellingCorrectedString.equalsIgnoreCase(query)) {
			spellingCorrectedString = ""
		}
		
		model.spellingCorrectedString = spellingCorrectedString
		
		if (!query.equals("")) {
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
			println "ANNO LENGTH: " + annotations.size()
			for(Annotation anno : annotations) {
				println anno.getEntityType() + " - " + anno.getEntityValue() 
				if (anno.getEntityType().equalsIgnoreCase("SONG")) {
					//Main search
					songSearcher.addQuery(anno.getEntityValue(), "songTitle", Occur.MUST)
					
					//Additional information
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseSongs", Occur.SHOULD)
					
					existSongAnno = true
				}
				else if (anno.getEntityType().equalsIgnoreCase("RELEASE")) {
					//Main search
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseName", Occur.MUST)
					
					//Additional information
					songSearcher.addQuery(anno.getEntityValue(), "songReleaseName", Occur.SHOULD)
					
					existReleaseAnno = true
				}
				else if (anno.getEntityType().equalsIgnoreCase("ARTIST")) {
					//Main search
					artistSearcher.addQuery(anno.getEntityValue(), "artistName", Occur.MUST)
					
					//Additional information
					releaseSearcher.addQuery(anno.getEntityValue(), "releaseArtists", Occur.SHOULD)
					
					existArtistAnno = true
				}
			}
			
			//Check if annotation for song/artist/release exist, if not we need to relax the query criteria
			if (!existSongAnno) {
				songSearcher.addQuery(query, "songTitle", Occur.SHOULD)
				songSearcher.addQuery(query, "songLabels", Occur.SHOULD)
				songSearcher.addQuery(query, "songLyrics", Occur.SHOULD)
				songSearcher.addQuery(query, "songCountries", Occur.SHOULD)
				songSearcher.addQuery(query, "songContinents", Occur.SHOULD)
			}
			
			if (!existReleaseAnno) {
				releaseSearcher.addQuery(query, "releaseName", Occur.SHOULD)
				releaseSearcher.addQuery(query, "releaseType", Occur.SHOULD)
				releaseSearcher.addQuery(query, "releaseSongs", Occur.SHOULD)
				releaseSearcher.addQuery(query, "releaseArtists", Occur.SHOULD)
				releaseSearcher.setPage(1)
				releaseSearcher.setResultPerPage(10)
			}
			
			if (!existArtistAnno) {
				artistSearcher.addQuery(query, "artistName", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistType", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistGender", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistCountry", Occur.SHOULD)
				artistSearcher.addQuery(query, "artistContinent", Occur.SHOULD)
				artistSearcher.setPage(1)
				artistSearcher.setPage(10)
			}
			
			//Execute query and get result
			songSearcher.execute();
			ArrayList<ApoloDocument> songs = songSearcher.getResults()
			println "SONG SIZE: " + songs.size()
			model.songs = songs
			
			releaseSearcher.execute();
			ArrayList<ApoloDocument> releases = releaseSearcher.getResults()
			println "RELEASE SIZE: " + releases.size()
			model.releases = releases
			
			artistSearcher.execute();
			ArrayList<ApoloDocument> artists = artistSearcher.getResults()
			println "ARTIST SIZE: " + artists.size()
			model.artists = artists
			
			//Get song of the first artist
			if (artists.size() > 0) {
				model.firstArtistSongs = getFirstArtistSongs(artists.get(0))
				println "FIRST ARTIST SONG: " + model.firstArtistSongs.size()
			}
			else {
				model.firstArtistSongs = new ArrayList<ApoloDocument>()
			}
			
			/*
			
			long start = System.currentTimeMillis();
			//Get additional information for  songs/releases/artists from DP pedia
			IDBpedia dbpediaClient = new DBPediaHTTPXML();
			
			//Song
			for(ApoloDocument song : songs) {
				ISong isong = new Song()
				
				isong.setTitle(song.getSongTitle())
				//TODO: Set more information to get correct information
				
				dbpediaClient.getAdditionalInformationSong(isong)
				song.setIsong(isong)
				
				//TODO Next Phase: To update the index with information crawl from DBpedia
			}
			
			//Release
			for(ApoloDocument release : releases) {
				IRelease irelease = new Release()
				
				irelease.setName(release.getReleaseName())
				irelease.setType(release.getReleaseType())
				//TODO: Set more information to get correct information
				
				dbpediaClient.getAdditionalInformationRelease(irelease)
				release.setIrelease(irelease)
				
				//TODO Next Phase: To update the index with information crawl from DBpedia
			}
			
			//Artist
			for(ApoloDocument artist : artists) {
				IArtist iartist = new Artist()
				
				iartist.setGender(artist.getArtistGender())
				iartist.setName(artist.getArtistName())
				//TODO: Set more information to get correct information
				
				dbpediaClient.getAdditionalInformationArtist(iartist)
				artist.setIartist(iartist)
				
				//TODO Next Phase: To update the index with information crawl from DBpedia
			}
			
			println "DBPEDIA in: " + ((System.currentTimeMillis() - start) * 1.0 / 1000)
			
			*/
		}
		
		model.isSearching = isSearching
		render (view : "search" , layout : "main" , model : model);
	}
	
	private ArrayList<ApoloDocument> getFirstArtistSongs(ApoloDocument artist) {
		Searcher searcher = new Searcher(Global_Configuration.INDEX_DIRECTORY)
		searcher.setPage(1)
		searcher.setResultPerPage(20)
		searcher.addQuery(artist.getDocumentId(), "documentId", Occur.MUST)
		searcher.addQuery("song", "type", Occur.MUST)
		return searcher.getResults()
	}
	
	def getSuggestion() {
		String query = params.keyword
		Autocomplete autoComplete = servletContext.getAttribute("autocomplete");
		
		ArrayList<String> suggestions = autoComplete.getCompletionsList(query)
		def result = [keyword : query , suggestions : suggestions]
		render result as JSON
	}
}
