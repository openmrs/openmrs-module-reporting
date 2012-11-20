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
