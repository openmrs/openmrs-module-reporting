package org.openmrs.module.reporting.common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.Date;

/**
 * A utility class for manipulating Excel documents via POI
 */
public class ExcelUtil {

	public static final String[] ILLEGAL_CHARS = {":", "\\", "*", "?", "/", "[", "]"};

	protected static Log log = LogFactory.getLog(ExcelUtil.class);

	/**
	 * Retrieves the contents of the passed cell as a String
	 * @param cell the cell to retrieve the contents for
	 * @return the contents of the passed cell as a String
	 */
	public static Object getCellContents(Cell cell) {
    	Object contents = "";
    	try {
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				if (ExcelUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue();
				}
				else {
					Double d = cell.getNumericCellValue();
					if (d.intValue() == d.doubleValue()) {
						return Integer.valueOf(d.intValue());
					}
					return d;
				}
			}
			else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
				return cell.getBooleanCellValue();
			}
			else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				return cell.getCellFormula();
			}
			else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
				return Byte.toString(cell.getErrorCellValue());
			}
			else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
				return "";
			}
			else {
				return cell.getRichStringCellValue() != null ? cell.getRichStringCellValue().toString() : cell.getStringCellValue();
			}
    	}
    	catch (Exception e) {
			if (cell.getRichStringCellValue() != null) {
    			contents = cell.getRichStringCellValue().toString();
			}
    	}
		if (contents instanceof String) {
			contents = ObjectUtil.nvlStr(contents, "").trim();
		}
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
		if (!cellHasValueSet(cell) || !cellValue.equals(getCellContents(cell))) {
			if (cellValue instanceof Number) {
				cell.setCellValue(((Number) cellValue).doubleValue());
				return;
			}
			if (cellValue instanceof Date) {
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

			if (!cellValueString.matches(".*[a-zA-Z]+.*")) {
				try {
					cell.setCellValue(wb.getCreationHelper().createRichTextString(Integer.toString(Integer.parseInt(cellValueString))));
					return;
				}
				catch (Exception e) {
				}
				try {
					cell.setCellValue(wb.getCreationHelper().createRichTextString(Double.toString(Double.parseDouble(cellValueString))));
					return;
				}
				catch (Exception e) {
				}
			}
			cell.setCellValue(wb.getCreationHelper().createRichTextString(cellValueString));
			return;
		}
		return;
	}

    /**
     * @param cell
     * @return whether this cell has had a value set on it before
     */
    public static boolean cellHasValueSet(Cell cell) {
        try {
            cell.toString();
            return true;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public static void formatAsDate(Cell cell) {
		Workbook wb = cell.getSheet().getWorkbook();
		CellStyle style = wb.createCellStyle();
		style.cloneStyleFrom(cell.getCellStyle());
		style.setDataFormat(wb.createDataFormat().getFormat("d/mmm/yyyy"));
		cell.setCellStyle(style);
	}

	public static double getDateAsNumber(Date d) {
		return DateUtil.getExcelDate(d);
	}

	public static Date getNumberAsDate(double d) {
		return DateUtil.getJavaDate(d);
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
	public static CellStyle createCellStyle(Workbook wb, String descriptor) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
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
					short dateFormat = wb.createDataFormat().getFormat("d/mmm/yyyy");
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
		return style;
	}

	/**
	 * @return a String, based on the passed String, which is suitable for use as a sheet title
	 */
	public static String formatSheetTitle(String s) {
		s = ObjectUtil.nvlStr(s, "Sheet");
		for (String illegal : ILLEGAL_CHARS) {
			s = s.replace(illegal, "");
		}
		s = (s.length() > 31 ? s.substring(0, 31) : s);
		return s;
	}

	/**
	 * @return a String, based on the passed String, which is suitable for use as a sheet title, ensuring that
	 * it is not in the set of used titles passed in
	 */
	public static String formatSheetTitle(String s, Collection<String> usedTitles) {
		s = formatSheetTitle(s);
		if (usedTitles.contains(s)) {
			s = s.length() > 28 ? s.substring(0, 28) : s;
			for (int i=1; ; i++) {
				String attempt = s + "-" + i;
				if (!usedTitles.contains(attempt)) {
					return attempt;
				}
			}
		}
		return s;
	}

	public static boolean isCellDateFormatted(Cell cell) {
		boolean ret = false;
		try {
			ret = DateUtil.isCellDateFormatted(cell);
		}
		catch (Exception e){}
		return ret;
	}

	public static Font getFont(Cell cell) {
		CellStyle style = cell.getCellStyle();
		return cell.getSheet().getWorkbook().getFontAt(style.getFontIndex());
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

    /**
     * Load a workbook from an InputStream.
     * The conditional logic is in order to support differences in xls and xlsx formats
     * See: http://stackoverflow.com/questions/26729618/read-xlsx-file-using-poifsfilesystem
     */
	public static Workbook loadWorkbookFromInputStream(InputStream is) {
		try {
            if (!is.markSupported()) {
                is = new PushbackInputStream(is, 8);
            }

            if (POIFSFileSystem.hasPOIFSHeader(is)) {
                POIFSFileSystem fs = new POIFSFileSystem(is);
                return WorkbookFactory.create(fs);
            }
            else {
                return new XSSFWorkbook(OPCPackage.open(is));
            }
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load excel workbook from resource", e);
		}
	}

	public static Workbook loadWorkbookFromResource(String resource) {
		InputStream is = null;
		try {
			is = OpenmrsClassLoader.getInstance().getResourceAsStream(resource);
			return loadWorkbookFromInputStream(is);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load excel workbook from resourceL " + resource, e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static Workbook loadWorkbookFromFile(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			return loadWorkbookFromInputStream(is);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load excel workbook from file: " + path, e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static void copyFormula(Cell fromCell, Cell toCell) {
		if (fromCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			if (!fromCell.isPartOfArrayFormulaGroup()) {
				Sheet sheet = fromCell.getSheet();
				Workbook workbook = sheet.getWorkbook();
				String formula = fromCell.getCellFormula();
				int shiftRows = toCell.getRowIndex() - fromCell.getRowIndex();
				int shiftCols = toCell.getColumnIndex() - fromCell.getColumnIndex();

				FormulaParsingWorkbook fpw = null;
				FormulaRenderingWorkbook frw = null;
				if (workbook instanceof HSSFWorkbook) {
					HSSFEvaluationWorkbook evaluationWorkbook = HSSFEvaluationWorkbook.create((HSSFWorkbook)workbook);
					fpw = evaluationWorkbook;
					frw = evaluationWorkbook;
				}
				else if (workbook instanceof XSSFWorkbook) {
					XSSFEvaluationWorkbook evaluationWorkbook = XSSFEvaluationWorkbook.create((XSSFWorkbook)workbook);
					fpw = evaluationWorkbook;
					frw = evaluationWorkbook;
				}

				if (fpw != null) {
					Ptg[] ptgs = FormulaParser.parse(formula, fpw, FormulaType.CELL, workbook.getSheetIndex(sheet));

					for (Ptg ptg : ptgs) {
						// Handle cell references
						if (ptg instanceof RefPtgBase) {
							RefPtgBase ref = (RefPtgBase) ptg;
							if (ref.isColRelative()) {
								ref.setColumn(ref.getColumn() + shiftCols);
							}
							if (ref.isRowRelative()) {
								ref.setRow(ref.getRow() + shiftRows);
							}
						}
						// Handle range references
						else if (ptg instanceof AreaPtg) {
							AreaPtg ref = (AreaPtg) ptg;
							if (ref.isFirstColRelative()) {
								ref.setFirstColumn(ref.getFirstColumn() + shiftCols);
							}
							if (ref.isLastColRelative()) {
								ref.setLastColumn(ref.getLastColumn() + shiftCols);
							}
							if (ref.isFirstRowRelative()) {
								ref.setFirstRow(ref.getFirstRow() + shiftRows);
							}
							if (ref.isLastRowRelative()) {
								ref.setLastRow(ref.getLastRow() + shiftRows);
							}
						}
					}
					formula = FormulaRenderer.toFormulaString(frw, ptgs);
					toCell.setCellFormula(formula);
				}
			}
		}
	}
}
