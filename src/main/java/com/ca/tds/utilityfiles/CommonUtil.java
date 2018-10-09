package com.ca.tds.utilityfiles;

import java.util.Iterator;
import java.util.Map;

import org.testng.ITestContext;

//import ca.com.UtilityFiles.ExcelException;
//import ca.com.UtilityFiles.ReadExcel;

public class CommonUtil {	
	public Object[][] getInputData(ITestContext testContext,
			String strRingBufferFile, String strRingBufferSheet) {
		String fileName = null, sheetName = null, testCaseIdKey = null;
		ReadExcel re = new ReadExcel();
		if (testContext == null)
			System.out.println("test context is null");
		else {
			
			String strfileName = testContext.getCurrentXmlTest().getParameter(
					strRingBufferFile);
			sheetName = testContext.getCurrentXmlTest().getParameter(
					strRingBufferSheet);
			testCaseIdKey = testContext.getCurrentXmlTest().getParameter(
					"TestCaseID");
			String dataProviderPath = testContext.getCurrentXmlTest().getParameter(
					"DataProviderPath");
			if(dataProviderPath!=null){
				fileName=dataProviderPath+strfileName;
			}
			else{
				fileName=strfileName;
			}
			//System.out.println("Excel Path ="+dataProviderPath);
			System.out.println("File Name ="+fileName);
			
		}
		if (testCaseIdKey == null)
			testCaseIdKey = "TestCaseID";

		Map<String, Map<String, String>> mom=null;
		try {
			System.out.println("Current File Name in Common Util->"+fileName);
			System.out.println("Current Sheet Name in Common Util->"+sheetName);
			mom = re.getTestAllData(fileName,
					sheetName, testCaseIdKey);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int sizeOfMap = mom.size();
		Iterator<String> keyIterator = mom.keySet().iterator();

		Object m[][] = new Object[sizeOfMap][];
		for (int i = 0; i < sizeOfMap; i++) {
			m[i] = new Object[1];
			m[i][0] = mom.get(keyIterator.next());
		}
		return m;
	}
	
	public Map<String, Map<String, String>> getInputDataFromExcel(ITestContext testContext,
			String strRingBufferFile, String strRingBufferSheet,String testCaseIDkey) {
		String fileName = null, sheetName = null, testCaseIdKey = null;
		ReadExcel re = new ReadExcel();
		if (testContext == null)
			System.out.println("test context is null");
		else {
			
			String strfileName = testContext.getCurrentXmlTest().getParameter(
					strRingBufferFile);
			sheetName = strRingBufferSheet;
			//testCaseIdKey = testContext.getCurrentXmlTest().getParameter(
				//	"TestCaseID");
			testCaseIdKey=testCaseIDkey;
			String dataProviderPath = testContext.getCurrentXmlTest().getParameter(
					"DataProviderPath");
			if(dataProviderPath!=null){
				fileName=dataProviderPath+strfileName;
			}
			else{
				fileName=strfileName;
			}
			//System.out.println("Excel Path ="+dataProviderPath);
			System.out.println("File Name ="+fileName);
			
		}
		if (testCaseIdKey == null)
			testCaseIdKey = "TestCaseID";

		Map<String, Map<String, String>> mom=null;
		try {
			System.out.println("Current File Name in Common Util->"+fileName);
			System.out.println("Current Sheet Name in Common Util->"+sheetName);
			mom = re.getTestAllData(fileName,
					sheetName, testCaseIdKey);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mom;
		
	}
	
	/**
	 * 
	 * @param testContext
	 * @param strRingBufferFile
	 * @param strRingBufferSheet
	 * @return
	 */
	public Object[][] getInputDataDontResolveSymbols(ITestContext testContext,
			String strRingBufferFile, String strRingBufferSheet) {
		String fileName = null, sheetName = null, testCaseIdKey = null;
		ReadExcel re = new ReadExcel();
		if (testContext == null)
			System.out.println("test context is null");
		else {
			String strfileName = testContext.getCurrentXmlTest().getParameter(
					strRingBufferFile);
			sheetName = testContext.getCurrentXmlTest().getParameter(
					strRingBufferSheet);
			testCaseIdKey = testContext.getCurrentXmlTest().getParameter(
					"TestCaseID");
			String dataProviderPath = testContext.getCurrentXmlTest()
					.getParameter("DataProviderPath");
			if (dataProviderPath != null) {
				fileName = dataProviderPath + strfileName;
			} else {
				fileName = strfileName;
			}
			//System.out.println("Excel Path =" + dataProviderPath);
			System.out.println("File Name =" + fileName);

		}
		if (testCaseIdKey == null)
			testCaseIdKey = "TestCaseID";

		Map<String, Map<String, String>> mom = null;
		try {
			System.out.println("Current File Name in Common Util" + fileName);
			System.out.println("Current Sheet Name in Common Util" + sheetName);
			mom = re.getTestAllDataDontResolveSymbols(fileName, sheetName, testCaseIdKey);
		} catch (ExcelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int sizeOfMap = mom.size();
		Iterator<String> keyIterator = mom.keySet().iterator();

		Object m[][] = new Object[sizeOfMap][];
		for (int i = 0; i < sizeOfMap; i++) {
			m[i] = new Object[1];
			m[i][0] = mom.get(keyIterator.next());
		}
		return m;
	}
}
