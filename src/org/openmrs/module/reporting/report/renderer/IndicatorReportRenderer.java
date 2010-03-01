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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDefinition;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
//@Handler
//@DisplayLabel(labelDefault="Indicator Report")
public class IndicatorReportRenderer extends AbstractReportRenderer {

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
		return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MAX_VALUE - 1));
	}

	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new PrintWriter(out);

		
		Map<String, Object> parameterValues = results.getContext().getParameterValues();

		
		// For each dataset in the report
		for (String dataSetKey : results.getDataSets().keySet()) {
			DataSet dataset = results.getDataSets().get(dataSetKey);

			
			//MapDataSet mapDataSet = (MapDataSet) dataset;
			List<DataSetColumn> columns = dataset.getDefinition().getColumns();
			w.write("<h1>" + results.getDefinition().getName() + "</h1>");			
			w.write("<span>" + results.getDefinition().getDescription() + "</span>");			
			w.write("<ul>");
			for (String key : parameterValues.keySet()) { 
				String value = "";
				Object object = parameterValues.get(key);
				if (object instanceof Date) { 
					value = Context.getDateFormat().format((Date)object);
				} 
				else { 
					value = object.toString();
				}		
				w.write("<li>" + key + ": <strong>" + value + "</strong></li>");								
			}
			w.write("</ul>");
			w.write("<table id=\"indicator-report-dataset-" + dataSetKey +"\" class=\"display indicator-report-dataset\">");
			for (DataSetColumn column : columns) {
				w.write("<tr>");
				w.write("<td>"+column.getColumnKey()+"</td>");
				w.write("<td>"+column.getDisplayName()+"</td>");
								
				// Wondering if you can even do this ... iterate over a dataset multiple times (once for each column?)
				// If not, then we need to get the actual dataset data (i.e. MapDataSet).
				for (DataSetRow row : dataset) {
					Object cellValue = row.getColumnValue(column.getColumnKey());	
					if (cellValue instanceof CohortIndicatorAndDimensionResult) { 
						CohortIndicatorAndDimensionResult result = (CohortIndicatorAndDimensionResult) cellValue;
						w.write("<td>" + ((cellValue != null) ? result.getValue() : "n/a") + "</td>");					
					}
					else { 
						w.write("<td>" + ((cellValue != null) ? cellValue : "n/a") + "</td>");					
					}
				}
				w.write("</tr>");
			}
			w.write("</table>");
		}		
		w.flush();
	}
	
}
