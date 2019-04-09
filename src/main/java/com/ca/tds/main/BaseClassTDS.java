package com.ca.tds.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.ca.tds.dao.TDSDao;
import com.ca.tds.utilityfiles.AppParams;
import com.ca.tds.utilityfiles.CommonUtil;
import com.ca.tds.utilityfiles.InitializeApplicationParams;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class BaseClassTDS {
	
	protected boolean hexEncode = false;
	protected static ExtentReports extent;
	public static ExtentTest parentTest = null;
	protected static Map<String, String> caPropMap = null;
	protected static boolean startchild = false;
	protected String apiresponse = null;
	static AppParams appParams;
	static Map<String, Map<String, Map<String, String>>> testScenarioData = new HashMap<String, Map<String, Map<String, String>>>();

	protected static String strTestCaseName = null;
	protected static int testNumber = 1;
	protected static List<String> threeDSServerTransIDList = new ArrayList<String>();
	protected static Map<String, String> threeDSServerTransIDMap = new HashMap<String, String>();

	protected static int TRANSACTIONLOOPCOUNT = 0;
	public static JSONObject aResMap = new JSONObject();
	public static JSONArray aResArr = new JSONArray();

	@BeforeSuite
	public void beforeSuite(ITestContext testContext) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("==========+++++++++Execution Started at : "+dateFormat.format(date)+"+++++++++==========");
		try {
			initialiseAdminProperties();
			initializeApplicationParams();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CommonUtil cu = new CommonUtil();
		testScenarioData.put("TEST SCENARIOS", cu.getInputDataFromExcel(testContext, "TDSExcelFile",
				"TEST SCENARIOS", "API Name"));
		initialiseReport(testContext);

	}

	@AfterSuite
	public void endSuite() {
		System.out.println("====+++++Execution Completed Kindly verify the Reports for the summary +++++=======");
	}

	public void initializeApplicationParams(){
		InitializeApplicationParams initializeApplicationParams = new InitializeApplicationParams();
		initializeApplicationParams.initializeAppParams();
		appParams = initializeApplicationParams.getAppParams();
	}
	public void initialiseReport(ITestContext testContext) {

		if (extent == null) {

			String dest = "3DSAutomationTestReport.html";
			System.out.println("Report file location : " + dest);
			extent = new ExtentReports(dest, true);
			extent.config().documentTitle(getPropertyValue("ReportTitle", testContext, null));
			APIAutomationCommonPage.extent = extent; // initializing the object inpage
			
		}
	}

	public String getPropertyValue(String parameterName, ITestContext testContext, Map<String, String> testDataMap) {
		if (parameterName != null) {
			if (testDataMap != null && testDataMap.containsKey(parameterName))
				return testDataMap.get(parameterName);
			else {
				System.out.println(parameterName + " : Could not find parameter from test data map");
				String parameterValue = testContext.getCurrentXmlTest().getParameter(parameterName);
				if (parameterValue != null) {
					return parameterValue;
				} else {
					System.out.println(parameterName + " : Could not find parameter name from test level parameter");

					parameterValue = testContext.getSuite().getParameter(parameterName);
					if (parameterValue != null) {
						return parameterValue;
					} else {
						System.out
								.println(parameterName + " : Could not find parameter name from suite level parameter");
						return getAdminPropertyValue(parameterName);
					}
				}
			}
		}
		return null;
	}

	public String getAdminPropertyValue(String parameterName) {
		if (parameterName != null && caPropMap.containsKey(parameterName))
			return caPropMap.get(parameterName);
		else
			System.out.println(parameterName + " : no such property exists");
		return null;
	}

	@AfterMethod
	public void afterMethodProcessing(ITestResult testResult){
		String message = "Test Passed";
		LogStatus status = LogStatus.PASS;
		try {

			if (testResult.getStatus() == ITestResult.FAILURE) {
				
				parentTest.log(LogStatus.ERROR, "response json", apiresponse);

				status = LogStatus.FAIL;
				Throwable objThrow = testResult.getThrowable();
				if (objThrow != null)
					message = testResult.getThrowable().getMessage();
				else
					message = null;

			}
		} catch (Exception e) {
			System.out.println(this.getClass().getSimpleName() + " error while getting screen shots ");
		} finally {
			if (testResult.getStatus() != ITestResult.SKIP) {				
				parentTest.log(status, message);
			}
			System.out.println("ACTIVE THREAD COUNTS : "+java.lang.Thread.activeCount());
			try {
				extent.endTest(parentTest);
			} catch (Exception e) {
				System.out.println("extent>>>" + extent);
				System.out.println("parentTest>>>" + parentTest);
				System.out.println("exception>>>" + e.getMessage());
			}
			extent.flush();
			startchild = false;

		}
	}

	private void initialiseAdminProperties() throws IOException {
		try {
			String workDirAbsPath = System.getProperty("user.dir");
			System.out.println("Work directory absolute path : " + workDirAbsPath);
			String configDir = workDirAbsPath + File.separator + "config";
			System.out.println("Config Location [" + configDir + "]");
			String configFile = configDir + File.separator + "3DSProperties.properties";

			Properties properties = new Properties();
			FileInputStream fis = new FileInputStream(configFile);
			properties.load(fis);
			fis.close();
			caPropMap = new HashMap<>();
			for (String name : properties.stringPropertyNames())
				caPropMap.put(name, properties.getProperty(name));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void threeDSFieldAssert(JSONObject apiResponse, Map<String,String> testCaseData, 
			String fieldName,SoftAssert sa, Object dbData) {
		
		try {
			String fromResponse = null;
			String testDataColumnName = "Expected-"+fieldName;
			String fromTestCaseConfig =  testCaseData.get(testDataColumnName);
			
			if(fromTestCaseConfig.equalsIgnoreCase("NA"))
				return;
			else if(fromTestCaseConfig.equalsIgnoreCase("G")) {
				fromResponse = apiResponse.getString(fieldName);
				parentTest.log(LogStatus.INFO, fieldName+":&nbsp;"+fromResponse+", &emsp; DB data:&nbsp;"+dbData);
				sa.assertNotNull(fromResponse);
				if(dbData == null){
					Assert.fail("DB table columns not updated properly, "+testDataColumnName+":&nbsp;"+fromResponse+", &emsp; DB data:&nbsp;"+dbData);
				}
				sa.assertEquals(fromResponse, dbData);
			}
			else {
				fromResponse = apiResponse.getString(fieldName);
				String[] fromTestCaseConfigArr = fromTestCaseConfig.split(",");
				if(fromTestCaseConfigArr.length > 1){
					sa.assertTrue(Arrays.asList(fromTestCaseConfigArr).contains(fromResponse));
				}else{
					parentTest.log(LogStatus.INFO, testDataColumnName+":&nbsp;"+fromTestCaseConfig+", &emsp;Actual:&nbsp;"+fromResponse);
					sa.assertEquals(fromResponse,fromTestCaseConfig);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void threeDSFieldAssert(JSONObject apiResponse, Map<String,String> testCaseData, 
			String fieldName,SoftAssert sa) {
		
		try {
			String fromResponse = null;
			String testDataColumnName = "Expected-"+fieldName;
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
				String[] fromTestCaseConfigArr = fromTestCaseConfig.split(",");
				if(fromTestCaseConfigArr.length > 1){
					sa.assertTrue(Arrays.asList(fromTestCaseConfigArr).contains(fromResponse));
				}else{
					parentTest.log(LogStatus.INFO, testDataColumnName+":&nbsp;"+fromTestCaseConfig+", &emsp;Actual:&nbsp;"+fromResponse);
					sa.assertEquals(fromResponse,fromTestCaseConfig);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public AppParams getAppParams() {
		return appParams;
	}
	
	public void assertTDSMethodRes(Map<String, String> testCaseData, JSONObject apiResponse, SoftAssert sa,
			String validateDBParams) throws SQLException {
		if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(apiResponse.getString("threeDSServerTransID"), caPropMap);
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Three DS method URL API threeDSServerTransID not found in MTDAUTHLOG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Three DS method URL API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
			}
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa, tdsMethodDBData.get("CALLERTXNREFID"));
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa, tdsMethodDBData.get("MESSAGEVERSION"));
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSMethodURL", sa, tdsMethodDBData.get("ACSURL"));
			
		}else if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "N".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
		
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getErrorLogDataByTDSTransID(apiResponse.getString("threeDSServerTransID"), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Three DS method URL API threeDSServerTransID not found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "Three DS method URL API threeDSServerTransID not found in MTDERRORMSG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Three DS method URL API more than 1 threeDSServerTransID found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "Pre AReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa, tdsMethodDBData.get("ERRORCODE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa, tdsMethodDBData.get("ERRORCOMPONENT"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa, null);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa, tdsMethodDBData.get("ERRORMESSAGETYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa, tdsMethodDBData.get("ERRORDETAIL"));
			
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSMethodURL", sa);
		}
		else{
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa);
			
		}
	}
	
	public void assertAres(Map<String, String> testCaseData, JSONObject apiResponse, SoftAssert sa,
			String validateDBParams) throws SQLException {
		if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
		
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(threeDSServerTransIDMap.get(testCaseData.get("TestCaseID")), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Pre AReq API threeDSServerTransID not found in MTDAUTHLOG table");
				//parentTest.log(LogStatus.FAIL, "Pre AReq API threeDSServerTransID not found in MTDAUTHLOG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Pre AReq API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
				//parentTest.log(LogStatus.FAIL, "Pre AReq API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa, null);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "dsTransID", sa, tdsMethodDBData.get("DSTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "acsTransID", sa, tdsMethodDBData.get("ACSTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "eci", sa, tdsMethodDBData.get("ECI"));
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa, tdsMethodDBData.get("MESSAGEVERSION"));
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa, tdsMethodDBData.get("CALLERTXNREFID"));
			threeDSFieldAssert(apiResponse, testCaseData, "dsReferenceNumber", sa, null);
			threeDSFieldAssert(apiResponse, testCaseData, "acsReferenceNumber", sa, null);
			
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa, tdsMethodDBData.get("TRANSSTATUS"));
			threeDSFieldAssert(apiResponse, testCaseData, "acsChallengeMandated", sa, null);
			threeDSFieldAssert(apiResponse, testCaseData, "acsOperatorID", sa, tdsMethodDBData.get("ECI"));
			threeDSFieldAssert(apiResponse, testCaseData, "acsURL", sa, tdsMethodDBData.get("ACSURL"));
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationType", sa, tdsMethodDBData.get("AUTHENTICATIONTYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa, hexEncode?hexEncode(tdsMethodDBData.get("AUTHENTICATIONVALUE")):tdsMethodDBData.get("AUTHENTICATIONVALUE"));
			threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa, tdsMethodDBData.get("TRANSSTATUSREASON"));
			threeDSFieldAssert(apiResponse, testCaseData, "creq", sa, null);
			threeDSFieldAssert(apiResponse, testCaseData, "cardholderInfo", sa, null);
			
		}else if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "N".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
		
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getErrorLogDataByTDSTransID(threeDSServerTransIDMap.get(testCaseData.get("TestCaseID")), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Pre AReq API threeDSServerTransID not found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "Pre AReq API threeDSServerTransID not found in MTDERRORMSG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Pre AReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "Pre AReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa, tdsMethodDBData.get("ERRORCODE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa, tdsMethodDBData.get("ERRORCOMPONENT"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa, null);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa, tdsMethodDBData.get("ERRORMESSAGETYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa, tdsMethodDBData.get("ERRORDETAIL"));
			
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			threeDSFieldAssert(apiResponse, testCaseData, "messageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "dsTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "acsTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "eci", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "dsReferenceNumber", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "acsReferenceNumber", sa);
		
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "acsChallengeMandated", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "acsOperatorID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "acsURL", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "creq", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "cardholderInfo", sa);
			
		}else{
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa);
		}
	}
	
	public void assertRRes(Map<String, String> testCaseData, JSONObject apiResponse, SoftAssert sa, String validateDBParams) throws SQLException {
		if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
		
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(threeDSServerTransIDMap.get(testCaseData.get("TestCaseID")), caPropMap);
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("RReq API threeDSServerTransID not found in MTDAUTHLOG table");
				//parentTest.log(LogStatus.FAIL, "RReq API threeDSServerTransID not found in MTDAUTHLOG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("RReq API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
				//parentTest.log(LogStatus.FAIL, "RReq API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "dsTransID",sa, tdsMethodDBData.get("DSTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "acsTransID",sa, tdsMethodDBData.get("ACSTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa, tdsMethodDBData.get("MESSAGEVERSION"));
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa, tdsMethodDBData.get("RESULTSSTATUS"));
		
		}else if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "N".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
			
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getErrorLogDataByTDSTransID(threeDSServerTransIDMap.get(testCaseData.get("TestCaseID")), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("RReq API threeDSServerTransID not found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "RReq API threeDSServerTransID not found in MTDERRORMSG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("RReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "RReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa, tdsMethodDBData.get("ERRORCODE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa, tdsMethodDBData.get("ERRORCOMPONENT"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa, tdsMethodDBData.get("ERRORMESSAGETYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa, tdsMethodDBData.get("ERRORDETAIL"));
			
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "dsTransID",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "acsTransID",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "messageVersion", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa);
		}else{
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa);
		}
	}
	
	public void assertVerifyAPIResponse(JSONObject apiResponse, Map<String, String> testCaseData, SoftAssert sa,
			String validateDBParams) throws SQLException {
		if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
		
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsMethodListFromDB = tDSDao.getAuthLogDataByTDSTransID(threeDSServerTransIDMap.get(testCaseData.get("TestCaseID")), caPropMap);
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("Verify API threeDSServerTransID not found in MTDAUTHLOG table");
				//parentTest.log(LogStatus.FAIL, "Verify API threeDSServerTransID not found in MTDAUTHLOG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("Verify API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
				//parentTest.log(LogStatus.FAIL, "Verify API more than 1 threeDSServerTransID found in MTDAUTHLOG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa, tdsMethodDBData.get("CALLERTXNREFID"));
			threeDSFieldAssert(apiResponse, testCaseData, "eci", sa, tdsMethodDBData.get("ECI"));
			threeDSFieldAssert(apiResponse, testCaseData, "authenticationValue", sa, hexEncode?hexEncode(tdsMethodDBData.get("AUTHENTICATIONVALUE")):tdsMethodDBData.get("AUTHENTICATIONVALUE"));
			threeDSFieldAssert(apiResponse, testCaseData, "transStatus", sa, tdsMethodDBData.get("TRANSSTATUSRREQ"));
			if("N".equalsIgnoreCase(apiResponse.getString("transStatus")) || "U".equalsIgnoreCase(apiResponse.getString("transStatus")) || "R".equalsIgnoreCase(apiResponse.getString("transStatus"))){
				threeDSFieldAssert(apiResponse, testCaseData, "transStatusReason", sa, tdsMethodDBData.get("TRANSSTATUSREASON"));
			}
			
			threeDSFieldAssert(apiResponse, testCaseData, "resultsStatus", sa, tdsMethodDBData.get("RESULTSSTATUS"));
			threeDSFieldAssert(apiResponse, testCaseData, "interactionCounter", sa, tdsMethodDBData.get("INTERACTIONCOUNTER"));
		
		}else if(validateDBParams != null && "Y".equalsIgnoreCase(validateDBParams) && "N".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			
			List<HashMap<String,Object>> tdsMethodListFromDB = null;
			
			TDSDao tDSDao = new TDSDao();
			tdsMethodListFromDB = tDSDao.getErrorLogDataByTDSTransID(threeDSServerTransIDMap.get(testCaseData.get("TestCaseID")), caPropMap);
			
			if(tdsMethodListFromDB == null || tdsMethodListFromDB.isEmpty()){
				Assert.fail("RReq API threeDSServerTransID not found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "RReq API threeDSServerTransID not found in MTDERRORMSG table");
			}else if(tdsMethodListFromDB.size() > 1){
				Assert.fail("RReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
				//parentTest.log(LogStatus.FAIL, "RReq API more than 1 threeDSServerTransID found in MTDERRORMSG table");
			}
			
			HashMap<String, Object> tdsMethodDBData = tdsMethodListFromDB.get(0);
			
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa, tdsMethodDBData.get("THREEDSSERVERTRANSID"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorCode", sa, tdsMethodDBData.get("ERRORCODE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorComponent", sa, tdsMethodDBData.get("ERRORCOMPONENT"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDescription", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa, tdsMethodDBData.get("ERRORMESSAGETYPE"));
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa, tdsMethodDBData.get("ERRORDETAIL"));
			
		}else if("P".equalsIgnoreCase(testCaseData.get("Test Case type"))){
			threeDSFieldAssert(apiResponse, testCaseData, "messageType",sa);
			threeDSFieldAssert(apiResponse, testCaseData, "threeDSServerTransID", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "callerTxnRefID", sa);
		    threeDSFieldAssert(apiResponse, testCaseData, "eci", sa);
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
			threeDSFieldAssert(apiResponse, testCaseData, "errorMessageType", sa);
			threeDSFieldAssert(apiResponse, testCaseData, "errorDetail", sa);
		}
	}
	
	private String hexEncode(Object data){
		byte[] bytes = data.toString().getBytes();
		return javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
	}

}
