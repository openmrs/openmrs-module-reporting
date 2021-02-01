/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ExcelUtil;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Report Renderer implementation that supports rendering to an Excel template
 */
@Handler
@Localized("reporting.ExcelTemplateRenderer")
public class ExcelTemplateRenderer extends ReportTemplateRenderer {

    public static String PASSWORD_PROPERTY = "password";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public ExcelTemplateRenderer() {
		super();
	}

	/**
	 * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
	 */
    @Override
	public String getFilename(ReportRequest request) {
		String fileName = super.getFilename(request);
		if (!fileName.contains(".xls")) {
			fileName += ".xls";
		}
		return fileName;
	}

	/**
	 * @see ReportRenderer#getRenderedContentType(org.openmrs.module.reporting.report.ReportRequest)
     * @param request
	 */
	public String getRenderedContentType(ReportRequest request) {
		return "application/vnd.ms-excel";
	}

	/** 
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {

		try {
			log.debug("Attempting to render report with ExcelTemplateRenderer");
			ReportDesign design = getDesign(argument);
			Workbook wb = getExcelTemplate(design);

        	if (wb == null) {
        		XlsReportRenderer xlsRenderer = new XlsReportRenderer();
				xlsRenderer.render(reportData, argument, out);
			}
			else {
				Map<String, String> repeatSections = getRepeatingSections(design);

				// Put together base set of replacements.  Any dataSet with only one row is included.
				Map<String, Object> replacements = getBaseReplacementData(reportData, design);

				// Iterate across all of the sheets in the workbook, and configure all those that need to be added/cloned
				List<SheetToAdd> sheetsToAdd = new ArrayList<SheetToAdd>();

				Set<String> usedSheetNames = new HashSet<String>();
				int numberOfSheets = wb.getNumberOfSheets();

				for (int sheetNum=0; sheetNum<numberOfSheets; sheetNum++) {

					Sheet currentSheet = wb.getSheetAt(sheetNum);
					String originalSheetName = wb.getSheetName(sheetNum);

					String dataSetName = getRepeatingSheetProperty(sheetNum, repeatSections);
					if (dataSetName != null) {

						DataSet repeatingSheetDataSet = getDataSet(reportData, dataSetName, replacements);
						int dataSetRowNum = 0;
						for (Iterator<DataSetRow> rowIterator = repeatingSheetDataSet.iterator(); rowIterator.hasNext();) {
							DataSetRow dataSetRow = rowIterator.next();
							dataSetRowNum++;
							Map<String, Object> newReplacements = getReplacementData(replacements, reportData, design, dataSetName, dataSetRow, dataSetRowNum);
							Sheet newSheet = (dataSetRowNum == 1 ? currentSheet : wb.cloneSheet(sheetNum));
							sheetsToAdd.add(new SheetToAdd(newSheet, sheetNum, originalSheetName, newReplacements));
						}
					}
					else {
						sheetsToAdd.add(new SheetToAdd(currentSheet, sheetNum, originalSheetName, replacements));
					}
				}

				// Then iterate across all of these and add them in
				for (int i=0; i<sheetsToAdd.size(); i++) {
					addSheet(wb, sheetsToAdd.get(i), usedSheetNames, reportData, design, repeatSections);
				}

                ExcelUtil.writeWorkbookToStream(wb, out, getPassword(design));
			}
		}
		catch (Exception e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
	}
	
	/**
	 * Clone the sheet at the passed index and replace values as needed
	 */
	public Sheet addSheet(Workbook wb, SheetToAdd sheetToAdd, Set<String> usedSheetNames, ReportData reportData, ReportDesign design, Map<String, String> repeatSections) {

		String prefix = getExpressionPrefix(design);
		String suffix = getExpressionSuffix(design);

		Sheet sheet = sheetToAdd.getSheet();
		sheet.setForceFormulaRecalculation(true);
		
		int sheetIndex = wb.getSheetIndex(sheet);

		// Configure the sheet name, replacing any values as needed, and ensuring it is unique for the workbook
		String sheetName = EvaluationUtil.evaluateExpression(sheetToAdd.getOriginalSheetName(), sheetToAdd.getReplacementData(), prefix, suffix).toString();
		sheetName = ExcelUtil.formatSheetTitle(sheetName, usedSheetNames);
		wb.setSheetName(sheetIndex, sheetName);
		usedSheetNames.add(sheetName);
		
		log.debug("Handling sheet: " + sheetName + " at index " + sheetIndex);
		
		// Iterate across all of the rows in the sheet, and configure all those that need to be added/cloned
		List<RowToAdd> rowsToAdd = new ArrayList<RowToAdd>();
			
		int totalRows = sheet.getPhysicalNumberOfRows();
		int rowsFound = 0;
		for (int rowNum=0; rowsFound < totalRows && rowNum < 50000; rowNum++) {  // check for < 50000 is a hack to prevent infinite loops in edge cases
			Row currentRow = sheet.getRow(rowNum);
            if (log.isDebugEnabled()) {
			    log.debug("Handling row: " + ExcelUtil.formatRow(currentRow));
            }
			if (currentRow != null) {
				rowsFound++;
			}
			// If we find that the row that we are on is a repeating row, then add the appropriate number of rows to clone

			String repeatingRowProperty = getRepeatingRowProperty(sheetToAdd.getOriginalSheetNum(), rowNum, repeatSections);
			if (repeatingRowProperty != null) {
				String[] dataSetSpanSplit = repeatingRowProperty.split(",");
				String dataSetName = dataSetSpanSplit[0];
				DataSet dataSet = getDataSet(reportData, dataSetName, sheetToAdd.getReplacementData());
				
				int numRowsToRepeat = 1;
				if (dataSetSpanSplit.length == 2) {
					numRowsToRepeat = Integer.parseInt(dataSetSpanSplit[1]);
				}
				log.debug("Repeating this row with dataset: " + dataSet + " and repeat of " + numRowsToRepeat);
				int repeatNum=0;
				for (DataSetRow dataSetRow : dataSet) {
					repeatNum++;
					for (int i=0; i<numRowsToRepeat; i++) {
						Row row = (i == 0 ? currentRow : sheet.getRow(rowNum+i));
						if (repeatNum == 1 && row != null && row != currentRow) {
							rowsFound++;
						}
						Map<String, Object> newReplacements = getReplacementData(sheetToAdd.getReplacementData(), reportData, design, dataSetName, dataSetRow, repeatNum);
						rowsToAdd.add(new RowToAdd(row, getCellValues(row), newReplacements));
                        if (log.isDebugEnabled()) {
						    log.debug("Adding " + ExcelUtil.formatRow(row) + " with dataSetRow: " + dataSetRow);
                        }
					}
				}
				if(numRowsToRepeat > 1) {
					rowNum += numRowsToRepeat-1;
				}
			}
			else {
				rowsToAdd.add(new RowToAdd(currentRow, getCellValues(currentRow), sheetToAdd.getReplacementData()));
                if (log.isDebugEnabled()) {
				    log.debug("Adding row: " + ExcelUtil.formatRow(currentRow));
                }
			}
		}

		// Now, go through all of the collected rows, and add them back in
		for (int i=0; i<rowsToAdd.size(); i++) {
			RowToAdd rowToAdd = rowsToAdd.get(i);
			if (CollectionUtils.isNotEmpty(rowToAdd.getCellValues())) {
				Row addedRow = addRow(wb, sheetToAdd, rowToAdd, i, reportData, design, repeatSections);
                if (log.isDebugEnabled()) {
				    log.debug("Wrote row " + i + ": " + ExcelUtil.formatRow(addedRow));
                }
			}
		}

		return sheet;
	}
	
