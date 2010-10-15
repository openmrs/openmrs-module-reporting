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
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * A renderer that displays an indicator report in an interactive fashion
 */
@Handler
public class IndicatorReportWebRenderer extends AbstractWebReportRenderer {

	/**
     * @see org.openmrs.module.reporting.report.renderer.ReportRenderer#canRender(org.openmrs.module.reporting.report.definition.ReportDefinition)
     */
    public boolean canRender(ReportDefinition reportDefinition) {
    	for (Mapped<? extends DataSetDefinition> def : reportDefinition.getDataSetDefinitions().values()) {
	    	if (def.getParameterizable() instanceof CohortIndicatorDataSetDefinition) {
	    		return true;
	    	}
	    	if (def.getParameterizable() instanceof CohortCrossTabDataSetDefinition) {
	    		return true;
	    	}	    	
	    }
	    return false;
    }

	/**
     * @see org.openmrs.report.ReportRenderer#getLabel()
     */
    public String getLabel() {
    	return "Indicator Web Report";
    }

	/**
	 * @see org.openmrs.report.ReportRenderer#getLinkUrl(org.openmrs.report.ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition reportDefinition) {
		return "module/reporting/reports/renderIndicatorReportData.form";
	}
		
	/**
	 * @see org.openmrs.report.ReportRenderer#getRenderingModes(org.openmrs.report.ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition schema) {
		return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MAX_VALUE - 10));
	}
}
