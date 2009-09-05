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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.ReportDesign;
import org.openmrs.module.report.service.ReportService;

/**
 * Renderers which use design files to influence the rendered report output
 * should extend this base renderer class
 */
public abstract class ReportDesignRenderer extends AbstractReportRenderer  {

	/** 
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		List<RenderingMode> ret = new ArrayList<RenderingMode>();
		List<ReportDesign> designs = Context.getService(ReportService.class).getReportDesigns(definition, null, false);
		for (ReportDesign d : designs) {
			ret.add(new RenderingMode(this, d.getName(), d.getUuid(), 100));
		}
		return ret;
	}

	/** 
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 * For this renderer, argument represents the uuid of the chosen ReportDesign instance
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		ReportDesign d = Context.getService(ReportService.class).getReportDesignByUuid(argument);
		render(reportData, d, out);
	}
	
	/**
	 * This is the primary method that implementations of this class should implement to render a report.
	 * @param reportData the {@link ReportData} to implement
	 * @param design the {@link ReportDesign} to use in the rendering
	 * @param out the OutputStream to output to
	 * @throws IOException
	 * @throws RenderingException
	 */
	public abstract void render(ReportData reportData, ReportDesign design, OutputStream out) throws IOException, RenderingException;
}
