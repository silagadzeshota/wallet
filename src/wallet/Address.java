package wallet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;

import database.Database;

public class Address extends Thread {
	
	//how many wallets should be reserved to be used as deposit wallet
	private final int reservedAddresses = 3;
	
	//lock for address usage signal
	final Lock lock = new ReentrantLock();
	
	//cond var for that address was used and should be filled
	final Condition addressSignal = lock.newCondition(); 
	
	//database class works on
	Database database = null;
	
	//initially fills the database with deposit wallets to be distributed
	public void Reserve(Database database) throws SQLException, UnsupportedEncodingException, IOException, JSONException {
		//save database 
		this.database = database;
		
		//get reserved addresses number
		int reservedAdressesInDatabase = database.getUnusedAddresses();

		//if we have enough addresses just return
		if (reservedAddresses <= reservedAdressesInDatabase) return;
		
		//generate that many addresses from node
		ArrayList<String> addresses = node.Node.getInstance().GenerateAddresses(reservedAddresses - reservedAdressesInDatabase);

		//insert new addresses into database
		for (int k = 0; k < addresses.size(); k++) database.AddAddress(addresses.get(k));
		
	}
	
	//returning new address from database
	public final String GetAddress() throws SQLException {
		String address = database.GetNewAddress();
		addressSignal.signal();
		return address;
	}
	
	public void run() {
		//fill addresses when used from database
		while (true) {
			
			lock.lock();
			try {
				while (reservedAddresses <= database.getUnusedAddresses()) addressSignal.await();
			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//generate that many addresses from node
			ArrayList<String> addresses = null;
			try {
				addresses = node.Node.getInstance().GenerateAddresses(reservedAddresses - database.getUnusedAddresses());
			} catch (IOException | JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//insert new addresses into database
			for (int k = 0; k < addresses.size(); k++)
				try {
					database.AddAddress(addresses.get(k));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			lock.unlock();
		}
		
	}

}
