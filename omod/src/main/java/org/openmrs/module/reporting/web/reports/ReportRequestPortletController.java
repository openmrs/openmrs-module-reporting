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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.PriorityComparator;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.controller.portlet.ReportingPortletController;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.web.controller.PortletController;

/**
 * Controller behind portlets for viewing Report Requests
 */
public class ReportRequestPortletController extends ReportingPortletController {

	/** */
	protected static Log log = LogFactory.getLog(ReportRequestPortletController.class);
	
	/**
	 * Default Constructor
	 */
	public ReportRequestPortletController() { }
	
	/**
	 * @see PortletController#populateModel(HttpServletRequest, Map)
	 */
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		ReportService rs = Context.getService(ReportService.class);
		
		String reportIdParam = (String) model.get("reportId");
		String statusParam = (String) model.get("status");
		String mostRecentNumParam = (String) model.get("mostRecentNum");
		
		ReportDefinition reportDefinition = null;
		if (ObjectUtil.notNull(reportIdParam)) {
			reportDefinition = rds.getDefinition(Integer.parseInt(reportIdParam));
		}
		
		Status[] statuses = null;
		if (ObjectUtil.notNull(statusParam)) {
			String[] statusNames = statusParam.split(",");
			statuses = new Status[statusNames.length];
			for (int i=0; i<statusNames.length; i++) {
				statuses[i] = Status.valueOf(statusNames[i]);
			}
		}

		Integer mostRecentNum = null;
		if (ObjectUtil.notNull(mostRecentNumParam)) {
			mostRecentNum = Integer.valueOf(mostRecentNumParam);
			if (mostRecentNum == 0) {
				mostRecentNum = null;
			}
		}
		
		List<ReportRequest> requests = rs.getReportRequests(reportDefinition, null, null, mostRecentNum, statuses);
		model.put("requests", requests);
    }    
}
