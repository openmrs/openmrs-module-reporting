package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;


public class ReportHistoryPortletController extends ReportingPortletController {

	/**
     * @see org.openmrs.module.reporting.web.controller.portlet.ReportingPortletController#populateModel(javax.servlet.http.HttpServletRequest, java.util.Map)
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
	    super.populateModel(request, model);
	    ReportService rs = Context.getService(ReportService.class);
	    Status[] statuses = new Status[] {Status.COMPLETED, Status.FAILED, Status.SAVED};
	    if ("false".equals(model.get("includeSaved"))) {
	    	statuses = new Status[] {Status.COMPLETED, Status.FAILED};
	    }
		List<ReportRequest> complete = rs.getReportRequests(null, null, null, statuses);
		Collections.reverse(complete);
		model.put("completedRequests", complete);
		
		Map<ReportRequest, String> shortNames = new HashMap<ReportRequest, String>();
		Map<ReportRequest, Boolean> isWebRenderer = new HashMap<ReportRequest, Boolean>();
		for (ReportRequest r : complete) {
			if (r.getRenderingMode() != null && r.getRenderingMode().getRenderer() != null
					&& r.getRenderingMode().getRenderer() instanceof WebReportRenderer) {
				shortNames.put(r, "Web");
				isWebRenderer.put(r, true);
			} else {
				try {
					String filename = r.getRenderingMode().getRenderer().getFilename(r.getReportDefinition().getParameterizable(),
					    r.getRenderingMode().getArgument());
					filename = filename.substring(filename.lastIndexOf('.') + 1);
					filename = filename.toUpperCase();
					shortNames.put(r, filename);
					isWebRenderer.put(r, false);
				}
				catch (Exception ex) {
					log.info("Error getting filename for ReportRequest " + r.getUuid() + " " , ex);
				}
			}
		}
		model.put("shortNames", shortNames);
		model.put("isWebRenderer", isWebRenderer);
    }
    
}
