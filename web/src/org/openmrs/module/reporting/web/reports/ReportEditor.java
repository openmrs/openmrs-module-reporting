package org.openmrs.module.reporting.web.reports;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportEditor {
	
	protected static Log log = LogFactory.getLog(ReportEditor.class);
	
	/**
	 * Default Constructor
	 */
	public ReportEditor() {
	}
	
	/**
	 * Retrieves either an existing or new report to edit
	 */
	@RequestMapping("/module/reporting/reports/reportEditor")
	public void editReport(ModelMap model, @RequestParam(required = false, value = "uuid") String uuid,
	                       @RequestParam(required = false, value = "type") Class<? extends ReportDefinition> type) {
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportDefinition r = rs.getDefinition(uuid, type);
		model.addAttribute("report", r);
		if (StringUtils.isNotEmpty(uuid)) {
			List<ReportDesign> designs = Context.getService(ReportService.class).getReportDesigns(r, null, false);
			model.addAttribute("designs", designs);
		}
	}
}
