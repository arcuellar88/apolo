package apolo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class DBPediaHTTPXML {
	
	
	
	
	public void testhttp()
	{
		try {
			String url = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryClass=Agent&QueryString=Shakira";
			 
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
		 
			// add request header
			//request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response;
			
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			response = client.execute(request);
			
			System.out.println("Response Code : " 
		                + response.getStatusLine().getStatusCode());
		 
			
			Document doc = builder.parse(response.getEntity().getContent());

	         // get the first element
	         Element element = doc.getDocumentElement();

	         // get all child nodes
	         NodeList nodes = element.getChildNodes();

	         /* print the text content of each child
	         for (int i = 0; i < nodes.getLength(); i++) {
	            System.out.println("Name: " + nodes.item(i).getNodeName()+" Value:"+nodes.item(i).);
	         }
	         */
			
			Node nodo2=nodes.item(1); 
			
	         System.out.println(nodo2.getNodeName());

	         
	         nodes = nodo2.getChildNodes();
	         
	         for (int i = 0; i < nodes.getLength(); i++) {
		            System.out.println("Name: " + nodes.item(i).getNodeName()+" Value:"+nodes.item(i).getTextContent());
		         }
	         
	         Node nodo3=nodes.item(6);
	         System.out.println(nodo3.getNodeName());

	         nodes = nodo3.getChildNodes();
			      
	         for (int i = 0; i < nodes.getLength(); i++) {
		            System.out.println("Name: " + nodes.item(i).getNodeName()+" Value:"+nodes.item(i).getTextContent());
		         }
	         
	         
	         /**
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
				System.out.println(line);
			}
			*/
			} 
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	}
	
	
	public void testhttp2()
	{
		try {
			//QueryClass=person	
			String url = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?&QueryString=Shakira";
			 
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
		 
			// add request header
			//request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response;
			
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	
			response = client.execute(request);
			
			System.out.println("Response Code : " 
		                + response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
			 
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
					System.out.println(line);
				}
			
		} 
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	public static void main(String[] args)
	{
		DBPediaHTTPXML db= new DBPediaHTTPXML();
		db.testhttp();
		
	}
}
