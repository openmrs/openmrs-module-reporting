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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
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
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.util.OpenmrsClassLoader;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (cellValue instanceof RichTextString) {
                cell.setCellValue((RichTextString)cellValue);
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
     *    valign=top | bottom | center | justify
	 *    date
     *    color=HSSFColor.XYZ.index
     *    background-color=HSSFColor.XYZ.index
     *    rotation=##
	 */
	public static CellStyle createCellStyle(Workbook wb, String descriptor) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		if (ObjectUtil.notNull(descriptor)) {
            log.debug("Setting cell style to: " + descriptor);
			for (String att : StringUtils.splitByWholeSeparatorPreserveAllTokens(descriptor, ",")) {
                log.debug("Handling style: " + att);
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
                else if (att.startsWith("valign=")) {
                    att = att.substring(7);
                    if (att.equals("top")) {
                        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
                    }
                    else if (att.equals("bottom")) {
                        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
                    }
                    else if (att.equals("center")) {
                        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                    }
                    else if (att.equals("justify")) {
                        style.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY);
                    }
                }
                else if (att.startsWith("rotation=")) {
                    att = att.substring(9);
                    style.setRotation(Short.parseShort(att));
                }
				else if (att.startsWith("border=")) {
				    setBorderStyle(style, att.substring(7));
				}
				else if (att.equals("date")) {
					short dateFormat = wb.createDataFormat().getFormat("d/mmm/yyyy");
					style.setDataFormat(dateFormat);
				}
                else if (att.startsWith("format=")) {
                    att = att.substring(7);
                    style.setDataFormat(wb.createDataFormat().getFormat(att));
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
				else if (att.startsWith("color=")) {
                    att = att.substring(6);
                    font.setColor(Short.parseShort(att));
                }
                else if (att.startsWith("background-color=")) {
                    att = att.substring(17);
                    try {
                        style.setFillForegroundColor(Short.parseShort(att));
                    }
                    catch (Exception e) {
                        if (style instanceof XSSFCellStyle) {
                            XSSFCellStyle cs = (XSSFCellStyle)style;
                            try {
                                String[] rgbStr = att.split("x");
                                if (rgbStr.length == 3) {
                                    Color color = new Color(Integer.parseInt(rgbStr[0]), Integer.parseInt(rgbStr[1]), Integer.parseInt(rgbStr[2]));
                                    cs.setFillForegroundColor(new XSSFColor(color));
                                }
                            }
                            catch (Exception e1) {
                                log.warn("Unable to set background color to: " + att, e1);
                            }
                        }
                    }

                    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
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

	public static void setBorderStyle(CellStyle style, String borderStyle) {
        List<String> l = Arrays.asList(borderStyle.toLowerCase().split("\\:"));
        if (l.contains("all") || l.contains("top")) {
            style.setBorderTop(findMatchingBorderStyle(l));
            style.setTopBorderColor(findMatchingColor(l));
        }
        if (l.contains("all") || l.contains("bottom")) {
            style.setBorderBottom(findMatchingBorderStyle(l));
            style.setBottomBorderColor(findMatchingColor(l));
        }
        if (l.contains("all") || l.contains("left")) {
            style.setBorderLeft(findMatchingBorderStyle(l));
            style.setLeftBorderColor(findMatchingColor(l));
        }
        if (l.contains("all") || l.contains("right")) {
            style.setBorderRight(findMatchingBorderStyle(l));
            style.setRightBorderColor(findMatchingColor(l));
        }
    }

    /**
     * @return the cellstyle from the passed list the represents a border style, defaulting to thin if none found
     */
    public static Short findMatchingBorderStyle(List<String> styles) {
        Map<String, Short> m = new HashMap<String, Short>();
        m.put("thin", CellStyle.BORDER_THIN);
        m.put("medium", CellStyle.BORDER_MEDIUM);
        m.put("dashed", CellStyle.BORDER_DASHED);
        m.put("hair", CellStyle.BORDER_HAIR);
        m.put("thick", CellStyle.BORDER_THICK);
        m.put("double", CellStyle.BORDER_DOUBLE);
        m.put("dotted", CellStyle.BORDER_DOTTED);
        m.put("mediumDashed", CellStyle.BORDER_MEDIUM_DASHED);
        m.put("dashDot", CellStyle.BORDER_DASH_DOT);
        m.put("mediumDashDot", CellStyle.BORDER_MEDIUM_DASH_DOT);
        m.put("dashDotDot", CellStyle.BORDER_DASH_DOT_DOT);
        m.put("mediumDashDotDot", CellStyle.BORDER_MEDIUM_DASH_DOT_DOT);
        m.put("slantedDashDot", CellStyle.BORDER_SLANTED_DASH_DOT);
        for (String s : styles) {
            Short ret = m.get(s.toLowerCase());
            if (ret != null) {
                return ret;
            }
        }
        return CellStyle.BORDER_THIN;
    }

    /**
     * @return the color from the passed list the represents a color, defaulting to black if none found
     */
    public static short findMatchingColor(List<String> styles) {
        for (HSSFColor color : HSSFColor.getIndexHash().values()) {
            String colorName = color.getClass().getSimpleName().toLowerCase();
            if (styles.contains(colorName)) {
                return color.getIndex();
            }
        }
        return HSSFColor.BLACK.index;
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

	/**
     * Outputs the Excel workbook to the specified output stream, first encrypting with a password if supplied
     * See: http://poi.apache.org/encryption.html
     */
    public static void writeWorkbookToStream(Workbook workbook, OutputStream out, String password) throws IOException {
        if (StringUtils.isBlank(password)) {
            workbook.write(out);
        }
        else {
            POIFSFileSystem fs = new POIFSFileSystem();
            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            Encryptor enc = info.getEncryptor();
            enc.confirmPassword(password);

            ByteArrayOutputStream baos = null;
            ByteArrayInputStream bais = null;

            try {
                baos = new ByteArrayOutputStream();
                workbook.write(baos);
                bais = new ByteArrayInputStream(baos.toByteArray());

                OPCPackage opc = OPCPackage.open(bais);
                OutputStream os = enc.getDataStream(fs);
                opc.save(os);
                opc.close();
            }
            catch (Exception e) {
                throw new IllegalStateException("Error writing encrypted Excel document", e);
            }
            finally {
                IOUtils.closeQuietly(baos);
                IOUtils.closeQuietly(bais);
            }

            fs.writeFilesystem(out);
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
