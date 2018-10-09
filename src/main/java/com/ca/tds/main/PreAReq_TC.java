package com.ca.tds.main;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.JsonUtility;

import ca.com.tds.restapi.PostHttpRequest;

public class PreAReq_TC extends BaseClassTDS {
	
	private String previousTest = "TestCaseName";

	//@Parameters({"SchemaFile"})
	@Test(dataProvider = "DataProviderPreAreq")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {
		String jsonSchemaPath=".//resource/schema/areq/preAreq_schema.json";
        extentTestInit(testCaseData);
		CommonUtil cu = new CommonUtil();
		Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
				"TEST SCENARIOS", "API Name");
			
		Map<String, String> apiTestdata = testScenarioData.get("Pre-Areq Request");
		String jsonRequest = apiTestdata.get("Request Json");
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		
		//JsonUtility.Validate(jsonRequest,jsonSchemaPath);
		System.out.println("Required repalced Json is ***:\n" + jsonRequest);
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		JSONObject APIResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("ArequestAPIURL"));
//		System.out.println("API Response is ::::"+APIResponse);
//		System.out.println("Trasaction id is ::::"+APIResponse.getString("threeDSServerTransID"));
//		System.out.println("Message is ::::"+APIResponse.getString("messageType"));
		//threeDSServerTransIDList=new ArrayList<String>();
		threeDSServerTransIDList.add(APIResponse.getString("threeDSServerTransID"));
		//System.out.println("3 DS Method URL is ::::"+APIResponse.getString("threeDSMethodURL"));		
	if(APIResponse.getString("messageType").equals("ThreeDSMethodURLRes")){
		 Assert.assertNotNull(APIResponse.getString("threeDSServerTransID"));
		// Add the remianing required fields for assertions		
	}
	}

	@DataProvider
	public Object[][] DataProviderPreAreq(ITestContext testContext) {
		try {
			return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error while reading data from excel sheet");
		}
		return null;
	}
	
	protected void extentTestInit( Map<String, String> testCaseData) {
		String testCaseID=testCaseData.get("TestCaseID");
		String strTestCase=testCaseData.get("TestCaseName");
        //System.out.println("inside extentTestInit strTestCase =" + strTestCase);
        //System.out.println("inside extentTestInit previousTest =" + previousTest);
        if ((previousTest != null) && !(previousTest.equalsIgnoreCase(strTestCase))) {
            testNumber = 1;
        }

        parentTest = extent.startTest("TC"+testCaseID+"_"+strTestCase);
        APIAutomationCommonPage.parentTest = parentTest;
        previousTest = strTestCase;
       // System.out.println("inside extentTestInit parentTest =" + parentTest);

    }

}
