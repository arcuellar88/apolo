package apolo.db;

import java.sql.*;

import apolo.entity.*;
import apolo.msc.Global_Configuration;
import apolo.msc.Log;

public class DWConnector {
	
	private Connection connect = null;
	
	public DWConnector()
	{
		
	}
	
	public void connect(String host, String user, String pwd, String db)
	{
		try
		{
		 // this will load the MySQL driver, each DB has its own driver
	      Class.forName("oracle.jdbc.driver.OracleDriver");
	      // setup the connection with the DB.
	      connect = DriverManager.getConnection("jdbc:oracle:thin:@//"+host+":1521/"+db,user,pwd);
	      
	      Log.println("CONNECTED");
		}
	    catch (Exception e)
		{ 
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a query from a file
	 * @param fileName
	 * @return
	 */
	public IArtist getArtist(int artist_id)
	{
		//String query="SELECT ROWNUM ROW_NUM, SUBQ.* FROM (select ARTIST_ID, ARTIST_NAME, ARTIST_TYPE, GENDER from  APOLO_MASTER.DIMARTIST) SUBQ DWHERE ROWNUM <= 75";
		String query="select ARTIST_ID, ARTIST_NAME, ARTIST_TYPE, GENDER from  APOLO_MASTER.DIMARTIST where artist_id=?";
		
		IArtist artist=new Artist();
		
		try 
		{
			PreparedStatement ps=connect.prepareStatement(query);
			ps.setInt(1, artist_id);
	        ResultSet rs = ps.executeQuery();
	        
	        while (rs.next()) {
	        	artist.setArtist_id(rs.getInt("artist_id"));
	        	artist.setGender(rs.getString("GENDER"));
	           artist.setName(rs.getString("ARTIST_NAME"));
	           artist.setType(rs.getString("ARTIST_TYPE"));

	           Log.print(artist.toString());
	        }
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return artist;
	}
	
	
	public static void main(String[] args)
	{
		DWConnector db = new DWConnector();
		
		//do not commit login details!
		db.connect(Global_Configuration.ORACLE_HOST,Global_Configuration.ORACLE_USER,Global_Configuration.ORACLE_PWD,Global_Configuration.ORACLE_DB);
		try {
			
			db.getArtist(4264124);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
