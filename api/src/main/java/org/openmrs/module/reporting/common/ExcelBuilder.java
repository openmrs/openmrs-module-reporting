/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel Helper class that facilitates creating rows and columns in a workbook
 */
public class ExcelBuilder {

	private Workbook workbook;
	private Sheet currentSheet = null;
	private List<String> sheetNames = new ArrayList<String>();
	private Row currentRow = null;
	private int currentRowNum = 0;
	private int currentColNum = 0;

	private Map<String, CellStyle> styleCache = new HashMap<String, CellStyle>();
    
    public ExcelBuilder() {
		workbook = new XSSFWorkbook();
    }

	/**
	 * Create a new sheet with a default name, and switch to this sheet
	 */
	public ExcelBuilder newSheet() {
		return newSheet(null);
	}

	/**
	 * Create a new sheet with the given name, and switch to this sheet
	 */
	public ExcelBuilder newSheet(String name) {
		name = ExcelUtil.formatSheetTitle(name, sheetNames);
		currentSheet = workbook.createSheet(name);
		sheetNames.add(name);
		currentRow = null;
		currentRowNum = 0;
		currentColNum = 0;
		return this;
	}

    /**
     * Turns off gridlines
     */
    public ExcelBuilder hideGridlinesInCurrentSheet() {
        currentSheet.setDisplayGridlines(false);
        return this;
    }

    /**
     * For printing, configures to shrink so that all columns fit on a page
     */
    public ExcelBuilder fitColumnsToPage() {
        currentSheet.setFitToPage(true);
        currentSheet.getPrintSetup().setFitHeight((short)0);
        currentSheet.getPrintSetup().setFitWidth((short)1);
        return this;
    }

    /**
     * For printing, sets landscape orientation
     */
    public ExcelBuilder setLandscape() {
        currentSheet.getPrintSetup().setLandscape(true);
        return this;
    }

    /**
     * Adds the next cell with the given value, and no style.
     */
    public ExcelBuilder addCell(Object cellValue) {
		return addCell(cellValue, null);
    }

	/**
	 * Adds the next cell with the given value, and style described by the String descriptor
	 */
	public ExcelBuilder addCell(Object cellValue, String style) {
        if (currentSheet == null) {
            newSheet();
        }
        if (currentRow == null) {
            currentRow = currentSheet.createRow(currentRowNum);
        }
        Cell cell;
        if (cellValue == null) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_BLANK);
        }
        else if (cellValue instanceof Number) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
        }
        else if (cellValue instanceof Date) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
        }
        else if (cellValue instanceof Boolean) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_BOOLEAN);
        }
        else if (cellValue instanceof Cohort) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
        }
        else if (cellValue instanceof CohortIndicatorResult) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
        }
        else if (cellValue instanceof CohortIndicatorAndDimensionResult) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
        }
        else {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_STRING);
        }

        if (ObjectUtil.isNull(style) && cellValue instanceof Date) {
            style = "date";
        }
        if (ObjectUtil.notNull(style)) {
            CellStyle cellStyle = loadStyle(style);
            cell.setCellStyle(cellStyle);
        }
        ExcelUtil.setCellContents(cell, cellValue);
        currentColNum++;
		return this;
    }

    public ExcelBuilder addCell(Object cellValue, String style, int columnWidth) {
        addCell(cellValue, style);
        currentSheet.setColumnWidth(currentColNum-1, columnWidth*256);
        return this;
    }

    public ExcelBuilder merge(int numColumns, int numRows) {
        int startCol = currentColNum-1;
        CellRangeAddress ra = new CellRangeAddress(currentRowNum, currentRowNum+numRows, startCol, startCol+numColumns);
        currentSheet.addMergedRegion(ra);
        currentColNum+=numColumns;
        return this;
    }

    public XSSFRichTextString createRichTextString(Map<String, String> textAndStyle) {
        XSSFRichTextString rt = new XSSFRichTextString("");
        for (Map.Entry<String, String> e : textAndStyle.entrySet()) {
            String text = e.getKey();
            String style = e.getValue();
            XSSFCellStyle cellStyle = (XSSFCellStyle) loadStyle(style);
            XSSFFont font = cellStyle.getFont();
            rt.append(text, font);
        }
        return rt;
    }

    public XSSFRichTextString createRichTextString(String... textAndStyle) {
        Map<String, String> m = new LinkedHashMap<String, String>();
        for (int i=0; i<textAndStyle.length; i+=2) {
            m.put(textAndStyle[i], textAndStyle[i + 1]);
        }
        return createRichTextString(m);
    }

    /**
     * Moves to the next row.
     */
    public ExcelBuilder nextRow() {
		currentRow = null;
        currentRowNum++;
        currentColNum = 0;
		return this;
    }

	/**
	 * Outputs the Excel workbook to the specified output stream
	 */
	public void write(OutputStream out) throws IOException {
		write(out, null);
	}

    /**
     * Outputs the Excel workbook to the specified output stream, first encrypting with a password if supplied
     * See: http://poi.apache.org/encryption.html
     */
    public void write(OutputStream out, String password) throws IOException {
        ExcelUtil.writeWorkbookToStream(workbook, out, password);
    }

    public CellStyle loadStyle(String style) {
        CellStyle cellStyle = styleCache.get(style);
        if (cellStyle == null) {
            cellStyle = ExcelUtil.createCellStyle(workbook, style);
            styleCache.put(style, cellStyle);
        }
        return cellStyle;
    }

	public Workbook getWorkbook() {
		return workbook;
	}

	public Sheet getCurrentSheet() {
		return currentSheet;
	}

	public Row getCurrentRow() {
		return currentRow;
	}

	public int getCurrentRowNum() {
	    return currentRowNum;
    }

    public int getCurrentColNum() {
        return currentColNum;
    }
}
