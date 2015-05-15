package apolo.db;

import java.sql.*;

import apolo.entity.*;
import apolo.msc.Global_Configuration;
import apolo.msc.Log;

public class DWConnector {

	private Connection connection = null;

	public DWConnector() {
		connect();
	}

	private void connect() {
		if (connection == null) {
			try {
				// this will load the MySQL driver, each DB has its own driver
				Class.forName("oracle.jdbc.driver.OracleDriver");
				// setup the connection with the DB.
				String host = Global_Configuration.ORACLE_HOST;
				String user = Global_Configuration.ORACLE_USER;
				String pwd = Global_Configuration.ORACLE_PWD;
				String db = Global_Configuration.ORACLE_DB;
				connection = DriverManager.getConnection("jdbc:oracle:thin:@//"
						+ host + ":1521/" + db, user, pwd);

				Log.println("CONNECTED: oracle.jdbc.driver.OracleDriver");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reads a query from a file
	 * @param fileName
	 * @return
	 */
	public Artist getArtist(int artist_id) {
		
		// query="SELECT ROWNUM ROW_NUM, SUBQ.* FROM (select ARTIST_ID, ARTIST_NAME, ARTIST_TYPE, GENDER from  APOLO_MASTER.DIMARTIST) SUBQ DWHERE ROWNUM <= 75";
		String query = "select ARTIST_ID, ARTIST_NAME, ARTIST_TYPE, GENDER from  APOLO_MASTER.DIMARTIST where SID=?";
		Artist artist=null;

		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, artist_id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				 artist = new Artist();

				artist.setArtist_id(rs.getInt("artist_id"));
				artist.setGender(rs.getString("GENDER"));
				artist.setName(rs.getString("ARTIST_NAME"));
				artist.setType(rs.getString("ARTIST_TYPE"));

				//Log.print(artist.toString());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return artist;
	}

	public static void main(String[] args) {
		DWConnector db = new DWConnector();

		// do not commit login details!
		try {

			db.getArtist(4264124);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
