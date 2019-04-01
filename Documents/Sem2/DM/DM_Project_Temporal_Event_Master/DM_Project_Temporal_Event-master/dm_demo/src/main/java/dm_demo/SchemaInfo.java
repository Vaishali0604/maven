package temporalEventMaster;

import java.sql.*;
import java.io.*; 
import java.util.*; 
import javax.swing.*;    
import java.awt.event.*;
public class SchemaInfo {

	static ArrayList<String> tableNames = new ArrayList<String>(); 
	
	static String data = null;
	static String db1 = null;
	public void getTables(String db) {
		// TODO Auto-generated method stub
		//String db= "companydb";
		//DatabaseMetaData dbmd;
		db1=db;
		Connection myconn=null;
		ResultSet myRst=null;
		try
		{
			//1. Get connection
			myconn= DriverManager.getConnection("jdbc:mysql://localhost/"+db1+"?verifyServerCertificate=false&useSSL=true","root","Walia@11");
			
			//2. Get metadata
			
			DatabaseMetaData dbmd=myconn.getMetaData();
			
			//3. Get list of tables
			System.out.println("List of tables");
			myRst= dbmd.getTables(db,null,"%",null);

			while(myRst.next())
			{
				tableNames.add(myRst.getString("TABLE_NAME"));
			}
			
			JFrame f;    
			
			
			f=new JFrame("Tables in database");   
			final JLabel label = new JLabel();          
			label.setHorizontalAlignment(JLabel.CENTER);  
		    label.setSize(400,100);  
		    JButton b=new JButton("Show");  
		    b.setBounds(200,100,75,20);  	            
		    final JComboBox cb=new JComboBox(tableNames.toArray());    
			cb.setBounds(50, 100,90,20);    
			f.add(cb); f.add(label); f.add(b);    
			f.setLayout(null);    
			f.setSize(350,350);    
			f.setVisible(true);     
			SchemaInfo si = new SchemaInfo();
			b.addActionListener(new ActionListener() {  
				public void actionPerformed(ActionEvent e) {   
				data = ""+cb.getItemAt(cb.getSelectedIndex());  
				String localData = "Table selected: " + data;
				label.setText(localData); 
				
				System.out.println(data);
				Connection myconn2=null;
				try {
					myconn2= DriverManager.getConnection("jdbc:mysql://localhost/"+db1+"?verifyServerCertificate=false&useSSL=true","root","Walia@11");
					DatabaseMetaData dbmd=myconn2.getMetaData();
					ResultSet columns = dbmd.getColumns(null, null, data , null);
					ArrayList<String> columnNames = new ArrayList<String>(); 
					int i = 1;
					
					while (columns.next())
					{
						
						columnNames.add(columns.getString("COLUMN_NAME"));
					 // System.out.printf("%d: %s (%d)\n", i++, columns.getString("COLUMN_NAME"), 
					//    columns.getInt("ORDINAL_POSITION"));
					}
					
					
					
					JFrame f;    
					f=new JFrame("Columns in table: " + data);   
					final JLabel label = new JLabel();          
					label.setHorizontalAlignment(JLabel.CENTER);  
				    label.setSize(400,100);  
				    JButton b=new JButton("Show");  
				    b.setBounds(200,100,75,20);  	            
				    final JComboBox cb=new JComboBox(columnNames.toArray());    
					cb.setBounds(50, 100,90,20);    
					f.add(cb); f.add(label); f.add(b);    
					f.setLayout(null);    
					f.setSize(350,350);    
					f.setVisible(true);
					
					System.out.println(columns);
					
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
				
				}  
			});
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(myconn,myRst);
		}


	}

	private static void close(Connection myconn, ResultSet myRst) {
		// TODO Auto-generated method stub
		
	}

}
