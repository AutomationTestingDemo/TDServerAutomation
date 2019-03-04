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

public class TDSRReq_TC extends BaseClassTDS {

	private String previousTest = "TestCaseName";
	private static int loopcount=0;


	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {
	
		JSONObject apiResponse=null;
		try {

		extentTestInit(testCaseData);
		CommonUtil cu = new CommonUtil();
		Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
				"TEST SCENARIOS", "API Name");	
		Map<String, String> apiTestdata = testScenarioData.get("Result Request API");
		String jsonRequest = apiTestdata.get("Request Json");
		JSONObject reqJson = new JSONObject(jsonRequest);

		List<String> keysToRemove = new ArrayList<>();
				
		for (Map.Entry<String, String> entry : testCaseData.entrySet()) {

			String key = entry.getKey().replaceAll("#", "");
			String value = entry.getValue();
			if (value.equalsIgnoreCase("#REMOVE#")){
				keysToRemove.add(key);
			}else if(reqJson.has(key) && value.equalsIgnoreCase("null")){
				reqJson.put(key, JSONObject.NULL);
			}else if(reqJson.has(key)){
				reqJson.put(key, value);
			}
			
		}

		System.out.println("Keys removed from AReq request : " + keysToRemove);

		for (String key : keysToRemove){
			reqJson.remove(key);
		}
		
		for(String key : keysToRemove)
			reqJson.remove(key);
		System.out.println("RReq before adding data from ARes "+reqJson);
	
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
		System.out.println("RReq Json Request ***:\n" + jsonRequest);
		System.out.println("================================================================");
		
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("ResultRequestAPI"));
		if(apiResponse == null){
			Assert.fail("3DS server is not responding at this moment");
			parentTest.log(LogStatus.FAIL, "3DS server is not responding at this moment");
			return;
		}
		if("Y".equalsIgnoreCase(testCaseData.get("Test Case type")) && "Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			parentTest.log(LogStatus.FAIL, "errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorComponent")+", errorDescription:"+apiResponse.getString("errorDescription"));
			return;
		}else if("N".equalsIgnoreCase(testCaseData.get("Test Case type")) && "RRes".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("Expected to Fail");
			parentTest.log(LogStatus.FAIL, "Expected to Fail");
			return;
		}else if("Y".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			JsonUtility.validate(apiResponse.toString(), "resource/schema/rres/rres_schema.json");
		}else{
			JsonUtility.validate(apiResponse.toString(), "resource/schema/rres/rres_schema_n.json");
		}
		
		TDSDao tDSDao = new TDSDao();
		List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(threeDSServerTransIDList.get(loopcount));
		if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
			Assert.fail("RReq API threeDSServerTransID not found in DB tables");
			parentTest.log(LogStatus.FAIL, "Pre AReq API threeDSServerTransID not found in DB tables");
		}else if(tdsMethodListFromDB.size() > 1){
			Assert.fail("RReq API more than 1 threeDSServerTransID found in DB tables");
			parentTest.log(LogStatus.FAIL, "Pre AReq API more than 1 threeDSServerTransID found in DB tables");
		}
		
		HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
		SoftAssert sa =new SoftAssert();
		threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa, null);
		threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
		threeDSFieldAssert(apiResponse, testCaseData, "dsTransID",sa, tdsMethodDBData.get("DSTRANSID"));
		threeDSFieldAssert(apiResponse, testCaseData, "acsTransID",sa, tdsMethodDBData.get("ACSTRANSID"));
		threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa, tdsMethodDBData.get("MESSAGEVERSION"));
		threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa, tdsMethodDBData.get("RESULTSSTATUS"));
		threeDSFieldAssert(apiResponse, testCaseData, "errorCode",sa, null);
		threeDSFieldAssert(apiResponse, testCaseData, "errorComponent",sa, null);
		threeDSFieldAssert(apiResponse, testCaseData, "errorDescription",sa, null);
		threeDSFieldAssert(apiResponse, testCaseData, "errorDetail",sa, null);
		sa.assertAll();
			
		}catch(ValidationException ve){
			Assert.fail("RRes response data validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} 
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("Browser Flow:: RRes Validation Failed."+apiResponse);
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
