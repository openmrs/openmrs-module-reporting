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
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.RenderingMode;

/**
 * A Default Renderer Implementation that aims to support all ReportDefinitions
 */
@Handler
public class IndicatorReportWebRenderer extends AbstractWebReportRenderer {
	
	/**
     * @see org.openmrs.report.ReportRenderer#getLabel()
     */
    public String getLabel() {
    	return "Indicator Web Report";
    }

	/**
     * @see org.openmrs.report.ReportRenderer#getRenderedContentType(org.openmrs.report.ReportDefinition, java.lang.String)
     */
    public String getRenderedContentType(ReportDefinition reportDefinition, String argument) {
    	return "text/html";
    }

	/**
	 * @see org.openmrs.report.ReportRenderer#getLinkUrl(org.openmrs.report.ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition reportDefinition) {
		return "module/reporting/reports/renderIndicatorReportData.form";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getFilename(org.openmrs.report.ReportDefinition)
	 */
	public String getFilename(ReportDefinition schema, String argument) {
		return schema.getName() + ".html";
	}
	
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderingModes(org.openmrs.report.ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition schema) {
		return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE));
	}
}
