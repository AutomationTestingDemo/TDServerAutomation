package com.ca.tds.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import com.ca.tds.utilityfiles.AppParams;
import com.ca.tds.utilityfiles.InitializeApplicationParams;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class BaseClassTDS {

	protected static ExtentReports extent;
	public static ExtentTest parentTest = null;
	protected static Map<String, String> caPropMap = null;
	protected static boolean startchild = false;
	private String apiresponse = null;
	static AppParams appParams;

	protected static String strTestCaseName = null;
	protected static int testNumber = 1;
	protected static List<String> threeDSServerTransIDList = new ArrayList<String>();

	protected static int TRANSACTIONLOOPCOUNT = 0;
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

			String dest = System.getProperty("user.dir") + "\\TestReports\\3DSAutomationTestReport.html";
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

			parentTest.log(status, message);
			try {
				extent.endTest(parentTest);
			} catch (Exception e) {
				System.out.println("rama extent>>>" + extent);
				System.out.println("rama parentTest>>>" + parentTest);
				System.out.println("rama exception>>>" + e.getMessage());
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
}
