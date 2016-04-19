//STEP 1. Import required packages
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class JDBCExample {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/dictionarydb";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "";
   
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");
      stmt = conn.createStatement();
//      //STEP 4: Execute a query
//      System.out.println("Inserting records into the table...");
//     
//      
//      String sql = "INSERT INTO Registration " +
//                   "VALUES (100, 'Zara', 'Ali', 18)";
//      stmt.executeUpdate(sql);
//      sql = "INSERT INTO Registration " +
//                   "VALUES (101, 'Mahnaz', 'Fatma', 25)";
      
      //read file
      String sCurrentLine;
      BufferedReader br = null;
		br = new BufferedReader(new FileReader("words2.txt"));

		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);
			String[] words= sCurrentLine.split("/");
			if(words.length== 1){
				String sql = "INSERT INTO word (word, related_words)" +
		                   "VALUES ('"+words[0]+"',"+"''"+")";
				System.out.println(sql);
				stmt.executeUpdate(sql);
			}
			else{
				String related="";
				for(int i=1;i<words.length;i++){
					related+=words[i]+",";
				}
				String sql = "INSERT INTO word (word, related_words)" +
		                   "VALUES ('"+words[0]+"','"+related+"' )";
				//System.out.println(sql);
				stmt.executeUpdate(sql);
			}
			 
		}

      

   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            conn.close();
      }catch(SQLException se){
      }// do nothing
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}//end main
}//end JDBCExample