	/**
	 * Adds in a Row to the given Sheet
	 */
	public Row addRow(Workbook wb, SheetToAdd sheetToAdd, RowToAdd rowToAdd, int rowIndex, ReportData reportData, ReportDesign design, Map<String, String> repeatSections) {
		
		// Create a new row and copy over style attributes from the row to add
		Row newRow = rowIndex > sheetToAdd.getSheet().getLastRowNum() ?
					sheetToAdd.getSheet().createRow(rowIndex) :
					sheetToAdd.getSheet().getRow(rowIndex);
		if (newRow == null) {
			return null;
		}
		Row rowToClone = rowToAdd.getRowToClone();
		try {
			CellStyle rowStyle = rowToClone.getRowStyle();
			if (rowStyle != null) {
				newRow.setRowStyle(rowStyle);
			}
		}
		catch (Exception e) {
			// No idea why this is necessary, but this has thrown IndexOutOfBounds errors getting the rowStyle.  Mysteries of POI
		}
		newRow.setHeight(rowToClone.getHeight());
		
		// Iterate across all of the cells in the row, and configure all those that need to be added/cloned
		List<CellToAdd> cellsToAdd = new ArrayList<CellToAdd>();

		Iterator<Cell> cellIterator = rowToClone.cellIterator();
		int cellNum = 0;
		while (cellIterator.hasNext()) {
			Cell currentCell = cellIterator.next();
			log.debug("Handling cell: " + currentCell);
			// If we find that the cell that we are on is a repeating cell, then add the appropriate number of cells to clone
			String repeatingColumnProperty = getRepeatingColumnProperty(sheetToAdd.getOriginalSheetNum(), cellNum, repeatSections);
			if (repeatingColumnProperty != null) {
				String[] dataSetSpanSplit = repeatingColumnProperty.split(",");
				String dataSetName = dataSetSpanSplit[0];
				DataSet dataSet = getDataSet(reportData, dataSetName, rowToAdd.getReplacementData());
				int numCellsToRepeat = 1;
				if (dataSetSpanSplit.length == 2) {
					numCellsToRepeat = Integer.parseInt(dataSetSpanSplit[1]);
				}
				log.debug("Repeating this cell with dataset: " + dataSet + " and repeat of " + numCellsToRepeat);
				int repeatNum=0;
				for (DataSetRow dataSetRow : dataSet) {
					repeatNum++;
					for (int i=0; i<numCellsToRepeat; i++) {
						int cellIndex = currentCell.getColumnIndex() + i;
						Cell cell = (i == 0 ? currentCell : rowToClone.getCell(cellIndex));

						Map<String, Object> newReplacements = getReplacementData(rowToAdd.getReplacementData(), reportData, design, dataSetName, dataSetRow, repeatNum);

						cellsToAdd.add(new CellToAdd(cell, rowToAdd.getCellContents(cellNum + i),newReplacements));
						log.debug("Adding " + cell + " with dataSetRow: " + dataSetRow);
					}
				}

				cellNum += numCellsToRepeat;

				// skip over additional repeated cells
				for (int i = 1; i < numCellsToRepeat; i++) {
					cellIterator.next();
				}
			}
			else {
				cellsToAdd.add(new CellToAdd(currentCell, rowToAdd.getCellContents(cellNum), rowToAdd.getReplacementData()));
				log.debug("Adding " + currentCell);
			}
			cellNum++;
		}
		
		// Now, go through all of the collected cells, and add them back in

		String prefix = getExpressionPrefix(design);
		String suffix = getExpressionSuffix(design);

		List<CellRangeAddress> newMergedRegions = new ArrayList<CellRangeAddress>();

		for (int i=0; i<cellsToAdd.size(); i++) {
			CellToAdd cellToAdd = cellsToAdd.get(i);
			Cell cellToClone = cellToAdd.getCellToClone();

			int cellIndex = cellToClone.getColumnIndex();
			Cell newCell = newRow.getCell(cellIndex);
			if (newCell == null) {
				newCell = newRow.createCell(cellIndex);
			}

			Object contents = cellToAdd.getContents();
			newCell.setCellStyle(cellToClone.getCellStyle());

			int numFormattings = sheetToAdd.getSheet().getSheetConditionalFormatting().getNumConditionalFormattings();
			for (int n=0; n<numFormattings; n++) {
				ConditionalFormatting f = sheetToAdd.getSheet().getSheetConditionalFormatting().getConditionalFormattingAt(n);
				for (CellRangeAddress add : f.getFormattingRanges()) {

					if (add.getFirstRow() == rowToAdd.getRowToClone().getRowNum() && add.getLastRow() == rowToClone.getRowNum()) {
						if (add.getFirstColumn() == cellToClone.getColumnIndex() && add.getLastColumn() == cellToClone.getColumnIndex()) {
							ConditionalFormattingRule[] rules = new ConditionalFormattingRule[f.getNumberOfRules()];
							for (int j=0; j<f.getNumberOfRules(); j++) {
								rules[j] = f.getRule(j);
							}
							CellRangeAddress[] cellRange = new CellRangeAddress[1];
							cellRange[0] = new CellRangeAddress(rowIndex, rowIndex, i, i);
							sheetToAdd.getSheet().getSheetConditionalFormatting().addConditionalFormatting(cellRange, rules);
						}
					}
				}
			}

			int numMergedRegions = sheetToAdd.getSheet().getNumMergedRegions();
			for (int n=0; n<numMergedRegions; n++) {
				CellRangeAddress add = sheetToAdd.getSheet().getMergedRegion(n);
				int rowNum = rowToClone.getRowNum();
				if (add.getFirstRow() == rowNum && add.getLastRow() == rowNum) {
					if (add.getFirstColumn() == cellToClone.getColumnIndex()) {
						newMergedRegions.add(new CellRangeAddress(rowNum, rowNum, i, i+add.getNumberOfCells()-1));
					}
				}
			}

			if (ObjectUtil.notNull(contents)) {
				if (contents instanceof String) {
					contents = EvaluationUtil.evaluateExpression(contents.toString(), cellToAdd.getReplacementData(), prefix, suffix);
				}
				ExcelUtil.setCellContents(newCell, contents);
				ExcelUtil.copyFormula(cellToClone, newCell);
			}
		}

		for (CellRangeAddress mergedRegion : newMergedRegions) {
			sheetToAdd.getSheet().addMergedRegion(mergedRegion);
		}
		
		return newRow;
	}
	
