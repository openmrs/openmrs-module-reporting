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
package org.openmrs.module.reporting.web.controller.mapping.renderers;

import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;


/**
 * Handler that determines what pages are redirected for creating and editing ReportDesign
 */
public abstract class RendererMappingHandler {

	/**
	 * @return The URL for viewing an existing ReportDesign
	 */
	public String getViewUrl(ReportDesign reportDesign) {
		return getEditUrl(reportDesign);
	}
	
	/**
	 * @return The URL for editing an existing ReportDesign
	 */
	public String getEditUrl(ReportDesign reportDesign) {
		String baseUrl = getCreateUrl( reportDesign.getRendererType() );
		return baseUrl + (baseUrl.indexOf("?") != -1 ? "&" : "?" ) + "reportDesignUuid=" + reportDesign.getUuid();
	}
	
	/**
	 * @return The URL for creating a new ReportDesign
	 */
	public abstract String getCreateUrl( Class<? extends ReportRenderer> rendererType );
}
