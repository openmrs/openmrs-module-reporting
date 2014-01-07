package org.openmrs.module.reporting.report.renderer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;

import java.util.Date;

/**
 * A wrapper around a POI HSSFSheet that lets you interact via
 *  * skipCell
 *  * addCell
 *  * nextRow
 *  
 *  This class was adapted from org.pih.SheetHelper in PIH-EMR.
 *  @see ExcelStyleHelper
 */
public class ExcelSheetHelper {

    int currentRowNum;
    int currentColNum;
    Sheet sheet;
    Workbook workbook;
    Row currentRow;
    
    public ExcelSheetHelper(Sheet sheet) {
        this.sheet = sheet;
        this.workbook = sheet.getWorkbook();
        currentRowNum = 0;
        currentColNum = 0;
    }

    /**
     * @return the currentRowNum
     */
    public int getCurrentRowNum() {
        return currentRowNum;
    }

    /**
     * @param currentRowNum the currentRowNum to set
     */
    public void setCurrentRowNum(int currentRowNum) {
        this.currentRowNum = currentRowNum;
    }

    /**
     * @return the currentColNum
     */
    public int getCurrentColNum() {
        return currentColNum;
    }

    /**
     * @param currentColNum the currentColNum to set
     */
    public void setCurrentColNum(int currentColNum) {
        this.currentColNum = currentColNum;
    }
    
    /**
     * Adds the next cell with the given value, and no style.
     * 
     * @param cellValue
     */
    public void addCell(Object cellValue) {
        addCell(cellValue, null);
    }
    
    /**
     * Adds the next cell with the given value and style.
     * 
     * @param cellValue
     * @param style
     */
    public void addCell(Object cellValue, CellStyle style) {
        //System.out.println("Creating cell " + currentRowNum + "," + currentColNum + ": " + cellValue);
        if (currentRow == null) {
            currentRow = sheet.createRow(currentRowNum);
        }
        Cell cell;
        if (cellValue == null) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_BLANK);
        } 
        else if (cellValue instanceof Number) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((Number) cellValue).doubleValue());
        } 
        else if (cellValue instanceof Date) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue((Date) cellValue);
        }
        else if (cellValue instanceof Boolean) {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_BOOLEAN);
            cell.setCellValue((Boolean) cellValue);
        } 
        else if (cellValue instanceof Cohort) {
        	cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((Cohort) cellValue).getSize());
        } 
        else if (cellValue instanceof CohortIndicatorResult) {
        	cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((CohortIndicatorResult) cellValue).getValue().doubleValue());
        } 
        else if (cellValue instanceof CohortIndicatorAndDimensionResult) {
        	cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((CohortIndicatorAndDimensionResult) cellValue).getValue().doubleValue());
        } 
        else {
            cell = currentRow.createCell(currentColNum, Cell.CELL_TYPE_STRING);
            cell.setCellValue(workbook.getCreationHelper().createRichTextString(ObjectUtil.format(cellValue)));
        } 
        if (style != null) {
            cell.setCellStyle(style);
        }
        ++currentColNum;
    }

    /**
     * Moves to the next row.
     */
    public void nextRow() {
        ++currentRowNum;
        currentRow = null;
        currentColNum = 0;
    }

    /**
     * Removes illegal characters from a sheet name and shortens it so it isn't too long.
     * 
     * @param name
     * @return
     */
    public static String fixSheetName(String name) {
        name = name.replace("[", "");
        name = name.replace("]", "");
        name = name.replace(" ", "");
        if (name.length() > 15)
            name = name.substring(0, 15);
        return name;
    }
    
}
