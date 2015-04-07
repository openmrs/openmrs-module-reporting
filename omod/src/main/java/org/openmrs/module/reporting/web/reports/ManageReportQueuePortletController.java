package org.openmrs.module.reporting.web.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.controller.portlet.ReportingPortletController;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the manage report queue page
 */
@Controller
@Order(50)
public class ManageReportQueuePortletController extends ReportingPortletController {
	
	@RequestMapping(value = "/module/reporting/portlets/manageReportQueue")
	public void showReportRequests() {
		//this method is just to get the controller registered for the portlet's url.
		//The model data is added from populateModel() method below
	}
	
	/**
	 * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
	 *      java.util.Map)
	 */
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		super.populateModel(request, model);
		ReportService rs = Context.getService(ReportService.class);
		List<ReportRequest> reportRequets = Context.getService(ReportService.class).getReportRequests(null, null, null,
		    Status.REQUESTED, Status.PROCESSING);
		
		//loop over all the queued reports and get their positions in the queue
		HashMap<Integer, Integer> reportPositionMap = new HashMap<Integer, Integer>(reportRequets.size());
		for (ReportRequest reportRequest : reportRequets) {
			if (reportRequest.getStatus() == Status.REQUESTED)
				reportPositionMap.put(reportRequest.getId(), getQueuePosition(reportRequest, rs));
		}
		
		model.put("reportRequests", reportRequets);
		model.put("reportPositionMap", reportPositionMap);
	}
	
	/**
	 * Utility method that extracts the position of a queued report request from its log file
	 * contents
	 * 
	 * @param reportRequest the {@link ReportRequest} object
	 * @param rs {@link ReportService} object
	 * @return the position of the report in the queue
	 */
	private Integer getQueuePosition(ReportRequest reportRequest, ReportService rs) {
		List<String> logs = rs.loadReportLog(reportRequest);
		Integer position = null;
		if (logs != null) {
			for (String log : logs) {
				log = log.trim();
				if (log.indexOf("position") > -1) {
					String[] tokens = log.split(" ");
					try {
						position = Integer.valueOf(tokens[tokens.length - 1]);
					}
					catch (NumberFormatException e) {
						//ignore
					}
				}
			}
		}
		return position;
	}
}
