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

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * ReportRenderer that renders to a default XML format
 */
@Handler
@Localized("reporting.XmlReportRenderer")
public class XmlReportRenderer extends ReportDesignRenderer {
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition reportDefinition, String argument) {
		return reportDefinition.getName() + ".xml";
	}
	
	/**
	 * @see ReportRenderer#getRenderedContentType(ReportDefinition, String)
	 */
	public String getRenderedContentType(ReportDefinition schema, String argument) {
		return "text/xml";
	}
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new OutputStreamWriter(out, "UTF-8");

		w.write("<?xml version=\"1.0\"?>\n");
		for (String dsKey : results.getDataSets().keySet()) {
			DataSet dataset = results.getDataSets().get(dsKey);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();
			w.write("<dataset name=\"" + dsKey + "\">\n");
			w.write("\t<rows>\n");
			for (DataSetRow row : dataset) {		
				w.write("\t\t<row>");
				for (DataSetColumn column : columns) {			
					Object colValue = row.getColumnValue(column);
					w.write("<" + column.getLabel() + ">");
					if (colValue != null) { 
						if (colValue instanceof Cohort) {
							w.write(Integer.toString(((Cohort) colValue).size()));
						} 
						else {
							StringBuilder sb = new StringBuilder();
							new ObjectUtil().printObject(sb, colValue);
							String value = sb.toString();
							w.write(value);
						}
					}
					w.write("</" + column.getLabel() + ">");
				}
				w.write("</row>\n");
			}
		}
		w.write("\t</rows>\n");
		w.write("</dataset>\n");
		w.flush();
	}
}
