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

import java.util.Collection;
import java.util.Collections;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

/**
 * A default renderer that should be available to all reports to display an interactive web-based view
 * of the report data.
 */
@Handler
public class DefaultWebRenderer extends AbstractWebReportRenderer {

	/**
     * @see ReportRenderer#canRender(ReportDefinition)
     */
    public boolean canRender(ReportDefinition reportDefinition) {
    	return true;
    }

	/**
	 * Return a user friendly display label
     */
    public String getLabel() {
    	return MessageUtil.translate("reporting.DefaultWebRenderer");
    }

	/**
	 * @see WebReportRenderer#getLinkUrl(ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition reportDefinition) {
		return "module/reporting/reports/renderDefaultReport.form";
	}
		
	/**
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition reportDefinition) {
		return Collections.singleton(new RenderingMode(this, this.getLabel(), null, 1000));
	}
}
