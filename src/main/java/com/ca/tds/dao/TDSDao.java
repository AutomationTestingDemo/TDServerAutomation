package com.ca.tds.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.DBConnection;

public class TDSDao {
	
	
	public List<HashMap<String,Object>> getAuthLogDataByTDSTransID(String transID, Map<String, String> caPropMap) throws SQLException{
		
		Connection con = null;
		try{
		con = DBConnection.getConnection(caPropMap);
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
	
public List<HashMap<String,Object>> getPResFromDB(Map<String, String> caPropMap) throws SQLException{
		
		Connection con = null;
		try{
		con = DBConnection.getConnection(caPropMap);
		Statement stmt = con.createStatement();  
		  
		//step4 execute query  
		ResultSet rs = stmt.executeQuery("select * from MTDCARDRANGEDATA where action='A'");
		return CommonUtil.convertResultSetToList(rs);
		}finally{
			if(con != null){
				con.close();
			}
		}
		
		
	}
	
public List<HashMap<String,Object>> getErrorLogDataByTDSTransID(String transID, Map<String, String> caPropMap) throws SQLException{
		
		Connection con = null;
		try{
		con = DBConnection.getConnection(caPropMap);
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

public String getMTDConfig(String key, Map<String, String> caPropMap) throws SQLException{
	
	Connection con = null;
	try{
	con = DBConnection.getConnection(caPropMap);
	Statement stmt = con.createStatement();  
	  
	//step4 execute query  
	ResultSet rs = stmt.executeQuery("select paramvalue from mtdconfig where paramname='"+key+"'");
	if(rs.next())
		return rs.getString("paramvalue");
	else
		return null;
	}finally{
		if(con != null){
			con.close();
		}
	}
		
}

}