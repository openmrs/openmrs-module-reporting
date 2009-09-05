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
package org.openmrs.module.report.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;

/**
 * ReportRenderer that renders to a delimited text file
 */
public abstract class DelimitedTextReportRenderer extends AbstractReportRenderer {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @return the filename extension for the particular type of delimited file
	 */
	public abstract String getFilenameExtension();
	
	/**
	 * @return the delimiter that occurs before each column
	 */
	public abstract String getBeforeColumnDelimiter();
	
	/**
	 * @return the delimiter that occurs after each column
	 */
	public abstract String getAfterColumnDelimiter();
	
	/**
	 * @return the delimiter that occurs before each row
	 */
	public abstract String getBeforeRowDelimiter();
	
	/**
	 * @return the delimiter that occurs after each row
	 */
	public abstract String getAfterRowDelimiter();
	
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
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition reportDefinition) {
		if (reportDefinition.getDataSetDefinitions() == null || reportDefinition.getDataSetDefinitions().size() != 1) {
			return null;
		}
		else {
			return Collections.singleton(new RenderingMode(this, getLabel(), null, Integer.MIN_VALUE));
		}
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new PrintWriter(out);
		@SuppressWarnings("unchecked")
		DataSet<Object> dataset = results.getDataSets().values().iterator().next();
		
		List<DataSetColumn> columns = dataset.getDefinition().getColumns();
		
		// header row
		w.write(getBeforeRowDelimiter());
		for (DataSetColumn column : columns) {
			w.write(getBeforeColumnDelimiter());
			w.write(escape(column.getColumnKey()));
			w.write(getAfterColumnDelimiter());
		}
		w.write(getAfterRowDelimiter());
		
		// data rows
		for (Iterator<DataSetRow<Object>> i = dataset.iterator(); i.hasNext();) {
			w.write(getBeforeRowDelimiter());
			DataSetRow<Object> map = i.next();
			for (DataSetColumn column : columns) {
				Object colValue = map.getColumnValue(column);
				w.write(getBeforeColumnDelimiter());
				if (colValue != null) { 
					if (colValue instanceof Cohort) {
						w.write(escape(Integer.toString(((Cohort) colValue).size())));
					} else if (colValue instanceof IndicatorResult<?>) {
						w.write(Double.toString(((IndicatorResult<?>) colValue).getValue().doubleValue()));
					}
					else {
						w.write(escape(colValue.toString()));
					}
				}
				w.write(getAfterColumnDelimiter());
			}
			w.write(getAfterRowDelimiter());
		}
		
		w.flush();
	}
}
