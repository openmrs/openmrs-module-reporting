package org.openmrs.module.reporting.common;

import java.util.Date;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.openmrs.module.reporting.report.renderer.ExcelStyleHelper;

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
			
			String cellValueString = ObjectUtil.format(cellValue);
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
	
	/**
	 * @return a String, based on the passed String, which is suitable for use as a sheet title
	 */
	public static String formatSheetTitle(String s) {
		s = ObjectUtil.nvlStr(s, "Sheet");
		s = (s.length() > 30 ? s.substring(0, 30) : s);
		return s;
	}
	
	/**
	 * @return a String, based on the passed String, which is suitable for use as a sheet title, ensuring that
	 * it is not in the set of used titles passed in
	 */
	public static String formatSheetTitle(String s, Set<String> usedTitles) {
		s = formatSheetTitle(s);
		if (usedTitles.contains(s)) {
			s = s.length() > 27 ? s.substring(0, 27) : s;
			for (int i=1; ; i++) {
				String attempt = s + "-" + i;
				if (!usedTitles.contains(attempt)) {
					return attempt;
				}
			}
		}
		return s;
	}
	
	public static String formatRow(HSSFRow row) {
		StringBuilder sb = new StringBuilder();
		if (row != null) {
			for (int i=0; i<row.getPhysicalNumberOfCells(); i++) {
				HSSFCell cell = row.getCell(i);
				sb.append((i == 0 ? "" : ", ") + (cell == null ? "" : cell.toString()));
			}
		}
		return sb.toString();
	}
}
