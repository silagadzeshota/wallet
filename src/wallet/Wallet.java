package wallet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.json.JSONException;

import database.Database;

public class Wallet {

	public static void main(String[] args) throws UnsupportedEncodingException, SQLException, IOException, JSONException, InterruptedException {
		//parse config file and store parameters
		Config.getInstance().parseConfig();
		
		Database database = new Database();
		database.Connect();
		
		
		Address addresses = new Address();
		addresses.Reserve(database);
		addresses.start();
		

		addresses.join();
		
		
	
	}

}
