package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.service.ReportService;


public class QueuedReportsPortletController extends ReportingPortletController {
	
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		ReportService service = Context.getService(ReportService.class);
		model.put("queue", service.getQueuedReportRequests());
		model.put("inProgress", service.getInProgress());
		model.put("any", service.getQueuedReportRequests().size() > 0 || service.getInProgress().size() > 0);
	}

}
