/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.report.renderer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ExcelUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;

/**
 * Report Renderer implementation that supports rendering to an Excel template
 */
@Handler
@Localized("Excel Template")
public class ExcelTemplateRenderer extends ReportTemplateRenderer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public ExcelTemplateRenderer() {
		super();
	}

	/** 
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	@SuppressWarnings("unchecked")
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		
		log.debug("Attempting to render report with ExcelTemplateRenderer");
		InputStream is = null;
		try {
			ReportDesign design = getDesign(argument);
			ReportDesignResource r = getTemplate(design);
			is = new ByteArrayInputStream(r.getContents());
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			ExcelStyleHelper styleHelper = new ExcelStyleHelper(wb);
			log.debug("New Workbook Constructed");
			
			HSSFSheet sheet = wb.getSheetAt(0);
			
			// TODO: Implement more complex logic around multiple sheets for multiple rows / multiple datasets
			if (reportData.getDataSets().size() != 1) {
				throw new RuntimeException("Currently only one dataset is supported.");
			}
			Iterator<Map.Entry<String, DataSet>> datSetEntryIterator = reportData.getDataSets().entrySet().iterator();
			Map.Entry<String, DataSet> dataSetEntry = datSetEntryIterator.next();
			DataSetRow dataSetRow = (DataSetRow)dataSetEntry.getValue().iterator().next();
			if (datSetEntryIterator.hasNext()) {
				throw new RuntimeException("Currently only one dataset with one row is supported.");
			}
			
			Map<String, Object> replacements = getReplacementData(reportData, design, dataSetEntry.getKey(), dataSetRow);
			
			String prefix = getExpressionPrefix(design);
			String postfix = getExpressionPostfix(design);
			
			for (Iterator<HSSFRow> rowIter = sheet.rowIterator(); rowIter.hasNext();) {
				HSSFRow row = rowIter.next();
				for (Iterator<HSSFCell> cellIter = row.cellIterator(); cellIter.hasNext();) {
					HSSFCell cell = cellIter.next();
			    	String contents = ExcelUtil.getCellContentsAsString(cell);
			    	if (StringUtils.isNotEmpty(contents)) {
			    		Object newContent = EvaluationUtil.evaluateExpression(contents, replacements, Object.class, prefix, postfix);
			    		ExcelUtil.setCellContents(styleHelper, cell, newContent);
			    	}
				}
			}
			wb.write(out);
		}
		catch (Exception e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
