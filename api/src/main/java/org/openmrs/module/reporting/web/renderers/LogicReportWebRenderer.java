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
