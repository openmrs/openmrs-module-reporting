package org.openmrs.module.reporting.common;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collection;
import java.util.Date;

/**
 * A utility class for manipulating Excel documents via POI
 */
public class ExcelUtil {

	/**
	 * Retrieves the contents of the passed cell as a String
	 * @param cell the cell to retrieve the contents for
	 * @return the contents of the passed cell as a String
	 */
	public static String getCellContentsAsString(Cell cell) {
    	String contents = "";
    	try {
	    	switch (cell.getCellType()) {
	    		case Cell.CELL_TYPE_STRING: 	contents = cell.getRichStringCellValue().toString(); break;
	    		case Cell.CELL_TYPE_NUMERIC: 	contents = Double.toString(cell.getNumericCellValue()); break;
	    		case Cell.CELL_TYPE_BOOLEAN:	contents = Boolean.toString(cell.getBooleanCellValue()); break;
	    		case Cell.CELL_TYPE_FORMULA:	contents = cell.getCellFormula(); break;
	    		case Cell.CELL_TYPE_ERROR:		contents = Byte.toString(cell.getErrorCellValue()); break;
	    		default: break;
	    	}
    	}
    	catch (Exception e) {
			if (cell.getRichStringCellValue() != null) {
    			contents = cell.getRichStringCellValue().toString();
			}
    	}
		contents = ObjectUtil.nvlStr(contents, "").trim();
    	return contents;
	}
	
	/**
	 * Sets the passed cell to the passed value
	 * @param cell the cell to set
	 * @param cellValue the value to set the cell to
	 */
	public static void setCellContents(Cell cell, Object cellValue) {
		Workbook wb = cell.getSheet().getWorkbook();
		if (cellValue == null) { cellValue = ""; }
		if (!cellValue.equals(getCellContentsAsString(cell))) {
			if (cellValue instanceof Number) {
				cell.setCellValue(((Number) cellValue).doubleValue());
				return;
			}
			if (cellValue instanceof Date) {
				if (!DateUtil.isCellDateFormatted(cell)) {
					addStyle(cell, "date");
				}
				cell.setCellValue(((Date) cellValue));
				return;
			}
			
			String cellValueString = ObjectUtil.format(cellValue);
			try {
				if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					cell.setCellValue(Boolean.valueOf(cellValueString));
					return;
				}
				if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					cell.setCellFormula(cellValueString);
					return;
				}
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					cell.setCellValue(Double.parseDouble(cellValueString));
					return;
				}
			}
			catch (Exception e) {}
			
			try {
				cell.setCellValue(wb.getCreationHelper().createRichTextString(Integer.toString(Integer.parseInt(cellValueString))));
				return;
			}
			catch (Exception e) {}
			try {
				cell.setCellValue(wb.getCreationHelper().createRichTextString(Double.toString(Double.parseDouble(cellValueString))));
				return;
			}
			catch (Exception e) {}
			cell.setCellValue(wb.getCreationHelper().createRichTextString(cellValueString));
			return;
		}
		return;
	}

	/**
	 * Descriptor supports a comma-separated string containing attributes:
	 *    bold
	 *    italic
	 *    underline
	 *    size=##
	 *    wraptext
	 *    border=all | bottom | top | left | right
	 *    align=center | left | right | fill
	 *    date
	 */
	public static void addStyle(Cell cell, String descriptor) {
		Workbook wb = cell.getSheet().getWorkbook();
		CellStyle style = cell.getCellStyle();
		if (style == null) {
			style = cell.getSheet().getWorkbook().createCellStyle();
		}
		Font font = wb.getFontAt(style.getFontIndex());
		if (font == null) {
			font = wb.createFont();
		}
		if (ObjectUtil.notNull(descriptor)) {
			for (String att : descriptor.split(",")) {
				att = att.toLowerCase().trim();
				if (att.equals("wraptext")) {
					style.setWrapText(true);
				}
				else if (att.startsWith("align=")) {
					att = att.substring(6);
					if (att.equals("left")) {
						style.setAlignment(CellStyle.ALIGN_LEFT);
					}
					else if (att.equals("center")) {
						style.setAlignment(CellStyle.ALIGN_CENTER);
					}
					else if (att.equals("right")) {
						style.setAlignment(CellStyle.ALIGN_RIGHT);
					}
					else if (att.equals("fill")) {
						style.setAlignment(CellStyle.ALIGN_FILL);
					}
				}
				else if (att.startsWith("border=")) {
					att = att.substring(7);
					if (att.equals("all")) {
						style.setBorderTop(CellStyle.BORDER_THIN);
						style.setBorderBottom(CellStyle.BORDER_THIN);
						style.setBorderLeft(CellStyle.BORDER_THIN);
						style.setBorderRight(CellStyle.BORDER_THIN);
					}
					else if (att.equals("top")) {
						style.setBorderTop(CellStyle.BORDER_THIN);
					}
					else if (att.equals("bottom")) {
						style.setBorderBottom(CellStyle.BORDER_THIN);
					}
					else if (att.equals("left")) {
						style.setBorderLeft(CellStyle.BORDER_THIN);
					}
					else if (att.equals("right")) {
						style.setBorderRight(CellStyle.BORDER_THIN);
					}
				}
				else if (att.equals("date")) {
					short dateFormat = wb.createDataFormat().getFormat("d-mmm-yy");
					style.setDataFormat(dateFormat);
				}
				else if (att.equals("bold")) {
					font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				}
				else if (att.equals("italic")) {
					font.setItalic(true);
				}
				else if (att.equals("underline")) {
					font.setUnderline(Font.U_SINGLE);
				}
				else if (att.startsWith("size=")) {
					att = att.substring(5);
					font.setFontHeightInPoints(Short.parseShort(att));
				}
			}
		}
		style.setFont(font);
		cell.setCellStyle(style);
	}
	
	/**
	 * @return a String, based on the passed String, which is suitable for use as a sheet title
	 */
	public static String formatSheetTitle(String s) {
		s = ObjectUtil.nvlStr(s, "Sheet");
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replace(" ", "");
		s = (s.length() > 30 ? s.substring(0, 30) : s);
		return s;
	}
	
	/**
	 * @return a String, based on the passed String, which is suitable for use as a sheet title, ensuring that
	 * it is not in the set of used titles passed in
	 */
	public static String formatSheetTitle(String s, Collection<String> usedTitles) {
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
	
	public static String formatRow(Row row) {
		StringBuilder sb = new StringBuilder();
		if (row != null) {
			for (int i=0; i<row.getPhysicalNumberOfCells(); i++) {
				Cell cell = row.getCell(i);
				sb.append(i == 0 ? "" : ", ").append(cell == null ? "" : cell.toString());
			}
		}
		return sb.toString();
	}
}
