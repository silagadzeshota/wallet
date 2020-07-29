package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	    String query2 = "SELECT address FROM addresses where used= false limit 1";
	
	    // create the java statement
	    Statement st2 = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    ResultSet rs = st2.executeQuery(query2);
	      
        // iterate through the java resultset
	    String address = null;
        while (rs.next()){
        	address = rs.getString("address"); 
	    }
        st2.close();
        
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "UPDATE addresses set used = true where address = '" + address + "'";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    st.execute(query);
	      
       
	    st.close();
	    
	    return address;
	}
	
	
	//check if transacation is that we requested when withdrawing
	public boolean IsWithdrawTransaction(wallet.Transaction transaction) throws SQLException {
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "SELECT count(*) as count FROM withdraws where transaction_id='" + transaction.transactionId+"' and address = '" + transaction.toAddress + "'";
	
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
	    
	    if (count == 1) return true; 
	    
		       
	    return false;
	}
	
	
	//save withdraw to know during parsing that it's our withdraw transaction
	public void SaveWithdraw(wallet.Transaction transaction,String withdrawId) throws SQLException {
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "INSERT INTO withdraws(address, amount, transaction_id) values('"+ transaction.toAddress + "'," + transaction.amount+ ", '" +transaction.transactionId + "')";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    st.execute(query);
	    st.close(); 
	}
	
	
	//save new processed block
	public void NewBlock(blockchain.Parser.BlockHeader blockHeader) throws SQLException {
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "INSERT INTO blocks(block_hash, prev_block_hash, block_height) values('"+ blockHeader.blockHash+ "','" + blockHeader.prevBlockHash+ "', " +blockHeader.blockHeight + ")";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    st.execute(query);
	    st.close(); 
	}
	
	
	//cecking for deposit address if exists
	public boolean IsDepositAddress(wallet.Transaction transaction) throws SQLException {
		// our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
	    String query = "SELECT count(*) as count FROM addresses where address='" + transaction.toAddress+"' and used = true";
	
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
	    
	    if (count == 1) return true; 
	    
		       
	    return false;
	}
	
	//cecking for deposit address if exists
		public ArrayList<wallet.Transaction> GetTransactions() throws SQLException {
			// our SQL SELECT query. 
		    // if you only need a few columns, specify them by name instead of using "*"
		    String query = "SELECT transaction_id, address, amount, is_deposit FROM transactions";
		    ArrayList<wallet.Transaction> transactions = new ArrayList<wallet.Transaction>();
		    // create the java statement
		    Statement st = conn.createStatement();
		      
		    // execute the query, and get a java resultset
		    ResultSet rs = st.executeQuery(query);
		      
	        // iterate through the java resultset
	        while (rs.next()){
	        	transactions.add(new wallet.Transaction(rs.getString("address"), rs.getString("amount"), rs.getString("transaction_id")));
		    }
		    st.close();
		    
		    return transactions;
		}
	
	//insert new native transaction
	public void InsertNewTransaction(wallet.Transaction transaction, boolean deposit) throws SQLException {
		 // our SQL SELECT query. 
	    // if you only need a few columns, specify them by name instead of using "*"
		String deposittx  = "";
		if (deposit) deposittx = "true"; else deposittx = "false";
	    String query = "INSERT INTO transactions(address, amount, transaction_id, is_deposit) values('"+ transaction.toAddress + "'," + transaction.amount+ ", '" +transaction.transactionId + "', " + deposittx+ ")";
	
	    // create the java statement
	    Statement st = conn.createStatement();
	      
	    // execute the query, and get a java resultset
	    st.execute(query);
	    st.close(); 
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
