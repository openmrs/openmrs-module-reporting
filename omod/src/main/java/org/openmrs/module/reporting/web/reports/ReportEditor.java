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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportEditor {
	
	protected static Log log = LogFactory.getLog(ReportEditor.class);
	
	/**
	 * Default Constructor
	 */
	public ReportEditor() {
	}
	
	/**
	 * Retrieves either an existing or new report to edit
	 */
	@RequestMapping("/module/reporting/reports/reportEditor")
	public void editReport(ModelMap model, @RequestParam(required = false, value = "uuid") String uuid,
	                       @RequestParam(required = false, value = "type") Class<? extends ReportDefinition> type) {
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportDefinition r = rs.getDefinition(uuid, type);
		model.addAttribute("report", r);
		if (StringUtils.isNotEmpty(uuid)) {
			List<ReportDesign> designs = Context.getService(ReportService.class).getReportDesigns(r, null, false);
			model.addAttribute("designs", designs);
		}
	}
}
