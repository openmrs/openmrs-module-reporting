/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.renderers;

import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.RenderingException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

/**
 * An abstract Web Renderer implementation that stubs all render methods.
 */
public abstract class AbstractWebReportRenderer implements WebReportRenderer {
		
	/**
     * @see org.openmrs.module.reporting.report.renderer.ReportRenderer#getRenderedContentType(org.openmrs.module.reporting.report.ReportRequest)
     */
    @Override
    public String getRenderedContentType(ReportRequest request) {
    	return "text/html";
    }

	/**
     * @see org.openmrs.module.reporting.report.renderer.ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
	 */
    @Override
	public String getFilename(ReportRequest request) {
		return request.getReportDefinition().getParameterizable().getName() + ".html";
	}

	public List<String> getDisplayColumns() { return null; }	
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {}
	public void render(ReportData reportData, OutputStream out) throws IOException, RenderingException {}
	public void render(ReportData reportData, Writer writer) throws IOException, RenderingException {}
	public void render(ReportData reportData, String argument, Writer writer)throws IOException, RenderingException {}
	public void setDisplayColumns(List<String> displayColumns) {}
}
