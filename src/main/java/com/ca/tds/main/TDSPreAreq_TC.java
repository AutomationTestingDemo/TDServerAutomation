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

public class TDSPreAreq_TC extends BaseClassTDS {

	private String previousTest = "TestCaseName";

	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {

		JSONObject apiResponse = null;
		try {
			int loopcount = TRANSACTIONLOOPCOUNT;

			extentTestInit(testCaseData);
			CommonUtil cu = new CommonUtil();
			
		/*	Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
					"TEST SCENARIOS", "API Name");*/
			
			String enableEncryption = AppParams.getEnableDecryption();
			Map<String, Map<String, String>> testScenarioData= null;
			if(enableEncryption != null && "true".equalsIgnoreCase(enableEncryption)){
				testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFileE",
					"TEST SCENARIOS", "API Name");
			}else{
				testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFileC",
						"TEST SCENARIOS", "API Name");
			}
			
			Map<String, String> apiTestdata=null;
			
			if (!threeDSServerTransIDList.isEmpty()) {
			 apiTestdata = testScenarioData.get("BRW_AReq_API");
			 }
			else {
			 apiTestdata = testScenarioData.get("BRW_AReq_API_ByPass3DSMethodurl");
			}
			String jsonRequest = apiTestdata.get("Request Json");
			
			String replaceTag = "#threeDSServerTransID#";
			if (!threeDSServerTransIDList.isEmpty()) {
				
				if(testCaseData.containsKey("DSName")) {
					
					if(testCaseData.get("DSName").equals("amex")) {
						jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(0));
					}
					else if (testCaseData.get("DSName").equals("mc")) {
						
						jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(1));
					}
					else {
						jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(2));
					}
				}
				else {	
					
					while (loopcount < threeDSServerTransIDList.size()) {
					jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(loopcount));
					TRANSACTIONLOOPCOUNT++;
					break;
				} }
			}
			
			JSONObject jsonReq = AssertionUtility.prepareRequest(testCaseData, jsonRequest);
			
			jsonRequest=jsonReq.toString();
			
			parentTest.log(LogStatus.INFO, jsonRequest);

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
				
				Assert.fail("errorComponent : "+apiResponse.getString("errorComponent")+", "
						+ "errorCode : "+apiResponse.getString("errorCode")+", errorDescription : "+apiResponse.getString("errorDescription")+", "
						+ "errorDetail : "+apiResponse.getString("errorDetail")+", threeDSServerTransID : "+apiResponse.getString("threeDSServerTransID"));
				parentTest.log(LogStatus.FAIL, "errorComponent: "+apiResponse.getString("errorComponent")+", "
						+ "errorCode: "+apiResponse.getString("errorCode")+", errorDescription:"+apiResponse.getString("errorDescription")+", "
								+ "errorDetail :"+apiResponse.getString("errorDetail")+", threeDSServerTransID : "+apiResponse.getString("threeDSServerTransID"));
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
			
			if(!"N".equalsIgnoreCase(testCaseData.get("Test Case type"))) {
			
			if("C".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				aResArr.put(apiResponse);
			}}
			
			SoftAssert sa = new SoftAssert();
			String validateDBParams = appParams.getValidateDBParams();
			assertAres(testCaseData, apiResponse, sa, validateDBParams);
			sa.assertAll();

		} catch(ValidationException ve){
			System.out.println(ve.getErrorMessage());
			Assert.fail("Json response json schema validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Browser Flow:: Json Validation Failed."+e.getMessage()+"<br> api response : "+ apiResponse);
		}
	}

	@DataProvider
	public Object[][] DataProvider3dsTestData(ITestContext testContext) {

		/*return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");*/
		
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
