package ca.com.tds.restapi;

import static com.jayway.restassured.RestAssured.given;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class PostHttpRequest {
	// @Test
	public JSONObject httpPost(String reqStr,String APIUrl) throws JSONException, InterruptedException {
		// Building request using requestSpecBuilder
		JSONObject jSONResponseBody=null;
		try {
			RequestSpecBuilder builder = new RequestSpecBuilder();
			// Setting API's body
			builder.setBody(reqStr);

			// Setting content type as application/json or application/xml
			builder.setContentType("application/json;");

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
}
