/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.common;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Testing the ExcelUtil class.
 */
public class ExcelUtilTest extends BaseModuleContextSensitiveTest {

	protected Log log = LogFactory.getLog(this.getClass());

	@Test
	public void shouldGetCellContentsAsString() throws Exception {
		Workbook wb = loadWorkbookFromResource("org/openmrs/module/reporting/common/ExcelUtilTest.xls");
		Sheet sheet = wb.getSheet("Testing");
		testCellContentsToTheRightOf(sheet, "String", "This is a String");
		testCellContentsToTheRightOf(sheet, "Bold String", "This is a bold String");
		testCellContentsToTheRightOf(sheet, "Integer", "100");
		testCellContentsToTheRightOf(sheet, "Number", "100.5");
		testCellContentsToTheRightOf(sheet, "Boolean", "true");
		testCellContentsToTheRightOf(sheet, "Formula", "B5*2");
		testCellContentsToTheRightOf(sheet, "Date", "October 31, 2011");
		testCellContentsToTheRightOf(sheet, "Time", "11:32:00 AM");
	}

	@Test
	public void shouldSetCellContents() throws Exception {
		Workbook wb = loadWorkbookFromResource("org/openmrs/module/reporting/common/ExcelUtilTest.xls");
		Sheet sheet = wb.getSheet("Testing");
		testSettingCellContents(sheet, "String", "New String", Cell.CELL_TYPE_STRING, "New String");
		testSettingCellContents(sheet, "String", 100, Cell.CELL_TYPE_NUMERIC, "100");
		testSettingCellContents(sheet, "Integer", 20.2, Cell.CELL_TYPE_NUMERIC, "20.2");
		testSettingCellContents(sheet, "Boolean", Boolean.FALSE, Cell.CELL_TYPE_BOOLEAN, "false");
		testSettingCellContents(sheet, "Date", DateUtil.getDateTime(1999,3,17), Cell.CELL_TYPE_NUMERIC, "March 17, 1999");
		testSettingCellContents(sheet, "String", DateUtil.getDateTime(1999,3,17), Cell.CELL_TYPE_NUMERIC, "17/Mar/1999");
		testSettingCellContents(sheet, "Formula", "B5*3", Cell.CELL_TYPE_FORMULA, "B5*3");
	}

	@Test
	public void shouldAddStyle() throws Exception {
		Workbook wb = loadWorkbookFromResource("org/openmrs/module/reporting/common/ExcelUtilTest.xls");
		Sheet sheet = wb.getSheet("Testing");

		// Test Fonts
		Cell cell = getCellToTheRightOf(sheet, "String");
		Assert.assertEquals("This is a String", ExcelUtil.getCellContentsAsString(cell));

		Assert.assertEquals(Font.BOLDWEIGHT_NORMAL, ExcelUtil.getFont(cell).getBoldweight());
		ExcelUtil.addStyle(cell, "bold");
		Assert.assertEquals(Font.BOLDWEIGHT_BOLD, ExcelUtil.getFont(cell).getBoldweight());

		Assert.assertFalse(ExcelUtil.getFont(cell).getItalic());
		Assert.assertEquals(Font.U_NONE, ExcelUtil.getFont(cell).getUnderline());
		ExcelUtil.addStyle(cell, "italic,underline");
		Assert.assertTrue(ExcelUtil.getFont(cell).getItalic());
		Assert.assertEquals(Font.U_SINGLE, ExcelUtil.getFont(cell).getUnderline());

		int fontSize = ExcelUtil.getFont(cell).getFontHeightInPoints() + 1;
		ExcelUtil.addStyle(cell, "size="+fontSize);
		Assert.assertEquals((short)fontSize, ExcelUtil.getFont(cell).getFontHeightInPoints());

		// Test other styles
		Assert.assertFalse(cell.getCellStyle().getWrapText());
		Assert.assertEquals(CellStyle.ALIGN_GENERAL, cell.getCellStyle().getAlignment());
		Assert.assertEquals(CellStyle.BORDER_NONE, cell.getCellStyle().getBorderBottom());
		ExcelUtil.addStyle(cell, "wraptext,align=center,border=bottom");
		Assert.assertTrue(cell.getCellStyle().getWrapText());
		Assert.assertEquals(CellStyle.ALIGN_CENTER, cell.getCellStyle().getAlignment());
		Assert.assertEquals(CellStyle.BORDER_THIN, cell.getCellStyle().getBorderBottom());

		// Test Date
		Date date = DateUtil.getDateTime(2013, 10, 31);
		cell.setCellValue(date);
		ExcelUtil.addStyle(cell, "date");
		Assert.assertEquals(Cell.CELL_TYPE_NUMERIC, cell.getCellType());
		Assert.assertTrue(ExcelUtil.isCellDateFormatted(cell));
		Assert.assertEquals("31/Oct/2013", ExcelUtil.getCellContentsAsString(cell));
	}

	@Test
	public void shouldFormatSheetTitle() throws Exception {

		Assert.assertEquals("TestSheet", ExcelUtil.formatSheetTitle("TestSheet"));
		Assert.assertEquals("Sheet", ExcelUtil.formatSheetTitle(null));
		Assert.assertEquals("IllegalCharacters", ExcelUtil.formatSheetTitle("Illegal [Characters]"));
		Assert.assertEquals("Thisisatitlewithover30characte", ExcelUtil.formatSheetTitle("This is a title with over 30 characters"));

		Set<String> usedTitles = new HashSet<String>();
		String startingTitle = "Thisisonetitlewith30characters";

		String title1 = ExcelUtil.formatSheetTitle(startingTitle, usedTitles);
		Assert.assertEquals(startingTitle, title1);
		usedTitles.add(title1);

		String title2 = ExcelUtil.formatSheetTitle(startingTitle, usedTitles);
		Assert.assertEquals("Thisisonetitlewith30charact-1", title2);
		usedTitles.add(title2);

		String title3 = ExcelUtil.formatSheetTitle(startingTitle, usedTitles);
		Assert.assertEquals("Thisisonetitlewith30charact-2", title3);
		usedTitles.add(title3);
	}

	protected Workbook loadWorkbookFromResource(String resource) {
		InputStream is = null;
		try {
			is = OpenmrsClassLoader.getInstance().getResourceAsStream(resource);
			POIFSFileSystem fs = new POIFSFileSystem(is);
			return WorkbookFactory.create(fs);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load excel workbook from resource", e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}

	protected void testCellContentsToTheRightOf(Sheet sheet, String contentsBefore, String contentsToTest) {
		Cell c = getCellToTheRightOf(sheet, contentsBefore);
		String contentsToCheck = ExcelUtil.getCellContentsAsString(c);
		Assert.assertEquals(contentsToTest, contentsToCheck);
	}

	protected void testSettingCellContents(Sheet sheet, String contentsBefore, Object valueToSet, int expectedCellType, String expectedContents) {
		Cell cell = getCellToTheRightOf(sheet, contentsBefore);
		ExcelUtil.setCellContents(cell, valueToSet);
		Assert.assertEquals(expectedCellType, cell.getCellType());
		Assert.assertEquals(expectedContents, ExcelUtil.getCellContentsAsString(cell));
	}

	protected Cell getCellToTheRightOf(Sheet sheet, String contents) {
		for (Iterator<Row> ri = sheet.rowIterator(); ri.hasNext();) {
			Row row = ri.next();
			for (Iterator<Cell> ci = row.cellIterator(); ci.hasNext();) {
				Cell cell = ci.next();
				if (contents.equals(ExcelUtil.getCellContentsAsString(cell))) {
					return ci.next();
				}
			}
		}
		return null;
	}
}