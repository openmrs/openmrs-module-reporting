package org.openmrs.module.reporting.web.reports;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
public class ManageReportsController {

	protected static Log log = LogFactory.getLog(ManageReportsController.class);
	
	/**
	 * Default Constructor
	 */
	public ManageReportsController() { }

    /**
     * Provide all reports, optionally including those that are retired, to a page 
     * that lists them and provides options for working with these reports.
     */
    @RequestMapping("/module/reporting/reports/manageReports")
    public ModelMap manageReports(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	// Get list of existing reports
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<ReportDefinition> reportDefinitions = Context.getService(ReportService.class).getReportDefinitions(includeRet);
    	model.addAttribute("reportDefinitions", reportDefinitions);
    	
    	// Get possible new reports to create
    	Map<String, String> types = new LinkedHashMap<String, String>();
    	types.put("Period Indicator Report", "periodIndicatorReportEditor.form");
    	types.put("Darius Period Indicator Report", "dariusPeriodIndicatorReport.form");
    	types.put("Custom Report (Advanced)", "reportEditor.form?type=" + ReportDefinition.class.getName());
    	model.addAttribute("createLinks", types);
    	
        return model;
    }
    
    /**
     * Provide all reports designs, optionally including those that are retired, to a page 
     * that lists them and provides options for working with them.
     */
    @RequestMapping("/module/reporting/reports/manageReportDesigns")
    public ModelMap manageReportDesigns(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	// Get list of existing reports
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<ReportDesign> reportDesigns = Context.getService(ReportService.class).getAllReportDesigns(includeRet);
    	model.addAttribute("reportDesigns", reportDesigns);
    	
        return model;
    }
    
    /**
     * Provide all reports designs, optionally including those that are retired, to a page 
     * that lists them and provides options for working with them.
     */
    @RequestMapping("/module/reporting/reports/viewDesignContent")
    public void viewDesignContent(ModelMap model, 
    									HttpServletResponse response,
    									@RequestParam(required=true, value="uuid") String uuid,
    									@RequestParam(required=true, value="property") String property) {
    	
    	ReportDesign d = Context.getService(ReportService.class).getReportDesignByUuid(uuid);
    	String baseName = d.getName().replace(" ", "_");
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		// TODO: Fix below
		/*
		try {
			if ("designSpecification".equals(property)) {
				response.setHeader("Content-Disposition", "attachment; filename=" + baseName + "_specification");
				response.getWriter().write(d.getDesignSpecification());
			}
			else if ("designData".equals(property)) {
				response.setHeader("Content-Disposition", "attachment; filename=" + baseName + "_data");
				response.getOutputStream().write(d.getDesignData());
			}
			else {
				response.getWriter().write("Please indicate an appropriate property to view");
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to render contents.", e);
		}
		*/
    }
}
