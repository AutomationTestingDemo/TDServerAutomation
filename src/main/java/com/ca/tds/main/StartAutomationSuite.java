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

public class StartAutomationSuite extends BaseClassTDS {

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
			List<String> keysToRemove = new ArrayList<>();

			for (Map.Entry<String, String> entry : testCaseData.entrySet()) {

				if (entry.getValue().equalsIgnoreCase("#REMOVE#"))
					keysToRemove.add(entry.getKey().replaceAll("#", ""));
				else
					jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
			}

			System.out.println("Keys not included in AReq request : " + keysToRemove);

			JSONObject reqJson = new JSONObject(jsonRequest);
			for (String key : keysToRemove)
				reqJson.remove(key);

			jsonRequest = reqJson.toString();

			System.out.println("================================================================");
			System.out.println("AFTER " + jsonRequest);
			System.out.println("================================================================");

			System.out.println("AReq Json Request ***:\n" + jsonRequest);
			PostHttpRequest sendHttpReq = new PostHttpRequest();
			apiResponse = sendHttpReq.httpPost(jsonRequest, caPropMap.get("ArequestAPIURL"));

			aResArr.put(apiResponse);
			SoftAssert sa = new SoftAssert();
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

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Browser Flow:: ARes Validation Failed." + apiResponse);
		}
	}

	public void threeDSAssert(JSONObject apiResponse, Map<String, String> testCaseData, String fieldName,
			SoftAssert sa) {

		try {
			String fromResponse = null;
			String testDataColumnName = "Expected-" + fieldName;
			String fromTestCaseConfig = testCaseData.get(testDataColumnName);

			if (fromTestCaseConfig.equalsIgnoreCase("NA"))
				return;
			else if (fromTestCaseConfig.equalsIgnoreCase("G")) {
				fromResponse = apiResponse.getString(fieldName);
				parentTest.log(LogStatus.INFO, fieldName + ":&nbsp;" + fromResponse);
				sa.assertNotNull(fromResponse);
			} else {
				fromResponse = apiResponse.getString(fieldName);
				parentTest.log(LogStatus.INFO, testDataColumnName + ":&nbsp;" + fromTestCaseConfig
						+ "&emsp;&emsp;&emsp;&emsp;Actual:&nbsp;" + fromResponse);
				sa.assertEquals(fromResponse, fromTestCaseConfig);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@DataProvider
	public Object[][] DataProvider3dsTestData(ITestContext testContext) {

		return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
	}

	protected void extentTestInit(Map<String, String> testCaseData) {
		String extentTestCase = "TC" + testCaseData.get("TestCaseID") + testCaseData.get("TestCaseName");
		System.out.println("inside extentTestInit strTestCase: " + extentTestCase);
		if ((previousTest != null) && !(previousTest.equalsIgnoreCase(extentTestCase))) {
			testNumber = 1;
		}

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;
		previousTest = extentTestCase;

	}

}
