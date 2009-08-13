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
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;

/**
 * ReportRenderer that renders to a delimited text file
 */
public abstract class DelimitedTextReportRenderer implements ReportRenderer {
	
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
	 * @return an escaped representation of the passed text
	 */
	public abstract String escape(String text);
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition schema, String argument) {
		return schema.getName() + "." + getFilenameExtension();
	}
	
	/**
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition schema) {
		if (schema.getDataSetDefinitions() == null || schema.getDataSetDefinitions().size() != 1) {
			return null;
		}
		else {
			return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE));
		}
	}
	
	/**
	 * @see .ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		render(results, argument, new PrintWriter(out));
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, Writer)
	 */
	public void render(ReportData results, String argument, Writer writer) throws IOException, RenderingException {
		DataSet dataset = results.getDataSets().values().iterator().next();
		
		List<DataSetColumn> columns = 
			dataset.getDataSetDefinition().getColumns();
		
		// header row
		writer.write(getBeforeRowDelimiter());
		for (DataSetColumn column : columns) {
			writer.write(getBeforeColumnDelimiter());
			writer.write(escape(column.getKey()));
			writer.write(getAfterColumnDelimiter());
		}
		writer.write(getAfterRowDelimiter());
		
		// data rows
		for (Iterator<Map<DataSetColumn, Object>> i = dataset.iterator(); i.hasNext();) {
			writer.write(getBeforeRowDelimiter());
			Map<DataSetColumn, Object> map = i.next();
			for (DataSetColumn column : columns) {
				Object colValue = map.get(column);
				writer.write(getBeforeColumnDelimiter());
				if (colValue != null)
					if (colValue instanceof Cohort) {
						writer.write(escape(Integer.toString(((Cohort) colValue).size())));
					} 
					else {
						writer.write(escape(colValue.toString()));
					}
				writer.write(getAfterColumnDelimiter());
			}
			writer.write(getAfterRowDelimiter());
		}
		
		writer.flush();
	}
}
