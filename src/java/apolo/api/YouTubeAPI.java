package apolo.api;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import apolo.msc.Global_Configuration;

public class YouTubeAPI {
	private int maxResults = 1;
	private String part = "id,snippet";
	private String baseUrl = "https://www.googleapis.com/youtube/v3/search";
	private String type = "video";
	private String topics = "/m/04rlf"; //This is Freebase topic id
	
	public String query(String query) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			
			String finalUrl = 	baseUrl + 
								"?part=" + part +
								"&type=" + type +
								"&maxResults=" + maxResults +
								"&topicId=" + topics +
								"&key=" + Global_Configuration.YOUTUBE_KEY +
								"&q=" + URLEncoder.encode(query, "UTF-8");
							  
			HttpGet request = new HttpGet(finalUrl);
			HttpResponse response;
			response = client.execute(request);
			return  EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
