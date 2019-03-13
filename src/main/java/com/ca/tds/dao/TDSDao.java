package com.ca.tds.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.DBConnection;

public class TDSDao {
	
	
	public List<HashMap<String,Object>> getAuthLogDataByTDSTransID(String transID) throws SQLException{
		
		Connection con = null;
		try{
		con = DBConnection.getConnection();
		Statement stmt = con.createStatement();  
		  
		//step4 execute query  
		ResultSet rs = stmt.executeQuery("select * from MTDAUTHLOG where threedsservertransid='"+transID+"'");
		return CommonUtil.convertResultSetToList(rs);
		}finally{
			if(con != null){
				con.close();
			}
		}
		
		
	}
	
public List<HashMap<String,Object>> getErrorLogDataByTDSTransID(String transID) throws SQLException{
		
		Connection con = null;
		try{
		con = DBConnection.getConnection();
		Statement stmt = con.createStatement();  
		  
		//step4 execute query  
		ResultSet rs = stmt.executeQuery("select * from mtderrormsg where threedsservertransid='"+transID+"'");
		return CommonUtil.convertResultSetToList(rs);
		}finally{
			if(con != null){
				con.close();
			}
		}
		
		
	}

}
