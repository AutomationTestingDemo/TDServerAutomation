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
			Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
					"TEST SCENARIOS", "API Name");
			Map<String, String> apiTestdata = testScenarioData.get("BRW_AReq_API");
			String jsonRequest = apiTestdata.get("Request Json");
			
			String replaceTag = "#threeDSServerTransID#";
			if (!threeDSServerTransIDList.isEmpty()) {
				while (loopcount < threeDSServerTransIDList.size()) {
					jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(loopcount));
					TRANSACTIONLOOPCOUNT++;
					break;
				}
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
			
			
			
			if("C".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				aResArr.put(apiResponse);
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

	@DataProvider
	public Object[][] DataProvider3dsTestData(ITestContext testContext) {

		return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
		
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