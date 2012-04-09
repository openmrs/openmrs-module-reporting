package org.openmrs.module.reporting.web.reports;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.module.reporting.web.util.AjaxUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ReportHistoryController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping("/module/reporting/reports/reportHistory")
	public void showReportHistory(ModelMap model) {
		List<ReportRequest> complete = getReportService().getReportRequests(null, null, null, Status.COMPLETED, Status.FAILED, Status.SAVED);
		List<ReportRequest> queue = getReportService().getReportRequests(null, null, null, Status.REQUESTED);
		Collections.reverse(complete);
		Collections.reverse(queue);
		model.addAttribute("complete", complete);
		model.addAttribute("queue", queue);
		model.addAttribute("cached", getReportService().getCachedReports().keySet());
		
		Map<ReportRequest, String> shortNames = new HashMap<ReportRequest, String>();
		Map<ReportRequest, Boolean> isWebRenderer = new HashMap<ReportRequest, Boolean>();
		for (ReportRequest r : complete) {
			if (r.getRenderingMode().getRenderer() instanceof WebReportRenderer) {
				shortNames.put(r, "Web");
				isWebRenderer.put(r, true);
			} else {
				String filename = r.getRenderingMode().getRenderer().getFilename(r.getReportDefinition().getParameterizable(),
				    r.getRenderingMode().getArgument());
				try {
					filename = filename.substring(filename.lastIndexOf('.') + 1);
					filename = filename.toUpperCase();
				}
				catch (Exception ex) {}
				shortNames.put(r, filename);
				isWebRenderer.put(r, false);
			}
		}
		model.addAttribute("shortNames", shortNames);
		model.addAttribute("isWebRenderer", isWebRenderer);
	}
	
	@RequestMapping("/module/reporting/reports/reportHistoryDelete")
	public String deleteFromHistory(@RequestParam("uuid") String uuid) {
		ReportService rs = Context.getService(ReportService.class);
		ReportRequest request = rs.getReportRequestByUuid(uuid);
		rs.purgeReportRequest(request);
		return "redirect:reportHistory.form";
	}
	
	@RequestMapping("/module/reporting/reports/deleteReportRequest")
	public String deleteReportRequest(@RequestParam("uuid") String uuid,
									@RequestParam("returnUrl") String returnUrl) {
		ReportService rs = Context.getService(ReportService.class);
		ReportRequest request = rs.getReportRequestByUuid(uuid);
		rs.purgeReportRequest(request);
		return "redirect:" + returnUrl;
	}
	
	@RequestMapping("/module/reporting/reports/loadReportStatus")
	public String loadReportStatus(ModelMap model, @RequestParam("uuid") String uuid) {
		ReportService rs = Context.getService(ReportService.class);
		ReportRequest request = rs.getReportRequestByUuid(uuid);
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("logEntries", rs.loadReportLog(request));
		statusMap.put("status", request.getStatus().toString());
		
		Report cachedReport = rs.getCachedReports().get(request);
		
		if (request.getStatus() == Status.COMPLETED || request.getStatus() == Status.SAVED) {
			File reportOutputFile = rs.getReportOutputFile(request);
			if (reportOutputFile.exists() || (cachedReport != null && cachedReport.getRenderedOutput() != null)) {
				statusMap.put("action", "download");
			}
			File reportDataFile = rs.getReportDataFile(request);
			if (request.getRenderingMode().getRenderer() instanceof WebReportRenderer) {
				if (reportDataFile.exists() || (cachedReport != null && cachedReport.getReportData() != null)) {
					statusMap.put("action", "view");
				}
			}
		}
		
		if (request.getStatus() == Status.FAILED) {
			File reportErrorFile = rs.getReportErrorFile(request);
			if (reportErrorFile.exists() || (cachedReport != null && cachedReport.getErrorMessage() != null)) {
				String errorDetails = rs.loadReportError(request);
				statusMap.put("errorDetails", errorDetails);
			}
		}
		
		model.addAttribute("json", AjaxUtil.toJson(statusMap));
		return "/module/reporting/json";
	}
	
	@RequestMapping("/module/reporting/reports/reportHistorySave")
	public String saveHistoryElement(@RequestParam("uuid") String uuid, @RequestParam(value="description", required=false) String description) {
		ReportService rs = Context.getService(ReportService.class);
		boolean saved = false;
		try {
			for (ReportRequest request : rs.getCachedReports().keySet()) {
				if (request.getUuid().equals(uuid)) {
					Report report = rs.getCachedReports().get(request);
					rs.saveReport(report, description);
					saved = true;
				}
			}
		}
		catch (Exception e) {
			// Do nothing
		}
		
		// TODO: Alert the user that saving failed
		
		return "redirect:reportHistory.form";
	}
	
	@RequestMapping("/module/reporting/reports/reportHistoryOpen")
	public String openFromHistory(@RequestParam("uuid") String uuid, HttpServletResponse response, WebRequest request, ModelMap model) throws IOException {
		ReportService rs = Context.getService(ReportService.class);
		ReportRequest req = rs.getReportRequestByUuid(uuid);
		if (req == null) {
			log.warn("Cannot load report request " + uuid);
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Cannot load report request", WebRequest.SCOPE_SESSION);
			return "redirect:/module/reporting/reports/reportHistory.form";
		}
		model.addAttribute("request", req);
		return "/module/reporting/reports/reportHistoryOpen";
	}
	
	@RequestMapping("/module/reporting/reports/reportHistoryView")
	public ModelAndView viewFromHistory(@RequestParam("uuid") String uuid, HttpServletResponse response, HttpServletRequest request) throws IOException {
		ReportRequest req = getReportService().getReportRequestByUuid(uuid);
		RenderingMode rm = req.getRenderingMode();
		String linkUrl = "/module/reporting/reports/reportHistoryOpen";
		
		if (rm.getRenderer() instanceof WebReportRenderer) {
			WebReportRenderer webRenderer = (WebReportRenderer) rm.getRenderer();
			linkUrl = webRenderer.getLinkUrl(req.getReportDefinition().getParameterizable());
			linkUrl = request.getContextPath() + (linkUrl.startsWith("/") ? "" : "/") + linkUrl;
		}
		
		if (req != null) {
			ReportData reportData = getReportService().loadReportData(req);
			if (reportData != null) {
				request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_DATA, reportData);
				request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT, rm.getArgument());
				request.getSession().setAttribute(ReportingConstants.OPENMRS_LAST_REPORT_URL, linkUrl);
			}
		}

		return new ModelAndView(new RedirectView(linkUrl));
	}

	@RequestMapping("/module/reporting/reports/reportHistoryDownload")
	public void downloadFromHistory(@RequestParam("uuid") String uuid, HttpServletResponse response, HttpServletRequest request) throws IOException {
		ReportRequest req = getReportService().getReportRequestByUuid(uuid);
		RenderingMode rm = req.getRenderingMode();

		String filename = rm.getRenderer().getFilename(req.getReportDefinition().getParameterizable(), rm.getArgument()).replace(" ", "_");
		response.setContentType(rm.getRenderer().getRenderedContentType(req.getReportDefinition().getParameterizable(), rm.getArgument()));
		byte[] data = getReportService().loadRenderedOutput(req);
		
		if (data != null) {
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setHeader("Pragma", "no-cache");
			IOUtils.write(data, response.getOutputStream());
		}
		else {
			response.getWriter().write("There was an error retrieving the report");
		}
	}
	
	private ReportService getReportService() {
		return Context.getService(ReportService.class);
	}
}
