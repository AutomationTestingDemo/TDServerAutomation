package com.ca.tds.utilityfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DBConnection {
	
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
	
	public static void main(String[] a) throws SQLException{
		
	}
	
	public static void queryTable(Map<String, String> caPropMap) throws SQLException{

		Connection con = null;
		try{
		con = getConnection(caPropMap);
		Statement stmt=con.createStatement();  
		  
		//step4 execute query  
		ResultSet rs = stmt.executeQuery("select * from MTDAUTHLOG where threedsservertransid='dfaa5a93-b2f3-4f38-b28d-9ae8b9cb1a0f'");
		System.out.println(CommonUtil.convertResultSetToList(rs));
		rs.close();
		}finally{
			if(con != null){
				con.close();
			}
		}
		
	
		
	}
	
	
}
