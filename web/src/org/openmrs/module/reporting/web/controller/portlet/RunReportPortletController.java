package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;


public class RunReportPortletController extends ReportingPortletController {

	/**
     * @see org.openmrs.module.reporting.web.controller.portlet.ReportingPortletController#populateModel(javax.servlet.http.HttpServletRequest, java.util.Map)
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
	    super.populateModel(request, model);
	    if (model.get("reportDefinitions") == null) {
	    	model.put("reportDefinitions", Context.getService(ReportService.class).getReportDefinitions());
	    }

	    if (model.get("lastReportRuns") == null) {
	    	model.put("lastReportRuns", Context.getService(ReportService.class).getLastReportRequestsByReport());
	    }
    }

}
