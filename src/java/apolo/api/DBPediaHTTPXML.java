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
	private final static String CLASS_ARTIST="Artist";
	private final static String CLASS_BAND="Band";
	private final static String CLASS_AGENT="Agent";
	private final static String CLASS_SONG="Song";
	private final static String CLASS_RELEASE="Album";
	private final static String CLASS_MUSIC="MusicalWork";
	
	private static String Defult_Desc = "";
	private static String Defult_URI = "";
	
	
	
	
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
		String name =  a.getName().replaceAll(" ", "_");
		httpRequestDBPedia(CLASS_ARTIST,name);
		
		if (  (!Defult_Desc.equals("EMPTY..")  && !Defult_URI.equals("EMPTY.."))){
     		a.setDescription(Defult_Desc);
     		a.setURI(Defult_URI);
     	}
		
		else
		{
			
			httpRequestDBPedia(CLASS_BAND,name);
			
			if (  (Defult_Desc.equals("EMPTY..")  && Defult_URI.equals("EMPTY.."))){
				httpRequestDBPedia(CLASS_AGENT,name);	     	
	     	}
			a.setDescription(Defult_Desc);
     		a.setURI(Defult_URI);
			
		}
		
		return a;
	}
	
	public ISong getAdditionalInformationSong(ISong s) {
    
		
		String name = s.getTitle().replaceAll(" ", "_");
		httpRequestDBPedia(CLASS_SONG,name);
		
		if (  (!Defult_Desc.equals("EMPTY..")  && !Defult_URI.equals("EMPTY.."))){
     		s.setDescription(Defult_Desc);
     		s.setURI(Defult_URI);
     	}
		
		else
		{
			
			httpRequestDBPedia(CLASS_MUSIC,name);
			s.setDescription(Defult_Desc);
     		s.setURI(Defult_URI);
			
		}
		return s;
	}

	@Override
	public IRelease getAdditionalInformationRelease(IRelease r) {
        
		String name = r.getName().replaceAll(" ", "_");
		httpRequestDBPedia(CLASS_RELEASE,name);
		
		if (  (!Defult_Desc.equals("EMPTY..")  && !Defult_URI.equals("EMPTY.."))){
     		r.setDescription(Defult_Desc);
     		r.setURI(Defult_URI);
     	}
		
		else
		{
			
			httpRequestDBPedia(CLASS_MUSIC,name);
			r.setDescription(Defult_Desc);
     		r.setURI(Defult_URI);
			
		}
		
		return r;
	}
	
	
	/**
	 * Methods that transforms the HTTP GET request into a DOM element
	 * @param qClass Type of the DBPedia Query: person, agent, release, etc. 
	 * @param name Name of the entity: of the artist, song or release
	 * @return The root element of the response from DBPedia
	 */
	public void httpRequestDBPedia(String qClass,String name)
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
	         // read from root
	 		if(root!=null)
			{
				// get all child nodes
		        NodeList nodes = root.getChildNodes();
		        
		        if(nodes.getLength()>0)
		        	{
		        	Node nodo2=nodes.item(1); 
		        	
		        	 if(nodo2 != null){
		        		 
		        	 nodes = nodo2.getChildNodes();
		        	
		 	        for (int i = 0; i < nodes.getLength(); i++) {
		 	            
		 		        //Look for the description  & URI
		 	        	if(nodes.item(i).getNodeName().equals("Description"))
		 	        		Defult_Desc= nodes.item(i).getTextContent().trim();
		 	        	
		 	        	if(nodes.item(i).getNodeName().equals("URI"))
		 	        		Defult_URI=nodes.item(i).getTextContent();
       
		 	        	
		 	  
		 	        }	}
		        	 
		        	 else
		        	 {   Defult_Desc = "EMPTY..";
		        	     Defult_URI = "EMPTY..";
		        	 }
		        	}  	}   
		 	       	         

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
	
		/*IArtist a = new Artist();
		a.setName("Kim Kardashian");
		db.getAdditionalInformationArtist(a);
		Log.println("Description: " + a.getDescription() + "\n"+ a.getURI());*/

	
		ISong s = new Song();
		s.setTitle("Yellow Submarine");
		db.getAdditionalInformationSong(s);
		Log.println("Description: " + s.getDescription() + "\n"+ s.getURI());

		
		
		/* 
		 
		IRelease r = new Release();
		r.setName("Brave");
		db.getAdditionalInformationRelease(r);
		Log.println("Description: " + r.getDescription()+ "\n"+ r.getURI());  
		
		*/
		
	}
}

