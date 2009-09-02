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

import org.openmrs.annotation.Handler;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
@Handler
public class IndicatorReportRenderer extends AbstractReportRenderer {
	
		
	/**
     * @see org.openmrs.report.ReportRenderer#getLabel()
     */
    public String getLabel() {
    	return "Indicator Report";
    }

	/**
     * @see org.openmrs.report.ReportRenderer#getRenderedContentType(org.openmrs.report.ReportDefinition, java.lang.String)
     */
    public String getRenderedContentType(ReportDefinition schema, String argument) {
    	return "text/html";
    }

	/**
	 * @see org.openmrs.report.ReportRenderer#getLinkUrl(org.openmrs.report.ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition schema) {
		return null;
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getFilename(org.openmrs.report.ReportDefinition)
	 */
	public String getFilename(ReportDefinition schema, String argument) {
		return schema.getName() + ".html";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderingModes(org.openmrs.report.ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition schema) {
		return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE));
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		render(results, argument, new PrintWriter(out));
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#render(ReportData, String, Writer)
	 */
	@SuppressWarnings("unchecked")
	public void render(ReportData results, String argument, Writer writer) throws IOException, RenderingException {
		
		// For each dataset in the report
		for (String dataSetKey : results.getDataSets().keySet()) {
			DataSet<Object> dataset = results.getDataSets().get(dataSetKey);

			//MapDataSet mapDataSet = (MapDataSet) dataset;
			List<DataSetColumn> columns = dataset.getDefinition().getColumns();
			writer.write("<h4>" + dataSetKey + "</h4>");			
			writer.write("<table id=\"indicator-report-dataset-" + dataSetKey +"\" class=\"display indicator-report-dataset\">");
			for (DataSetColumn column : columns) {
				writer.write("<tr>");
				writer.write("<td>"+column.getColumnKey()+"</td>");
				writer.write("<td>"+column.getDisplayName()+"</td>");
								
				// Wondering if you can even do this ... iterate over a dataset multiple times (once for each column?)
				// If not, then we need to get the actual dataset data (i.e. MapDataSet).
				for (Iterator<DataSetRow<Object>> i = dataset.iterator(); i.hasNext();) {	
				//for (Iterator<Map<DataSetColumn, Object>> i = dataset.iterator(); i.hasNext();) {
					DataSetRow<Object> row = i.next();

					Object cellValue = row.getColumnValue(column.getColumnKey());				
					writer.write("<td><a href=\"#\">" + ((cellValue != null) ? cellValue : "n/a") + "</a></td>");					
				}
				writer.write("</tr>");
			}
			writer.write("</table>");
		}		
		writer.flush();
	}
	
}
