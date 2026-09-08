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

import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.InteractiveReportRenderer;


/**
 * Renders report schemas for the web. The web renderer can render using the render method, but will
 * most likely be used to redirect to another URL (which, in most cases, delegates to another
 * rendering engine).
 */
public interface WebReportRenderer extends InteractiveReportRenderer {
	
	/**
	 * If this method returns a value, then this renderer should be called by redirecting to that
	 * link, rather than with the render(ReportData, OutputStream) method. In this situation, the
	 * ReportData to be displayed will be passed to that page via the session attribute called
	 * WebConstants.OPENMRS_REPORT_DATA
	 */
	public String getLinkUrl(ReportDefinition reportDefinition);
	
}
