package com.ca.tds.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class BaseClassTDS {

	public static ExtentReports extent;
    public static ExtentTest parentTest = null;
    protected static Map<String, String> caPropMap = null;
    // public static ExtentTest childtest = null;
    protected static boolean startchild = false;
    private static boolean ifProperlyInitialised = false;
    public String apiresponse = null;

    public static String strTestCaseName = null;
    public static int testNumber = 1;
    long testStartTime = 0;
    public static List<String> threeDSServerTransIDList=new ArrayList<String>();
    public static int INSTACTIONLOOPCOUNT=0;
	
    @BeforeSuite
    public void beforeSuite(ITestContext testContext) {
        System.out.println("Before suite in TestSuiteBaseEx");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("BeforeSuite Current Date and Time =" + dateFormat.format(date));
        try {
            initialiseAdminProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // initialiseMerchantProperties();
        initialiseReport(testContext);
        /*
         * launchAdminBrowser(testContext); launchMerchantBrowser(testContext); launchMasterAdminBrowser(testContext);
         */
        System.out.println("in before suite  initialiseReport ");

    }
    
//	@BeforeClass
//	public void setup(){
//		
//		System.out.println("Before Class SetUp");
//		
//	}
	
	@AfterSuite
	public void endSuite() {
		System.out.println("====+++++Execution Completed Kindly verify the Reports for the summary +++++=======");
	}
	
	private void initialiseReport(ITestContext testContext) {
        if (extent == null) {
            String dest = System.getProperty("user.dir") +"\\TestReports\\3DSAutomationTestReport.html";
            System.out.println("Report file destination is " + dest);
            extent = new ExtentReports(dest, true);
            System.out.println("extent is" + extent);
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
                        System.out.println(parameterName + " : Could not find parameter name from suite level parameter");
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
    public void afterMethodProcessing(ITestResult testResult) throws IOException, SQLException {
        String message = "Test Passed";
        LogStatus status = LogStatus.PASS;
        try {
            //System.out.println("testResult.getStatus()" + testResult.getStatus());
            if (testResult.getStatus() == ITestResult.FAILURE) {
                System.out.println("calling take screen shot in CaptureScreen method in failure case");
                // if(merchantDriver!=null)
                //takeScreenShot(extent, parentTest, strTestCaseName, "Error Captured", LogStatus.FAIL);
                parentTest.log(LogStatus.ERROR, "response json", apiresponse);

                // takeScreenShot(merchantDriver, extent, parentTest,strTestCaseName, "Error Captured", LogStatus.FAIL);
                // if(adminDriver!=null)
                // takeScreenShot(extent, parentTest,strTestCaseName, "Error Captured", LogStatus.FAIL);
                // takeScreenShot(adminDriver, extent, parentTest,strTestCaseName, "Error Captured", LogStatus.FAIL);
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
//        	Markup msg;
//        	if(status == LogStatus.PASS) {
//        		msg= MarkupHelper.createLabel(message.toString(), ExtentColor.GREEN);
//        	}
//        	else if(status == LogStatus.FAIL) {
//        		msg= MarkupHelper.createLabel(message, ExtentColor.RED);
//        	}
        	
            parentTest.log(status, message);
            // parentTest.appendChild(childtest);
            System.out.println("adding parent test to report");
            try {
                extent.endTest(parentTest);
            } catch (Exception e) {
                System.out.println("rama extent>>>" + extent);
                System.out.println("rama parentTest>>>" + parentTest);
                System.out.println("rama exception>>>" + e.getMessage());
            }
            extent.flush();
            // childtest = null;
            startchild = false;

        }
    }
    
    private void initialiseAdminProperties() throws IOException {
        try {
            String workDirAbsPath = System.getProperty("user.dir");
            System.out.println("workDirAbsPath" + workDirAbsPath);
            //String testSuitesDir = workDirAbsPath + File.separator + "test-suites";
            String confingDir = workDirAbsPath + File.separator + "config";
            System.out.println("Config LOcation [" + confingDir + "]");
            String configFile = confingDir + File.separator + "3DSProperties.properties";
            //String deviceConfig = workDirAbsPath + File.separator + "test-suites" + File.separator +"ca-acs-client.properties";
            		
            // Object configFile = inputFilesDir + File.separator +
            // "ca-acs-client.properties";
            //System.out.println("Using configuration properties file from " + configFile);
            //System.out.println("input files path" + testSuitesDir);
            //System.out.println("deviceConfig files path" + deviceConfig);

            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream(configFile);
            properties.load(fis);
            fis.close();
            caPropMap = new HashMap<>();
            for (String name : properties.stringPropertyNames())
                caPropMap.put(name, properties.getProperty(name));

            ifProperlyInitialised = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ifProperlyInitialised = false;
        } catch (IOException e) {
            e.printStackTrace();
            ifProperlyInitialised = false;
        }
    }
}
