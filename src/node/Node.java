package node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.*;
public class Node {
	public class AddressJson{
		public String result;

		public String get(String string) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	// static variable single_instance of type Singleton 
    private static volatile Node instance = null; 
    
    //singleton initialization synchronization purpose
	private static Object mutex = new Object();
	
	//forbid class initialization with constructor from outside
	private Node() {}

    // static method to create instance of Singleton class 
    public static Node getInstance() 
    { 
    	Node result = instance;
		if (result == null) {
			synchronized (mutex) {
				result = instance;
				if (result == null)
					instance = result = new Node();
			}
		}
		return result;
    } 
    
    
    //makes connection to rpc node and checks if parameters are ok
    public ArrayList<String> GenerateAddresses(int addressesNum) throws UnsupportedEncodingException, IOException, JSONException {
    	ArrayList<String> result = new ArrayList<String>();
    	for (int k=0; k<addressesNum; k++) {
    		String jsonResponse = rpc(" --data-binary '{\"jsonrpc\": \"1.0\", \"id\":\"curltest\",\"method\": \"getnewaddress\", \"params\": []}'" + " -H 'content-type:text/plain;'");
    		// parsing file "JSONExample.json" 
    		System.out.println(jsonResponse);

    	}
		return result;
    }
    
    private String rpc(String rpc_params) throws UnsupportedEncodingException, IOException {
    	Process process = Runtime.getRuntime().exec("curl -X POST -s -k " + rpc_params +  " http://" +wallet.Config.getInstance().getNodeUser() + ":" + wallet.Config.getInstance().getNodePassword() +"@" + wallet.Config.getInstance().getNodeHost() + ":" + wallet.Config.getInstance().getNodePort());
    	String rpcResponse = "";
    	System.out.println("curl -X POST -s -k " + rpc_params +  " http://" +wallet.Config.getInstance().getNodeUser() + ":" + wallet.Config.getInstance().getNodePassword() +"@" + wallet.Config.getInstance().getNodeHost() + ":" + wallet.Config.getInstance().getNodePort());
    	try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
    	    for (String line; (line = reader.readLine()) != null;) {
    	    	rpcResponse += line;
    	    }
    	}
		return rpcResponse;
    }
}
