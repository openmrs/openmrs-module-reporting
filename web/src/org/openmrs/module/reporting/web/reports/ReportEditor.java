package org.openmrs.module.reporting.web.reports;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.ReportDesign;
import org.openmrs.module.report.service.ReportService;
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
	public ReportEditor() { }

    /**
     * Retrieves either an existing or new report to edit
     */
    @RequestMapping("/module/reporting/reports/reportEditor")
    public void editReport(ModelMap model,
		    		@RequestParam(required=false, value="uuid") String uuid,
		            @RequestParam(required=false, value="type") Class<? extends ReportDefinition> type) {
    	
    	ReportService rs = Context.getService(ReportService.class);
    	ReportDefinition r = rs.getReportDefinition(uuid, type);
    	model.addAttribute("report", r);
    	if (StringUtils.isNotEmpty(uuid)) {
	    	List<ReportDesign> designs = rs.getReportDesigns(r, null, false);
	    	model.addAttribute("designs", designs);
    	}
    }
}
