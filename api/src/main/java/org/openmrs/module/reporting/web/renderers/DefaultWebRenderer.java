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
