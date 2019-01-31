package com.ca.tds.main;
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
public class PreAReqFor3RI extends BaseClassTDS {
	
	private String previousTest = "TestCaseName";
	@Test(dataProvider = "DataProviderPreAreq")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)throws JSONException, InterruptedException {
		JSONObject apiResponse=null;
		try {
			//String jsonSchemaPath=".//resource/schema/areq/preAreq_schema.json";
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
			apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("ArequestAPIURL"));
//		System.out.println("Trasaction id is ::::"+APIResponse.getString("threeDSServerTransID"));
//		System.out.println("Message is ::::"+APIResponse.getString("messageType"));
			//threeDSServerTransIDList=new ArrayList<String>();
			threeDSServerTransIDList.add(apiResponse.getString("threeDSServerTransID"));
			//System.out.println("3 DS Method URL is ::::"+APIResponse.getString("threeDSMethodURL"));		
			//if(APIResponse.getString("messageType").equals("ThreeDSMethodURLRes")){
			 //Assert.assertNotNull(APIResponse.getString("threeDSServerTransID"));
			// Add the remianing required fields for assertions		
//}
			
			SoftAssert sa =new SoftAssert();
			threeDSMethodURLAssert(apiResponse, testCaseData, "messageType", sa);
			threeDSMethodURLAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			sa.assertAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("App Flow:: ARes Validation Failed.<br>"+apiResponse);
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
	
	public  void threeDSMethodURLAssert(JSONObject apiResponse, Map<String,String> testCaseData, String fieldName,SoftAssert sa) {
		
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
				parentTest.log(LogStatus.INFO, testDataColumnName+":&nbsp;"+fromTestCaseConfig+":&emsp;&emsp;&emsp;&emsp;Actual:&nbsp;"+fromResponse);
				sa.assertEquals(fromResponse,fromTestCaseConfig);
			}
		} catch (JSONException e) {
			e.printStackTrace();	
			//sa.fail("ARes Validation Failed");
		}
	}
	
	protected void extentTestInit( Map<String, String> testCaseData) {
		String extentTestCase="TC"+testCaseData.get("TestCaseID")+testCaseData.get("TestCaseName");
		System.out.println("inside extentTestInit strTestCase: " + extentTestCase);
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
