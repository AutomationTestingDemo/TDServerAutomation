package com.ca.tds.utilityfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	public static Connection getConnection(){  
		try{  
		//step1 load the driver class  
		Class.forName("oracle.jdbc.driver.OracleDriver");  
		  
		//step2 create  the connection object  
		Connection con = DriverManager.getConnection(  
		"jdbc:oracle:thin:@10.131.136.47:1521:orcl","ms_user","dost1234");  
		  
		return con; 
		  
		}catch(Exception e){ 
			e.printStackTrace();
		}
		return null;  
		  
		}   
	
	public static void main(String[] a) throws SQLException{
		loadCardRanges();
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
	
	public static void loadCardRanges(){
		
		Connection conn = null;
			PreparedStatement pStmt = null;
		
		String sqlModified = "Insert into ARDSRANGE(PANLENGTH,CARDSTARTNUMBER,CARDENDNUMBER,SERIALNUMBER,ACTION,ACSURL1,ACSURL2,ACSURL3,ACSURL4,ACSURL5,PROCESSORID,ISSUERID,UPDATETIME,STATUS,RANGESTATUS,ACS2URL1,ACS2URL2,ENABLE3DS,THREEDSMETHODURL,ACS2URL3,ACS2URL4,ACS2ATTEMPTSURL,ACS2OPERATORID,ACS2REFERENCENUMBER,ACS2ATTEMPTSOPERATORID,ACS2ATTEMPTSREFERENCENUMBER,ACSSTARTPROTOCOLVER,ACSENDPROTOCOLVER)"+
" values "+ 
" (16,?,?,7,'A','http://10.131.94.242:1080/acs/api/tds2/txn/v1/acs-url',null,null,null,null,1,1,to_date('04-JAN-19','DD-MON-RR'),0,1,'http://10.131.94.242:1080/acs/api/tds2/txn/v1/acs-url','http://10.131.94.242:1080/acs/api/tds2/txn/v1/acs-url',3,'http://10.131.94.242:1080/content-server/api/tds2/txn/browser/v1/tds-method',null,null,'http://10.131.138.169:1080/acs/api/tds2/txn/v1/acs-url/','ACS_OPERATOR_ID','ABCDEF987654321','ACSOPERATORID','ABCDEF987654321','2.1.0','2.1.0')";
		try
		{
			
			conn = getConnection();
			pStmt = conn.prepareStatement(sqlModified);
			String startSerialNumX = "4000400085xxxxxx";
			String endSerialNumX = "4000400085xxxxxx";
			int i = 100000;
			while(i < 101000){
				i=i+1;
				String startSerialNum = startSerialNumX.replace("xxxxxx", i+"");
				pStmt.setString(1, startSerialNum);
				i=i+1;
				String endSerialNum = endSerialNumX.replace("xxxxxx", i+"");
				pStmt.setString(2, endSerialNum);
				pStmt.execute();
				
			}
			pStmt.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
            return;
		}
		finally
		{
			try
			{
				if (pStmt != null)
					pStmt.close();
			}
			catch(Exception e)
			{
	            return;
			}
			try
			{
				
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				return;
			}
		}
	}
}
