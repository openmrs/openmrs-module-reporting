package org.openmrs.module.reporting.web.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.report.ReportRequest;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportHistoryController {

	@RequestMapping("/module/reporting/reports/reportHistory")
	public void showReportHistory(ModelMap model) {
		List<ReportRequest> complete = new ArrayList<ReportRequest>(Context.getService(ReportService.class).getCompletedReportRequests());
		List<ReportRequest> queue = new ArrayList<ReportRequest>(Context.getService(ReportService.class).getQueuedReportRequests());
		Collections.reverse(complete);
		Collections.reverse(queue);
		model.addAttribute("complete", complete);
		model.addAttribute("queue", queue);

		Map<ReportRequest, String> shortNames = new HashMap<ReportRequest, String>();
		for (ReportRequest r : complete) {
			if (r.getRenderingMode().getRenderer() instanceof WebReportRenderer) {
				shortNames.put(r, "Web");
			} else {
				String filename = r.getRenderingMode().getRenderer().
						getFilename(r.getReportDefinition(), r.getRenderingMode().getArgument());
				try {
					filename = filename.substring(filename.lastIndexOf('.') + 1);
					filename = filename.toUpperCase();
				} catch (Exception ex) { }
				shortNames.put(r, filename);
			}
		}
		model.addAttribute("shortNames", shortNames);
	}
	
	@RequestMapping("/module/reporting/reports/reportHistoryDelete")
	public String deleteFromHistory(@RequestParam("uuid") String uuid) {
		Context.getService(ReportService.class).deleteFromHistory(uuid);
		return "redirect:reportHistory.form";
	}
	
	@RequestMapping("/module/reporting/reports/reportHistorySave")
	public String saveHistoryElement(@RequestParam("uuid") String uuid) {
		ReportService service = Context.getService(ReportService.class);
		ReportRequest req = service.getReportRequestByUuid(uuid);
		if (req != null)
			service.archiveReportRequest(req);
		return "redirect:reportHistory.form";
	}
	
	@RequestMapping("/module/reporting/reports/reportHistoryAddLabel")
	public String addLabel(@RequestParam("uuid") String uuid,
	                                 @RequestParam("label") String label) {
		ReportService service = Context.getService(ReportService.class);
		ReportRequest req = service.getReportRequestByUuid(uuid);
		req.addLabel(label);
		service.saveReportRequest(req);
		return "redirect:reportHistory.form";
	}
	
	@RequestMapping("/module/reporting/reports/reportHistoryRemoveLabel")
	public String removeLabel(@RequestParam("uuid") String uuid,
	                                 @RequestParam("label") String label) {
		ReportService service = Context.getService(ReportService.class);
		ReportRequest req = service.getReportRequestByUuid(uuid);
		req.removeLabel(label);
		service.saveReportRequest(req);
		return "redirect:reportHistory.form";
	}

	@RequestMapping("/module/reporting/reports/reportHistoryOpen")
	public String openFromHistory(@RequestParam("uuid") String uuid) {
		throw new RuntimeException("Not Yet Implemented");
	}
	
}
