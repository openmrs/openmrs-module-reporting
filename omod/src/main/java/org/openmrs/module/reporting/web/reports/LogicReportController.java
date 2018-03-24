/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
 package org.openmrs.module.reporting.web.reports;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is basically a shortcut to create a report definition with a single
 * {@link LogicDataSetDefinition}
 */
@Controller
public class LogicReportController {
	
	@RequestMapping("/module/reporting/reports/logicReport")
	public String showFormOrRedirect(@RequestParam(required=false, value="uuid") String uuid) {
		if (uuid != null) {
			// TODO find the logic dataset in this report, and redirect to its edit page
			throw new RuntimeException("Not Yet Implemented");
		}
		return null; // show the default view
	}
	
	@RequestMapping("/module/reporting/reports/logicReportCreate")
	public String createLogicReport(@RequestParam("name") String name,
	                                @RequestParam(required=false, value="description") String description) {
		LogicDataSetDefinition dsd = new LogicDataSetDefinition();
		dsd.setName(name + " (DSD)");
		Context.getService(DataSetDefinitionService.class).saveDefinition(dsd);
		String dsdUuid = dsd.getUuid();
		
		ReportDefinition report = new ReportDefinition();
		report.setName(name);
		report.setDescription(description);
		report.addDataSetDefinition("dataset", dsd, null);
		Context.getService(ReportDefinitionService.class).saveDefinition(report);
		return "redirect:../datasets/logicDataSetEditor.form?uuid=" + dsdUuid;
	}

}
