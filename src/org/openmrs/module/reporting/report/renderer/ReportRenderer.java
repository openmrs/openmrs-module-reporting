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
import java.util.Collection;

import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDefinition;

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
	 * @param definition The ReportDefinition to render
	 * @param argument Argument from the RenderingMode that the user selected
	 * @return the <code>String</code> representation of the rendered content type
	 */
	public String getRenderedContentType(ReportDefinition definition, String argument);
	
	/**
	 * @param definition
	 * @param argument Argument from the RenderingMode that the user selected
	 * @return Suggested filename to save the rendered report as.
	 */
	public String getFilename(ReportDefinition definition, String argument);

	/**
	 * Render the report's data to a stream
	 * @param reportData Data that was calculated by the Reporting API and service
	 * @param out	the output stream to write report data to
	 * @throws RenderingException
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException;
}
