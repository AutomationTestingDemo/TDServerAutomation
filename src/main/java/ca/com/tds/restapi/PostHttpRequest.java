package ca.com.tds.restapi;

import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class PostHttpRequest {
	// @Test
	public JSONObject httpPostBuilder(String reqStr,String APIUrl) throws JSONException, InterruptedException {
		// Building request using requestSpecBuilder
		JSONObject jSONResponseBody=null;
		try {
			RequestSpecBuilder builder = new RequestSpecBuilder();
			// Setting API's body
			builder.setBody(reqStr);
			
			// Setting content type as application/json or application/xml
			builder.setContentType("application/json;charset=UTF-8");

			RequestSpecification requestSpec = builder.build();

			// Making post request with authentication, leave blank in case there are no
			// credentials- basic("","")
			System.out.println("API URL is: "+APIUrl);
			Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(APIUrl);
			jSONResponseBody = new JSONObject(response.body().asString());

			System.out.println("API Response is: "+jSONResponseBody);
			Assert.assertEquals(response.getStatusCode(),200);
			// Fetching the desired value of a parameter
			//String result = JSONResponseBody.getString("errorCode");
			return jSONResponseBody;
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("3DS server is not responding at this moment");
		}
		return jSONResponseBody;
	}
	
	public JSONObject httpPost(String reqStr,String APIUrl)
	  throws ClientProtocolException, IOException {
		JSONObject jSONResponseBody=null;
		
		System.out.println("API URL is: "+APIUrl);
	    CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(APIUrl);
	 
	   // String json = "{\"caMerchantID\":\"CA_EU\",\"acctNumber\":\"4000500060000008\",\"messageType\":\"ThreeDSMethodURLReq\",\"callerTxnRefID\":\"341b7153-da86-4543-860f-2ba47f0168a5\"}";
	    StringEntity entity = new StringEntity(reqStr);
	    httpPost.setEntity(entity);
	    //httpPost.setHeader("Accept", "application/json;");
	    httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
	 
	    CloseableHttpResponse response = client.execute(httpPost);
	    HttpEntity resentity = response.getEntity();
	    String responseString = EntityUtils.toString(resentity, "UTF-8");
	    System.out.println("API Response is: "+responseString);
	    jSONResponseBody = new JSONObject(responseString);
	   
	    client.close();
	    return jSONResponseBody;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException{
		
	}
}
