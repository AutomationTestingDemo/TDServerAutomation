package com.ca.tds.main;

import java.util.Map;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ca.tds.utilityfiles.AppParams;
import com.ca.tds.utilityfiles.AssertionUtility;
import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.JsonUtility;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;


public class TDSMethodURL_TC extends BaseClassTDS {
	
	private String previousTest = "TestCaseName";
	@Test(dataProvider = "DataProviderTDSMethodURL")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)throws JSONException, InterruptedException {
		JSONObject apiResponse=null;
		try {

			extentTestInit(testCaseData);
			CommonUtil cu = new CommonUtil();
			String enableEncryption = AppParams.getEnableDecryption();
			Map<String, Map<String, String>> testScenarioData= null;
			if(enableEncryption != null && "true".equalsIgnoreCase(enableEncryption)){
				testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFileE",
					"TEST SCENARIOS", "API Name");
			}else{
				testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFileC",
						"TEST SCENARIOS", "API Name");
			}
			Map<String, String> apiTestdata = testScenarioData.get("Pre-Areq Request");
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
				threeDSServerTransIDList.add(apiResponse.getString("threeDSServerTransID"));
			}else{
				JsonUtility.validate(apiResponse.toString(), "resource/schema/ares/preAres_schema_n.json");
				threeDSServerTransIDList.add(apiResponse.getString("threeDSServerTransID"));
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

	@DataProvider
	public Object[][] DataProviderTDSMethodURL(ITestContext testContext) {
		try {
			String enableEncryption = AppParams.getEnableDecryption();
			if(enableEncryption != null && "true".equalsIgnoreCase(enableEncryption)){
				return new CommonUtil().getInputData(testContext, "TDSExcelFileE", "ExcelSheetVerify");
			}else{
				return new CommonUtil().getInputData(testContext, "TDSExcelFileC", "ExcelSheetVerify");
			}
		} catch (Exception e) {
			System.out.println("Error while reading data from excel sheet");
		}
		return null;
	}
	
	protected void extentTestInit(Map<String, String> testCaseData) {
		
		String extentTestCase = "TC" + testCaseData.get("TestCaseID") + testCaseData.get("TestCaseName");
		System.out.println("Inside extentTestInit strTestCase: " + extentTestCase);
		if ((previousTest != null) && !(previousTest.equalsIgnoreCase(extentTestCase))) {
			testNumber = 1;
		}

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;
		previousTest = extentTestCase;

	}

}
