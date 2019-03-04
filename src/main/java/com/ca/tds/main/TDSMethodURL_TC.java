package com.ca.tds.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ca.tds.dao.TDSDao;
import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.JsonUtility;

import ca.com.tds.restapi.PostHttpRequest;


public class TDSMethodURL_TC extends BaseClassTDS {
	
	private String previousTest = "TestCaseName";
	@Test(dataProvider = "DataProviderTDSMethodURL")
	public void testArequestAPI(ITestContext testContext, Map<String, String> testCaseData)throws JSONException, InterruptedException {
		JSONObject apiResponse=null;
		try {

			extentTestInit(testCaseData);
			CommonUtil cu = new CommonUtil();
			Map<String, Map<String, String>> testScenarioData = cu.getInputDataFromExcel(testContext, "TDSExcelFile",
					"TEST SCENARIOS", "API Name");
				
			Map<String, String> apiTestdata = testScenarioData.get("Pre-Areq Request");
			String jsonRequest = apiTestdata.get("Request Json");
			JSONObject jsonObject = new JSONObject(jsonRequest);
			
			for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
				
				String key = entry.getKey().replaceAll("#", "");
				String value = entry.getValue();
				if(jsonObject.has(key) && value.equalsIgnoreCase("null")){
					jsonObject.put(key, JSONObject.NULL);
				}else if(jsonObject.has(key)){
					jsonObject.put(key, value);
				}
				
			}
			
			System.out.println("TDSMethodURL Request :\n" + jsonObject.toString());
			PostHttpRequest sendHttpReq = new PostHttpRequest();
			apiResponse=sendHttpReq.httpPost(jsonObject.toString(), caPropMap.get("TDSMethodURL"));
			if(apiResponse == null){
				Assert.fail("3DS server is not responding at this moment");
				return;
			}
			JsonUtility.validate(apiResponse.toString(), "resource/schema/ares/preAres_schema.json");
			threeDSServerTransIDList.add(apiResponse.getString("threeDSServerTransID"));
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(apiResponse.getString("threeDSServerTransID"));
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Three DS method URL API threeDSServerTransID not found in DB tables");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Three DS method URL API more than 1 threeDSServerTransID found in DB tables");
			}
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			//needs to change this when 3ds does insertion into tables properly
			
			SoftAssert sa =new SoftAssert();
			//threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa, null);
			//threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			//threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa, tdsMethodDBData.get("CALLERTXNREFID"));
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa, tdsMethodDBData.get("MESSAGEVERSION"));
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSMethodURL", sa, tdsMethodDBData.get("ACSURL"));
			sa.assertAll();
		}catch(ValidationException ve){
			Assert.fail("Three DS method URL API response data validation failed.<br>"+ve.getErrorMessage()+"<br> api response : "+apiResponse);
		}catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Three DS method URL API data validation failed.<br>"+e.getMessage()+"<br> api response : "+apiResponse);
		}
	}

	@DataProvider
	public Object[][] DataProviderTDSMethodURL(ITestContext testContext) {
		try {
			return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
		} catch (Exception e) {
			System.out.println("Error while reading data from excel sheet");
		}
		return null;
	}
	
	protected void extentTestInit(Map<String, String> testCaseData) {
		String extentTestCase = testCaseData.get("TestCaseName");
		System.out.println("Test case name : " + extentTestCase);
		
		if ((previousTest != null) && !(previousTest.equalsIgnoreCase(extentTestCase))) {
			testNumber = 1;
		}

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;
		previousTest = extentTestCase;


    }

}
