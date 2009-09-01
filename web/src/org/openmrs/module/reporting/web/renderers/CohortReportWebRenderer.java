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
package org.openmrs.module.reporting.web.renderers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.RenderingException;
import org.openmrs.module.report.renderer.RenderingMode;

/**
 * A ReportRenderer that provides a dynamic web view of a CohortDataSet. This renderer can only
 * handle reports with a single data set, that's a cohort data set.
 */
public class CohortReportWebRenderer implements WebReportRenderer {
	
	public CohortReportWebRenderer() { }
	
	/**
	 * 
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition reportDefinition) {
		if (reportDefinition.getDataSetDefinitions().size() == 1) { 
			Mapped<? extends DataSetDefinition> mapped = reportDefinition.getDataSetDefinitions().get(0);
			if (mapped.getParameterizable() instanceof CohortDataSetDefinition) {
				return Collections.singleton(new RenderingMode(this, this.getLabel(), null, 100));
			}
		} 
		return null;
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getLabel(org.openmrs.report.ReportSchema)
	 */
	public String getLabel() {
		return "Cohort report web preview";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getLinkUrl(org.openmrs.report.ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition reportDefinition) {
		return "admin/reports/reportData.form";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderedContentType(org.openmrs.report.ReportDefinition)
	 */
	public String getRenderedContentType(ReportDefinition reportDefinition, String argument) {
		return "text/html";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getFilename(org.openmrs.report.ReportSchema)
	 */
	public String getFilename(ReportDefinition reportDefinition, String argument) {
		return null;
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#render(org.openmrs.report.ReportData,
	 *      java.io.OutputStream)
	 */
	public void render(ReportData reportData, String argument, OutputStream out) {
		// Do nothing. This renderer returns a value from getLinkUrl() 
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#render(org.openmrs.report.ReportData,
	 *      java.lang.String, java.io.Writer)
	 */
	public void render(ReportData reportData, String argument, Writer writer) throws IOException {
		// Do nothing.  This renderer returns a value from getLinkUrl()		
	}

	public String escape(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getDisplayColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	public void render(ReportData reportData, OutputStream out)
			throws IOException, RenderingException {
		// TODO Auto-generated method stub
		
	}

	public void render(ReportData reportData, Writer writer)
			throws IOException, RenderingException {
		// TODO Auto-generated method stub
		
	}

	public void setDisplayColumns(List<String> displayColumns) {
		// TODO Auto-generated method stub
		
	}
	
}
