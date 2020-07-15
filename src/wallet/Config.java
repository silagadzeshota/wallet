package wallet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import node.Node;

public class Config {
	//default config file path 
	final String config_file = "config.cfg";
	
	//after parsing config file paramters will be stored in map
	private static HashMap<String, String> parameters;
	
	//forbid class initialization with constructor from outside
	private Config() {
		parameters = new HashMap<String,String>();
	}
	
	 // static variable single_instance of type Singleton 
    private static volatile Config instance = null; 
    
    //singleton initialization synchronization purpose
	private static Object mutex = new Object();
	

    // static method to create instance of Singleton class 
    public static Config getInstance() 
    { 
    	Config result = instance;
		if (result == null) {
			synchronized (mutex) {
				result = instance;
				if (result == null)
					instance = result = new Config();
			}
		}
		return result;
    } 
	
	//parse parses config file line by line storing parameters into map
	public void parseConfig() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(config_file));
			String line = reader.readLine();
			while (line != null && line != "") {
				int ind = line.indexOf('=');
				if (ind == -1) {
					line = reader.readLine();
					if (ind == -1) continue;
				}
				parameters.put(line.substring(0,ind),line.substring(ind+1,line.length()));
				line = reader.readLine();
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/* All getters for parameters of config file*/
	public String getNodeHost() {
		return parameters.get("NODE_HOST");
	}
	
	public String getNodePort() {
		return parameters.get("NODE_PORT");
	}
	
	public String getNodeUser() {
		return parameters.get("NODE_USER");
	}
	
	public String getNodePassword() {
		return parameters.get("NODE_PASSWORD");
	}
	
	public String getDatabaseHost() {
		return parameters.get("DATABASE_HOST");
	}
	
	public String getDatabaseName() {
		return parameters.get("DATABASE_NAME");
	}
	
	public String getDatabasePort() {
		return parameters.get("DATABASE_PORT");
	}
	
	public String getDatabaseUser() {
		return parameters.get("DATABASE_USER");
	}
	
	public String getDatabasePassword() {
		return parameters.get("DATABASE_PASSWORD");
	}
	
}
