package com.ca.tds.utilityfiles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ReadExcel {
	private FileInputStream file = null;
	private XSSFWorkbook workbook = null;
	public final static String testCaseIdKey = "TestCaseID";

	public boolean isSheetExist(String sheetName) {

		if (sheetName == null) {
			return true;
		}
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1) {
			index = workbook.getSheetIndex(sheetName.toUpperCase());
			if (index == -1)
				return false;
			else
				return true;
		} else
			return true;
	}

	/**
	 * This method is used to get Map based on user TestCaseID.
	 * @param filename
	 * @param sheetName
	 * @param testCaseId
	 * @return Keys and values in form of Map(Keys will be headers) Values will
	 *         be return based on testcaseId
	 * @throws ExcelException
	 */
	public Map<String, String> getTestData(String filename, String sheetName,
			String testCaseId) throws ExcelException {

		int sheetIndex = -1;

		if (filename == null) {
			throw new ExcelException("file name cannot be null");
		}

		if (sheetName == null) {
			sheetIndex = 0;
		}

		try {
			// filename = filename.trim();
			file = new FileInputStream(filename);
			try {
				workbook = new XSSFWorkbook(file);
			} catch (IOException e) {
				throw new ExcelException("io exception:" + e.getMessage());
			}
		} catch (FileNotFoundException e) {
			throw new ExcelException("file not found exception:" + filename);
		}

		if (sheetName != null) {
			if (!isSheetExist(sheetName)) {
				throw new ExcelException("sheet name:" + sheetName
						+ " does not exists");
			} else {
				sheetIndex = workbook.getSheetIndex(sheetName);
			}
		}

		XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = currentSheet.iterator();

		// extracting header
		Row firstRow = rowIterator.next();
		List<String> keyList = new ArrayList<String>();
		int indexOfTheKey = extractArrayListAndReturnIndexOfTheKey(firstRow,
				testCaseIdKey, keyList);
		if (indexOfTheKey < 0) {
			throw new ExcelException(
					"testCaseid coulumn not found in the excel");
		}

		// Find the row where testCaseId=value is found
		Row rowWhereValueIsFound = getRowOfTheTestCaseId(rowIterator, keyList,
				indexOfTheKey, testCaseId);
		if (rowWhereValueIsFound == null)
			throw new ExcelException("test case id value:" + testCaseId
					+ " is not found in the excel under the column testCaseId");

		Map<String, String> testCaseMap = getMapFromRow(rowWhereValueIsFound,
				keyList);
		ResolveSymbolsData rs = new ResolveSymbolsData();
		Map<String, String> outputMap = null;;
		try {
			outputMap = rs.getSymbolsToReplaceWithActualData(testCaseMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMap;
	}

	private Map<String, String> getMapFromRow(Row rowWhereValueIsFound,
			List<String> keyList) {

		Map<String, String> valueMap = new LinkedHashMap<String, String>();
		if (rowWhereValueIsFound == null)
			return null;
		if (keyList == null)
			return null;

		for (int i = 0; i < keyList.size(); i++) {
			String cellValue = null;

			if (rowWhereValueIsFound.getCell(i) == null) {
				cellValue = null;
			} else {
				cellValue = getCellDataAsString(rowWhereValueIsFound.getCell(i));
			}
			valueMap.put(keyList.get(i), cellValue);

		}

		// commented bcz empty cell is not accepting from cell // mukesh
		/*
		 * Iterator<Cell> cellIterator = rowWhereValueIsFound.cellIterator();
		 * int index = 0;
		 * 
		 * while (cellIterator.hasNext()) { Cell currentCell =
		 * cellIterator.next(); String cellValue = null; if (currentCell !=
		 * null) cellValue = getCellDataAsString(currentCell);
		 * valueMap.put(keyList.get(index), cellValue); index++;
		 * 
		 * }
		 */

		if (valueMap.containsKey("")) {
			valueMap.remove("");
			}
		return valueMap;
	}

	private Map<String, String> getKeyValue(List<String> keyList,
			Row rowWhereValueIsFound) {
		Map<String, String> valueMap = new LinkedHashMap<String, String>();

		if (rowWhereValueIsFound == null)
			return null;
		if (keyList == null)
			return null;

		for (int i = 0; i < keyList.size(); i++) {
			String cellValue = null;

			if (rowWhereValueIsFound.getCell(i) == null) {
				cellValue = null;
			} else {
				cellValue = getCellDataAsString(rowWhereValueIsFound.getCell(i));
			}
			valueMap.put(keyList.get(i), cellValue);

		}
		/*
		Iterator<Cell> cellIterator = rowWhereValueIsFound.cellIterator();
		int index = 0;
		while (cellIterator.hasNext()) {
			Cell currentCell = cellIterator.next();
			String cellValue = null;
			if (currentCell != null)
				cellValue = getCellDataAsString(currentCell);

			valueMap.put(keyList.get(index), cellValue);
			index++;
		}*/

		if (valueMap.containsKey("")) {
			valueMap.remove("");
			}
		return valueMap;
	}

	private Row getRowOfTheTestCaseId(Iterator<Row> rowIterator,
			List<String> keyList, int indexOfTheKey, String testCaseId) {

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Cell cellTxnId = row.getCell(indexOfTheKey);
			if (cellTxnId != null) {
				String cellValue = getCellDataAsString(cellTxnId);
				if (cellValue.equalsIgnoreCase(testCaseId))
					return row;
			}
		}
		return null;
	}

	private Row getRowOfTheTestCaseIdM(XSSFSheet currentSheet,
			List<String> keyList, int indexOfTheKey, String testCaseId) {

		Iterator<Row> rowIterator = currentSheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Cell cellTxnId = row.getCell(indexOfTheKey);
			if (cellTxnId != null) {
				String cellValue = getCellDataAsString(cellTxnId);
				if (cellValue.equalsIgnoreCase(testCaseId))
					return row;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param rowIterator
	 *            should ideally point to 2nd row. As first row contains the
	 *            header only.
	 * @param columnNumberOfTheTestCaseId
	 *            the column number of the testcaseId
	 * @return all the keys will be returned as List.
	 */
	private List<String> getAllTestCaseIds(Iterator<Row> rowIterator,
			int columnNumberOfTheTestCaseId) {

		List<String> keysOfTestId = new ArrayList<String>();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Cell cellTxnId = row.getCell(columnNumberOfTheTestCaseId);
			if (cellTxnId != null) {
				String cellValue = getCellDataAsString(cellTxnId);
				keysOfTestId.add(cellValue);
			}
		}
		return keysOfTestId;
	}

	private int extractArrayListAndReturnIndexOfTheKey(Row firstRow,
			String TestCaseID, List<String> keyList)

	{
		int testIDindex = -1;
		int colIndex = -1;
		Iterator<Cell> cellIterator = firstRow.cellIterator();
		while (cellIterator.hasNext()) {
			colIndex++;
			Cell cell = cellIterator.next();
			String cellValue = getCellDataAsString(cell);
			keyList.add(cellValue);
			if (cellValue.equals(TestCaseID)) {
				testIDindex = colIndex;
			}
		}
		return testIDindex;
	}

	private String getCellDataAsString(Cell cell) {
		String cellValueAsString = null;
		//System.out.println(" Cell Type "+cell.getCellType());
		
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			// long integer = (long) cell.getNumericCellValue();

			double value = cell.getNumericCellValue();
			//System.out.println(" cell value before Conversion " + value);
			long longValue = (long) value;
			if (longValue == value)
				cellValueAsString = longValue + "";
			else	
				cellValueAsString = value + "";
			break;

		case Cell.CELL_TYPE_STRING:
			cellValueAsString = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			value = (int) cell.getNumericCellValue();
			cellValueAsString = value + "";
			break;

		case Cell.CELL_TYPE_BLANK:
			cellValueAsString = "";
			break;
			
		case Cell.CELL_TYPE_BOOLEAN:
			boolean booleanValue = cell.getBooleanCellValue();
			cellValueAsString = booleanValue+"";
			break;

		}
		//System.out.println(" FInal Value is "+cellValueAsString);
		return cellValueAsString;

	}
	
	

	/**
	 * This method is used to get Map of Map for get entire excelsheet data.
	 * @param filename
	 *            name of the excel file name
	 * @param sheetName
	 *            sheet name
	 * @param testCaseIdKeyName
	 *            testCaseIdKeyName example it could be "testCaseId" or
	 *            "testCaseNo" or "Id" what ever the name you have given in the
	 *            first row for test case id. If you pass null then default name
	 *            "TestCaseId" is assumed.
	 * @return All of excel sheet data in form of Map of Map where key will be
	 *         testcaseIds and value will be map of input data for that testcase
	 *         id
	 * @throws ExcelException
	 */
	public Map<String, Map<String, String>> getTestAllData(String filename,
			String sheetName, String testCaseIdKeyName) throws ExcelException {

		if (testCaseIdKeyName == null || testCaseIdKeyName.length() < 1)
			testCaseIdKeyName = testCaseIdKey;

		int sheetIndex = -1;

		if (filename == null) {
			throw new ExcelException("file name cannot be null");
		}

		if (sheetName == null) {
			sheetIndex = 0;
		}

		try {
			file = new FileInputStream(filename);
			try {
				workbook = new XSSFWorkbook(file);
			} catch (IOException e) {
				throw new ExcelException("io exception:" + e.getMessage());
			}
		} catch (FileNotFoundException e) {
			throw new ExcelException("file not found exception:"
					+ e.getMessage());
		}

		if (sheetName != null) {
			if (!isSheetExist(sheetName)) {
				throw new ExcelException("sheet name:" + sheetName
						+ " does not exists");
			} else {
				sheetIndex = workbook.getSheetIndex(sheetName);
			}
		}

		XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = currentSheet.iterator();

		// extracting header
		Row firstRow = rowIterator.next();
		List<String> keyList = new ArrayList<String>();
		int indexOfTheKey = extractArrayListAndReturnIndexOfTheKey(firstRow,
				testCaseIdKeyName, keyList);
		if (indexOfTheKey < 0) {
			throw new ExcelException(
					"testCaseid coulumn not found in the excel");
		}

		List<String> testIdKeys = getAllTestCaseIds(rowIterator, indexOfTheKey);

		Map<String, Map<String, String>> mapOfmap = new LinkedHashMap<String, Map<String, String>>();

		Iterator<String> itr = testIdKeys.iterator();
		while (itr.hasNext()) {
			String testkeys = itr.next();
			Row rowWhereValueIsFound = getRowOfTheTestCaseIdM(currentSheet,
					keyList, indexOfTheKey, testkeys);

			if (rowWhereValueIsFound == null)
				throw new ExcelException(
						"test case id value:"
								+ testkeys
								+ " is not found in the excel under the column testCaseId");

			
			ResolveSymbolsData rs = new ResolveSymbolsData();
			Map<String, String> outputMap = new LinkedHashMap<String, String>();
			try {
				Map<String, String> keyValue = getKeyValue(keyList, rowWhereValueIsFound);
				outputMap = rs.getSymbolsToReplaceWithActualData(keyValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Set<String> mapKeyset =  outputMap.keySet();
			boolean keyFound = false;
			String mapKey = null;
			for(String key:mapKeyset){
				
				if(key.equalsIgnoreCase("SanityFlag")){
					keyFound = true;
					mapKey = key;
					break;
				}
				
			}
			if(keyFound){
				if(outputMap.get(mapKey)!=null&&outputMap.get(mapKey).equalsIgnoreCase("N")){
			
					continue;
				}
			}
			mapOfmap.put(testkeys, outputMap);

		}
		return mapOfmap;
	}
	
	public Map<String, Map<String, String>> getTestAllDataDontResolveSymbols(String filename,
			String sheetName, String testCaseIdKeyName) throws ExcelException {

		if (testCaseIdKeyName == null || testCaseIdKeyName.length() < 1)
			testCaseIdKeyName = testCaseIdKey;

		int sheetIndex = -1;

		if (filename == null) {
			throw new ExcelException("file name cannot be null");
		}

		if (sheetName == null) {
			sheetIndex = 0;
		}

		try {
			file = new FileInputStream(filename);
			try {
				workbook = new XSSFWorkbook(file);
			} catch (IOException e) {
				throw new ExcelException("io exception:" + e.getMessage());
			}
		} catch (FileNotFoundException e) {
			throw new ExcelException("file not found exception:"
					+ e.getMessage());
		}

		if (sheetName != null) {
			if (!isSheetExist(sheetName)) {
				throw new ExcelException("sheet name:" + sheetName
						+ " does not exists");
			} else {
				sheetIndex = workbook.getSheetIndex(sheetName);
			}
		}

		XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = currentSheet.iterator();

		// extracting header
		Row firstRow = rowIterator.next();
		List<String> keyList = new ArrayList<String>();
		int indexOfTheKey = extractArrayListAndReturnIndexOfTheKey(firstRow,
				testCaseIdKeyName, keyList);
		if (indexOfTheKey < 0) {
			throw new ExcelException(
					"testCaseid coulumn not found in the excel");
		}

		List<String> testIdKeys = getAllTestCaseIds(rowIterator, indexOfTheKey);

		Map<String, Map<String, String>> mapOfmap = new LinkedHashMap<String, Map<String, String>>();

		Iterator<String> itr = testIdKeys.iterator();
		while (itr.hasNext()) {
			String testkeys = itr.next();
			Row rowWhereValueIsFound = getRowOfTheTestCaseIdM(currentSheet,
					keyList, indexOfTheKey, testkeys);

			if (rowWhereValueIsFound == null)
				throw new ExcelException(
						"test case id value:"
								+ testkeys
								+ " is not found in the excel under the column testCaseId");

			
			ResolveSymbolsData rs = new ResolveSymbolsData();
			Map<String, String> outputMap = new LinkedHashMap<String, String>();
			try {
				Map<String, String> keyValue = getKeyValue(keyList, rowWhereValueIsFound);
				outputMap = keyValue;
				//outputMap = rs.getSymbolsToReplaceWithActualData(keyValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mapOfmap.put(testkeys, outputMap);

		}
		return mapOfmap;
	}
	
}
