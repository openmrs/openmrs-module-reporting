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

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public class RunReportPortletController extends ReportingPortletController {

	/**
     * @see org.openmrs.module.reporting.web.controller.portlet.ReportingPortletController#populateModel(javax.servlet.http.HttpServletRequest, java.util.Map)
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
	    super.populateModel(request, model);
	    if (model.get("reportDefinitions") == null) {
	    	/*
	    	 * if we ever start caching these report definitions across page loads, or else if we
	    	 * speed up deserialization of reports to be instantaneous, it would be better to do
	    	 * the following:
	    	 * 
	    	 * model.put("reportDefinitions", Context.getService(ReportDefinitionService.class).getAllDefinitions(false)); 
	    	 */
	    	model.put("reportDefinitions", Context.getService(ReportDefinitionService.class).getAllDefinitionSummaries(false));
	    }
    }

}
