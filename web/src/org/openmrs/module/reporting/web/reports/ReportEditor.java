package org.openmrs.module.reporting.web.reports;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.report.ReportDefinition;
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
    	
    	ReportDefinition r = Context.getService(ReportService.class).getReportDefinition(uuid, type);
    	model.addAttribute("report", r);
    }
    
    /**
     * Saves a report to the database
     */
    @RequestMapping("/module/reporting/reports/saveReportDefinition")
    public String saveReport(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends ReportDefinition> type,
            @RequestParam(required=true, value="name") String name,
            @RequestParam(required=false, value="description") String description
    ) {
    	ReportDefinition r = Context.getService(ReportService.class).getReportDefinition(uuid, type);
    	r.setName(name);
    	r.setDescription(description);
    	r.getParameters().clear();
 
    	// TODO: Parameters and DataSets

    	log.warn("Saving: " + r);
    	Context.getService(ReportService.class).saveReportDefinition(r);

        return "redirect:/module/reporting/reports/manageReports.form";
    }
}
