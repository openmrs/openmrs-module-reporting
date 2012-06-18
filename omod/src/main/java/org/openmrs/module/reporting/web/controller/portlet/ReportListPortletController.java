package org.openmrs.module.reporting.web.controller.portlet;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

public class ReportListPortletController extends ReportingPortletController {
	
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		super.populateModel(request, model);
		List<DefinitionSummary> definitionSummaries = Context.getService(ReportDefinitionService.class).getAllDefinitionSummaries(false);
		model.put("definitionSummaries", definitionSummaries);
	}

}
