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

import org.json.JSONException;

import database.Database;

public class Address extends Thread {
	
	//how many wallets should be reserved to be used as deposit wallet
	private final int reservedAddresses = 3;
	
	
	//initially fills the database with deposit wallets to be distributed
	public void Reserve(Database database) throws SQLException, UnsupportedEncodingException, IOException, JSONException {
		//get reserved addresses number
		int reservedAdressesInDatabase = database.getUnusedAddresses();

		//if we have enough addresses just return
		if (reservedAddresses <= reservedAdressesInDatabase) return;
		
		//generate that many addresses from node
		ArrayList<String> addresses = node.Node.getInstance().GenerateAddresses(reservedAddresses - reservedAdressesInDatabase);

		//insert new addresses into database
		for (int k = 0; k < addresses.size(); k++) database.AddAddress(addresses.get(k));
		
		return;
		
	}
	
	public void run() {
	
	}

}
