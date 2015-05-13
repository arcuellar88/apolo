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
    
  
    public final String artistFile = "data/artist1.csv";
    public  final List<HashMap<String, String>> artistList = new ArrayList<HashMap<String, String>>();
    public  final List<HashMap<String, String>> releaseList = new ArrayList<HashMap<String, String>>();
    public  final List<HashMap<String, String>> releaseSongList = new ArrayList<HashMap<String, String>>();

    public final static String INDEX_SEPARATOR1 = "\\[\\|\\]";
    public final static String INDEX_SEPARATOR2 = "\\{";
   
 
 public static void main(String[] args) throws IOException {
        
        ArtistReleaseDataPreparation dp = new ArtistReleaseDataPreparation();
        
        long start = System.currentTimeMillis();
     // dp.readArtist();
      // dp.readRelease();
     
        System.out.println(" Done..loaded_IN: " + (System.currentTimeMillis() - start)  );
        
    
    }
 
 
    
    public void readArtist() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(artistFile));
        String line;
        
        
while ((line = br.readLine()) != null) {

     HashMap<String, String> artistMap = new HashMap<String, String>();
        
         if (line != null ){ 
          
            String[] txt = line.split(INDEX_SEPARATOR1);
            
            artistMap.put("artistID", txt[0]);
            artistMap.put("artistName", txt[1]);
            artistMap.put("artistGender", txt[2]);
            artistMap.put("artistType", txt[3]);
            artistMap.put("artistCountry", txt[4]);
            artistMap.put("artistContinent", txt[5]);
           
            
            if ( txt.length >=7 && txt[6]!="" && txt[7]!="" && txt[8]!="" && txt[9]!="" && txt[10]!="" ){ 
                artistMap.put("artistRatingAVG", txt[6]);
                artistMap.put("artistRatingCount", txt[7]);
                artistMap.put("artistP", txt[8]);
                artistMap.put("artistNU", txt[9]);
                artistMap.put("artistN", txt[10]);
              
            }
            else
            {
                
                artistMap.put("artistRatingAVG", "0.0");
                artistMap.put("artistRatingCount", "0");
                artistMap.put("artistP", "0");
                artistMap.put("artistNU","0");
                artistMap.put("artistN", "0");
            }
            
            if (txt.length >= 12 && txt[11]!="")
                artistMap.put("artistMBID", txt[11]);
                else
                artistMap.put("artistMBID","0");
               
            artistList.add(artistMap);
        }
 }
        br.close();
    }

    
    public void readRelease() throws IOException {
            
        
        for (int file =1 ; file <= 5 ; file++) {
            
           String  releaseFile =  "data/Exp_File_"+ file+ ".txt";
            BufferedReader br = new BufferedReader(new FileReader(releaseFile));
            String line;
       
            
     //while ((line = br.readLine()) != null) {
         line = br.readLine();
            HashMap<String, String> releaseMap = new HashMap<String, String>();
            
            if (line != null ){
                String[] txt = line.split(INDEX_SEPARATOR2);
                

                releaseMap.put("releaseID", txt[0]);
                releaseMap.put("releaseName", txt[1]);
                releaseMap.put("releaseType", txt[2]);
                releaseMap.put("releaseMBID", txt[3]);
                
                if(txt.length>=5  && txt[4] !="")
                releaseMap.put("releaseArtist", txt[4]);
                else
                    releaseMap.put("releaseArtist", "");
                

                if(txt.length>=6  && txt[5] !="")
                releaseMap.put("releaseArtistID", txt[5]);
                else
                    releaseMap.put("releaseArtistID", "0");
                
                if(txt.length>=7  && txt[6] !="")
                    releaseMap.put("releaseSong", txt[6]);
                    else
                        releaseMap.put("releaseSong", "");
                
                if(txt.length>=8  && txt[7] !="")
                    releaseMap.put("releaseSongID", txt[7]);
                    else
                        releaseMap.put("releaseSongID", "0");
                
                System.out.println(  releaseMap.get("releaseID"));
                System.out.println(  releaseMap.get("releaseName"));
                System.out.println(  releaseMap.get("releaseArtist"));
                System.out.println(  releaseMap.get("releaseMBID"));
                System.out.println(  releaseMap.get("releaseSong"));
                System.out.println(  releaseMap.get("releaseSongID"));
                
                System.out.println( "----------------");
                releaseList.add(releaseMap);
                
            }
               
              
   //    }
            br.close();
            
        }
        }
         
    }