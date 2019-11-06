package com.ca.tds.utilityfiles;

import java.util.Base64;

public class CRESPrep {
	
	public static String cresBody(String acsTransid, String threedstransid, String transStatus) {
		
        String cresBody = "{\r\n" + 
        		"   \"acsTransID\" : \""+acsTransid+"\",\r\n" + 
        		"   \"transStatus\" : \""+transStatus+"\",\r\n" + 
        		"   \"messageType\" : \"CReq\",\r\n" + 
        		"   \"messageVersion\" : \"2.1.0\",\r\n" + 
        		"   \"threeDSServerTransID\" : \""+threedstransid+"\"\r\n" + 
        		"}";
        
		//System.out.println("cresBody :"+cresBody);
		
		String encodedCres = Base64.getEncoder().encodeToString(cresBody.getBytes()); 
		
		//System.out.println("encodedCres :"+encodedCres);
		 
        return encodedCres;
	}
}
