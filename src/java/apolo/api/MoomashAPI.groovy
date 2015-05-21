package apolo.api
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import apolo.msc.Global_Configuration;
import groovy.json.JsonSlurper


class MoomashAPI {
	private String baseUrl = "http://api.mooma.sh/v1/song/identify";
	
	def getCode(filePath, length) {
		def command = Global_Configuration.CODEGEN_FOLDER + "/codegen.exe " + filePath + " 1 " + length
		def result = command.execute().text
		def slurper = new JsonSlurper()
		def json = slurper.parseText(result)
		
		if (json.size() > 0) {
			return json[0].code
		}
	}
	
	def query(code) {
		try {
			CloseableHttpClient client = HttpClients.custom().disableContentCompression().build();
			
			String finalUrl = 	baseUrl +
								"?api_key=" + Global_Configuration.MOOMASH_KEY +
								"&code=" + URLEncoder.encode(code, "UTF-8")
								
			
			HttpGet request = new HttpGet(finalUrl);
			
			HttpResponse response;
			response = client.execute(request);
			def stringResult =  EntityUtils.toString(response.getEntity());
			
			def slurper = new JsonSlurper()
			def json = slurper.parseText(stringResult)
			
			if (json.response.songs.size() > 0) {
				return json.response.songs[0].title
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	static main(args) {
		MoomashAPI mmapi = new MoomashAPI()
		def code = mmapi.getCode("recording.wav")
		print mmapi.query(code)
	}

}
