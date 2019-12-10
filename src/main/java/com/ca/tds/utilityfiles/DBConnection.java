package com.ca.tds.utilityfiles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import com.ca.tds.main.BaseClassTDS;


/**
 * @author munka03
 *
 */
@SuppressWarnings("unused")
public class DBConnection {
	private static String dbHost = BaseClassTDS.dbHost;
	private static String dbPort = BaseClassTDS.dbPort;
	private static String dbService = BaseClassTDS.dbservice;
	private static String dbPwd = BaseClassTDS.dbpwd;
	private static String dbuser = BaseClassTDS.dbusr;
	
/*	public Connection getConnACS(String dbUser) throws SQLException {
		Connection dbConn = DriverManager.getConnection("jdbc:oracle:thin:@"
				+ dbHost + ":" + dbPort + ":" + dbSid, dbUser, dbPwd);
		return dbConn;
	}*/
	
	public Connection getConn3DS(Map<String, String> caPropMap) throws SQLException {
		try {
			Class.forName("com.edb.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection dbConn = DriverManager.getConnection("jdbc:edb://"+caPropMap.get("DB_HOST")+":"+caPropMap.get("DB_PORT")+"/"+caPropMap.get("DB_SERVICE")+"",caPropMap.get("DB_USER"),caPropMap.get("DB_PWD"));
		
		return dbConn;
	}
	
	public void closeConn(Connection conn) throws SQLException {
		DbUtils.close(conn);
	}

	public QueryRunner runQuery() {
		QueryRunner run = new QueryRunner();
		return run;
	}
	
	public static String convertValueToString(Object data) {
		String convertedValue = null;

		try{
		if (data != null) {
			
			convertedValue = (String) data;
		}else{
			
			System.out.println("Data is Null");
		}}
		catch(ClassCastException e){
			convertedValue= data.toString();
		}
		return convertedValue;
	}
	
	
     public static Connection getConnection(Map<String, String> caPropMap){
		
		try{  
		//step1 load the driver class  
		Class.forName("com.edb.Driver");    
		//step2 create  the connection object  
		return DriverManager.getConnection(  
		"jdbc:edb://"+caPropMap.get("DB_HOST")+":5444/"+caPropMap.get("DB_SERVICE")+"",caPropMap.get("DB_USER"),caPropMap.get("DB_PWD"));
		/*return DriverManager.getConnection(  
				"jdbc:oracle:thin:@10.131.136.47:1521:orcl","ds_user","dost1234"); */
		  
		}catch(Exception e){ 
			e.printStackTrace();
		}
		return null;  
		  
		} 
}
