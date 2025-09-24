/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.controller.portlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.processor.ReportProcessor;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * This Controller loads a ReportDesign for editing
 */
public class ReportProcessorPortletController extends ReportingPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		// TODO: Figure out why this is necessary.
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		model.put("portletUUID", UUID.randomUUID().toString().replace("-", ""));

		// Get the uuid of the reportDesign, if supplied
		ReportService rs = Context.getService(ReportService.class);
		String processorUuid = (String)model.get("processorUuid");
		ReportProcessorConfiguration reportProcessorConfiguration = new ReportProcessorConfiguration();
		if (StringUtils.isNotEmpty(processorUuid)) {
			reportProcessorConfiguration = rs.getReportProcessorConfigurationByUuid(processorUuid);
		}
		model.put("reportProcessorConfiguration", reportProcessorConfiguration);
		
		List<Class<? extends ReportProcessor>> processorTypes = new ArrayList<Class<? extends ReportProcessor>>();
		for (ReportProcessor p : Context.getRegisteredComponents(ReportProcessor.class)) {
			processorTypes.add(p.getClass());
		}
		model.put("reportProcessorTypes", processorTypes);
		model.put("reportDesigns", rs.getAllReportDesigns(false));
	}
}
