package com.ca.tds.utilityfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	static AppParams appParams;
	
	public static Connection getConnection(){
		
		if(appParams == null){
			InitializeApplicationParams initializeApplicationParams = new InitializeApplicationParams();
			initializeApplicationParams.initializeAppParams();
			appParams = initializeApplicationParams.getAppParams();
		}
		
		try{  
		//step1 load the driver class  
		Class.forName("com.edb.Driver");    
		//step2 create  the connection object  
		return DriverManager.getConnection(  
		"jdbc:edb://"+appParams.getDBHost()+":5444/"+appParams.getDBService()+"",appParams.getDBUser(),appParams.getDBPassword());
		/*return DriverManager.getConnection(  
				"jdbc:oracle:thin:@10.131.136.47:1521:orcl","ds_user","dost1234"); */
		  
		}catch(Exception e){ 
			e.printStackTrace();
		}
		return null;  
		  
		}    
	
	public static void main(String[] a) throws SQLException{
		
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
