package blockchain;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONException;

public class Parser extends Thread{
	
	//current database parser works on
	database.Database database = null;
	
	//for checking current last block in blockchain
	public class BlockHeader{
		public BlockHeader(int blockHeight, String blockHash, String prevBlockHash) {
			this.blockHeight = blockHeight;
			this.blockHash = blockHash;
			this.prevBlockHash = prevBlockHash;
		}
		
		public BlockHeader() {
			
		}
		
 		//current block hash
		public String blockHash;
		
		//previous block hash from blockchain
		public String prevBlockHash;
		
		//current block index in blockchain
		public int blockHeight;
		
	}
	
	//create initializing constructors
	public Parser(database.Database database) {
		this.database = database;
	}
	public Parser() {
		this.database = database;
	}
	
	public void run() {
		//endlessly check new blocks and process for native transactions
		while (true) {
			//get last mined block from blockchain
			BlockHeader blockHeader;
			try {
				blockHeader = node.Node.getInstance().GetLastBlock();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
				continue;
			}
			System.out.println("BHHGGGG");
			
			//check if block associated with given hash is processed
			boolean processed;
			try {
				processed = database.IsBlockProcessed(blockHeader.blockHash);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			//process blocks recursively
			boolean success;
			try {
				success = ProcessBlock(blockHeader);
			} catch (IOException | JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			if (!success) {
				System.out.println("Cannot process block");
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	  function enters while loop and  retrieves the block index and hash for the last block of blockchain.
	  if previous hash isn't added (and that happens when chain is forked or block is added very fast) we
	  recursively dive back into previous blocks until we find block we have parsed. along the path we delete
	  canceled (forked) block's transactions and, on the way back, add new transactions for the same block index
	  but from valid block.
	*/
	public boolean ProcessBlock(BlockHeader blockHeader) throws UnsupportedEncodingException, IOException, JSONException, SQLException {
		//first check if previous block is processed if not go back recursively
		//check if block associated with given hash is processed
		boolean processed;
		try {
			processed = database.IsBlockProcessed(blockHeader.blockHash);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		//if not processed process previous block
		if (!processed) {
			System.out.println("GGGG");
			BlockHeader prevBlockHeader = node.Node.getInstance().GetBlockHeaderByHash(blockHeader.prevBlockHash);
			if (!ProcessBlock(prevBlockHeader)) {
				return false;
			}
		}
		
		boolean transactionsProcessed = ProcessTransactions(blockHeader);
		if (transactionsProcessed == true) {
			database.NewBlock(blockHeader);
			return true;
		} else {
			return false;
		}
		
	}
	
	//processing individual transactions parsed from block with given header
	public boolean ProcessTransactions(BlockHeader blockHeader) throws UnsupportedEncodingException, IOException, JSONException, SQLException {
		//get block transactions 
		ArrayList<wallet.Transaction> transactions = node.Node.getInstance().GetBlockTransactions(blockHeader);
		for (int k=0; k<transactions.size(); k++) {
			if (database.IsWithdrawTransaction(transactions.get(k))) database.InsertNewTransaction(transactions.get(k), false); 
			if (database.IsDepositAddress(transactions.get(k))) database.InsertNewTransaction(transactions.get(k), true); 
		}
		return true;
	}
}
 