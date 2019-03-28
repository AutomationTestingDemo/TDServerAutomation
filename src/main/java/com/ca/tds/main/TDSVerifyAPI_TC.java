package com.ca.tds.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ca.tds.dao.TDSDao;
import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.JsonUtility;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;

public class TDSVerifyAPI_TC extends BaseClassTDS {

	private String previousTest = "TestCaseName";
	private static int loopcount=0;

	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {
		
		if(aResArr == null || aResArr.length() == 0){
			System.out.println("============ No challenges request to test Verify API ================");
			return;
		}
	
		JSONObject apiResponse=null;
		try {

		extentTestInit(testCaseData);
		CommonUtil cu = new CommonUtil();
		Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
				"TEST SCENARIOS", "API Name");	
		Map<String, String> apiTestdata = testScenarioData.get("Verify Request API");
		String jsonRequest = apiTestdata.get("Request Json");
		
		
		if (!threeDSServerTransIDList.isEmpty()) {
			String replaceTag = "#threeDSServerTransID#";
			while (loopcount < threeDSServerTransIDList.size()) {
				jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(loopcount));
				TRANSACTIONLOOPCOUNT++;
				break;
			}
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
		
		JSONObject aResJSON = aResArr.getJSONObject(loopcount);
		Iterator<String> iterAres = aResJSON.keys();
		while(iterAres.hasNext()){
			String replaceTag = iterAres.next();
			if(reqJson.has(replaceTag) && "".equalsIgnoreCase(reqJson.getString(replaceTag))) {
				reqJson.put(replaceTag, aResJSON.getString(replaceTag));
			}
		}
		loopcount++;
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
		if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
		
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(threeDSServerTransIDList.get(loopcount), caPropMap);
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Verify API threeDSServerTransID not found in DB tables");
				parentTest.log(LogStatus.FAIL, "Verify API threeDSServerTransID not found in DB tables");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Verify API more than 1 threeDSServerTransID found in DB tables");
				parentTest.log(LogStatus.FAIL, "Verify API more than 1 threeDSServerTransID found in DB tables");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa, tdsMethodDBData.get("CALLERTXNREFID"));
			threeDSFieldAssert(apiResponse, testCaseData, "eci", sa, tdsMethodDBData.get("ECI"));
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa, tdsMethodDBData.get("AUTHENTICATIONVALUE"));
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa, tdsMethodDBData.get("TRANSSTATUSRREQ"));
			if("N".equalsIgnoreCase(apiResponse.getString("transStatus")) || "U".equalsIgnoreCase(apiResponse.getString("transStatus")) || "R".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa, tdsMethodDBData.get("TRANSSTATUSREASON"));
			}
			
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa, tdsMethodDBData.get("RESULTSSTATUS"));
			threeDSFieldAssert(apiResponse, testCaseData, "interactionCounter", sa, tdsMethodDBData.get("INTERACTIONCOUNTER"));
		
		}else if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "N".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
			
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getErrorLogDataByTDSTransID(threeDSServerTransIDList.get(loopcount), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("RReq API threeDSServerTransID not found in DB tables");
				parentTest.log(LogStatus.FAIL, "RReq API threeDSServerTransID not found in DB tables");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("RReq API more than 1 threeDSServerTransID found in DB tables");
				parentTest.log(LogStatus.FAIL, "RReq API more than 1 threeDSServerTransID found in DB tables");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa, tdsMethodDBData.get("ERRORCODE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa, tdsMethodDBData.get("ERRORCOMPONENT"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa, tdsMethodDBData.get("ERRORMESSAGETYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa, tdsMethodDBData.get("ERRORDETAIL"));
			
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa);
		    threeDSFieldAssert(apiResponse, testCaseData, "eci", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa);
			
			if("N".equalsIgnoreCase(apiResponse.getString("transStatus")) || "U".equalsIgnoreCase(apiResponse.getString("transStatus")) || "R".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa);
			}
			
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "interactionCounter", sa);
		}else{
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa);
		}
		sa.assertAll();
			
		}catch(ValidationException ve){
			Assert.fail("Verify API response data validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} 
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("Browser Flow:: Verify API Validation Failed."+apiResponse);
		}
	}

	@DataProvider
	public Object[][] DataProvider3dsTestData(ITestContext testContext) {

		return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
	}

	protected void extentTestInit( Map<String, String> testCaseData) {
		String extentTestCase="TC"+testCaseData.get("TestCaseID")+testCaseData.get("TestCaseName");
		System.out.println("inside extentTestInit strTestCase: " + extentTestCase);
		if ((previousTest != null) && !(previousTest.equalsIgnoreCase(extentTestCase))) {
			testNumber = 1;
		}

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;
		previousTest = extentTestCase;

	}

}