	/**
	 * @return an Excel Workbook for the given argument
	 */
	protected Workbook getExcelTemplate(ReportDesign design) throws IOException {
		Workbook wb = null;
		InputStream is = null;
		try {
			ReportDesignResource r = getTemplate(design);
			is = new ByteArrayInputStream(r.getContents());
            wb = ExcelUtil.loadWorkbookFromInputStream(is);
		}
		catch (Exception e) {
			log.warn("No template file found, will use default Excel output");
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		return wb;
	}

	/**
	 * @return a Map of String to String that can be used to find repeating sections
	 * This converts a user design property in the format:
	 * sheet:3,dataset:allPatients | sheet:1,row:6-8,dataset:allPatients | sheet:2,column:4,dataset:malePatients
	 * into a Map which can be quickly accessed as each row / column combination is accessed during processing
	 */
	protected Map<String, String> getRepeatingSections(ReportDesign design) {
		Map<String, String> m = new HashMap<String, String>();
		String propertyValue = design.getPropertyValue("repeatingSections", null);
		if (propertyValue != null) {
			for (String sectionConfig : propertyValue.split("\\|")) {
				try {
					Integer sheetNum = null;
					Integer rowNum = null;
					Integer columnNum = null;
					Integer spanNum = null;
					String dataSetName = null;
					for (String sectionComponent : sectionConfig.split(",")) {
						String[] keyValue = sectionComponent.split("\\:");
						String key = keyValue[0].trim().toLowerCase();
						String[] valueSplit = keyValue[1].trim().split("\\-");
						String lowerBound = valueSplit[0].trim();
						String upperBound = valueSplit.length == 1 ? lowerBound : valueSplit[1].trim();
						if ("sheet".equals(key)) {
							sheetNum = Integer.parseInt(lowerBound);
						}
						else if ("row".equals(key)) {
							rowNum = Integer.parseInt(lowerBound);
							spanNum = Integer.parseInt(upperBound) - rowNum + 1;
						}
						else if ("column".equals(key)) {
							columnNum = Integer.parseInt(lowerBound);
							spanNum = Integer.parseInt(upperBound) - columnNum + 1;
						}
						else if ("dataset".equals(key)) {
							dataSetName = lowerBound;
						}
					}
					String key = "repeatSheet"+sheetNum + (rowNum != null ? "Row"+rowNum : "") + (columnNum != null ? "Column"+columnNum : "");
					String value = dataSetName + (spanNum != null ? ","+spanNum : "");
					m.put(key, value);
				}
				catch (Exception e) {
					log.warn("Error in configuration of repeating sections of ExcelTemplateRenderer.  Please check your configuration.", e);
				}
			}
		}
		return m;
	}
	
	/**
	 * @return if the sheet with the passed number (1-indexed) is repeating, returns the dataset name to use
	 * for example:  repeatSheet0=myIndicatorDataSet would indicate that sheet 0 should be repeated for each row in the dataset
	 */
	protected String getRepeatingSheetProperty(int sheetNumber, Map<String, String> repeatingSections) {
		return repeatingSections.get("repeatSheet" + (sheetNumber+1));
	}
	
	/**
	 * @return if the row with the passed number (1-indexed) is repeating, returns the dataset name to use, optionally with a span
	 * for example:  repeatSheet0Row7=myPatientDataSet,2 would indicate that rows 7 and 8 in sheet 0 should be repeated for each row in the dataset
	 */
	protected String getRepeatingRowProperty(int sheetNumber, int rowNumber, Map<String, String> repeatingSections) {
		return repeatingSections.get("repeatSheet" + (sheetNumber+1) + "Row" + (rowNumber+1));
	}
	
	/**
	 * @return if the column with the passed number (1-indexed) is repeating, returns the dataset name to use, optionally with a span
	 * for example:  repeatSheet0Column5=myPatientDataSet,2 would indicate that columns 5 and 6 in sheet 0 should be repeated for each row in the dataset
	 */
	protected String getRepeatingColumnProperty(int sheetNumber, int columnNumber, Map<String, String> repeatingSections) {
		return repeatingSections.get("repeatSheet" + (sheetNumber+1) + "Column" + (columnNumber+1));
	}
	
	/**
	 * @return the DataSet with the passed name in the passed ReportData, throwing an Exception if one does not exist
	 */
	public DataSet getDataSet(ReportData reportData, String dataSetName, Map<String, Object> replacementData) {
		DataSet ds = reportData.getDataSets().get(dataSetName);
		if (ds == null) {
			Object result = replacementData.get(dataSetName);
			if (result != null && result instanceof DataSet) {
				return (DataSet) result;
			}
			throw new RenderingException("Invalid Report Design Configuration.  There is no Data Set named " + dataSetName + " in this Report Definition");
		}
		return ds;
	}
	
	/**
	 * @return a new Map with the original map values cloned and new values inserted as appropriate from the passed DataSetRow
	 */
	public Map<String, Object> getReplacementData(Map<String, Object> replacements, ReportData reportData, ReportDesign design, 
												  String dataSetName, DataSetRow dataSetRow, Integer dataSetRowNum) {
	
		Map<String, Object> newReplacements = new HashMap<String, Object>(replacements);
		newReplacements.putAll(getReplacementData(reportData, design, dataSetName, dataSetRow));
		newReplacements.put(dataSetName + SEPARATOR + ROW_CONTEXT_PREFIX + SEPARATOR + INDEX, dataSetRowNum);
		return newReplacements;
	}

    /**
     * @return a password configured for this spreadsheet, or an empty string if none configured
     */
    public String getPassword(ReportDesign design) {
        return design.getPropertyValue(PASSWORD_PROPERTY, "");
    }

    protected List<Object> getCellValues(Row row) {
		List<Object> cellValues = new ArrayList<Object>();
		if (row != null) {
			for (Iterator<Cell> it = row.cellIterator(); it.hasNext(); ) {
				Cell cell = it.next();

				Object value = ExcelUtil.getCellContents(cell);

				cellValues.add(value);
			}
		}
		return cellValues;
	}
	
	/**
	 * Inner class to encapsulate a sheet that should be rendered
	 */
	public class SheetToAdd {
		
		private Sheet sheet;
		private Integer originalSheetNum;
		private String originalSheetName;
		private Map<String, Object> replacementData;
		
		/**
		 * Default Constructor
		 */
		public SheetToAdd(Sheet sheet, Integer originalSheetNum, String originalSheetName, Map<String, Object> replacementData) {
			this.sheet = sheet;
			this.originalSheetNum = originalSheetNum;
			this.originalSheetName = originalSheetName;
			this.replacementData = replacementData;
		}
		
		/**
		 * @return the sheet
		 */
		public Sheet getSheet() {
			return sheet;
		}
		/**
		 * @param sheet the sheet to set
		 */
		public void setSheet(Sheet sheet) {
			this.sheet = sheet;
		}
		/**
		 * @return the originalSheetNum
		 */
		public Integer getOriginalSheetNum() {
			return originalSheetNum;
		}
		/**
		 * @param originalSheetNum the originalSheetNum to set
		 */
		public void setOriginalSheetNum(Integer originalSheetNum) {
			this.originalSheetNum = originalSheetNum;
		}
		/**
		 * @return the originalSheetName
		 */
		public String getOriginalSheetName() {
			return originalSheetName;
		}
		/**
		 * @param originalSheetName the originalSheetName to set
		 */
		public void setOriginalSheetName(String originalSheetName) {
			this.originalSheetName = originalSheetName;
		}
		/**
		 * @return the replacementData
		 */
		public Map<String, Object> getReplacementData() {
			return replacementData;
		}
		/**
		 * @param replacementData the replacementData to set
		 */
		public void setReplacementData(Map<String, Object> replacementData) {
			this.replacementData = replacementData;
		}
	}
	
	/**
	 * Inner class to encapsulate a row that should be rendered
	 */
	public class RowToAdd {

		private Row rowToClone;
		private List<Object> cellValues;
		private Map<String, Object> replacementData;

		/**
		 * Default Constructor
		 */
		public RowToAdd(Row rowToClone, List<Object> cellValues, Map<String, Object> replacementData) {
			this.rowToClone = rowToClone;
			this.cellValues = cellValues;
			this.replacementData = replacementData;
		}

		/**
		 * @return the row
		 */
		public Row getRowToClone() {
			return rowToClone;
		}

		/**
		 * @param rowToClone the row to set
		 */
		public void setRowToClone(Row rowToClone) {
			this.rowToClone = rowToClone;
		}

		public List<Object> getCellValues() {
			return cellValues;
		}

		public void setCellValues(List<Object> cellValues) {
			this.cellValues = cellValues;
		}

		/**
		 * @return the replacementData
		 */
		public Map<String, Object> getReplacementData() {
			return replacementData;
		}

		/**
		 * @param replacementData the replacementData to set
		 */
		public void setReplacementData(Map<String, Object> replacementData) {
			this.replacementData = replacementData;
		}

		public Object getCellContents(int cellIndex) {
			if (cellIndex < CollectionUtils.size(cellValues)) {
				return cellValues.get(cellIndex);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Inner class to encapsulate a cell that should be cloned
	 */
	public class CellToAdd {
		
		private Cell cellToClone;
		private Object contents;
		private Map<String, Object> replacementData;
		
		/**
		 * Default Constructor
		 */
		public CellToAdd(Cell cellToClone, Object contents, Map<String, Object> replacementData) {
			this.cellToClone = cellToClone;
			this.contents = contents;
			this.replacementData = replacementData;
		}

		/**
		 * @return the cellToClone
		 */
		public Cell getCellToClone() {
			return cellToClone;
		}

		/**
		 * @param cellToClone the cellToClone to set
		 */
		public void setCellToClone(Cell cellToClone) {
			this.cellToClone = cellToClone;
		}

		public Object getContents() {
			return contents;
		}

		public void setContents(Object contents) {
			this.contents = contents;
		}

		/**
		 * @return the replacementData
		 */
		public Map<String, Object> getReplacementData() {
			return replacementData;
		}

		/**
		 * @param replacementData the replacementData to set
		 */
		public void setReplacementData(Map<String, Object> replacementData) {
			this.replacementData = replacementData;
		}
	}
}
