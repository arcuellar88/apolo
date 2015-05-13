package apolo.querybuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import apolo.querybuilder.ArtistReleaseDataPreparation;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;



public class Indexer {

     Analyzer analyzer;
     Directory indexDir;
     IndexWriter indexWriter = null;
     ArtistReleaseDataPreparation1 dp =null;
    
    /** Creates a new instance of Indexer */
    public Indexer(Directory indexDir, Analyzer analyzer) {
        this.analyzer = analyzer;
        this.indexDir = indexDir;
          dp = new ArtistReleaseDataPreparation1();
    }
 
    
    /** Gets index writer */
    public IndexWriter getIndexWriter() throws IOException {
        if (indexWriter == null) {
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
   }    
   
    
    /** Closes index writer */
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
   }
    
    
    /** Deletes all documents in the index */
    public void deleteIndex() throws IOException {
        if (indexWriter != null) {
            indexWriter.deleteAll();
        }
   }

    
    /** Adds a document (a artist) to the index */ 
    
   public void indexArtist(HashMap<String, String> artist) throws IOException {
        
         IndexWriter writer = getIndexWriter();
         Document doc = new Document();
         doc.add(new StoredField("documentId", "artist_"+artist.get("artistID")));

         doc.add(new StoredField("artistID", artist.get("artistID")));
         doc.add(new TextField("artistName", artist.get("artistName"), Field.Store.YES));
         doc.add(new StringField("artistGender", artist.get("artistGender"), Field.Store.YES));
         doc.add(new StringField("artistType", artist.get("artistType"), Field.Store.YES));
         doc.add(new StringField("artistCountry", artist.get("artistCountry"), Field.Store.YES));
         doc.add(new StringField("artistContinent", artist.get("artistContinent"), Field.Store.YES)); 
         
         
         if (artist.get("artistRatingAVG") != null)
         doc.add(new DoubleField("artistRatingAVG", Double.parseDouble(artist.get("artistRatingAVG")), Field.Store.YES));   
         else
         doc.add(new DoubleField("artistRatingAVG", 0.0, Field.Store.YES));  
         
         if (artist.get("artistRatingCount") != null)
             doc.add(new IntField("artistRatingCount", Integer.parseInt(artist.get("artistRatingCount")), Field.Store.YES));   
         else
             doc.add(new IntField("artistRatingCount", 0, Field.Store.YES));   
         
         if (artist.get("artistP") != null)
         doc.add(new IntField("artistP", Integer.parseInt(artist.get("artistP")), Field.Store.YES));  
         else
             doc.add(new IntField("artistP", 0, Field.Store.YES)); 
         
         if (artist.get("artistNU") != null)
             doc.add(new IntField("artistNU", Integer.parseInt(artist.get("artistNU")), Field.Store.YES));    
         else
         doc.add(new IntField("artistNU", 0, Field.Store.YES));    
    
         if (artist.get("artistN") != null)
             doc.add(new IntField("artistN", Integer.parseInt(artist.get("artistN")), Field.Store.YES));      
         else
             doc.add(new IntField("artistN", 0, Field.Store.YES));     
        
         if (artist.get("artistMBID") != null)
             doc.add(new IntField("artistMBID", Integer.parseInt(artist.get("artistMBID")), Field.Store.YES));      
         else
             doc.add(new IntField("artistMBID", 0, Field.Store.YES));  
         
         writer.addDocument(doc);
         
     }   
    
   /** Adds a document (a release) to the index */ 
   
