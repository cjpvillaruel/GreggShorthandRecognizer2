//STEP 1. Import required packages
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class WordDB {
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/dictionarydb";

	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "";
	   
	   public WordDB(){
		   
	   }
	   
	   public ResultSet select(String sql) throws ClassNotFoundException{
		   Connection conn = null;
		   Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();

		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      return rs;
		      
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   
		   }//end try
		return null;
		   
	   }
	   
}
