package wallet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.json.JSONException;

public class Transaction {
	
	//address to where transaction is sent 
	public String toAddress = null;
	
	//amount to be transfered
	public String amount;
	
	//tx id in blockchain
	public String transactionId;
	
	//mark transaction as processed
	private boolean processed = false;
	
	public Transaction(String toAddress, String amount) {
		this.toAddress = toAddress;
		this.amount = amount;
	}
	
	
	public Transaction(String toAddress, String amount, String txid) {
		this.toAddress = toAddress;
		this.amount = amount;
		this.transactionId = txid;
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
	public String send(database.Database database) throws  IOException, JSONException, SQLException {
		//check transaction status
		if (processed) return "processed";
		
		//check that balance is sufficient
		Double balance = node.Node.getInstance().GetBalance();
		if (this.amount == null || amount == "" || amount.isEmpty()) return "Incorrect amount format";
		if (toAddress == null || toAddress == "" || toAddress.isEmpty()) return "Incorrect address";
		//check format;
		double amountDouble = Double.parseDouble(amount);
		
		System.out.println(balance.toString());
		System.out.println(amountDouble);
		if (balance < amountDouble) return "Not enough balance";

		//mark transaction as processed
		processed = true;
		
		//finally send transaction to network
		this.transactionId = node.Node.getInstance().Send(toAddress, amount);
		
		
		//save as withdraw transaction to know during parsing
		if (this.transactionId != null) database.SaveWithdraw(this, this.transactionId);
		
		return this.transactionId;
	}
}
