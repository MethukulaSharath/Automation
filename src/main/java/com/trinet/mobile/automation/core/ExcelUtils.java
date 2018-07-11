package com.trinet.mobile.automation.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.xml.XmlTest;

/**
 * ExcelUtils has data provider methods that read an excel sheet in the given
 * excel file and return an array of LinkedHashMap objects. There are primarily
 * two methods that act as data providers. One takes ITestContext as input and
 * reads the excel file path and sheet name from ITestContext. These parameters
 * are configured in the testng xml file for a given test. Another method takes
 * the excel file path and sheet name and returns the data. This should be used
 * from each test class and invoked directly from the corresponding data
 * providers.
 * 
 * @author sthoutam
 *
 */
public class ExcelUtils {

	protected static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	protected static Object[][] EMPTY_DATA = new Object[0][0];

	/**
	 * Data provider that reads the excel file and sheet provided as test
	 * parameters.
	 * 
	 * @param ITestContext
	 *            context.
	 * @return Object[][] two dimensional array, each element is an instance of
	 *         LinkedHashMap.
	 */
	public static Object[][] getData(ITestContext context) {

		XmlTest xmlTest = context.getCurrentXmlTest();
		String path = xmlTest.getParameter("readexcelPath");
		String sheet = xmlTest.getParameter("DataSheetName");
		ArrayList<LinkedHashMap<String, String>> testContextList = ExcelUtils.getTestContext(path, sheet);
		int size = testContextList.size();

		Object[][] testContext = new Object[size][];

		for (int i = 0; i < size; i++) {
			testContext[i] = new Object[1];
			testContext[i][0] = testContextList.get(i);
		}

		return testContext;
	}

	/**
	 * Data provider that reads the excel file and sheet provided as test
	 * parameters.
	 * 
	 * @param String
	 *            filePath - Excel file path
	 * @param String
	 *            sheetName - Excel Sheet Name that has data for data provider
	 * @return Object[][] two dimensional array, each element is an instance of
	 *         LinkedHashMap.
	 */
	public static Object[][] getDataFromSheet(String filePath, String sheetName) {

		if (StringUtils.isEmpty(filePath) || StringUtils.isEmpty(sheetName))
			return EMPTY_DATA;

		ArrayList<LinkedHashMap<String, String>> testContextList = ExcelUtils.getTestContext(filePath, sheetName);
		int size = testContextList.size();

		Object[][] testContext = new Object[size][];

		for (int i = 0; i < size; i++) {
			testContext[i] = new Object[1];
			testContext[i][0] = testContextList.get(i);
		}

		return testContext;
	}

	protected static ArrayList<LinkedHashMap<String, String>> getTestContext(String path, String sheetName) {
		ArrayList<LinkedHashMap<String, String>> testContextList = new ArrayList<LinkedHashMap<String, String>>();

		if (StringUtils.isBlank(path) || StringUtils.isBlank(sheetName)) {
			logger.error("Invalid datafile path {} or sheet {} is provided", path, sheetName);
			return testContextList;
		}

		logger.debug("Reading datafile {} and sheet {} ", path, sheetName);

		File f = new File(path);
		if (!f.exists() || f.isDirectory()) {
			logger.error("Invalid datafile path {} provided", path);
			return testContextList;
		}

		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(path));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Caught exception while reading input file {} ", path);
			logger.error("Exception {} ", e.getMessage());
			return testContextList;
		}

		XSSFSheet sheet = workbook.getSheet(sheetName);
		XSSFRow firstRow = sheet.getRow(0);

		int rows = sheet.getLastRowNum() + 1;
		int columns = firstRow.getLastCellNum();
		String[] headers = new String[columns];
		LinkedHashMap<String, String> testContext;
		String cellValue;
		XSSFRow xssfRow;

		// Read headers.
		for (int column = 0; column < columns; column++) {
			headers[column] = ExcelUtils.getValue(firstRow.getCell(column));
		}

		// Read from row 1 for data.
		for (int row = 1; row < rows; row++) {
			xssfRow = sheet.getRow(row);
			if (!"on".equalsIgnoreCase(ExcelUtils.getValue(xssfRow.getCell(0))))
				continue;

			testContext = new LinkedHashMap<String, String>();
			for (int column = 1; column < columns; column++) {
				cellValue = ExcelUtils.getValue(xssfRow.getCell(column));
				testContext.put(headers[column], cellValue);
			}

			testContextList.add(testContext);
		}

		return testContextList;
	}

	/**
	 * Return the value of the given cell from Excel sheet.
	 * 
	 * @param XSSFCell
	 *            cell.
	 * 
	 * @return String value from the given cell.
	 */
	private static String getValue(XSSFCell cell) {
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				return String.valueOf(cell.getNumericCellValue()).trim();
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue().trim();
			}
		}

		return StringUtils.EMPTY;
	}
}
