package com.ca.tds.main;

import java.util.ArrayList;
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

public class StartAutomationSuiteCReq extends BaseClassTDS {

	private String previousTest = "TestCaseName";

	//	@Parameters({"SchemaFile"})

	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {
		JSONObject apiResponse=null;
		try {
		int loopcount=TRANSACTIONLOOPCOUNT;

		//String jsonSchemaPath=".//resource/schema/areq/areq_schema.json";
		extentTestInit(testCaseData);
		CommonUtil cu = new CommonUtil();
		Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
				"TEST SCENARIOS", "API Name");	
		Map<String, String> apiTestdata = testScenarioData.get("Challenge API");
		String jsonRequest = apiTestdata.get("Request Json");
		String replaceTag="#threeDSServerTransID#";
		if(!threeDSServerTransIDList.isEmpty())
		{
			while(loopcount<=threeDSServerTransIDList.size())
			{
				jsonRequest=jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(loopcount));
				TRANSACTIONLOOPCOUNT++;
				break;
			}
		}
		
		List<String> keysToRemove = new ArrayList<>();
		
//		System.out.println("================================================================");
//		System.out.println("BEFORE "+jsonRequest);
//		System.out.println(testCaseData);
//		System.out.println("================================================================");
		
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			
			if(entry.getValue().equalsIgnoreCase("#REMOVE#"))			
				keysToRemove.add(entry.getKey().replaceAll("#", ""));
			else			
			jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		
		System.out.println(keysToRemove);
		
		JSONObject reqJson = new JSONObject(jsonRequest);
		for(String key : keysToRemove)
			reqJson.remove(key);
		
		jsonRequest = reqJson.toString();
		
		System.out.println("================================================================");
		System.out.println("AFTER "+jsonRequest);
		System.out.println("================================================================");
		
		//JsonUtility.Validate(jsonRequest,jsonSchemaPath);
		System.out.println("Required repalced Json is ***:\n" + jsonRequest);
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("ArequestAPIURL"));
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
			SoftAssert sa =new SoftAssert();
			threeDSAssert(apiResponse, testCaseData, "messageType", sa);
			threeDSAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSAssert(apiResponse, testCaseData, "dsTransID", sa);
			threeDSAssert(apiResponse, testCaseData, "acsTransID", sa);
			threeDSAssert(apiResponse, testCaseData, "eci", sa);
			threeDSAssert(apiResponse, testCaseData, "messageVersion", sa);
			threeDSAssert(apiResponse, testCaseData, "callerTxnRefID", sa);
			threeDSAssert(apiResponse, testCaseData, "dsReferenceNumber", sa);
			threeDSAssert(apiResponse, testCaseData, "acsReferenceNumber", sa);
			threeDSAssert(apiResponse, testCaseData, "transStatus", sa);
			threeDSAssert(apiResponse, testCaseData, "acsChallengeMandated", sa);
			threeDSAssert(apiResponse, testCaseData, "acsOperatorID", sa);
			threeDSAssert(apiResponse, testCaseData, "acsURL", sa);
			threeDSAssert(apiResponse, testCaseData, "authenticationType", sa);
			threeDSAssert(apiResponse, testCaseData, "authenticationValue", sa);
			threeDSAssert(apiResponse, testCaseData, "transStatusReason", sa);
			threeDSAssert(apiResponse, testCaseData, "creq", sa);
			threeDSAssert(apiResponse, testCaseData, "cardholderInfo", sa);
			threeDSAssert(apiResponse, testCaseData, "errorCode", sa);
			threeDSAssert(apiResponse, testCaseData, "errorComponent", sa);
			threeDSAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSAssert(apiResponse, testCaseData, "errorDetail", sa);
			sa.assertAll();
			
//			Map<String, String> rReqTemplate = testScenarioData.get("Result Request API");
//			String rReqTemplateRequest = rReqTemplate.get("Request Json");
//				//}
//		//else
			//	{
			//	if(APIResponse.getString("messageType").equals("Erro"))
				//	Assert.fail(APIResponse.getString("errorCode")+"==>"+APIResponse.getString("errorDescription")+"==>"+APIResponse.getString("errorDetail"));
					//				}
		//}
		}
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("ARes Validation Failed."+apiResponse);
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
			//sa.fail("ARes Validation Failed");
		}
	}

	@DataProvider
	public Object[][] DataProvider3dsTestData(ITestContext testContext) {

		return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
	}

	protected void extentTestInit( Map<String, String> testCaseData) {
		String extentTestCase="TC"+testCaseData.get("TestCaseID")+testCaseData.get("TestCaseName");
		System.out.println("Inside extentTestInit for CReq testcase Name: " + extentTestCase);
		//System.out.println("inside extentTestInit previousTest =" + previousTest);
		if ((previousTest != null) && !(previousTest.equalsIgnoreCase(extentTestCase))) {
			testNumber = 1;
		}

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;
		previousTest = extentTestCase;
		// System.out.println("inside extentTestInit parentTest =" + parentTest);

	}

}
