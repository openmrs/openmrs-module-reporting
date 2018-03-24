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
