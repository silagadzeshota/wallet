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
    		String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"generateaddress\",\"method\": \"getnewaddress\", \"params\": []}");
    		// parsing file "JSONExample.json" 
    		JSONObject obj = new JSONObject(jsonResponse);
    		result.add(obj.getString("result"));
    	}
		return result;
    }
    
    
    //send transaction to network
    public String Send(String toAddress, String amount) throws UnsupportedEncodingException, IOException, JSONException {
    	String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"se\",\"method\": \"sendtoaddress\", \"params\": [\"" + toAddress + "\", " + amount +"]}");
		// parsing file "JSONExample.json" 
		JSONObject obj = new JSONObject(jsonResponse);
		return obj.getString("result");
    }
    
    //validate address to be correct format for testnet bitcoin address
    public boolean ValidateAddress(String toAddress) throws UnsupportedEncodingException, IOException, JSONException {
    	String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"se\",\"method\": \"validateaddress\", \"params\": [\"" + toAddress + "\"]}");
		// parsing file "JSONExample.json" 
		JSONObject obj = new JSONObject(jsonResponse);
    	return obj.getJSONObject("result").getBoolean("isvalid");
    }
    
    
    //returns casino balance that node stores 
    public Double GetBalance() throws UnsupportedEncodingException, IOException, JSONException {
    	String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"balance\",\"method\": \"getbalance\", \"params\": []}");
    	JSONObject obj = new JSONObject(jsonResponse);
    	return obj.getDouble("result");
    }
    
    
    //get last mined block from blockchain
    public blockchain.Parser.BlockHeader GetLastBlock() throws UnsupportedEncodingException, IOException, JSONException {
    	//get index first and then index associated block he
    	String jsonResponse1 = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"balance\",\"method\": \"getblockcount\", \"params\": []}");
    	JSONObject obj = new JSONObject(jsonResponse1);
    	String blockIndex = String.valueOf(obj.getInt("result"));
    	
    	
    	//get index first and then index associated block he
    	String jsonResponse2 = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"balance\",\"method\": \"getblockhash\", \"params\": ["+ blockIndex + "]}");
    	JSONObject obj2 = new JSONObject(jsonResponse2);
    	String blockHash = obj2.getString("result");
    	
    	return GetBlockHeaderByHash(blockHash);
    }
    
    
    //returns block header by block hash
    public blockchain.Parser.BlockHeader GetBlockHeaderByHash(String blockHash) throws UnsupportedEncodingException, IOException, JSONException {
    	//get index first and then index associated block he
    	String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"balance\",\"method\": \"getblock\", \"params\": [\""+ blockHash + "\"]}");
    	JSONObject obj = new JSONObject(jsonResponse);
    	blockchain.Parser.BlockHeader blockHeader = new blockchain.Parser().new BlockHeader(obj.getJSONObject("result").getInt("height"), blockHash, obj.getJSONObject("result").getString("previousblockhash"));
    	return blockHeader;
    }
    
    //returns block transactions
    public ArrayList<wallet.Transaction> GetBlockTransactions(blockchain.Parser.BlockHeader blockHeader) throws UnsupportedEncodingException, IOException, JSONException {
    	//get index first and then index associated block he
    	String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"balance\",\"method\": \"getblock\", \"params\": [\""+ blockHeader.blockHash + "\"]}");
    	JSONObject obj = new JSONObject(jsonResponse);
    	JSONArray txArray = obj.getJSONObject("result").getJSONArray("tx");
    	
    	
    	ArrayList<wallet.Transaction> txIds = new ArrayList<wallet.Transaction>();
    	//process transactions with transaction id
    	for (int k=0; k<txArray.length(); k++) {
    		ProcessTransaction(txArray.get(k).toString(),txIds);
    	}
    
    	return txIds;
    }
    
    
    //obtains transaction inner data from txid
    private void ProcessTransaction(String txid, ArrayList<wallet.Transaction> txIds) throws UnsupportedEncodingException, IOException {
    	//get index first and then index associated block he
    	String jsonResponse = rpc("{\"jsonrpc\": \"1.0\", \"id\":\"balance\",\"method\": \"getrawtransaction\", \"params\": [\""+ txid + "\", true]}");
    	JSONObject obj;
		try {
			obj = new JSONObject(jsonResponse);
			JSONObject result = obj.getJSONObject("result");
	    	if (result.getJSONArray("vout") != null) {
	    		//process transactions with transaction id
	        	for (int k=0; k<result.getJSONArray("vout").length(); k++) {
	        		JSONObject transfer = (JSONObject) result.getJSONArray("vout").get(k);
	        		if (!transfer.getJSONObject("scriptPubKey").has("addresses")) continue;
	        		txIds.add(new wallet.Transaction(transfer.getJSONObject("scriptPubKey").getJSONArray("addresses").get(0).toString(), String.valueOf(transfer.getDouble("value")), txid));
	        	}
	        
	    	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

    }
    
    
    //main functinn that executes curl command and returns result as a string (json)
    private String rpc(String rpc_params) throws UnsupportedEncodingException, IOException {
    	Process process = Runtime.getRuntime().exec(new String[] {"curl", "-X", "POST", "-s", "-k","-d", rpc_params  ,"http://" +wallet.Config.getInstance().getNodeUser() + ":" + wallet.Config.getInstance().getNodePassword() +"@" + wallet.Config.getInstance().getNodeHost() + ":" + wallet.Config.getInstance().getNodePort()});
    	String rpcResponse = "";
    	try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
    	    for (String line; (line = reader.readLine()) != null;) {
    	    	rpcResponse += line;
    	    }
    	}
		return rpcResponse;
    }
    
    
}
