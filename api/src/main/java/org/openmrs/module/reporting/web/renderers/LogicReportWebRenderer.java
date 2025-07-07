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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

/**
 * Shows a LogicDataSet from a report in an interactive fashion, letting you page through
 * the patients. (If the logic rules are straightforward, this will be fast enough for
 * interactive use.)
 */
@Handler
public class LogicReportWebRenderer extends AbstractWebReportRenderer {
	
	/**
	 * @see ReportRenderer#canRender(ReportDefinition)
	 */
	public boolean canRender(ReportDefinition reportDefinition) {
		// if there's at least one LogicDataSetDefinition we can render
	    for (Mapped<? extends DataSetDefinition> def : reportDefinition.getDataSetDefinitions().values()) {
	    	if (def.getParameterizable() instanceof LogicDataSetDefinition)
	    		return true;
	    }
	    return false;
	}
	
	/**
	 * @see WebReportRenderer#getLinkUrl(ReportDefinition)
	 */
	public String getLinkUrl(ReportDefinition reportDefinition) {
		return "module/reporting/reports/renderLogicDataSet.form";
	}
	
	/**
	 * @see ReportRenderer#getRenderingModes(ReportDefinition)
	 */
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		int numMatching = 0;
		for (Mapped<? extends DataSetDefinition> def : definition.getDataSetDefinitions().values()) {
	    	if (def.getParameterizable() instanceof LogicDataSetDefinition)
	    		++numMatching;
	    }
		List<RenderingMode> ret = new ArrayList<RenderingMode>();
		for (Map.Entry<String, Mapped<? extends DataSetDefinition>> e : definition.getDataSetDefinitions().entrySet()) {
			String name = e.getKey();
			DataSetDefinition def = e.getValue().getParameterizable();
	    	if (def instanceof LogicDataSetDefinition) {
				ret.add(new RenderingMode(this, getLabel(def, numMatching == 1), name, Integer.MAX_VALUE - 10));
	    	}
		}
		return ret;
	}

	private String getLabel(DataSetDefinition def, boolean onlyOne) {
		String ret = "Row-per-patient Web Report";
		if (!onlyOne)
			ret += " (" + def.getName() + ")";
	    return ret;
    }
	
}
