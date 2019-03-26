package com.ca.tds.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ca.tds.utilityfiles.AssertionUtility;
import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.JsonUtility;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;

public class TDSFlowTest extends BaseClassTDS{
	
	private String previousTest = "TestCaseName";
	
	ITestContext testContext;
	String testCaseID;
	
	public TDSFlowTest(ITestContext testContext, String testCaseID){
		this.testContext = testContext;
		this.testCaseID = testCaseID;
	}
	
	@Test(priority = 0)
    public void brwTDSMethodTest() {
	JSONObject apiResponse=null;
	try {
		
		if(!testScenarioData.containsKey("BRW_3DSMURL_CALL")){
			testScenarioData.put("BRW_3DSMURL_CALL", new CommonUtil().getInputDataFromExcel(testContext, "TDSExcelFile", "BRW_3DSMURL_CALL", null));
		}
		Map<String, String> testCaseData = testScenarioData.get("BRW_3DSMURL_CALL").get(testCaseID);
		extentTestInit(testCaseID, testCaseData.get("TestCaseName"));
		Map<String, String> apiTestdata = testScenarioData.get("TEST SCENARIOS").get("Pre-Areq Request");
		String jsonRequest = apiTestdata.get("Request Json");
		
		jsonRequest = AssertionUtility.prepareRequest(testCaseData, jsonRequest);
		
		System.out.println("================================================================");
		System.out.println("TDS MethodURL Json Request ***:\n" + jsonRequest);
		System.out.println("================================================================");
		
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		apiResponse=sendHttpReq.httpPost(jsonRequest, caPropMap.get("TDSMethodURL"));
		
		if(apiResponse == null){
			Assert.fail("3DS server is not responding at this moment");
			return;
		}
		if("P".equalsIgnoreCase(testCaseData.get("Test Case type")) && "Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			parentTest.log(LogStatus.FAIL, "errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			return;
		}else if("N".equalsIgnoreCase(testCaseData.get("Test Case type")) && !"Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("Expected to Fail");
			parentTest.log(LogStatus.FAIL, "Expected to Fail");
			return;
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			JsonUtility.validate(apiResponse.toString(), "resource/schema/ares/preAres_schema.json");
			threeDSServerTransIDMap.put(testCaseID, apiResponse.getString("threeDSServerTransID"));
		}else{
			JsonUtility.validate(apiResponse.toString(), "resource/schema/ares/preAres_schema_n.json");
			threeDSServerTransIDMap.put(testCaseID, apiResponse.getString("threeDSServerTransID"));
		}
		
		SoftAssert sa =new SoftAssert();
		String validateDBParams = appParams.getValidateDBParams();
		assertTDSMethodRes(testCaseData, apiResponse, sa, validateDBParams);
		sa.assertAll();
	}catch(ValidationException ve){
		Assert.fail("Three DS method URL API response schema validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
	}catch (Exception e) {
		e.printStackTrace();
		Assert.fail("Three DS method URL API data validation failed.<br>"+e.getMessage()+"<br> api response : "+apiResponse);
	}
		
    }
     
    @Test(priority = 1)
    public void brwPreAreq() {


		JSONObject apiResponse = null;
		try {
			
			if(!testScenarioData.containsKey("BRW_AREQ")){
				testScenarioData.put("BRW_AREQ", new CommonUtil().getInputDataFromExcel(testContext, "TDSExcelFile", "BRW_AREQ", null));
			}
			Map<String, String> testCaseData = testScenarioData.get("BRW_AREQ").get(testCaseID);
			extentTestInit(testCaseID, testCaseData.get("TestCaseName"));
			Map<String, String> apiTestdata = testScenarioData.get("TEST SCENARIOS").get("BRW_AReq_API");
			String jsonRequest = apiTestdata.get("Request Json");
			
			String replaceTag = "#threeDSServerTransID#";
			if (!threeDSServerTransIDMap.isEmpty() && threeDSServerTransIDMap.containsKey(testCaseID)) {
				jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDMap.get(testCaseID));		
			}
			
			jsonRequest = AssertionUtility.prepareRequest(testCaseData, jsonRequest);

			System.out.println("================================================================");
			System.out.println("AReq Json Request ***:\n" + jsonRequest);
			System.out.println("================================================================");

			PostHttpRequest sendHttpReq = new PostHttpRequest();
			apiResponse = sendHttpReq.httpPost(jsonRequest, caPropMap.get("ArequestAPIURL"));
			if(apiResponse == null){
				Assert.fail("3DS server is not responding at this moment");
				parentTest.log(LogStatus.FAIL, "3DS server is not responding at this moment");
				return;
			}
			if("P".equalsIgnoreCase(testCaseData.get("Test Case type")) && "Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
				Assert.fail("errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
				parentTest.log(LogStatus.FAIL, "errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
				return;
			}else if("N".equalsIgnoreCase(testCaseData.get("Test Case type")) && !"Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
				Assert.fail("Expected to Fail");
				parentTest.log(LogStatus.FAIL, "Expected to Fail");
				return;
			}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
				JsonUtility.validate(apiResponse.toString(), "resource/schema/ares/ares_schema.json");
			}else{
				JsonUtility.validate(apiResponse.toString(), "resource/schema/ares/ares_schema_n.json");
			}
			
			
			
			if(apiResponse.has("transStatus") && "C".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				aResMap.put(testCaseID, apiResponse);
			}
			
			SoftAssert sa = new SoftAssert();
			String validateDBParams = appParams.getValidateDBParams();
			assertAres(testCaseData, apiResponse, sa, validateDBParams);
			sa.assertAll();

		}catch(ValidationException ve){
			Assert.fail("ARes response json schema validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Browser Flow:: ARes Validation Failed." + apiResponse);
		}
    }
     
    @Test(priority = 2)
    public void rReqTest() {
		
		if(aResMap == null || !aResMap.has(testCaseID)){
			System.out.println("============ No challenges from downstream to test RReq for test case id : "+testCaseID+" ================");
			return;
		}
	
		JSONObject apiResponse=null;
		try {

		if(!testScenarioData.containsKey("RREQ_TESTDATA")){
			testScenarioData.put("RREQ_TESTDATA", new CommonUtil().getInputDataFromExcel(testContext, "TDSExcelFile", "RREQ_TESTDATA", null));
		}
		Map<String, String> testCaseData = testScenarioData.get("RREQ_TESTDATA").get(testCaseID);
		extentTestInit(testCaseID, testCaseData.get("TestCaseName"));
		Map<String, String> apiTestdata = testScenarioData.get("TEST SCENARIOS").get("Result Request API");
		String jsonRequest = apiTestdata.get("Request Json");
		
		String replaceTag = "#threeDSServerTransID#";
		if (!threeDSServerTransIDMap.isEmpty() && threeDSServerTransIDMap.containsKey(testCaseID)) {
			jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDMap.get(testCaseID));		
		}

		List<String> keysToRemove = new ArrayList<>();
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			
			if(entry.getValue().equalsIgnoreCase("#REMOVE#"))			
				keysToRemove.add(entry.getKey().replaceAll("#", ""));
			else			
				jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		
		
		JSONObject reqJson = new JSONObject(jsonRequest);
		for (Map.Entry<String, String> entry : testCaseData.entrySet()) {

			String key = entry.getKey().replaceAll("#", "");
			String value = entry.getValue();
			if(reqJson.has(key) && value.equalsIgnoreCase("null")){
				reqJson.put(key, JSONObject.NULL);
			}
			
		}

		System.out.println("Keys removed from RReq request : " + keysToRemove);

		for (String key : keysToRemove){
			reqJson.remove(key);
		}
		
		JSONObject aResJSON = aResMap.getJSONObject(testCaseID);
		Iterator<String> iterAres = aResJSON.keys();
		while(iterAres.hasNext()){
			replaceTag = iterAres.next();
			if(reqJson.has(replaceTag) && "".equalsIgnoreCase(reqJson.getString(replaceTag))) {
				reqJson.put(replaceTag, aResJSON.getString(replaceTag));
			}
		}
		
		jsonRequest = reqJson.toString();
		System.out.println("================================================================");
		System.out.println("RReq Json Request ***:\n" + jsonRequest);
		System.out.println("================================================================");
		
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("ResultRequestAPI"));
		if(apiResponse == null){
			Assert.fail("3DS server is not responding at this moment");
			parentTest.log(LogStatus.FAIL, "3DS server is not responding at this moment");
			return;
		}
		if("P".equalsIgnoreCase(testCaseData.get("Test Case type")) && "Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			parentTest.log(LogStatus.FAIL, "errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			return;
		}else if("N".equalsIgnoreCase(testCaseData.get("Test Case type")) && !"Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("Expected to Fail");
			parentTest.log(LogStatus.FAIL, "Expected to Fail");
			return;
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			JsonUtility.validate(apiResponse.toString(), "resource/schema/rres/rres_schema.json");
		}else{
			JsonUtility.validate(apiResponse.toString(), "resource/schema/rres/rres_schema_n.json");
		}
		
		SoftAssert sa =new SoftAssert();
		String validateDBParams = appParams.getValidateDBParams();
		assertRRes(testCaseData, apiResponse, sa, validateDBParams);
		sa.assertAll();
			
		}catch(ValidationException ve){
			Assert.fail("RRes response data validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} 
		catch(Exception e) {
			e.printStackTrace();	
			Assert.fail("Browser Flow:: RRes Validation Failed."+apiResponse);
		}
	}

    @Test(priority = 3)
    public void verifyAPI() {
		
    	if(aResMap == null || !aResMap.has(testCaseID)){
			System.out.println("============ No challenges request to test Verify API for test case id : "+testCaseID+" ================");
			return;
		}
	
		JSONObject apiResponse=null;
		try {

		if(!testScenarioData.containsKey("VERIFY_TESTDATA")){
			testScenarioData.put("VERIFY_TESTDATA", new CommonUtil().getInputDataFromExcel(testContext, "TDSExcelFile", "VERIFY_TESTDATA", null));
		}
		Map<String, String> testCaseData = testScenarioData.get("VERIFY_TESTDATA").get(testCaseID);
		extentTestInit(testCaseID, testCaseData.get("TestCaseName"));
		Map<String, String> apiTestdata = testScenarioData.get("TEST SCENARIOS").get("Verify Request API");
		String jsonRequest = apiTestdata.get("Request Json");
		
		
		String replaceTag = "#threeDSServerTransID#";
		if (!threeDSServerTransIDMap.isEmpty() && threeDSServerTransIDMap.containsKey(testCaseID)) {
			jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDMap.get(testCaseID));		
		}

		List<String> keysToRemove = new ArrayList<>();
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			
			if(entry.getValue().equalsIgnoreCase("#REMOVE#"))			
				keysToRemove.add(entry.getKey().replaceAll("#", ""));
			else			
				jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		
		
		JSONObject reqJson = new JSONObject(jsonRequest);
		for (Map.Entry<String, String> entry : testCaseData.entrySet()) {

			String key = entry.getKey().replaceAll("#", "");
			String value = entry.getValue();
			if(reqJson.has(key) && value.equalsIgnoreCase("null")){
				reqJson.put(key, JSONObject.NULL);
			}
			
		}

		System.out.println("Keys removed from Verify API request : " + keysToRemove);

		for (String key : keysToRemove){
			reqJson.remove(key);
		}
		
		JSONObject aResJSON = aResMap.getJSONObject(testCaseID);
		Iterator<String> iterAres = aResJSON.keys();
		while(iterAres.hasNext()){
			replaceTag = iterAres.next();
			if(reqJson.has(replaceTag) && "".equalsIgnoreCase(reqJson.getString(replaceTag))) {
				reqJson.put(replaceTag, aResJSON.getString(replaceTag));
			}
		}
		jsonRequest = reqJson.toString();
		System.out.println("================================================================");
		System.out.println("Verify API Json Request ***:\n" + jsonRequest);
		System.out.println("================================================================");
		
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("TDSVerifyAPIURL"));
		if(apiResponse == null){
			Assert.fail("3DS server is not responding at this moment");
			parentTest.log(LogStatus.FAIL, "3DS server is not responding at this moment");
			return;
		}
		if("P".equalsIgnoreCase(testCaseData.get("Test Case type")) && "Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			parentTest.log(LogStatus.FAIL, "errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			return;
		}else if("N".equalsIgnoreCase(testCaseData.get("Test Case type")) && !"Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("Expected to Fail");
			parentTest.log(LogStatus.FAIL, "Expected to Fail");
			return;
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			JsonUtility.validate(apiResponse.toString(), "resource/schema/verifyapi/vapi_schema.json");
		}else{
			JsonUtility.validate(apiResponse.toString(), "resource/schema/verifyapi/vapi_schema_n.json");
		}
		
		SoftAssert sa =new SoftAssert();
		String validateDBParams = appParams.getValidateDBParams();
		assertVerifyAPIResponse(apiResponse, testCaseData, sa, validateDBParams);
		sa.assertAll();
			
		}catch(ValidationException ve){
			Assert.fail("Verify API response data validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} 
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("Browser Flow:: Verify API Validation Failed."+apiResponse);
		}
	}

	
    
    protected void extentTestInit(String testCaseID, String testCaseName) {
		
		String extentTestCase = "TC" + testCaseID + testCaseName;
		System.out.println("Inside extentTestInit strTestCase: " + extentTestCase);
		if ((previousTest != null) && !(previousTest.equalsIgnoreCase(extentTestCase))) {
			testNumber = 1;
		}

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;
		previousTest = extentTestCase;

	}

}
