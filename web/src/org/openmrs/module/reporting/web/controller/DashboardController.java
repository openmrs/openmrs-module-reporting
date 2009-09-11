package org.openmrs.module.reporting.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the Reporting dashboard.
 * (Not much should happen here--portlets should do most of the work.) 
 */
@Controller
public class DashboardController {

	@RequestMapping("/module/reporting/dashboard/index")
	public void showDashboard() {
	}
	
}
