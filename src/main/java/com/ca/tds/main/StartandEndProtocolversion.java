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
public class StartandEndProtocolversion {

	private static Map<String, String> caPropMap = null;
	
	@Test(dataProvider="QueryProvider")
	public static void startEndProtocolversion(String key, String value) throws Exception {
		
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
			 
			String result = dbapi.updatemtdcardrangedata(caPropMap,value);
			ServiceRestart.server3DSrestart();
			System.out.println("Back to Main");
			System.out.println(result);
			Thread.sleep(100000);
			PostAreq.Areqpost("1000000000090");
			
			System.out.println("######################################## End of Test ##################################");
			
			/*
			 * result = dbapi.updatemtdcardrangedata(caPropMap,"04");
			 * ServiceRestart.server3DSrestart(); System.out.println("Back to Main");
			 * System.out.println(result); Thread.sleep(100000);
			 * System.out.println("FinalThreadSleepOver"+100000);
			 */
			
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
            { "DSStartHighest", "update mtdcardrangedata set dsstartprotocolversion='2.2.0' where cardtype=4;"},
            { "DSStartLowest", "update mtdcardrangedata set dsstartprotocolversion='2.0.0' where cardtype=4;"},
            { "DSStartoriginal", "update mtdcardrangedata set dsstartprotocolversion='2.1.0' where cardtype=4;"},
            { "DSEndtHighest", "update mtdcardrangedata set dsendprotocolversion='2.2.0' where cardtype=4;"},
            { "DSEndLowest", "update mtdcardrangedata set dsendprotocolversion='2.0.0' where cardtype=4;"},
            { "DSENDOriginal", "update mtdcardrangedata set dsendprotocolversion='2.1.0' where cardtype=4;"},
            { "ACSStartHighest", "update mtdcardrangedata set acsstartprotocolversion='2.2.0' where cardtype=4;"},
            { "ACSStartLowest", "update mtdcardrangedata set acsstartprotocolversion='2.0.0' where cardtype=4;"},
            { "ACSStartOriginal", "update mtdcardrangedata set acsstartprotocolversion='2.1.0' where cardtype=4;"},
            { "ACSSEndtHighest", "update mtdcardrangedata set acsendprotocolversion='2.2.0' where cardtype=4;"},
            { "ACSSEndLowest", "update mtdcardrangedata set acsendprotocolversion='2.0.0' where cardtype=4;"},
            { "ACSENdOriginal", "update mtdcardrangedata set acsendprotocolversion='2.1.0' where cardtype=4;"},
            
    };

    }
	
}

