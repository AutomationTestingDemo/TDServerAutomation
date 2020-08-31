package com.ca.tds.main;

import java.util.Map;
import java.util.Set;
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
import com.ca.tds.utilityfiles.JsonText;
import com.ca.tds.utilityfiles.JsonUtility;
import com.ca.tds.utilityfiles.ServiceRestart;
import com.ca.tds.utilityfiles.ThreeDSSdbAPI;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;

public class IdempotencyAReq extends BaseClassTDS {

	private String previousTest = "TestCaseName";
	int count=0;
	
	int counter=0;

	@SuppressWarnings("unused")
	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {

		JSONObject apiResponse = null;
		try {
			
			String threeDSServerTransID=null;
			String responseAres=null;

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
					
					if(testCaseData.get("DSName").contains("amex")) {
						jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(0));
						threeDSServerTransID = threeDSServerTransIDList.get(0).toString();
						count++;
					}
					else if (testCaseData.get("DSName").contains("mc")) {
						
						jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(1));
						threeDSServerTransID = threeDSServerTransIDList.get(1).toString();
						count++;
					}
					else {
						jsonRequest = jsonRequest.replace(replaceTag, threeDSServerTransIDList.get(2));
						threeDSServerTransID = threeDSServerTransIDList.get(2).toString();
						count++;
					}
				}
			}
			
				
			/*
			 * if(counter==0) {
			 * 
			 * ThreeDSSdbAPI dbapi = new ThreeDSSdbAPI(); String result =
			 * dbapi.updateMtdConfig(BaseClassTDS.caPropMap,1);
			 * ServiceRestart.server3DSrestart(); System.out.println("Back to Main");
			 * System.out.println(result);
			 * 
			 * Thread.sleep(80000); }
			 */
			
			JSONObject jsonReq = AssertionUtility.prepareRequest(testCaseData, jsonRequest);
			
			jsonRequest=jsonReq.toString();
			
			parentTest.log(LogStatus.INFO, jsonRequest);
			
			counter++;
			System.out.println("CounterValue "+counter);
			
			
			/*
			 * if(counter==7) {
			 * 
			 * Thread.sleep(120000); }
			 */
			

				 /*ThreeDSSdbAPI db = new ThreeDSSdbAPI();
	        	  responseAres = db.getAResFromDB(threeDSServerTransID,caPropMap);*/

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
			
			if(count%2!=0) {
				
				JsonText.jsonResponseWrite(apiResponse.toString().trim());
			}
			
			else {
				
				responseAres = JsonText.jsonResponseRead();
				/*System.out.println("Reading Data from file : "+responseAres);*/
			}
				
			if(responseAres!=null) {
	
				String response = apiResponse.toString();
				JSONObject Expectedjson = new JSONObject(responseAres.trim());
				System.out.println("Actual response : "+response);
				System.out.println("Expected response : "+responseAres);
				
			Set<String>keys = Expectedjson.keySet();		
			for(String key : keys) {
				
				if(Expectedjson.get(key).equals(apiResponse.get(key))) {
					
					SoftAssert sa = new SoftAssert();
                	parentTest.log(LogStatus.INFO, "Expected-"+key+":&nbsp;"+Expectedjson.get(key)+", &emsp;Actual-"+key+":&nbsp;"+apiResponse.get(key));
    				sa.assertEquals(apiResponse.get(key),Expectedjson.get(key));
                	sa.assertAll();
					
				} }
				
			}
			
			else {
				
			SoftAssert sa = new SoftAssert();
			String validateDBParams = appParams.getValidateDBParams();
			assertAres(testCaseData, apiResponse, sa, validateDBParams);
			sa.assertAll();
			
			}
					
			/*
			 * if(counter==9) {
			 * 
			 * ThreeDSSdbAPI dbapi = new ThreeDSSdbAPI(); String result =
			 * dbapi.updateMtdConfig(BaseClassTDS.caPropMap,60);
			 * ServiceRestart.server3DSrestart(); System.out.println(result);
			 * Thread.sleep(90000);
			 * 
			 * }
			 */
			 

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
