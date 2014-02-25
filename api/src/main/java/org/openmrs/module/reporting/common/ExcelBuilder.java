package org.openmrs.module.reporting.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
		workbook = new HSSFWorkbook();
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
			CellStyle cellStyle = styleCache.get(style);
			if (cellStyle == null) {
				cellStyle = ExcelUtil.createCellStyle(workbook, style);
				styleCache.put(style, cellStyle);
			}
			cell.setCellStyle(cellStyle);
		}
		ExcelUtil.setCellContents(cell, cellValue);
        currentColNum++;
		return this;
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
		workbook.write(out);
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
}
