package wallet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;

public class Transaction {
	
	//address to where transaction is sent 
	private String toAddress = null;
	
	//amount to be transfered
	private String amount;
	
	//mark transaction as processed
	private boolean processed = false;
	
	public Transaction(String toAddress, String amount) {
		this.toAddress = toAddress;
		this.amount = amount;
	}
	
	
	//validate amount format and address to be correct bitcoin testnet address
	public boolean Validate() throws UnsupportedEncodingException, IOException, JSONException {
		//check amount string format to be convertible
		try {
	        Double amountDouble = Double.parseDouble(amount);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
		
		return node.Node.getInstance().ValidateAddress(toAddress);
	}
	
	
	
	//make transfer using rpc node to send transaction into network
	public String send(database.Database database) throws UnsupportedEncodingException, IOException, JSONException {
		//check transaction status
		if (processed) return "processed";
		
		//check that balance is sufficient
		Double balance = node.Node.getInstance().GetBalance();
		
		//check format;
		double amountDouble = Double.parseDouble(amount);

		//mark transaction as processed
		processed = true;
		
		//finally send transaction to network
		return node.Node.getInstance().Send(toAddress, amount);
		
	}
}