   public void indexRelease(HashMap<String, String> release) throws IOException {
        
         IndexWriter writer = getIndexWriter();
         Document doc = new Document();
         
         doc.add(new StoredField("documentId", "release_"+release.get("releaseID")));

         doc.add(new StoredField("releaseID", release.get("releaseID")));
         doc.add(new TextField("releaseName", release.get("releaseName"), Field.Store.YES));
         doc.add(new StringField("releaseType", release.get("releaseType"), Field.Store.YES));
         
         
         if (release.get("releaseMBID") != null)
         doc.add(new IntField("releaseMBID",Integer.parseInt(release.get("releaseMBID")), Field.Store.YES));   
         else
         doc.add(new DoubleField("releaseMBID", 0, Field.Store.YES));  
         
         if (release.get("releaseArtist") != null)
             doc.add(new TextField("releaseArtist", release.get("releaseArtist"), Field.Store.YES));  
         else
             doc.add(new TextField("releaseArtist","", Field.Store.YES));   
         
         if (release.get("releaseArtistID") != null)
         doc.add(new IntField("releaseArtistID", Integer.parseInt(release.get("releaseArtistID")), Field.Store.YES));  
         else
             doc.add(new IntField("releaseArtistID", 0, Field.Store.YES));  
         
         if (release.get("releaseSong") != null)
             doc.add(new TextField("releaseSong",release.get("releaseSong"), Field.Store.YES));   
         else
             doc.add(new TextField("releaseSong","", Field.Store.YES));   
    
         if (release.get("releaseSongID") != null)
             doc.add(new TextField("releaseSongID",release.get("releaseSongID"), Field.Store.YES));    
         else
             doc.add(new TextField("releaseSongID","", Field.Store.YES));   
        
         
         
         writer.addDocument(doc);
         
     }   
   
   
    /** Rebuilds index */
    public void rebuildIndexArtist() throws IOException {
          // Erase existing index
          deleteIndex();

          // Index 
          dp.readArtist();
        
          for(HashMap<String, String> artist : dp.artistList) {
              indexArtist(artist);              
          }

          // Don't forget to close the index writer when done
          closeIndexWriter();
     }
    
    /** Rebuilds index */
    public void rebuildIndexRelease() throws IOException {
          // Erase existing index
          deleteIndex();

          // Index 
          dp.readRelease();
        
          for(HashMap<String, String> release : dp.releaseList) {
              indexRelease(release);              
          }

          // Don't forget to close the index writer when done
          closeIndexWriter();
     }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

      try {

          // configure index propertie
        
          long start = System.currentTimeMillis();       
          
          // build a lucene index
     
          System.out.println("---rebuild: Indexe Artist...");
        
          Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
         Directory indexDir = new RAMDirectory();
        
        Indexer indexer = new Indexer(indexDir, analyzer);
        indexer.rebuildIndexArtist();
        System.out.println("---rebuild Indexes Artist done in: "+ (System.currentTimeMillis() - start));
       
        System.out.println("---rebuild: Indexe Release...");
        
        Analyzer analyzer1 = new StandardAnalyzer(Version.LUCENE_46);
         Directory indexDir1 = new RAMDirectory();
        
        Indexer indexer1 = new Indexer(indexDir1, analyzer1);
        
         indexer1.rebuildIndexRelease();
        
          System.out.println("---rebuild Indexes Release done in: "+ (System.currentTimeMillis() - start));
        
      } catch (Exception e) {
        System.out.println("Exception caught.");
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
}



class ArtistReleaseDataPreparation1 {
    /**
     * For song extraction
     */
    public final String releaseFile = "data/Exp_File_3.txt";
    public final String artistFile = "data/artist1.csv";
    public  final List<HashMap<String, String>> artistList = new ArrayList<HashMap<String, String>>();
    public  final List<HashMap<String, String>> releaseList = new ArrayList<HashMap<String, String>>();
    public  final List<HashMap<String, String>> releaseSongList = new ArrayList<HashMap<String, String>>();

    public final static String INDEX_SEPARATOR1 = "\\[\\|\\]";
    public final static String INDEX_SEPARATOR2 = "\\{";
   
  /*
 public static void main(String[] args) throws IOException {
        
        ArtistReleaseDataPreparation dp = new ArtistReleaseDataPreparation();
        
        long start = System.currentTimeMillis();
     // dp.readArtist();
      dp.readRelease();
     
        System.out.println(" Done..loaded_IN: " + (System.currentTimeMillis() - start));
        
    
    } */
 
 
    
    public void readArtist() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(artistFile));
        String line;
        
        

line = br.readLine();
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

        br.close();
    }

    
    public void readRelease() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(releaseFile));
        String line;
   
        
 // while ((line = br.readLine()) != null) {
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
                     
           
            releaseList.add(releaseMap);
            
        }
           
          
    // }
        br.close();
    }

}