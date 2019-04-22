package com.ca.tds.main;

import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.ca.tds.utilityfiles.AppParams;
import com.ca.tds.utilityfiles.CommonUtil;

public class TDSFactory {
	
	static ITestContext testContxt;
	static String enableEncryption;
	
	
	@Factory(dataProvider="dp")
    public Object[] createInstances(Map<String, String> testCaseData) {
        return new Object[] {new TDSFlowTest(testContxt, testCaseData.get("TestCaseID"), enableEncryption)};
    }
     
    @DataProvider(name="dp", parallel = true)
    public static Object[][] dataProvider(ITestContext testContext) {
    	try {
    		AppParams.initialiseAdminProperties();
    		testContxt = testContext;
    		enableEncryption = AppParams.getEnableDecryption();
    		if(enableEncryption != null && "true".equalsIgnoreCase(enableEncryption))
    			return new CommonUtil().getInputData(testContext, "TDSExcelFileE", "ExcelSheetVerify");
    		else
    			return new CommonUtil().getInputData(testContext, "TDSExcelFileC", "ExcelSheetVerify");
		} catch (Exception e) {
			System.out.println("Error while reading data from excel sheet");
		}
		return null;
    }
		
}
