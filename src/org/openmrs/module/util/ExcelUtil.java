package org.openmrs.module.util;

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.openmrs.module.report.renderer.ExcelStyleHelper;

/**
 * A utility class for manipulating Excel documents via POI
 */
public class ExcelUtil {

	/**
	 * Retrieves the contents of the passed cell as a String
	 * @param cell the cell to retrieve the contents for
	 * @return the contents of the passed cell as a String
	 */
	public static String getCellContentsAsString(HSSFCell cell) {
    	String contents = "";
    	try {
	    	switch (cell.getCellType()) {
	    		case HSSFCell.CELL_TYPE_STRING: 	contents = cell.getRichStringCellValue().toString(); break;
	    		case HSSFCell.CELL_TYPE_NUMERIC: 	contents = Double.toString(cell.getNumericCellValue()); break;
	    		case HSSFCell.CELL_TYPE_BOOLEAN:	contents = Boolean.toString(cell.getBooleanCellValue()); break;
	    		case HSSFCell.CELL_TYPE_FORMULA:	contents = cell.getCellFormula(); break;
	    		case HSSFCell.CELL_TYPE_ERROR:		contents = Byte.toString(cell.getErrorCellValue()); break;
	    		default: break;
	    	}
    	}
    	catch (Exception e) {
    		contents = cell.getRichStringCellValue().toString();
    	}
    	contents = contents.trim();
    	return contents;
	}
	
	/**
	 * Sets the passed cell to the passed value
	 * @param cell the cell to set
	 * @param cellValue the value to set the cell to
	 */
	public static void setCellContents(ExcelStyleHelper styleHelper, HSSFCell cell, Object cellValue) {
		
		if (cellValue == null) { cellValue = ""; }
		if (!cellValue.equals(getCellContentsAsString(cell))) {
			if (cellValue instanceof Number) {
				cell.setCellValue(((Number) cellValue).doubleValue());
				return;
			}
			if (cellValue instanceof Date) {
				cell.setCellStyle(styleHelper.getStyle("date"));
				cell.setCellValue(((Date) cellValue));
				return;
			}
			
			String cellValueString = cellValue.toString();
			try {
				if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
					cell.setCellValue(Boolean.valueOf(cellValueString).booleanValue());
					return;
				}
				if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					cell.setCellFormula(cellValueString);
					return;
				}
				if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					cell.setCellValue(Double.parseDouble(cellValueString));
					return;
				}
			}
			catch (Exception e) {}
			
			try {
				cell.setCellValue(new HSSFRichTextString(Integer.toString(Integer.parseInt(cellValueString))));
				return;
			}
			catch (Exception e) {}
			try {
				cell.setCellValue(new HSSFRichTextString(Double.toString(Double.parseDouble(cellValueString))));
				return;
			}
			catch (Exception e) {}
			cell.setCellValue(new HSSFRichTextString(cellValueString));
			return;
		}
		return;
	}
}
