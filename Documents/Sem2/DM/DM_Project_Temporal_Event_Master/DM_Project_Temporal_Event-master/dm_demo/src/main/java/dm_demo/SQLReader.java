/**
 * This class can be used to read sql files into an array of Strings, each 
 * representing a single query terminated by ";" 
 * Comments are filtered out. 
 */
package temporalEventMaster;

import java.io.BufferedReader;  

import java.io.File;  
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


/*
 * ATTENTION: SQL file must not contain column names, etc. including comment signs (#, --, /* etc.)
 *          like e.g. a.'#rows' etc. because every characters after # or -- in a line are filtered 
 *          out of the query string the same is true for every characters surrounded by /* and */
/**/

public class SQLReader {
	private static ArrayList<String> listOfQueries = null;
	Statement statement;
	ResultSet resultSet;
	Connection connection = null;
	String query = null;

    /*
     * @param   path    Path to the SQL file
     * @return          List of query strings 
     */
    public ArrayList<String> createQueries(String path)  
    { 
        String queryLine =      new String();
        StringBuffer sBuffer =  new StringBuffer();
        listOfQueries =         new ArrayList<String>();

        try 
        {  
            FileReader fr =     new FileReader(new File(path));       
            BufferedReader br = new BufferedReader(fr);  

            //read the SQL file line by line
            while((queryLine = br.readLine()) != null)  
            {  
                // ignore comments beginning with #
                int indexOfCommentSign = queryLine.indexOf('#');
                if(indexOfCommentSign != -1)
                {
                    if(queryLine.startsWith("#"))
                    {
                        queryLine = new String("");
                    }
                    else
                        queryLine = new String(queryLine.substring(0, indexOfCommentSign-1));
                }
                // ignore comments beginning with --
                indexOfCommentSign = queryLine.indexOf("--");
                if(indexOfCommentSign != -1)
                {
                    if(queryLine.startsWith("--"))
                    {
                        queryLine = new String("");
                    }
                    else
                        queryLine = new String(queryLine.substring(0, indexOfCommentSign-1));
                }
                // ignore comments surrounded by /* */
                indexOfCommentSign = queryLine.indexOf("/*");
                if(indexOfCommentSign != -1)
                {
                    if(queryLine.startsWith("/*"))
                    {
                        queryLine = new String("");
                    }
                    else
                        queryLine = new String(queryLine.substring(0, indexOfCommentSign-1));

                    sBuffer.append(queryLine + " "); 
                    // ignore all characters within the comment
                    do
                    {
                        queryLine = br.readLine();
                    }
                    while(queryLine != null && !queryLine.contains("*/"));
                    indexOfCommentSign = queryLine.indexOf("*/");
                    if(indexOfCommentSign != -1)
                    {
                        if(queryLine.endsWith("*/"))
                        {
                            queryLine = new String("");
                        }
                        else
                            queryLine = new String(queryLine.substring(indexOfCommentSign+2, queryLine.length()-1));
                    }
                }

                //  the + " " is necessary, because otherwise the content before and after a line break are concatenated
                // like e.g. a.xyz FROM becomes a.xyzFROM otherwise and can not be executed 
                if(queryLine != null)
                    sBuffer.append(queryLine + " ");  
            }  
            br.close();

            // here is our splitter ! We use ";" as a delimiter for each request 
            String[] splittedQueries = sBuffer.toString().split(";");

            // filter out empty statements
            for(int i = 0; i<splittedQueries.length; i++)  
            {
                if(!splittedQueries[i].trim().equals("") && !splittedQueries[i].trim().equals("\t"))  
                {
                    listOfQueries.add(new String(splittedQueries[i]));
                }
            }

        }  
        catch(Exception e)  
        {  
            System.out.println("*** Error : "+e.toString());  
            System.out.println("*** ");  
            System.out.println("*** Error : ");  
            e.printStackTrace();  
            System.out.println("################################################");  
            System.out.println(sBuffer.toString());  
        }
        return listOfQueries;
    } 

    public void runQueries(ArrayList<String> listOfQueries, String database_name) {
    	java.sql.PreparedStatement preparedStatement = null;
    	try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver Found");
		}

		catch (ClassNotFoundException e) {
			System.out.println("Driver Not Found: " + e);
		}

		String url = "jdbc:mysql://localhost/?verifyServerCertificate=false&useSSL=true";
		//Wishlist_Service?verifyServerCertificate=false&useSSL=true

		String user = "root";
		String password = "Walia@11";
		connection = null;

		try {
			connection = (Connection) DriverManager.getConnection(url, user, password);
			System.out.println("Successfully Connected to MySQL");
			query="create database "+database_name;
			preparedStatement = connection.prepareStatement(query);
	        preparedStatement.executeUpdate();
	        url = "jdbc:mysql://localhost/"+database_name+"?verifyServerCertificate=false&useSSL=true";
	        connection = (Connection) DriverManager.getConnection(url, user, password);
			System.out.println("Successfully Connected to Database "+database_name);
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e);
		}





		try {
			int count=0;
			for(String query: listOfQueries){

		           preparedStatement = connection.prepareStatement(query);
		           preparedStatement.executeUpdate();
		           count++;
		           System.out.println(count+" "+query+"\n");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}

    }

}