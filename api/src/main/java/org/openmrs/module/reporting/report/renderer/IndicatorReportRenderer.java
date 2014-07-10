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

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
//@Handler
@Localized("reporting.IndicatorReportRenderer")
public class IndicatorReportRenderer extends ReportDesignRenderer {

	/**
     * @see ReportRenderer#getRenderedContentType(org.openmrs.module.reporting.report.ReportRequest)
     * @param request
     */
    public String getRenderedContentType(ReportRequest request) {
    	return "text/html";
    }

	/**
	 * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
	 */
    @Override
	public String getFilename(ReportRequest request) {
		return getFilenameBase(request) + ".html";
	}

	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new OutputStreamWriter(out,"UTF-8");

		Map<String, Object> parameterValues = results.getContext().getParameterValues();

		
		// For each dataset in the report
		for (String dataSetKey : results.getDataSets().keySet()) {
			DataSet dataset = results.getDataSets().get(dataSetKey);

			
			//MapDataSet mapDataSet = (MapDataSet) dataset;
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();
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
				w.write("<td>"+column.getName()+"</td>");
				w.write("<td>"+column.getLabel()+"</td>");
								
				// Wondering if you can even do this ... iterate over a dataset multiple times (once for each column?)
				// If not, then we need to get the actual dataset data (i.e. MapDataSet).
				for (DataSetRow row : dataset) {
					Object cellValue = row.getColumnValue(column.getName());	
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
