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
package org.openmrs.module.reporting.web.dwr;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.service.ReportService;

public class DWRReportingService {
	
	/**
	 * Processes requests to cancel a queued {@link ReportRequest}
	 * 
	 * @param id the id of the {@link ReportRequest} to purge
	 * @return true if the request was successfully purged otherwise false
	 */
	public boolean purgeReportRequest(Integer id) {
		if (id != null) {
			ReportService rs = Context.getService(ReportService.class);
			ReportRequest request = rs.getReportRequest(id);
			if (request != null) {
				try {
					rs.purgeReportRequest(request);
					return true;
				}
				catch (Exception e) {
					//
				}
			}
		}
		return false;
	}
}
