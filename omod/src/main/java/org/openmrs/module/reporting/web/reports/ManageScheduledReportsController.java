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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple controller for managing all scheduled reports 
 */
@Controller
public class ManageScheduledReportsController {

	/** */
	protected static Log log = LogFactory.getLog(ManageScheduledReportsController.class);
	
	/**
	 * Default c~tor
	 */
	public ManageScheduledReportsController() { }
	
    /**
     * Provide all scheduled reports for working with these reports.
     */
    @RequestMapping("/module/reporting/reports/manageScheduledReports")
    public ModelMap manageReports(ModelMap model) {
    	// getting all currently scheduled reports
    	ReportService reportService = Context.getService(ReportService.class);
		List<ReportRequest> scheduledReportRequests = reportService.getReportRequests(null, null, null, Status.SCHEDULED, Status.SCHEDULE_COMPLETED);
		// getting list of available rendering modes
		List<RenderingMode> renderingModes = new ArrayList<RenderingMode>();
		for (ReportRequest reportRequest : scheduledReportRequests) {
			renderingModes.addAll(reportService.getRenderingModes(reportRequest.getReportDefinition().getParameterizable()));
        }
		model.addAttribute("scheduledReports", scheduledReportRequests);
		model.addAttribute("renderingModes", renderingModes);
        return model;
    }    
}
