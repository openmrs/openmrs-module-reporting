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
