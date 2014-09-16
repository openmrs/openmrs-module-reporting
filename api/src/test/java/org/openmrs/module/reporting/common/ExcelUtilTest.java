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

import org.junit.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing the ExcelUtil class.
 */
public class ExcelUtilTest extends BaseModuleContextSensitiveTest {

	protected Log log = LogFactory.getLog(this.getClass());

	@Test
	public void shouldGetCellContents() throws Exception {
		Workbook wb = ExcelUtil.loadWorkbookFromResource("org/openmrs/module/reporting/common/ExcelUtilTest.xls");
		Sheet sheet = wb.getSheet("Testing");
		testCellContentsToTheRightOf(sheet, "String", "This is a String");
		testCellContentsToTheRightOf(sheet, "Bold String", "This is a bold String");
		testCellContentsToTheRightOf(sheet, "Integer", 100);
		testCellContentsToTheRightOf(sheet, "Number", 100.5);
		testCellContentsToTheRightOf(sheet, "Boolean", true);
		testCellContentsToTheRightOf(sheet, "Formula", "B5*2");
		testCellContentsToTheRightOf(sheet, "Date", DateUtil.getDateTime(2011,10,31));
		testCellContentsToTheRightOf(sheet, "Time", DateUtil.getDateTime(2011,10,31,11,32,0,0));
	}

	@Test
	public void shouldSetCellContents() throws Exception {
		Workbook wb = ExcelUtil.loadWorkbookFromResource("org/openmrs/module/reporting/common/ExcelUtilTest.xls");
		Sheet sheet = wb.getSheet("Testing");
		Date testDate = DateUtil.getDateTime(1999,3,17);
		int testDateExcel = (int)ExcelUtil.getDateAsNumber(testDate);
		testSettingCellContents(sheet, "String", "New String", Cell.CELL_TYPE_STRING, "New String");
		testSettingCellContents(sheet, "String", 100, Cell.CELL_TYPE_NUMERIC, 100);
		testSettingCellContents(sheet, "Integer", 20.2, Cell.CELL_TYPE_NUMERIC, 20.2);
		testSettingCellContents(sheet, "Boolean", Boolean.FALSE, Cell.CELL_TYPE_BOOLEAN, false);
		testSettingCellContents(sheet, "Date", testDate, Cell.CELL_TYPE_NUMERIC, testDate);
		testSettingCellContents(sheet, "String", testDate, Cell.CELL_TYPE_NUMERIC, testDateExcel);
		testSettingCellContents(sheet, "Formula", "B5*3", Cell.CELL_TYPE_FORMULA, "B5*3");
	}

	@Test
	public void shouldAddStyle() throws Exception {
		Workbook wb = ExcelUtil.loadWorkbookFromResource("org/openmrs/module/reporting/common/ExcelUtilTest.xls");
		Sheet sheet = wb.getSheet("Testing");

		// Test Fonts
		Cell cell = getCellToTheRightOf(sheet, "String");
		Assert.assertEquals("This is a String", ExcelUtil.getCellContents(cell));

		Assert.assertEquals(Font.BOLDWEIGHT_NORMAL, ExcelUtil.getFont(cell).getBoldweight());
		cell.setCellStyle(ExcelUtil.createCellStyle(wb, "bold"));
		Assert.assertEquals(Font.BOLDWEIGHT_BOLD, ExcelUtil.getFont(cell).getBoldweight());

		Assert.assertFalse(ExcelUtil.getFont(cell).getItalic());
		Assert.assertEquals(Font.U_NONE, ExcelUtil.getFont(cell).getUnderline());
		cell.setCellStyle(ExcelUtil.createCellStyle(wb, "italic,underline"));
		Assert.assertTrue(ExcelUtil.getFont(cell).getItalic());
		Assert.assertEquals(Font.U_SINGLE, ExcelUtil.getFont(cell).getUnderline());

		int fontSize = ExcelUtil.getFont(cell).getFontHeightInPoints() + 1;
		cell.setCellStyle(ExcelUtil.createCellStyle(wb, "size="+fontSize));
		Assert.assertEquals((short)fontSize, ExcelUtil.getFont(cell).getFontHeightInPoints());

		// Test other styles
		Assert.assertFalse(cell.getCellStyle().getWrapText());
		Assert.assertEquals(CellStyle.ALIGN_GENERAL, cell.getCellStyle().getAlignment());
		Assert.assertEquals(CellStyle.BORDER_NONE, cell.getCellStyle().getBorderBottom());
		cell.setCellStyle(ExcelUtil.createCellStyle(wb, "wraptext,align=center,border=bottom"));
		Assert.assertTrue(cell.getCellStyle().getWrapText());
		Assert.assertEquals(CellStyle.ALIGN_CENTER, cell.getCellStyle().getAlignment());
		Assert.assertEquals(CellStyle.BORDER_THIN, cell.getCellStyle().getBorderBottom());

		// Test Date
		Date date = DateUtil.getDateTime(2013, 10, 31);
		cell.setCellValue(date);
		ExcelUtil.formatAsDate(cell);
		Assert.assertEquals(Cell.CELL_TYPE_NUMERIC, cell.getCellType());
		Assert.assertTrue(ExcelUtil.isCellDateFormatted(cell));
		Assert.assertEquals(date, ExcelUtil.getCellContents(cell));
	}

