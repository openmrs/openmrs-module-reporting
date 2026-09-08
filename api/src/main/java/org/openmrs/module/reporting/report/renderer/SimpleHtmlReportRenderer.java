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

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
@Handler
@Localized("reporting.SimpleHtmlReportRenderer")
public class SimpleHtmlReportRenderer extends ReportDesignRenderer {

	/**
     * @see ReportRenderer#getRenderedContentType(org.openmrs.module.reporting.report.ReportRequest)
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
		
		w.write("<html>");
		w.write("<head>");				
		w.write("<body>");
		for (String key : results.getDataSets().keySet()) {
			DataSet dataset = results.getDataSets().get(key);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();
			w.write("<h4>" + key + "</h4>");
			w.write("<table id=\"simple-html-dataset-" + key + "\" class=\"display simple-html-dataset\"><tr>");
			for (DataSetColumn column : columns) {
				w.write("<th>"+column.getName()+"</th>");
			}
			w.write("</tr>");

			for (DataSetRow row : dataset) {
				w.write("<tr>");
				for (DataSetColumn column : columns) {
					w.write("<td>");
					Object colValue = row.getColumnValue(column.getName());
					if (colValue != null) {
						if (colValue instanceof Cohort) {
							w.write(Integer.toString(((Cohort) colValue).size()));
						} else {
							w.write(colValue.toString());
						}
					}
					w.write("</td>");
				}
				w.write("</tr>");
			}
			w.write("</table>");
		}
		w.write("</body>");
		w.write("</head>");		
		w.write("</html>");
		w.flush();
	}
	
}
