package com.ca.tds.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ca.tds.utilityfiles.CommonUtil;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;

public class StartAutomationSuiteForRReq extends BaseClassTDS {

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

		List<String> keysToRemove = new ArrayList<>();
				
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			
			if(entry.getValue().equalsIgnoreCase("#REMOVE#"))			
				keysToRemove.add(entry.getKey().replaceAll("#", ""));
			else			
			jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		
		System.out.println("Keys not included in RReq request : "+keysToRemove);
		
		JSONObject reqJson = new JSONObject(jsonRequest);
		
		for(String key : keysToRemove)
			reqJson.remove(key);
		System.out.println("RReq before adding data from ARes "+reqJson);
		
		//System.out.println("aResArr values are \t"+aResArr.length());
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
		//System.out.println("RRes Json Response ***:\n" + jsonRequest);
		SoftAssert sa =new SoftAssert();
		threeDSAssert(apiResponse, testCaseData, "messageType",sa);
		threeDSAssert(apiResponse, testCaseData, "dsTransID",sa);
		threeDSAssert(apiResponse, testCaseData, "acsTransID",sa);
		//threeDSAssert(apiResponse, testCaseData, "eci",sa);
		threeDSAssert(apiResponse, testCaseData, "messageVersion",sa);
		//threeDSAssert(apiResponse, testCaseData, "messageExtension",sa);
		//threeDSAssert(apiResponse, testCaseData, "acsReferenceNumber",sa);
		threeDSAssert(apiResponse, testCaseData, "resultsStatus",sa);
		threeDSAssert(apiResponse, testCaseData, "errorCode",sa);
		threeDSAssert(apiResponse, testCaseData, "errorComponent",sa);
		threeDSAssert(apiResponse, testCaseData, "errorDescription",sa);
		threeDSAssert(apiResponse, testCaseData, "errorDetail",sa);
		sa.assertAll();
			
		}
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("Browser Flow:: RRes Validation Failed."+apiResponse);
		}
	}
		
	public void threeDSAssert(JSONObject apiResponse, Map<String,String> testCaseData, String fieldName,SoftAssert sa) {
		
		try {
			String fromResponse = null;
			String testDataColumnName="Expected-"+fieldName;
			String fromTestCaseConfig =  testCaseData.get(testDataColumnName);
			
			if(fromTestCaseConfig.equalsIgnoreCase("NA"))
				return;
			else if(fromTestCaseConfig.equalsIgnoreCase("G")) {
				fromResponse = apiResponse.getString(fieldName);
				parentTest.log(LogStatus.INFO, fieldName+":&nbsp;"+fromResponse);
				sa.assertNotNull(fromResponse);
			}
			else {
				fromResponse = apiResponse.getString(fieldName);
				parentTest.log(LogStatus.INFO, testDataColumnName+":&nbsp;"+fromTestCaseConfig+"&emsp;&emsp;&emsp;&emsp;Actual:&nbsp;"+fromResponse);
				sa.assertEquals(fromResponse,fromTestCaseConfig);
			}
		} catch (JSONException e) {
			e.printStackTrace();	
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
