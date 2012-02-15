package org.openmrs.module.reporting.report.renderer;

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;

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
    HSSFSheet sheet;
    HSSFRow currentRow;
    
    public ExcelSheetHelper(HSSFSheet sheet) {
        this.sheet = sheet;
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
    public void addCell(Object cellValue, HSSFCellStyle style) {
        //System.out.println("Creating cell " + currentRowNum + "," + currentColNum + ": " + cellValue);
        if (currentRow == null) {
            currentRow = sheet.createRow(currentRowNum);
        }
        HSSFCell cell;
        if (cellValue == null) {
            cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_BLANK);
        } 
        else if (cellValue instanceof Number) {
            cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((Number) cellValue).doubleValue());
        } 
        else if (cellValue instanceof Date) {
            cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue((Date) cellValue);
        }
        else if (cellValue instanceof Boolean) {
            cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_BOOLEAN);
            cell.setCellValue((Boolean) cellValue);
        } 
        else if (cellValue instanceof Cohort) {
        	cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((Cohort) cellValue).getSize());
        } 
        else if (cellValue instanceof CohortIndicatorResult) {
        	cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((CohortIndicatorResult) cellValue).getValue().doubleValue());
        } 
        else if (cellValue instanceof CohortIndicatorAndDimensionResult) {
        	cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(((CohortIndicatorAndDimensionResult) cellValue).getValue().doubleValue());
        } 
        else {
            cell = currentRow.createCell(currentColNum, HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(new HSSFRichTextString(cellValue.toString()));
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
        name = name.replace(" ", "");
        if (name.length() > 15)
            name = name.substring(0, 15);
        return name;
    }
    
}
