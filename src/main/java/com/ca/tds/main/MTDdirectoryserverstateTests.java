package com.ca.tds.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ca.tds.utilityfiles.PostAreq;
import com.ca.tds.utilityfiles.ServiceRestart;
import com.ca.tds.utilityfiles.ThreeDSSdbAPI;

@SuppressWarnings("unused")
public class MTDdirectoryserverstateTests {

	private static Map<String, String> caPropMap = null;
	
	@Test(dataProvider="QueryProvider")
	public static void directoryserverstatetest(String key, String value) throws Exception {
		
		try {
			Properties	prop = new Properties(); 
		    FileInputStream ip = new FileInputStream("config/3DSProperties.properties");
			prop.load(ip);
			ip.close();
			
			caPropMap = new HashMap<>();
			for (String name : prop.stringPropertyNames())
				caPropMap.put(name, prop.getProperty(name));
			
			ThreeDSSdbAPI dbapi = new ThreeDSSdbAPI();
			
			System.out.println("######################################## Start of Test ##################################");
			 System.out.println("QueryName :"+key+"......."+"Query :"+value);
			 
			String result = dbapi.updateDBTable(caPropMap,value);
			ServiceRestart.server3DSrestart();
			System.out.println("Back to Main");
			System.out.println(result);
			Thread.sleep(100000);
			PostAreq.Areqpost("1000000000090");
			
			System.out.println("######################################## End of Test ##################################");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
	    }
}
	
    @DataProvider(name="QueryProvider")
    public Object[][] getDataFromDataprovider(){
    return new Object[][] 
    	{
				
				
				  { "Query1",
				  "update mtddirectoryserverstate set status='A', serialnumber='AB' where cardtype=4;"
				  },
				 
			 { "Query8", "update mtddirectoryserverstate set status='0', serialnumber='AB' where cardtype=4;"}, 
				
				
				  {
				  "Query2","update mtddirectoryserverstate set status=NULL, serialnumber='1' where cardtype=4;"
				  },
				  {
				  "Query9","update mtddirectoryserverstate set status='1', serialnumber='1' where cardtype=4;"
				}, 
					  {
					  "Query3","update mtddirectoryserverstate set status='B' where cardtype=4;"},
					  {"Query4",
					  "update mtddirectoryserverstate set cardtype=9 where cardtype=4;"}, {
					  "Query5", "update mtddirectoryserverstate set cardtype=4 where cardtype=9;"},
					  
					  { "Query6", "Delete from mtddirectoryserverstate where cardtype=4;"},
					 
				 
             { "Query7", "INSERT INTO mtddirectoryserverstate (cardtype,dsid,serialnumber,rid,status,lastmodified) VALUES (4,'4','1','4','1','2019-12-23 13:13:27.31752');"},
          
            
    };

    }
	
}

