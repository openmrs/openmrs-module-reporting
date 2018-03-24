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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.IndicatorReportRenderer;

/**
 * Handler that determines what page is used to edit/create a new IndicatorReportRenderer
 */

@Handler(supports=IndicatorReportRenderer.class, order=50)
public class IndicatorReportRendererMappingHandler extends RendererMappingHandler {

	/**
	 * @see RendererMappingHandler#getCreateUrl(Class)
	 */
	public String getCreateUrl( Class<? extends ReportRenderer> rendererType ) {
		return "/module/reporting/reports/renderers/nonConfigurableReportRenderer.form?type=" + rendererType.getName();
	}
	
}
