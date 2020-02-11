package com.ca.tds.main;

import java.io.IOException;
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
import org.json.JSONObject;

import com.ca.tds.utilityfiles.CRESPrep;
import com.jayway.restassured.path.json.config.JsonPathConfig;
import com.jayway.restassured.response.Response;

@SuppressWarnings("unused")
public class Verify2 {

	@SuppressWarnings("null")
	public static String verify(String response) throws InterruptedException, UnsupportedOperationException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost postRq = new HttpPost("http://10.80.89.206:9608/3ds-server/api/v1/getResultsStatus");
		postRq.addHeader("Content-Type", "application/json;charset=utf-8");
		
		//String response="{\"acsOperatorID\":\"acsOperatorID\",\"acsReferenceNumber\":\"acsReferenceNumber\",\"acsTransID\":\"d5d9a21f-9496-4c72-8108-220e1e49ef37\",\"authenticationValue\":\"AJkBAnFlCYgmkEJ1ZWUJAAAAAAA=\",\"callerTxnRefID\":\"341b7153-da86-4543-860f-2ba47f0168a5\",\"dsName\":\"visa\",\"dsReferenceNumber\":\"dsReferenceNumber\",\"dsTransID\":\"0f2e4bf2-bd00-4425-8437-e3aea29fc355\",\"eci\":\"01\",\"messageType\":\"ARes\",\"messageVersion\":\"2.1.0\",\"threeDSServerTransID\":\"69e8a771-a66a-47ea-9f7b-574dccce010e\",\"transStatus\":\"Y\"}\r\n" + 
				//"";
		
		JSONObject jSONResponseBody = new JSONObject(response);
		
		String cres = CRESPrep.cresBody(jSONResponseBody.get("acsTransID").toString(),jSONResponseBody.get("threeDSServerTransID").toString(),jSONResponseBody.get("transStatus").toString());
		
		System.out.println(cres);
		
		String threeDSServerTransID = jSONResponseBody.get("threeDSServerTransID").toString();
		
		String reqStr="{\"caMerchantID\":\"MTD000000\",\"cres\":\""+cres+"\",\"messageType\":\"RSRq\",\"callerTxnRefID\":\"341b7153-da86-4543-860f-2ba47f0168a5\",\"threeDSServerTransID\": \""+threeDSServerTransID+"\"}";
		System.out.println(reqStr);

		
			try {
				postRq.setEntity(new StringEntity(reqStr));
				HttpResponse resp = client.execute(postRq);				
				HttpEntity entity = resp.getEntity();
				response = EntityUtils.toString(entity, "UTF-8");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return response;
		
		
		}
		
}
