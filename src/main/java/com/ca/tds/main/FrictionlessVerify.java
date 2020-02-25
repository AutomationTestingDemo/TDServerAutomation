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
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.config.JsonPathConfig;
import com.jayway.restassured.response.Response;

@SuppressWarnings("unused")
public class FrictionlessVerify {

	@SuppressWarnings("null")
	
	@Test
	public static void flVerify() throws InterruptedException, UnsupportedOperationException, IOException {
		/*
		 * public static void main(String[] args) throws InterruptedException,
		 * UnsupportedOperationException, IOException {
		 */
		HttpClient client = HttpClientBuilder.create().build();
		CloseableHttpClient clients = HttpClients.createDefault();
		HttpPost postRq = new HttpPost("http://10.80.120.49:9608/3ds-server/api/v1/authenticate3dsTxn");
		postRq.addHeader("Content-Type", "application/json;charset=utf-8");
		
		String response=null;
		
		
		String threeDSServerTransID = UUID.randomUUID().toString();
		String reqStr = "{\r\n" + 
				"	\"caMerchantID\": \"MTD000000\",\r\n" + 
				"	\"challengeWindowSize\":\"02\",\r\n" + 
				"	\"callerTxnRefID\": \"341b7153-da86-4543-860f-2ba47f0168a5\",\r\n" + 
				"	\"messageType\": \"AReq\",\r\n" + 
				"	\"billAddrCountry\": \"840\",\r\n" + 
				"	\"browserLanguage\": \"en-US\",\r\n" + 
				"	\"billAddrLine2\": \"ABC\",\r\n" + 
				"	\"billAddrLine3\": \"HMS\",\r\n" + 
				"	\"deviceChannel\": \"02\",\r\n" + 
				"	\"browserIP\": \"155.35.122.4\",\r\n" + 
				"	\"notificationURL\": \"https://10.138.159.110:8080/3ds-merchant-stub/notify\",\r\n" + 
				"	\"browserAcceptHeader\": \"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\",\r\n" + 
				"	\"billAddrLine1\": \"500 West Maude Ave\",\r\n" + 
				"	\"purchaseDate\": \"20180928160450\",\r\n" + 
				"	\"browserColorDepth\": \"24\",\r\n" + 
				"	\"billAddrState\": \"CA\",\r\n" + 
				"	\"browserJavaEnabled\": true,\r\n" + 
				"	\"purchaseCurrency\": \"840\",\r\n" + 
				"	\"cardExpiryDate\": \"2008\",\r\n" + 
				"	\"browserTZ\": \"-330\",\r\n" + 
				"	\"acctNumber\": \"4000000000001000\",\r\n" + 
				"	\"browserScreenHeight\": \"720\",\r\n" + 
				"	\"billAddrPostCode\": \"95102\",\r\n" + 
				"	\"billAddrCity\": \"San Jose\",\r\n" + 
				"	\"browserUserAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36\",\r\n" + 
				"	\"purchaseAmount\": \"300\",\r\n" + 
				"	\"purchaseExponent\": \"2\",\r\n" + 
				"	\"cardholderName\": \"Aimee\",\r\n" + 
				"	\"threeDSCompInd\": \"Y\",\r\n" + 
				"	\"browserScreenWidth\": \"1280\",\r\n" + 
				"	\"messageVersion\" : \"2.1.0\",\r\n" + 
				"	\"threeDSServerTransID\": \""+threeDSServerTransID+"\"\r\n" + 
				"}";
		
		
		
			try {
				postRq.setEntity(new StringEntity(reqStr));
				HttpResponse resp = client.execute(postRq);				
				HttpEntity entity = resp.getEntity();
				response = EntityUtils.toString(entity, "UTF-8");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		System.out.println(response);
		
		System.out.print(Verify2.verify(response));
		
		}
		
}
