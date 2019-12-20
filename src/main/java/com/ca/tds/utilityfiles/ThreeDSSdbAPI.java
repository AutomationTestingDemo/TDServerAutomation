package com.ca.tds.utilityfiles;
import static org.testng.Assert.assertNotNull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.Assert;

import com.ca.tds.main.BaseClassTDS;
import com.relevantcodes.extentreports.LogStatus;

@SuppressWarnings("unused")
public class ThreeDSSdbAPI extends BaseClassTDS {

	DBConnection dbConn = null;
	public ResultSet rs = null;
	Connection con = null;
	Statement stmt = null;

	public ThreeDSSdbAPI() {
		
		dbConn = new DBConnection();
	}
	
	
	/**
	 * This API executes the given DB query
	 * @param  query - DB query to be executed  
	 *         dbUser - Db schema
	 * @return DB query result in form of HasMap where KEY is the db table column Name and VALUE is the value of the key   
	 */	
	
	public String getDSTransidFromDB(String threeDStransactionId,Map<String, String> caPropMap) throws Exception {
		
		HashMap<String, String> dataMap = new HashMap<String, String>();
		
		String dstransid=null;
	
		String query= "select dstransid from mraqa.mtdauthlog where threedsservertransid='"+threeDStransactionId+"'";
		try {
			
			stmt = dbConn.getConn3DS(caPropMap).createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rowData = rs.getMetaData();
			int columns = rowData.getColumnCount();
			dataMap = new HashMap<String, String>(columns);
			while (rs.next()) {
				
				for (int i = 1; i <= columns; ++i) {
					
					dataMap.put(rowData.getColumnName(i), DBConnection.convertValueToString(rs.getObject(i)));
				}
			}
			
		} catch (Exception e) {
			
			throw new Exception(e.getMessage());
			
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		
		if(dataMap.get("dstransid")!=null){
			
			dstransid = dataMap.get("dstransid").trim().toString();
		}
		
		return dstransid;
	}
	
	public String getAResFromDB(String threeDStransactionId,Map<String, String> caPropMap) throws Exception {
		
		HashMap<String, String> dataMap = new HashMap<String, String>();
		
		String responseARes=null;
	
		String query= "select responsejson from mraqa.mtdresponsedata where threedsservertransid='"+threeDStransactionId+"'";
		try {
			
			stmt = dbConn.getConn3DS(caPropMap).createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rowData = rs.getMetaData();
			int columns = rowData.getColumnCount();
			dataMap = new HashMap<String, String>(columns);
			while (rs.next()) {
				
				for (int i = 1; i <= columns; ++i) {
					
					dataMap.put(rowData.getColumnName(i), DBConnection.convertValueToString(rs.getObject(i)));
				}
			}
			
		} catch (Exception e) {
			
			throw new Exception(e.getMessage());
			
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		
		if(dataMap.get("responsejson")!=null){
			
			responseARes = dataMap.get("responsejson").trim().toString();
		}
		
		return responseARes;
	}

} 
