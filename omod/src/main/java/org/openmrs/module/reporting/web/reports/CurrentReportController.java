package org.openmrs.module.reporting.web.reports;

import org.openmrs.module.reporting.ReportingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

/**
 * Controllers that deal with the current report in the session. E.g. discard, save.
 */
@Controller
public class CurrentReportController {

	@RequestMapping("/module/reporting/run/currentReportDiscard")
	public String discardFromSession(WebRequest request) {
		request.removeAttribute(ReportingConstants.OPENMRS_REPORT_DATA, WebRequest.SCOPE_SESSION);
		request.removeAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT, WebRequest.SCOPE_SESSION);
		request.removeAttribute(ReportingConstants.OPENMRS_LAST_REPORT_URL, WebRequest.SCOPE_SESSION);
		return "redirect:../dashboard/index.form";
	}
	
}
