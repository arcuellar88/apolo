package apolo.msc;

public class Global_Configuration 
{


	// --------------------------------------------------------
	// MYSQL
	// --------------------------------------------------------
	public static final String MYSQL = "MYSQL";
	public final static String MYSQL_HOST="localhost";
	public final static String MYSQL_USER="root";
	public final static String MYSQL_PWD="";
	public final static String MYSQL_DB="apolo";

	// --------------------------------------------------------
	// ORACLE
	// --------------------------------------------------------
	public final static String ORACLE="Oracle";

	public final static String ORACLE_HOST="164.15.78.16";
	public final static String ORACLE_USER="ora_stagews";
	public final static String ORACLE_PWD="ora_stagews";
	public final static String ORACLE_DB="ORCL";

	// --------------------------------------------------------
	// LUCENE
	// --------------------------------------------------------
	
	//Do not trim spaces in INDEX_SEPARATOR, separator 
	public final static String INDEX_SEPARATOR = " [|] ";
	public final static String INDEX_DELIMITER = "\\[\\|\\]";
	public final static String INDEX_DIRECTORY = "index";
	
	// --------------------------------------------------------
	// YOUTUBE
	// --------------------------------------------------------
		
	public final static String YOUTUBE_KEY = "";
	
	// --------------------------------------------------------
	// MOOMASH
	// --------------------------------------------------------
	
	public final static String MOOMASH_KEY = "";
	
	// --------------------------------------------------------
	// OTHER
	// --------------------------------------------------------
	
	public final static String DATA_FOLDER = "";
	public final static String CODEGEN_FOLDER = "";
	public final static String TEMP_FOLDER = "";
}
