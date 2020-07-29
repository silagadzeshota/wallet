package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

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



public class Interface extends JFrame {
	private JTextArea text1;
	private JTextArea text2;
	private JCheckBox autoCheck;
	
	//table initial contant
	JTable jt;
	String data[][]={{"jasdnpqwdjqpwjqpwoej","2.3","Withdraw"},    
            {"1asdknw;kenqwe;;qw","1.3", "Deposit"}};    
    String column[]={"Address","Amount", "Type"};  
 // Private variables of the GUI components
    JTextField tField;
    JTextField pwField;
    JTextArea tArea;
    JFormattedTextField formattedField;
    JLabel depositAddress;
    
    wallet.Address address;
	public Interface(wallet.Address addresss) throws SQLException {
		  super("");
		  this.address = addresss;
	      setVisible(true);
	      // JPanel for the text fields
		  JPanel panel= new JPanel(new GridLayout(3, 2, 10, 2));
		  panel.setBorder(BorderFactory.createTitledBorder("Make Withdraw: "));
	      // Regular text field (Row 1)
	      panel.add(new JLabel("  Address: "));
	      tField = new JTextField(10);
	      panel.add(tField);
	 
	      depositAddress = new JLabel("Deposit: " + address.GetAddress());
	 
	      // Password field (Row 2)
	      panel.add(new JLabel("  Amount: "));
	      pwField = new JPasswordField(10);
	      panel.add(pwField);
	     
	      // Formatted text field (Row 3)
	      panel.add(new JButton("Send"));
	      
	      // Create a JTextArea
	      jt = new JTable(data, column);
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
	
}
