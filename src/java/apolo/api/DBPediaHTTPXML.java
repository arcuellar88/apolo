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

import apolo.entity.*;
import apolo.msc.Log;

public class DBPediaHTTPXML implements IDBpedia {
	
	// --------------------------------------------------------
	// Constants
	// --------------------------------------------------------
	private final static String HTTP_URL = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryClass=QCLASS&QueryString=QUERYSTR";
	private final static String QUERY_CLASS="QCLASS";
	private final static String QUERY_STRING="QUERYSTR";
	private final static String CLASS_ARTIST="Agent";

	
	// --------------------------------------------------------
	// Attributes
	// --------------------------------------------------------

	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------

	public DBPediaHTTPXML()
	{
		
	}
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	
	@Override
	public IArtist getAdditionalInformationArtist(IArtist a)
	{
		Element root=httpRequestDBPedia(CLASS_ARTIST, a.getName());
		
		if(root!=null)
		{
			// get all child nodes
	        NodeList nodes = root.getChildNodes();
	        
	        if(nodes.getLength()>0)
	        	{
	        	Node nodo2=nodes.item(1); 
	        	 nodes = nodo2.getChildNodes();
	 	        
	 	        for (int i = 0; i < nodes.getLength(); i++) {
	 	        	
	 		            //Log.println("Name: " + nodes.item(i).getNodeName()+" Value:"+nodes.item(i).getTextContent());
	 		        //Look for the description    
	 	        	if(nodes.item(i).getNodeName().equals("Description"))
	 		            	a.setDescription(nodes.item(i).getTextContent().trim());
	 	        	if(nodes.item(i).getNodeName().equals("URI"))
 		            	a.setURI(nodes.item(i).getTextContent());

	 	        }
	 	       
	        	}  
	        
		}        
        
		return a;
	}
	
	public ISong getAdditionalInformationSong(ISong s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRelease getAdditionalInformationRelease(IRelease r) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Methods that transforms the HTTP GET request into a DOM element
	 * @param qClass Type of the DBPedia Query: person, agent, release, etc. 
	 * @param name Name of the entity: of the artist, song or release
	 * @return The root element of the response from DBPedia
	 */
	public Element httpRequestDBPedia(String qClass,String name)
	{
		Element root=null;
		try {
			 
			HttpClient client = HttpClientBuilder.create().build();
			String query=HTTP_URL.replaceAll(QUERY_CLASS, qClass);
			
			HttpGet request = new HttpGet(query.replaceAll(QUERY_STRING, name));
		 
			// add request header
			//request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response;
			
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			response = client.execute(request);
			
			
			Document doc = builder.parse(response.getEntity().getContent());

	         // get the first element
	         root = doc.getDocumentElement();

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
		
		 return root;
	}
	
	
	/**
	 * Method to print the HTTP Request (Structure of the XML)
	 * Testing purposes
	 */
	public void printHttpRequest()
	{
		try {
			//QueryClass=person	
			String url = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?&QueryString=Shakira";
			 
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
		 
			// add request header
			//request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response;
			
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
			} 
	}
	
	public static void main(String[] args)
	{
		IDBpedia db= new DBPediaHTTPXML();
		//db.printHttpRequest();
		IArtist a = new Artist();
		a.setName("Shakira");
		db.getAdditionalInformationArtist(a);
		Log.println("Description" + a.getDescription());
		
	}
}
