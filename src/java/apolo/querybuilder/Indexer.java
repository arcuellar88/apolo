package apolo.querybuilder;
import java.io.IOException;
import java.util.HashMap;

import apolo.querybuilder.ArtistReleaseDataPreparation;

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
     ArtistReleaseDataPreparation dp =null;
    
    /** Creates a new instance of Indexer */
    public Indexer(Directory indexDir, Analyzer analyzer) {
        this.analyzer = analyzer;
        this.indexDir = indexDir;
          dp = new ArtistReleaseDataPreparation();
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

          // configure index properties
         Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
          Directory indexDir = new RAMDirectory();
        
          long start = System.currentTimeMillis();       
          
          
          // build a lucene index
     /*
          System.out.println("---rebuild: Indexe Artist...");
        Indexer indexer = new Indexer(indexDir, analyzer);
        indexer.rebuildIndexArtist();
        System.out.println("---rebuild Indexes Artist done in: "+ (System.currentTimeMillis() - start));
        */
          System.out.println("---rebuild: Indexe Release...");
          Indexer indexer = new Indexer(indexDir, analyzer);
          indexer.rebuildIndexRelease();
          System.out.println("---rebuild Indexes Release done in: "+ (System.currentTimeMillis() - start));
        
      } catch (Exception e) {
        System.out.println("Exception caught.");
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
    
    
    
}