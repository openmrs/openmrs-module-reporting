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
