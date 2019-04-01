package temporalEventMaster;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class Index extends Applet implements ActionListener {
	public void init() {

		setBackground(Color.PINK);
	    this.setSize(new Dimension(550,500));

		Label label1 = new Label("Enter Database Name: ");
		textField1 = new TextField(30);

		JButton b1= new JButton("Create Database");
		b1.addActionListener(this);

		Label label2 = new Label("Select SQL File (.sql): ");

		textField2 = new TextField(30);
		textField2.setEditable(false);


		JButton b = new JButton( "Bwrose File" );
	    b.addActionListener( 
	      new ActionListener()
	      {
	        public void actionPerformed( ActionEvent ae )
	        {
	          JFileChooser fc = new JFileChooser();
	          fc.setCurrentDirectory( new File(System.getProperty("user.home") ));
	          int returnVal = fc.showSaveDialog( Index.this ); 
	          String fileName = " " ;
	          if ( returnVal == JFileChooser.APPROVE_OPTION )   
	          {  
	            File aFile = fc.getSelectedFile();  
	            fileName = aFile.getName(); 
	            textField2.setText(aFile.getAbsolutePath());
	            System.out.println( aFile.getAbsolutePath() );
	          }



	        }
	      });

	    add(label1);
		add(textField1);
		add(label2);
		add(textField2);
	    add( b );
	    add(b1);

	}

	public void actionPerformed(ActionEvent e) {

		String database_name = textField1.getText();
		String path = textField2.getText();

		SQLReader sqlReader = new SQLReader();
    	ArrayList<String> listOfQueries=sqlReader.createQueries(path);
    	sqlReader.runQueries(listOfQueries, database_name);
    	Label label3 = new Label();
    	label3.setText("Database successfully created!!!");
    	add(label3);
    	
    	SchemaInfo si = new SchemaInfo();
    	si.getTables(database_name);
	}

	TextField textField1;
	TextField textField2; Button b;
	Button b1;


} 
