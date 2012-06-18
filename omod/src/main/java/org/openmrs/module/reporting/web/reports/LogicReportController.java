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
