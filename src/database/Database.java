package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import wallet.Config;

public class Database {
	
	//connection that will be used for the application
	public Connection conn;
	
	//driver used for connection
	private final String myDriver = "com.mysql.cj.jdbc.Driver";
	
	//creates and initializes connection
	public void Connect() {
		// create our mysql database connection
        String myUrl = "jdbc:mysql://" + Config.getInstance().getDatabaseHost() + ":" + Config.getInstance().getDatabasePort() + "/" + Config.getInstance().getDatabaseName();
        try {
			Class.forName(myDriver);
			this.conn = DriverManager.getConnection(myUrl, Config.getInstance().getDatabaseUser(), Config.getInstance().getDatabasePassword());
        } catch (ClassNotFoundException | SQLException e) {
        	System.err.println("Got an exception! ");
  	        System.err.println(e.getMessage());
		}
	}
	
	public void InitializeDatabase() {
//		  //Initialize the script runner
//	      ScriptRunner sr = new ScriptRunner(con);
//	      //Creating a reader object
//	      Reader reader = new BufferedReader(new FileReader("E:\\sampleScript.sql"));
//	      //Running the script
//	      sr.runScript(reader);
	}
	
	//retrieves new unused address from database and returns to user (marking address as used)
	public String GetNewAddress() throws SQLException {
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "UPDATE addresses set used = true where used = false limit 1 returning address";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    ResultSet rs = st.executeQuery(query);
	      
        // iterate through the java resultset
	    String address = null;
        while (rs.next()){
        	address = rs.getString("address"); 
	    }
	    st.close();
	    return address;
	}
	
	
	/* query returns the number of unused addresses that are reserved for further usage*/
	public int getUnusedAddresses() throws SQLException {
	    // our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "SELECT count(*) as count FROM addresses where used = false";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    ResultSet rs = st.executeQuery(query);
	      
        // iterate through the java resultset
	    int count = 0;
        while (rs.next()){
	      count = rs.getInt("count"); 
	    }
	    st.close();
		       
	    return count;
	}
	
	public boolean IsBlockProcessed(String blockHash) throws SQLException {
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "SELECT count(*) as count FROM blocks where block_hash = '" + blockHash +"'";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    ResultSet rs = st.executeQuery(query);
	      
        // iterate through the java resultset
	    int count = 0;
        while (rs.next()){
	      count = rs.getInt("count"); 
	      if (count == 0) {
	    	  st.close();
	    	  return false;
	      } else {
	    	  st.close();
	    	  return true;
	      }
	    }
        
        return false;
	    
	}
	
	
	/* adds newly generated address into database as yet unused */
	public void AddAddress(String address) throws SQLException {
	    // our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "INSERT INTO addresses(address, used) values('"+ address + "', false)";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    st.execute(query);
	    st.close(); 
    }
}
