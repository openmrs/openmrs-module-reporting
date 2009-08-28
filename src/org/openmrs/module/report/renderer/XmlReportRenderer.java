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

import org.openmrs.Cohort;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;

/**
 * ReportRenderer that renders to a delimited text file
 */
public class XmlReportRenderer extends AbstractReportRenderer {
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition reportDefinition, String argument) {
		return reportDefinition.getName() + ".xml";
	}
	
	/**
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition reportDefinition) {
		if (reportDefinition.getDataSetDefinitions() == null || reportDefinition.getDataSetDefinitions().size() != 1) {
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
	public void render(ReportData results, String argument, Writer xmlWriter) throws IOException, RenderingException {
		
		@SuppressWarnings("unchecked")
		DataSet<Object> dataset = results.getDataSets().values().iterator().next();	
		
		List<DataSetColumn> columns = dataset.getDefinition().getColumns();
		xmlWriter.write("<?xml version=\"1.0\"?>\n");
		xmlWriter.write("<dataset>\n");
		xmlWriter.write("\t<rows>\n");
		for (Iterator<DataSetRow<Object>> i = dataset.iterator(); i.hasNext();) {
			xmlWriter.write("\t\t<row>");
			DataSetRow<Object> row = i.next();
			for (DataSetColumn column : columns) {
				
				if (isDisplayColumn(column.getColumnKey())) { 
					Object colValue = row.getColumnValue(column);
					xmlWriter.write("<" + column.getDisplayName() + ">");
					if (colValue != null) { 
						if (colValue instanceof Cohort) {
							xmlWriter.write(escape(Integer.toString(((Cohort) colValue).size())));
						} 
						else {
							xmlWriter.write(escape(colValue.toString()));
						}
					}
					xmlWriter.write("</" + column.getDisplayName() + ">");
				}
			}
			xmlWriter.write("</row>\n");
		}		
		xmlWriter.write("\t</rows>\n");
		xmlWriter.write("</dataset>\n");
		xmlWriter.flush();
	}

	public String getLabel() {
		return "XML";
	}

	public String getRenderedContentType(ReportDefinition schema,
			String argument) {
		return "text/xml";
	}
}
