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
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
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
	
	@RequestMapping("/module/reporting/reports/reportHistorySave")
	public String saveHistoryElement(@RequestParam("uuid") String uuid, @RequestParam(value="description", required=false) String description) {
		ReportService rs = Context.getService(ReportService.class);
		ReportRequest req = rs.getReportRequestByUuid(uuid);
		req.setStatus(Status.SAVED);
		req.setDescription(description);
		rs.saveReportRequest(req);
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
		File reportOutputFile = rs.getReportOutputFile(req);
		if (reportOutputFile.exists()) {
			model.addAttribute("action", "download");
		}
		File reportDataFile = rs.getReportDataFile(req);
		if (reportDataFile.exists() && req.getRenderingMode().getRenderer() instanceof WebReportRenderer) {
			model.addAttribute("action", "view");
		}
		File reportErrorFile = rs.getReportErrorFile(req);
		if (reportErrorFile.exists() && req.getStatus() == Status.FAILED) {
			String errorDetails = rs.loadReportError(req);
			model.addAttribute("errorDetails", errorDetails);
		}
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
			if (linkUrl != null) {
				ReportData reportData = getReportService().loadReportData(req);
				if (reportData != null) {
					request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_DATA, reportData);
					request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT, rm.getArgument());
					linkUrl = request.getContextPath() + (linkUrl.startsWith("/") ? "" : "/") + linkUrl;
					request.getSession().setAttribute(ReportingConstants.OPENMRS_LAST_REPORT_URL, linkUrl);
				}
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
