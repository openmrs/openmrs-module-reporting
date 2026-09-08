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

import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Takes a ReportDefinition and renders it.
 */
public interface ReportRenderer {
	
	/**
	 * @return	whether the render implementation can handle the given report definition
	 */
	public boolean canRender(ReportDefinition reportDefinition);
	
	/**
	 * Returns the {@link RenderingMode}s in which this report definition could be rendered.
	 * @param definition - The {@link ReportDefinition} to check
	 * @return a <code>Collection<RenderingMode></code> of all modes in which the given ReportDefinition can be rendered
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition);
	
	/**
	 * The content-type that will be rendered
	 * @param request The ReportRequest to render
	 * @return the <code>String</code> representation of the rendered content type
	 */
	public String getRenderedContentType(ReportRequest request);
	
	/**
	 * @param request
	 * @return Suggested filename to save the rendered report as.
	 */
	public String getFilename(ReportRequest request);

	/**
	 * Render the report's data to a stream
	 * @param reportData Data that was calculated by the Reporting API and service
	 * @param out	the output stream to write report data to
	 * @throws RenderingException
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException;
}
