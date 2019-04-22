package crudOperations;

import java.security.Timestamp;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mysql.jdbc.Connection;

public class Retrieval<E> {

	String databaseName = "companydb";
	
	Connection connection = null;
	ResultSet resultSet = null;
	String query = null;
	ResultSet resultSet2 = null;
	String query2 = null;
	
	public void DatabaseConnection(String dbName) {
		//Statement statement;
		//ResultSet resultSet;
		
		//String query = null;

		// Constructor for opening the Database Connection
			try {
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("Driver Found");
			}

			catch (ClassNotFoundException e) {
				System.out.println("Driver Not Found: " + e);
			}

			String url = "jdbc:mysql://localhost/"+dbName+"?verifyServerCertificate=false&useSSL=true";
			
			String user = "root";
			String password = "Walia@11";
			connection = null;

			try {
				connection = (Connection) DriverManager.getConnection(url, user, password);
				System.out.println("Successfully Connected to Database");
			} catch (SQLException e) {
				System.out.println("SQL Exception: " + e);
			}
	}
	
	//1st Operation
	public void firstOrLastColumn(String tableName, String column,  String operation) throws SQLException {
		
		DatabaseConnection(databaseName);
		String query = null;
		
		//order by preferred over max-min to avoid use of nested queries 
		if(operation == "First") {
		     query = "select "+column+", startTime, endTime from "+tableName+column+"VT order by startTime LIMIT 1";
		}
		else {
			 query = "select "+column+", startTime, endTime from "+tableName+column+"VT order by startTime DESC LIMIT 1";
		}
		
		System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()) {
        	System.out.println("salary : "+resultSet.getFloat(column)+"  stratTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
	}
	
	//2nd Operation: select previous value of given value (wherever value is present)
    public void previousOfGivenValueOfColumn(String tableName, String column, E value) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	query = "select "+column+"from "+tableName+column+"VT where endTime IN (select startTime from "+tableName+column+"VT where "+column+" = "+value+")";
    	
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        if(resultSet == null) {
        	query = "select "+column+", startTime, endTime from "+tableName+column+"VT where "+column+" = "+value+" order by startTime LIMIT 1";
        	
        	System.out.println(query);
    		java.sql.PreparedStatement preparedStatement2 = null;
    		preparedStatement2 = connection.prepareStatement(query);
            resultSet = preparedStatement2.executeQuery();
            
            while(resultSet.next()) {
            	System.out.println("stratTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
            }
        }
        else {
        	while(resultSet.next()) {
            	System.out.println("stratTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
            }
        }
    }
    
    //3rd Operation: select next value of given value (wherever value is present )
    public void nextOfGivenValueOfColumn(String tableName, String column, E value) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	query = "select "+column+"from "+tableName+column+"VT where startTime IN (select endTime from "+tableName+column+"VT where "+column+" = "+value+")";
    	
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        if(resultSet == null) {
        	query = "select "+column+", startTime, endTime from "+tableName+column+"VT where "+column+" = "+value+" order by endTime LIMIT 1";
        	
        	System.out.println(query);
    		java.sql.PreparedStatement preparedStatement2 = null;
    		preparedStatement2 = connection.prepareStatement(query);
            resultSet = preparedStatement2.executeQuery();
            
            while(resultSet.next()) {
            	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
            }
        }
        else {
        	while(resultSet.next()) {
            	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
            }
        }
    }
	
    //4th Operation: Value which precedes the current value and its timeStamp according to specified granule
    public void precedingValueGivenTimeStampGranule(String tableName, String column, String granule, int granule_value) throws SQLException {
    	
		DatabaseConnection(databaseName);
		String query = null;
		
		//Calendar calendar = Calendar.getInstance();
		//java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
		
        query = "select max(startTime) from "+tableName+column+"VT";
		
		System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        java.sql.Timestamp currentTimestamp = null;
        java.sql.Timestamp newTimestamp = null;
        
        if(resultSet.next()) {
        	currentTimestamp = resultSet.getTimestamp("max(startTime)");
        	System.out.println("Current Timestamp: "+currentTimestamp);
        	//Calendar calendar = Calendar.getInstance(resultSet.getTimestamp("startTime"));
        	//newTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTimestamp);
        SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
        String newTimestampString = null;
        
        if(granule.equals("YEAR"))
            cal.add(Calendar.YEAR, -granule_value);
        else if(granule.equals("MONTH"))
        	cal.add(Calendar.MONTH, -granule_value);
        else if(granule.equals("DAY"))
        	//Can check for better day calculation
        	cal.add(Calendar.DAY_OF_YEAR, -granule_value);
        else if(granule.equals("HOUR"))
        	cal.add(Calendar.HOUR, -granule_value);
        else if(granule.equals("MINUTE"))
        	cal.add(Calendar.MINUTE, -granule_value);
        else if(granule.equals("SECOND"))
        	cal.add(Calendar.SECOND, -granule_value);
        
        //newTimestamp.setTime(cal.getTime().getTime()); // or
        newTimestamp = new java.sql.Timestamp(cal.getTime().getTime());
        //currentTimestampString = df.format(currentTimestamp);
        newTimestampString = df.format(newTimestamp);
        System.out.println("New Timestamp: "+newTimestamp);
        
		query2 = "select "+column+" from "+tableName+column+"VT where startTime >= '"+newTimestampString+"' order by startTime DESC LIMIT 1";
		
		System.out.println(query2);
		java.sql.PreparedStatement preparedStatement2 = null;
		preparedStatement2 = connection.prepareStatement(query2);
        resultSet2 = preparedStatement2.executeQuery();
        
        System.out.println("Column Evolution : "+column);
        while(resultSet2.next()) {
        	System.out.println("startTime: "+resultSet2.getString(column));
        }
    }
    
    //5th Operation: Value which is next to the current value and its timeStamp according to specified granule
    public void nextValueGivenTimeStampGranule(String tableName, String column, String granule, int granule_value) throws SQLException {
    	
		DatabaseConnection(databaseName);
		String query = null;
		
		//Calendar calendar = Calendar.getInstance();
		//java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
		
        query = "select max(startTime) from "+tableName+column+"VT";
		
		System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        java.sql.Timestamp currentTimestamp = null;
        java.sql.Timestamp newTimestamp = null;
        
        if(resultSet.next()) {
        	currentTimestamp = resultSet.getTimestamp("max(startTime)");
        	System.out.println("Current Timestamp: "+currentTimestamp);
        	//Calendar calendar = Calendar.getInstance(resultSet.getTimestamp("startTime"));
        	//newTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTimestamp);
        SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
        String newTimestampString = null;
        
        if(granule.equals("YEAR"))
            cal.add(Calendar.YEAR, granule_value);
        else if(granule.equals("MONTH"))
        	cal.add(Calendar.MONTH, granule_value);
        else if(granule.equals("DAY"))
        	//Can check for better day calculation
        	cal.add(Calendar.DAY_OF_YEAR, granule_value);
        else if(granule.equals("HOUR"))
        	cal.add(Calendar.HOUR, granule_value);
        else if(granule.equals("MINUTE"))
        	cal.add(Calendar.MINUTE, granule_value);
        else if(granule.equals("SECOND"))
        	cal.add(Calendar.SECOND, granule_value);
        
        //newTimestamp.setTime(cal.getTime().getTime()); // or
        newTimestamp = new java.sql.Timestamp(cal.getTime().getTime());
        //currentTimestampString = df.format(currentTimestamp);
        newTimestampString = df.format(newTimestamp);
        System.out.println("New Timestamp: "+newTimestamp);
        
		query2 = "select "+column+" from "+tableName+column+"VT where startTime >= '"+newTimestampString+"' order by startTime DESC LIMIT 1";
		
		System.out.println(query2);
		java.sql.PreparedStatement preparedStatement2 = null;
		preparedStatement2 = connection.prepareStatement(query2);
        resultSet2 = preparedStatement2.executeQuery();
        
        System.out.println("Column Evolution : "+column);
        while(resultSet2.next()) {
        	System.out.println("startTime: "+resultSet2.getString(column));
        }
    }
    
    //6th Operation: Indicates all evolution dates of column
    public void columnEvolutionHistory(String tableName, String column) throws SQLException {
  		
		DatabaseConnection(databaseName);
		String query = null;
		
		query = "select startTime, endTime from "+tableName+column+"VT";
		
		System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        System.out.println("Column Evoltion : "+column);
        while(resultSet.next()) {
        	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
	}
    
    //7th Operation: Indicates the evolution date to the current value (start time of latest value)
    public void columnEvolutionTillCurrentDate(String tableName, String column) throws SQLException {
  		
		DatabaseConnection(databaseName);
		String query = null;
		
		//Calendar calendar = Calendar.getInstance();
		//java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
		    
		
		query = "select max(startTime) from "+tableName+column+"VT";
		
		System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        System.out.println("Column Evoltion : "+column);
        while(resultSet.next()) {
        	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
	}
    
    //8th Operation: Indicates first/last evolutin date of the column
    public void firstOrLastEvolutionColumnDate(String tableName, String column, String operation) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	if(operation.equals("First")) {
    		query = "select min(startTime) from "+tableName+column+"VT";
    	}
    	else {
    		//not max(endTime) as max endTime can be infinity
    		query = "select max(startTime) from "+tableName+column+"VT";
    	}
    	
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()) {
        	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
    }
    
    //9th Operation: Indicates the evolution date from value1 to value2 (value1 and value2 must be adjacent)
    public void evolutionDateFromVal1ToVal2(String tableName, String column, String value1, String value2) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	query = "select endTime from "+tableName+column+"VT where "+column+" = "+value1+" endTime IN (select startTime from "+tableName+column+"VT where "+column+" = "+value2+")";
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()) {
        	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
    }
    
    
    //10th Operation: Indicates the timestamps associated to a value
    public void columnTimestampsValue(String tableName, String column, String value) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	query = "select startTime and endTime from "+tableName+column+"VT where column = "+value;
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()) {
        	System.out.println("startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
    }
    
    //11th Operation: Indicates the value associated with a date
    public void valueAssociatedWithDate(String tableName, String column, Timestamp date) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	//Assumption is current value at startTime
    	query = "select "+column+" from "+tableName+column+"VT where startTime >= "+date+" and endTime < "+date;
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()) {
        	
        	System.out.println("Value as Resultset row: "+resultSet.getRow()+" startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
    }
    
    //12th Operation: Gives the specified timestamp (startTime or endTime) of the given value 
    public void specifiedTimeStampAssociatedWithValue(String tableName, String column, String value, String specifiedTimeStamp) throws SQLException {
    	
    	DatabaseConnection(databaseName);
    	String query = null;
    	
    	query = "select "+specifiedTimeStamp+" from "+tableName+column+"VT where column = "+value;
    	System.out.println(query);
		java.sql.PreparedStatement preparedStatement = null;
		preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()) {
        	
        	System.out.println("Value as Resultset row: "+resultSet.getRow()+" startTime: "+resultSet.getTimestamp("startTime")+ "  endTime: "+resultSet.getTimestamp("endTime"));
        }
    }
}
