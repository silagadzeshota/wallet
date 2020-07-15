package wallet;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;

import database.Database;

public class Wallet {

	public static void main(String[] args) {
		//parse config file and store parameters
		Config.getInstance().parseConfig();
		
		Database database = new Database();
		database.Connect();
		
		
		
		
		
		try {
			Address addresses = new Address();
			addresses.Reserve(database);
			addresses.start();
		} catch (SQLException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
