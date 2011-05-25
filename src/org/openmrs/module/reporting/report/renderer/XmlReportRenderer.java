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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * ReportRenderer that renders to a delimited text file
 */
@Handler
@Localized("reporting.XmlReportRenderer")
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
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer w = new OutputStreamWriter(out,"UTF-8");
		
		DataSet dataset = results.getDataSets().values().iterator().next();	
		List<DataSetColumn> columns = dataset.getMetaData().getColumns();
		
		w.write("<?xml version=\"1.0\"?>\n");
		w.write("<dataset>\n");
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
						w.write(colValue.toString());
					}
				}
				w.write("</" + column.getLabel() + ">");
			}
			w.write("</row>\n");
		}		
		w.write("\t</rows>\n");
		w.write("</dataset>\n");
		w.flush();
	}

	public String getRenderedContentType(ReportDefinition schema,
			String argument) {
		return "text/xml";
	}
}
