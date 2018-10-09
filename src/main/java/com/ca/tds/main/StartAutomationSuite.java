package com.ca.tds.main;

import java.io.File;
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

public class StartAutomationSuite extends BaseClassTDS {

	private String previousTest = "TestCaseName";

	//	@Parameters({"SchemaFile"})

	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {

		//System.out.println("Persisted data is ::::"+threeDSServerTransIDList);
		//System.out.println("Size of Persisted data is ::::"+threeDSServerTransIDList.size());
		
		try {
		int loopcount=INSTACTIONLOOPCOUNT;

		String jsonSchemaPath=".//resource/schema/areq/areq_schema.json";
		extentTestInit(testCaseData);
		CommonUtil cu = new CommonUtil();
		Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
				"TEST SCENARIOS", "API Name");	
		Map<String, String> apiTestdata = testScenarioData.get("Arequest API");
		String jsonRequest = apiTestdata.get("Request Json");
		String replaceTag="#threeDSServerTransID#";
		if(!threeDSServerTransIDList.isEmpty())
		{

			while(loopcount<=threeDSServerTransIDList.size())
			{
				jsonRequest=jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(loopcount));
				INSTACTIONLOOPCOUNT++;
				break;
			}
		}
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		//JsonUtility.Validate(jsonRequest,jsonSchemaPath);
		System.out.println("Required repalced Json is ***:\n" + jsonRequest);
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		JSONObject apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("ArequestAPIURL"));
		/*System.out.println("Ref number: "+testCaseData.get("ExpecteddsReferenceNumber"));
		System.out.println("Trans id: "+testCaseData.get("ExpecteddsTransID"));
		System.out.println("Msg type: "+testCaseData.get("ExpectedmessageType"));
		System.out.println("Trans status: "+testCaseData.get("ExpectedtransStatus"));
		System.out.println("Error code: "+testCaseData.get("ExpectdErrorCode"));
		System.out.println("Error msg: "+testCaseData.get("ExpectedErrorMessage"));*/

		//if(APIResponse.getString("messageType").equals("ARes")){
            /*Assert.assertNotNull(APIResponse.getString("threeDSServerTransID"));	
			Assert.assertNotNull(APIResponse.getString("dsTransID"));
			Assert.assertNotNull(APIResponse.getString("acsTransID"));
			Assert.assertNotNull(APIResponse.getString("eci"));
			Assert.assertEquals(APIResponse.getString("messageVersion"), testCaseData.get("ExpectedMessageVersion"));
			Assert.assertEquals(APIResponse.getString("dsReferenceNumber"), testCaseData.get("ExpectedDSReferenceNumber"));
			Assert.assertEquals(APIResponse.getString("acsReferenceNumber"), testCaseData.get("ExpectedACSReferenceNumber"));
			Assert.assertEquals(APIResponse.getString("transStatus"), testCaseData.get("ExpectedTransStatus"));
            */
			threeDSAssert(apiResponse, testCaseData, "messageType");
			threeDSAssert(apiResponse, testCaseData, "threeDSServerTransID");
			threeDSAssert(apiResponse, testCaseData, "dsTransID");
			threeDSAssert(apiResponse, testCaseData, "acsTransID");
			threeDSAssert(apiResponse, testCaseData, "eci");
			threeDSAssert(apiResponse, testCaseData, "messageVersion");
			threeDSAssert(apiResponse, testCaseData, "dsReferenceNumber");
			threeDSAssert(apiResponse, testCaseData, "acsReferenceNumber");
			threeDSAssert(apiResponse, testCaseData, "transStatus");
			threeDSAssert(apiResponse, testCaseData, "errorCode");
			threeDSAssert(apiResponse, testCaseData, "errorComponent");
			threeDSAssert(apiResponse, testCaseData, "errorDescription");
			threeDSAssert(apiResponse, testCaseData, "errorDetail");
				//}
		//else
			//	{
			//	if(APIResponse.getString("messageType").equals("Erro"))
				//	Assert.fail(APIResponse.getString("errorCode")+"==>"+APIResponse.getString("errorDescription")+"==>"+APIResponse.getString("errorDetail"));
					//				}
		//}
		}
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("ARes Validation Failed");
		}
	}
		
	private static void threeDSAssert(JSONObject apiResponse, Map<String,String> testCaseData, String fieldName) {
		
		try {
			String fromResponse = null;
			String fromTestCaseConfig =  testCaseData.get("Expected-"+fieldName);
			
			if(fromTestCaseConfig.equalsIgnoreCase("NA"))
				return;
			else if(fromTestCaseConfig.equalsIgnoreCase("G")) {
				fromResponse = apiResponse.getString(fieldName);
				Assert.assertNotNull(fromResponse);
			}
			else {
				fromResponse = apiResponse.getString(fieldName);
				Assert.assertEquals(fromResponse,fromTestCaseConfig);
			}
		} catch (JSONException e) {
			e.printStackTrace();	
			Assert.fail("ARes Validation Failed");
		}
	}

	@DataProvider
	public Object[][] DataProvider3dsTestData(ITestContext testContext) {

		return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
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
