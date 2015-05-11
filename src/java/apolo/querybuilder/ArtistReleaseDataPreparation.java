package apolo.querybuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

public class ArtistReleaseDataPreparation {
    /**
     * For song extraction
     */
    public final String releaseFile = "data/release.csv";
    public final String artistFile = "data/artist.csv";
    public  final List<HashMap<String, String>> artistList = new ArrayList<HashMap<String, String>>();
    public  final List<HashMap<String, String>> releaseList = new ArrayList<HashMap<String, String>>();

    public final String separator = ",";
    
    
    public static void main(String[] args) throws IOException {
        
        ArtistReleaseDataPreparation dp = new ArtistReleaseDataPreparation();
        
        long start = System.currentTimeMillis();
        
    
        start = System.currentTimeMillis();
        //dp.readArtist();
         dp.readRelease();
        System.out.println(" loaded_IN: " + (System.currentTimeMillis() - start));
        
    
    }
    

    public void readArtist() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(artistFile));
        String line;
        HashMap<String, String> artistMap = new HashMap<String, String>();
        
       while ((line = br.readLine()) != null) {
        line = br.readLine();
       
        if (line != null ){ 
          
        	String[] txt = line.split(separator);
     
            artistMap.put("artistID", txt[0]);
            artistMap.put("artistName", txt[1]);
            artistMap.put("artistGender", txt[2]);
            artistMap.put("artistType", txt[3]);
            artistMap.put("artistCountry", txt[4]);
            artistMap.put("artistContinent", txt[5]);
            
            if (txt.length >= 7)
            artistMap.put("artistMBID", txt[6]);
            else
            artistMap.put("artistMBID","");
            
            if ( txt.length ==12){ 
                artistMap.put("artistRatingAVG", txt[7]);
                artistMap.put("artistRatingCount", txt[8]);
                artistMap.put("artistP", txt[9]);
                artistMap.put("artistNU", txt[10]);
                artistMap.put("artistN", txt[11]);
            }
            else
            {
            	
                artistMap.put("artistRatingAVG", "");
                artistMap.put("artistRatingCount", "");
                artistMap.put("artistP", "");
                artistMap.put("artistNU","");
                artistMap.put("artistN", "");
            }

            artistList.add(artistMap);
        }
        }
        br.close();
    }

    
    public void readRelease() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(releaseFile));
        String line;
        HashMap<String, String> releaseMap = new HashMap<String, String>();
        
       while ((line = br.readLine()) != null) {
        line = br.readLine();
     
        if (line != null ){
            String[] txt = line.split(separator);

            releaseMap.put("releaseID", txt[0]);
            releaseMap.put("releaseName", txt[1]);
            releaseMap.put("releaseType", txt[2]);
            releaseMap.put("releaseMBID", txt[3]);
            if(txt.length>=5)
            releaseMap.put("releaseArtist", txt[4]);
            else
            	releaseMap.put("releaseArtist", "");
            releaseList.add(releaseMap);
            
        }
           
          
        }
        br.close();
    }

}