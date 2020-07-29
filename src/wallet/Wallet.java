package wallet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.json.JSONException;

import database.Database;
import gui.Interface;

public class Wallet {

	public static void main(String[] args) throws UnsupportedEncodingException, SQLException, IOException, JSONException, InterruptedException {
		//parse config file and store parameters
		Config.getInstance().parseConfig();
		
		
		//initialize database
		Database database = new Database();
		database.Connect();
		
		
		//initialize addresses in database and start listening to address usage
		Address addresses = new Address();
		addresses.Reserve(database);
		addresses.start();
		
		//start blockchain parsing for receiving deposits and withdraw confirmations
		Interface frame = new Interface(addresses, database);
		blockchain.Parser parser = new blockchain.Parser(database,frame);
		frame.UpdateTransactions(database.GetTransactions());
		parser.start();

		//wait for threads to finish
		parser.join();
		addresses.join();
		
		
	
	}

}
