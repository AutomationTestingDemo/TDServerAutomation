package com.ca.tds.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ca.tds.dao.TDSDao;
import com.ca.tds.utilityfiles.AppParams;
import com.ca.tds.utilityfiles.AssertionUtility;
import com.ca.tds.utilityfiles.CRESPrep;
import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.JsonUtility;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;

@SuppressWarnings("unused")
public class TDSVerifyAPI_TC extends BaseClassTDS {

	private String previousTest = "TestCaseName";
	private static int loopcount=0;

	@Test(dataProvider = "DataProvider3dsTestData")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)
			throws JSONException, InterruptedException {
		
		if(aResArr == null || aResArr.length() == 0){
			
			extentTestInit(testCaseData);
			parentTest.log(LogStatus.INFO,"============No challenges request to test Verify API================");
			System.out.println("============ No challenges request to test Verify API ================");
			Assert.fail();
			return;
		}
	
		JSONObject apiResponse=null;
		try {

		extentTestInit(testCaseData);
		CommonUtil cu = new CommonUtil();
		
		Map<String, String> apiTestdata=null;
		String enableEncryption = AppParams.getEnableDecryption();
		Map<String, Map<String, String>> testScenarioData= null;
		if(enableEncryption != null && "true".equalsIgnoreCase(enableEncryption)){
			testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFileE",
				"TEST SCENARIOS", "API Name");
		}else{
			testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFileC",
					"TEST SCENARIOS", "API Name");
		}
			
		apiTestdata = testScenarioData.get("Verify Request API");
		String jsonRequest = apiTestdata.get("Request Json"); 
		
		/* int i=0;
		    
		    while(i!=5) {*/
		    	
		JSONObject jsonReq = AssertionUtility.prepareRequest(testCaseData, jsonRequest);
	    String transStatus = null;
	   
	    JSONObject aResJSON = aResArr.getJSONObject(loopcount);
		//System.out.println("acsTransID :"+aResJSON.get("acsTransID"));
		//System.out.println("threeDSServerTransID :"+aResJSON.get("threeDSServerTransID"));
	for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			
		if(entry.getKey().equals("#transStatus#")) {
			
			transStatus=entry.getValue();
		}
		
		}
	 String cres = CRESPrep.cresBody(aResJSON.get("acsTransID").toString(), aResJSON.get("threeDSServerTransID").toString(),transStatus);
		Iterator<String> iterAres = aResJSON.keys();
		while(iterAres.hasNext()){
			String replaceTag = iterAres.next();
			if(jsonReq.has(replaceTag) && "".equalsIgnoreCase(jsonReq.getString(replaceTag))) {
				jsonReq.put(replaceTag, aResJSON.getString(replaceTag));
			}  
			
			if(jsonReq.has(replaceTag) && "empty".equalsIgnoreCase(jsonReq.getString(replaceTag))) {
				jsonReq.put(replaceTag,"");
			} 
		}
		loopcount++;
		
		if(jsonReq.has("cres") && "".equalsIgnoreCase(jsonReq.getString("cres"))) {
			jsonReq.put("cres",cres);
		}
		
		jsonRequest = jsonReq.toString();
		parentTest.log(LogStatus.INFO, jsonRequest);
		
		System.out.println("================================================================");
		System.out.println("Verify API Json Request ***:\n" + jsonRequest);
		System.out.println("================================================================");
		
		PostHttpRequest sendHttpReq = new PostHttpRequest();
		apiResponse=sendHttpReq.httpPost(jsonRequest,caPropMap.get("TDSVerifyAPIURL"));
		if(apiResponse == null){
			Assert.fail("3DS server is not responding at this moment");
			parentTest.log(LogStatus.FAIL, "3DS server is not responding at this moment");
			return;
		}
		if("P".equalsIgnoreCase(testCaseData.get("Test Case type")) && "Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorCode")+", errorDescription:"+apiResponse.getString("errorDescription")
			+ "errorDetail : "+apiResponse.getString("errorDetail")+", threeDSServerTransID : "+apiResponse.getString("threeDSServerTransID"));
			parentTest.log(LogStatus.FAIL,"errorComponent: "+apiResponse.getString("errorComponent")+", errorCode: "+apiResponse.getString("errorCode")+", errorDescription:"+apiResponse.getString("errorDescription")
			+ "errorDetail : "+apiResponse.getString("errorDetail")+", threeDSServerTransID : "+apiResponse.getString("threeDSServerTransID"));
			return;
		}else if("N".equalsIgnoreCase(testCaseData.get("Test Case type")) && !"Erro".equalsIgnoreCase(apiResponse.getString("messageType"))){
			Assert.fail("Expected to Fail");
			parentTest.log(LogStatus.FAIL, "Expected to Fail");
			return;
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			JsonUtility.validate(apiResponse.toString(), "resource/schema/verifyapi/vapi_schema.json");
		}else{
			JsonUtility.validate(apiResponse.toString(), "resource/schema/verifyapi/vapi_schema_n.json");
		}
		
		SoftAssert sa =new SoftAssert();
		String validateDBParams = appParams.getValidateDBParams();
		if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
		
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(threeDSServerTransIDList.get(loopcount), caPropMap);
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Verify API threeDSServerTransID not found in DB tables");
				parentTest.log(LogStatus.FAIL, "Verify API threeDSServerTransID not found in DB tables");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Verify API more than 1 threeDSServerTransID found in DB tables");
				parentTest.log(LogStatus.FAIL, "Verify API more than 1 threeDSServerTransID found in DB tables");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa, tdsMethodDBData.get("CALLERTXNREFID"));
			threeDSFieldAssert(apiResponse, testCaseData, "eci", sa, tdsMethodDBData.get("ECI"));
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa, tdsMethodDBData.get("AUTHENTICATIONVALUE"));
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa, tdsMethodDBData.get("TRANSSTATUSRREQ"));
			if("N".equalsIgnoreCase(apiResponse.getString("transStatus")) || "U".equalsIgnoreCase(apiResponse.getString("transStatus")) || "R".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa, tdsMethodDBData.get("TRANSSTATUSREASON"));
			}
			
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa, tdsMethodDBData.get("RESULTSSTATUS"));
			threeDSFieldAssert(apiResponse, testCaseData, "interactionCounter", sa, tdsMethodDBData.get("INTERACTIONCOUNTER"));
		
		}else if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "N".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
			
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getErrorLogDataByTDSTransID(threeDSServerTransIDList.get(loopcount), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("RReq API threeDSServerTransID not found in DB tables");
				parentTest.log(LogStatus.FAIL, "RReq API threeDSServerTransID not found in DB tables");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("RReq API more than 1 threeDSServerTransID found in DB tables");
				parentTest.log(LogStatus.FAIL, "RReq API more than 1 threeDSServerTransID found in DB tables");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa, tdsMethodDBData.get("ERRORCODE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa, tdsMethodDBData.get("ERRORCOMPONENT"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa, tdsMethodDBData.get("ERRORMESSAGETYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa, tdsMethodDBData.get("ERRORDETAIL"));
			
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);	
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa);
		   // threeDSFieldAssert(apiResponse, testCaseData, "eci", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa);
			
			if("N".equalsIgnoreCase(apiResponse.getString("transStatus")) || "U".equalsIgnoreCase(apiResponse.getString("transStatus")) || "R".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa);
			}
			
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "interactionCounter", sa);
		}else{
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa);
		}
		sa.assertAll();
			
	/*	i++; }*/  }catch(ValidationException ve){
			Assert.fail("Verify API response data validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		} 
		catch(Exception e) {
		e.printStackTrace();	
		Assert.fail("Browser Flow:: Verify API Validation Failed."+apiResponse);
		} 
		
	/*	finally {
			
		loopcount++;
		
		}*/
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
