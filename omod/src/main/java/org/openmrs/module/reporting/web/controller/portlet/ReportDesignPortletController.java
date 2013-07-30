package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.web.WebConstants;

/**
 * This Controller loads a ReportDesign for editing
 */
public class ReportDesignPortletController extends ReportingPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		// TODO: Figure out why this is necessary.
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		model.put("portletUUID", UUID.randomUUID().toString().replace("-", ""));

		// Get the uuid of the reportDesign, if supplied
		ReportService rs = Context.getService(ReportService.class);
		String reportDesignUuid = (String)model.get("reportDesignUuid");
		String reportDefinitionUuid = (String)model.get("reportDefinitionUuid");
		ReportDesign design = null;
		if (StringUtils.isNotEmpty(reportDesignUuid)) {
			design = rs.getReportDesignByUuid(reportDesignUuid);
		}
		else {
			design = new ReportDesign();
			if (StringUtils.isNotEmpty(reportDefinitionUuid)) {
				design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
			}
		}
		
		String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
		String returnUrl = (String) model.get("returnUrl");
    	if (StringUtils.isEmpty(returnUrl)) {
    		returnUrl = "/module/reporting/reports/manageReportDesigns.form";
    	}
    	else if (returnUrl.startsWith(pathToRemove)) {
    		returnUrl = returnUrl.substring(pathToRemove.length());
    	}

		model.put("design", design);
		model.put("successUrl", returnUrl);
		model.put("cancelUrl",  returnUrl);
	}
}
