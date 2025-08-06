/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
