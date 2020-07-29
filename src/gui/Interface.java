package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;



public class Interface extends JFrame {
	private JTextArea text1;
	private JTextArea text2;
	private JCheckBox autoCheck;
	
	//table initial contant
	JTable jt;
	String data[][]={};    
    String column[]={"Tx","Amount", "Type"};  
 // Private variables of the GUI components
    JTextField tField;
    JTextField pwField;
    JTextArea tArea;
    JFormattedTextField formattedField;
    JTextField depositAddress;
    JLabel message;
    JLabel balance;
    database.Database database = null;
    
    DefaultTableModel model;
    
    wallet.Address address;
	public Interface(wallet.Address addresss, database.Database database) throws SQLException, UnsupportedEncodingException, IOException, JSONException {
		  super("");
		  this.address = addresss;
		  this.database = database;
		  
	      setVisible(true);
	      // JPanel for the text fields
		  JPanel panel= new JPanel(new GridLayout(5, 2, 10, 2));
		  panel.setBorder(BorderFactory.createTitledBorder("Make Withdraw: "));
	      // Regular text field (Row 1)
	      panel.add(new JLabel("  Address: "));
	      tField = new JTextField(10);
	      panel.add(tField);
	 
	     // panel.add(depositAddress);
	      // Password field (Row 2)
	      panel.add(new JLabel("  Amount: "));
	      pwField = new JTextField(10);
	      panel.add(pwField);
	     
	      // Formatted text field (Row 3)
	      JButton send = new JButton("Send");
	      send.addActionListener(new ActionListener(){  
	    	  public void actionPerformed(ActionEvent e){  
	    		 wallet.Transaction transaction = new wallet.Transaction(tField.getText(), pwField.getText());
	    		 tField.setText("");
	    		 pwField.setText("");
	    		 try {
	    			 String result = transaction.send(database);
	    			 if (result == "Success!") {
	    				 message.setText(result);
	    				 message.setForeground(Color.GREEN);
	    			 } else {
	    				 message.setText(result);
	    				 message.setForeground(Color.RED);
	    			 }
				} catch (IOException | JSONException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	          }  
	      });
	      panel.add(send);
	      // Formatted text field (Row 3)
	      
	      panel.add(new JLabel("Deposit Address: "));
	      depositAddress = new JTextField(address.GetAddress());
	      depositAddress.setEditable(false);
	      JButton j = new JButton("New Deposit Address");
	      j.addActionListener(new ActionListener(){  
	    	  public void actionPerformed(ActionEvent e){  
	    		  try {
					depositAddress.setText(address.GetAddress());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	          }  
	      });
	      message = new JLabel("");
	      balance = new JLabel("Balance: " + node.Node.getInstance().GetBalance().toString());
	      panel.add(j);
	      panel.add(depositAddress);
	      panel.add(message);
	      panel.add(balance);

	      // Create a JTextArea
	      model = new DefaultTableModel(data,column);
	      jt = new JTable(model);
	      
	      jt.setBackground(new Color(204, 238, 241)); // light blue
	      // Wrap the JTextArea inside a JScrollPane
	      JScrollPane tAreaScrollPane = new JScrollPane(jt);
	      tAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	      tAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	 
	      // Setup the content-pane of JFrame in BorderLayout
	      Container cp = this.getContentPane();
	      cp.setLayout(new BorderLayout(5, 5));
	      cp.add(panel, BorderLayout.NORTH);
	      cp.add(tAreaScrollPane, BorderLayout.CENTER);
	      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      setTitle("BTC Wallet");
	      setSize(350, 350);
	      setVisible(true);
		  panel.setSize(300, 200);  
	      panel.setVisible(true);  
	}
	
	
	
	//adds transaction to display
	public void AddTransaction(wallet.Transaction transaction) {
		jt.setValueAt(transaction.toAddress, jt.getRowCount(), 0);
		jt.setValueAt(transaction.amount, jt.getRowCount(), jt.getColumnCount());
	}
	
	public void UpdateTransactions(ArrayList<wallet.Transaction> transactions) throws SQLException, UnsupportedEncodingException, IOException, JSONException {
		int count = jt.getRowCount();
		for(int k=0;k<count;k++) jt.removeRowSelectionInterval(0, count-1);
		DefaultTableModel model = (DefaultTableModel) jt.getModel();
		for (int k=0;k<transactions.size(); k++) {
			
			if (database.IsDepositAddress(transactions.get(k))) {
				model.insertRow(0, new Object[]{transactions.get(k).toAddress, transactions.get(k).amount, "Deposit"});
			} else {
				model.insertRow(0, new Object[]{transactions.get(k).toAddress, transactions.get(k).amount, "Withdraw"});
			}
		}
		balance.setText("Balance: " + node.Node.getInstance().GetBalance().toString());
		
	}
	
}
