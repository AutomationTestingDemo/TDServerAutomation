package com.ca.tds.utilityfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	public static Connection getConnection(){  
		try{  
		//step1 load the driver class  
		Class.forName("com.edb.Driver");  
		  
		//step2 create  the connection object  
		Connection con = DriverManager.getConnection(  
		"jdbc:edb://manyo02-I19632:5444/postgres?user=ms_user","enterprisedb","dost1234");  
		  
		return con; 
		  
		}catch(Exception e){ 
			e.printStackTrace();
		}
		return null;  
		  
		}   
	
	public static void main(String[] a) throws SQLException{
		queryTable();
	}
	
	public static void queryTable() throws SQLException{

		Connection con = null;
		try{
		con = getConnection();
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
