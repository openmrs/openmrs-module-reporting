package org.openmrs.module.reporting.web.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ReportManager {

	protected static Log log = LogFactory.getLog(ReportManager.class);
	
	/**
	 * Default Constructor
	 */
	public ReportManager() { }

    /**
     * Provide all reports, optionally including those that are retired, to a page 
     * that lists them and provides options for working with these reports.
     */
    @RequestMapping("/module/reporting/reports/reportManager")
    public ModelMap manageReports(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	// Get list of existing reports
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<ReportDefinition> reportDefinitions = Context.getService(ReportService.class).getReportDefinitions(includeRet);
    	model.addAttribute("ReportDefinitions", reportDefinitions);
    	
    	// Get possible new reports to create
    	Map<Class<? extends ReportDefinition>, String> types = new HashMap<Class<? extends ReportDefinition>, String>();
    	types.put(ReportDefinition.class, "Basic Report");
    	model.addAttribute("types", types);
    	
        return model;
    }
}
