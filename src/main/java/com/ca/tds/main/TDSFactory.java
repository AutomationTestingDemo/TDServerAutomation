package com.ca.tds.main;

import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.ca.tds.utilityfiles.CommonUtil;

public class TDSFactory {
	
	static ITestContext testContxt;
	
	@Factory(dataProvider="dp")
    public Object[] createInstances(Map<String, String> testCaseData) {
        return new Object[] {new TDSFlowTest(testContxt, testCaseData.get("TestCaseID"))};
    }
     
    @DataProvider(name="dp", parallel = true)
    public static Object[][] dataProvider(ITestContext testContext) {
    	try {
    		testContxt = testContext;
			return new CommonUtil().getInputData(testContext, "TDSExcelFile", "ExcelSheetVerify");
		} catch (Exception e) {
			System.out.println("Error while reading data from excel sheet");
		}
		return null;
    }
		
}