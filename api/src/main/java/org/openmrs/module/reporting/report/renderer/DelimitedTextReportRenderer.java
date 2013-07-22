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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * ReportRenderer that renders to a delimited text file
 */
@Handler
@Localized("reporting.DelimitedTextReportRenderer")
public class DelimitedTextReportRenderer extends ReportDesignRenderer {
	
	transient protected final Log log = LogFactory.getLog(getClass());

	private String filenameExtension;
	private String afterColumnDelimiter;
	
	public DelimitedTextReportRenderer() {}

	public DelimitedTextReportRenderer( String filenameExtension, String afterColumnDelimiter ) {
		this.filenameExtension = filenameExtension;
		this.afterColumnDelimiter = afterColumnDelimiter;
	}

	/**
	 * @return the filename extension for the particular type of delimited file
	 */
	public String getFilenameExtension() {
		return filenameExtension;
	}

	public void setFilenameExtension( String filenameExtension ) {
		this.filenameExtension = filenameExtension;
	}

	
	/**
	 * @return the delimiter that occurs after each column
	 */
	public String getAfterColumnDelimiter() {
		return afterColumnDelimiter;
	}

	public void setAfterColumnDelimiter( String afterColumnDelimiter ) {
		this.afterColumnDelimiter = afterColumnDelimiter;
	}
	
	public String getUrlForm() {
		return "/module/reporting/reports/renderers/delimitedTextReportRenderer.form";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderedContentType(ReportDefinition, String)
	 */
	public String getRenderedContentType(ReportDefinition model, String argument) {
		return "text/" + getFilenameExtension();
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getBeforeColumnDelimiter()
	 */
	public String getBeforeColumnDelimiter() {
		return "\"";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getBeforeRowDelimiter()
	 */
	public String getBeforeRowDelimiter() {
		return "";
	}
	
	/**
	 * @see DelimitedTextReportRenderer#getAfterRowDelimiter()
	 */
	public String getAfterRowDelimiter() {
		return "\n";
	}
	
	/**
	 * Convenience method used to escape a string of text.
	 * 
	 * @param	text 	The text to escape.
	 * @return	The escaped text.
	 */
	public String escape(String text) {
		if (text == null) {
			return null;
		}
		else {
			return text.replaceAll("\"", "\\\"");
		}
	}			
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition reportDefinition, String argument) {
		return reportDefinition.getName() + "." + getFilenameExtension();
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new OutputStreamWriter(out,"UTF-8");
		DataSet dataset = results.getDataSets().values().iterator().next();
		
		List<DataSetColumn> columns = dataset.getMetaData().getColumns();
		
		// header row
		w.write(getBeforeRowDelimiter());
		for (DataSetColumn column : columns) {
			w.write(getBeforeColumnDelimiter());
			w.write(escape(column.getName()));
			w.write(getAfterColumnDelimiter());
		}
		w.write(getAfterRowDelimiter());
		
		// data rows
		for (DataSetRow row : dataset) {
			w.write(getBeforeRowDelimiter());
			for (DataSetColumn column : columns) {
				Object colValue = row.getColumnValue(column);
				w.write(getBeforeColumnDelimiter());
				if (colValue != null) {
					if (colValue instanceof Cohort) {
						w.write(escape(Integer.toString(((Cohort) colValue).size())));
					} else if (colValue instanceof IndicatorResult) {
						w.write(((IndicatorResult) colValue).getValue().toString());
					}
					else {
						// this check is because a logic EmptyResult .toString() -> null
						String temp = escape(colValue.toString());
						if (temp != null)
							w.write(temp);
					}
				}
				w.write(getAfterColumnDelimiter());
			}
			w.write(getAfterRowDelimiter());
		}
		
		w.flush();
	}
}