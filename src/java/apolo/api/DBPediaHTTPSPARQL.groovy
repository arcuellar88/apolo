package apolo.api;

import groovy.json.JsonSlurper
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import apolo.entity.Artist
import apolo.entity.IArtist;
import apolo.entity.IRelease;
import apolo.entity.ISong;
import apolo.entity.Release
import apolo.entity.Song
import apolo.msc.Global_Configuration;

public class DPPediaHTTPSPARQL implements IDBpedia {

	private String baseUrl = "http://dbpedia.org/sparql";
	private String format = "application/sparql-results+json";
	private int timeout = 30000;
	private String debug = "on";

	public String makeQuery(String query) {
		try {

			String finalUrl =     baseUrl +
					"?default-graph-uri=" + URLEncoder.encode("http://dbpedia.org", "UTF-8") +
					"&query=" + URLEncoder.encode(query, "UTF-8") +
					"&format=" + URLEncoder.encode(format, "UTF-8") +
					"&timeout=" + timeout +
					"&debug=" + debug;

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(finalUrl);
			HttpResponse response;
			response = client.execute(request);
			return  EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			return "";
		}
	}

	public static void main(String[] args) {
		DPPediaHTTPSPARQL client = new DPPediaHTTPSPARQL()
		/*
		 IArtist a = new Artist()
		 a.setName(capitalizeString("Paul yOung"))
		 a.setCountry("United Kingdom")
		 client.getAdditionalInformationArtist(a)
		 */

		ISong s = new Song();
		s.setTitle(client.capitalizeString("Hotel California"));
		client.getAdditionalInformationSong(s)
		System.out.println ("URI:" + s.getURI());
		System.out.println ("DESC:" + s.getDescription());

		IRelease r = new Release();
		r.setName(client.capitalizeString("dangerous"));
		client.getAdditionalInformationRelease(r)
		System.out.println ("URI:" + r.getURI());
		System.out.println ("DESC:" + r.getDescription());
	}



	@Override
	public IArtist getAdditionalInformationArtist(IArtist a) {

		String query = "SELECT ?artistName ?abstract ?thumbnail" +
				" WHERE {" +
				" {" +
				" ?resource rdf:type <http://dbpedia.org/ontology/Agent> . " +
				" ?resource dbpprop:name ?artistName ." +
				" ?resource dbpedia-owl:thumbnail ?thumbnail ." +
				" ?resource rdfs:label \"" + a.getName() + "\"@en ."

		if (a.getCountry() != null && a.getCountry().equalsIgnoreCase("others")) {
			query += " ?resource dbpedia-owl:birthDate ?birthdate ." +
					" ?resource dbpedia-owl:birthPlace [ a dbpedia-owl:Country ; rdfs:label ?birthplace]."
		}

		query +=      " ?resource dbpedia-owl:abstract ?abstract ." +
				" filter langMatches(lang(?artistName),\"en\")" +
				" filter langMatches(lang(?abstract),\"en\")" +
				" }" +
				" }";

		String queryResult = this.makeQuery(query)

		try {
			def slurper = new JsonSlurper()
			def result = slurper.parseText(queryResult)

			if (result.results.size() > 0) {
				if (result.results.bindings.size() > 0) {
					def artist = result.results.bindings[0]

					a.setThumbnail(artist.thumbnail.value)
					a.setDescription(artist.abstract.value)
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
		}

		return a
	}



	//@Override
	public ISong getAdditionalInformationSong(ISong s) {

		String query = "SELECT DISTINCT ?song  ?single ?desc "+

				"WHERE { ?song dbpprop:name \"" + s.getTitle()+ "\"@en .?song dbpprop:thisSingle  ?single  ."+
				"?song dbpedia-owl:abstract  ?desc  ."+
				"}";

		String queryResult = this.makeQuery(query)
		// System.out.println (queryResult);

		try {
			def slurper = new JsonSlurper()
			def result = slurper.parseText(queryResult)


			if (result.results.size() > 0) {
				if (result.results.bindings.size() > 0) {
					def song = result.results.bindings[0]

					s.setDescription(song.desc.value)
					s.setURI(song.song.value)
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
		}

		return s
	}

	@Override

	public IRelease getAdditionalInformationRelease(IRelease r) {
		String query = "SELECT DISTINCT ?song  ?single ?desc "+

				"WHERE { ?song dbpprop:name \"" + r.getName()+ "\"@en .?song dbpprop:thisAlbum ?single  ."+
				"?song dbpedia-owl:abstract  ?desc  ."+
				"}";

		String queryResult = this.makeQuery(query)
		// System.out.println (queryResult);

		try {
			def slurper = new JsonSlurper()
			def result = slurper.parseText(queryResult)


			if (result.results.size() > 0) {
				if (result.results.bindings.size() > 0) {
					def rel = result.results.bindings[0]

					r.setDescription(rel.desc.value)
					r.setURI(rel.song.value)
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
		}

		return  r

	}
	public String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}
}