	@Test
	public void shouldFormatSheetTitle() throws Exception {

		Assert.assertEquals("TestSheet", ExcelUtil.formatSheetTitle("TestSheet"));
		Assert.assertEquals("Sheet", ExcelUtil.formatSheetTitle(null));
		Assert.assertEquals("Illegal Characters", ExcelUtil.formatSheetTitle("Illegal [Characters]"));
		Assert.assertEquals("This is a title with over 31 ch", ExcelUtil.formatSheetTitle("This is a title with over 31 characters"));

		Set<String> usedTitles = new HashSet<String>();
		String startingTitle = "Starting Title With Too Many Characters";

		String title1 = ExcelUtil.formatSheetTitle(startingTitle, usedTitles);
		Assert.assertEquals("Starting Title With Too Many Ch", title1);
		usedTitles.add(title1);

		String title2 = ExcelUtil.formatSheetTitle(startingTitle, usedTitles);
		Assert.assertEquals("Starting Title With Too Many-1", title2);
		usedTitles.add(title2);

		String title3 = ExcelUtil.formatSheetTitle(startingTitle, usedTitles);
		Assert.assertEquals("Starting Title With Too Many-2", title3);
		usedTitles.add(title3);
	}

    @Test
    public void shouldCheckWhetherCellValueIsSet() throws Exception {
        ExcelBuilder builder = new ExcelBuilder();
        builder.newSheet("Sheet1");
        builder.addCell("One");

        assertFalse(ExcelUtil.cellHasValueSet(builder.getCurrentRow().getCell(1)));

        builder.addCell("Two");
        assertTrue(ExcelUtil.cellHasValueSet(builder.getCurrentRow().getCell(1)));
    }

	protected void testCellContentsToTheRightOf(Sheet sheet, String contentsBefore, Object contentsToTest) {
		Cell c = getCellToTheRightOf(sheet, contentsBefore);
		Object contentsToCheck = ExcelUtil.getCellContents(c);
		Assert.assertEquals(contentsToTest, contentsToCheck);
	}

	protected void testSettingCellContents(Sheet sheet, String contentsBefore, Object valueToSet, int expectedCellType, Object expectedContents) {
		Cell cell = getCellToTheRightOf(sheet, contentsBefore);
		ExcelUtil.setCellContents(cell, valueToSet);
		Assert.assertEquals(expectedCellType, cell.getCellType());
		Object actualContents = ExcelUtil.getCellContents(cell);
		Assert.assertEquals(expectedContents, actualContents);
	}

	protected Cell getCellToTheRightOf(Sheet sheet, Object contents) {
		for (Iterator<Row> ri = sheet.rowIterator(); ri.hasNext();) {
			Row row = ri.next();
			for (Iterator<Cell> ci = row.cellIterator(); ci.hasNext();) {
				Cell cell = ci.next();
				if (contents.equals(ExcelUtil.getCellContents(cell))) {
					return ci.next();
				}
			}
		}
		return null;
	}
}