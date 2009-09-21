package org.openmrs.module.reporting.web.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.renderer.XlsReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageReportController {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Constructor
	 */
	public ManageReportController() { }
	
	/**
	 * Registers custom editors for fields of the command class.
	 * 
	 * @param binder
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	// TODO Switch this to the Context.getDateFormat()
    	//SimpleDateFormat dateFormat = Context.getDateFormat();
    	//SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); 
    	//dateFormat.setLenient(false); 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), false)); 
    }    

	
    /**
     * Manage reports.
     * 
     * @param includeRetired
     * @param model
     * @return
     */
    //@RequestMapping("/module/reporting/manageReports")
    public String manageReports(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	
    	List<ReportDefinition> reportSchemas = 
    		Context.getService(ReportService.class).getReportDefinitions();

    	model.addAttribute("reportSchemas", reportSchemas);
    	
        return "/module/reporting/reports/reportManager";
    }
    
    /**
     * Edit a report schema.
     * 
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/editReportDefinition")
    public String editReportDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
    		ModelMap model) {
		ReportService reportService = 
			Context.getService(ReportService.class);
    	
    	List<ReportDefinition> reportSchemas = reportService.getReportDefinitions();
    	
    	ReportDefinition reportSchema = reportService.getReportDefinitionByUuid(uuid);
    	
    	model.addAttribute("reportSchema", reportSchema);    	
    	model.addAttribute("reportSchemas", reportSchemas);
    	
        return "/module/reporting/reports/reportEditor";
    }    

    /**
     * Render a report schema.
     * 
     * @param model		the model 
     * @return	jsp used to render this report form
     */
    @RequestMapping("/module/reporting/renderReport")
    public String renderReport(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="action") String action,
    		@RequestParam(required=false, value="renderType") String renderType,
    		HttpServletRequest request,
    		HttpServletResponse response,
    		ModelMap model) throws Exception {
    	
		ReportService reportService = (ReportService) Context.getService(ReportService.class);
		
		ReportDefinition reportSchema = null;
		// Will return the first instance of a report
		if (uuid != null) { 
			reportSchema = reportService.getReportDefinitionByUuid(uuid);				
		}
		
		if (reportSchema == null) { 
			throw new APIException("Unable to locate report schema with UUID " + uuid);
		}

		
    	// If the user has submitted the form, we render it as a CSV
    	if (action != null && action.equals("render")) { 
			
			EvaluationContext evalContext = new EvaluationContext();
			for (Parameter param : reportSchema.getParameters() ) { 
				log.info("Setting parameter " + param.getName() + " of class " + param.getType() + " = " + request.getParameter(param.getName()) );
				String paramValue = request.getParameter(param.getName());
				// TODO Need to convert from string to object
				// TODO Parameter needs a data type property
				// We don't have enough information at this point
				evalContext.addParameterValue(param.getName(), paramValue);
			}

			// Set the default parameter
			evalContext.addParameterValue("report.startDate", Context.getDateFormat().parse("1980-01-01"));
			evalContext.addParameterValue("report.endDate", Context.getDateFormat().parse("2008-01-01"));
			
			ReportData reportData = reportService.evaluate(reportSchema, evalContext);
	
			ReportRenderer renderer = null;
			if ("csv".equalsIgnoreCase(renderType)) { 
				renderer = new CsvReportRenderer();
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"");  
			} 
			else if ("tsv".equalsIgnoreCase(renderType)) { 
				renderer = new TsvReportRenderer();
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"report.tsv\"");  
			} 
			else if ("xls".equalsIgnoreCase(renderType)) { 
				renderer = new TsvReportRenderer();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=\"report.xls\"");  
			} 
			else { 
				throw new APIException("Unknown rendering type");
			}
			renderer.render(reportData, null, response.getOutputStream()); 
	    	return "redirect:/module/reporting/reports/reportManager.list";
    	}    
    	
    	model.addAttribute("reportSchema", reportSchema);
    	return "/module/reporting/reports/reportViewer";    
    }    
    
    
    @RequestMapping("/module/reporting/evaluateReport")	
	public void evaluateReport(
			HttpServletResponse response,
			@RequestParam(required=false, value="uuid") String uuid) {

    	log.info("Evaluating report schema with uuid " + uuid);
		ReportService service = Context.getService(ReportService.class);		
		ReportDefinition reportSchema = service.getReportDefinitionByUuid(uuid);
		
		
		if (reportSchema != null) { 						
			log.info("Report schema " + reportSchema);
			ReportData reportData = 
				service.evaluate(reportSchema, new EvaluationContext());

			
			log.info("Report datasets: " + reportData.getDataSets());
			try { 
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"");				
				new CsvReportRenderer().render(reportData, null, response.getOutputStream());
			} 
			catch (IOException e) { 
				log.error("Could not render report", e);
				throw new APIException("Could not render report " + uuid + " using CSV renderer", e);
			}
			
		}
		else { 
			throw new APIException("Report schema " + uuid + " could not be located");
		}
		
	}	    
    
    /**
     * Purges a report schema.
     * 
     * @param uuid
     * @return
     */
    @RequestMapping("/module/reporting/purgeReport")
    public String purgeCohortDefinition(@RequestParam(required=false, value="uuid") String uuid) {

    	ReportService reportService = 
    		Context.getService(ReportService.class);
    	
    	reportService.
    		deleteReportDefinition(reportService.getReportDefinitionByUuid(uuid));	
    	
    	return "redirect:/module/reporting/reports/manageReports.form";
    }        
    
    
    
    // ================================================================================
    //	The following are specific report user stories that will be refactor
    //	to work with any types of reports.  For now, we just experimenting to 
    // 	see what type of reports we need to be able to generate and how to 
    // 	accomplish that.
    // ================================================================================
    
    /**
     * Get a simple indicator report.
     * 
     * @param model		the model 
     * @return	jsp used to render this report form
     */
    @RequestMapping("/module/reporting/editIndicatorReport")
    public String editIndicatorReport(ModelMap model) {
    	
		ReportService reportService = (ReportService) Context.getService(ReportService.class);

		ReportDefinition reportSchema = reportService.getReportDefinition(2);
		
		// actions (save, delete) 
		
		
		
		// Will add the first report		    	
    	model.addAttribute("reportSchema", reportSchema);

    	// TODO eventually this should be a single reportEditor JSP
    	return "/module/reporting/reports/indicatorReportEditor";
    }    
    
    /**
     * Get a simple cohort report form.
     * 
     * @param model		the model 
     * @return	jsp used to render this report form
     */
    @RequestMapping("/module/reporting/renderCohortReport")
    public String renderCohortReport(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="action") String action,
    		HttpServletResponse response,
    		ModelMap model) throws Exception {

    	
		ReportService reportService = (ReportService) Context.getService(ReportService.class);

    	// If the user has submitted the form, we render it as a CSV
    	if (action != null && action.equals("render")) { 
	
			// Will return the first instance of a report
			ReportDefinition reportSchema = reportService.getReportDefinition(1);

			EvaluationContext ec = new EvaluationContext();
			ec.addParameterValue("report.startDate", Context.getDateFormat().parse("1980-01-01"));
			ec.addParameterValue("report.endDate", Context.getDateFormat().parse("2008-01-01"));
			
			ReportData reportData = reportService.evaluate(reportSchema, ec);
	
			CsvReportRenderer renderer = new CsvReportRenderer();
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"cohort-report.csv\"");  
			renderer.render(reportData, null, response.getOutputStream());    	    
			return "";
    	}    

    	// Otherwise we need to show the report form 		
		ReportDefinition reportSchema = reportService.getReportDefinition(1);
    	model.addAttribute("reportSchema", reportSchema);
    	
    	// TODO eventually this should be a single reportViewer JSP
    	return "/module/reporting/reports/cohortReportViewer";    
    }    
    
    
    /**
     * 
     * @param response
     * @throws Exception
     */
    @RequestMapping("/module/reporting/renderIndicatorReport")
    public String renderIndicatorReport(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="action") String action,    		
    		HttpServletResponse response,
    		ModelMap model) throws Exception { 
    	
		ReportService reportService = (ReportService) Context.getService(ReportService.class);
    	
    	// Show user the form 
		if (action != null && action.equals("render")) {
			EvaluationContext context = new EvaluationContext();
			CsvReportRenderer renderer = new CsvReportRenderer();
			
			ReportDefinition reportSchema = reportService.getReportDefinition(2);
			
			context = new EvaluationContext();
			context.addParameterValue("report.location", Context.getLocationService().getLocation(26));
			context.addParameterValue("report.reportDate", Context.getDateFormat().parse("2007-01-01"));
	
			//context.addParameterValue("report.location", Context.getLocationService().getLocation(29));
			//context.addParameterValue("report.reportDate", Context.getDateFormat().parse("2007-01-01"));
			//context.addParameterValue("report.location", Context.getLocationService().getLocation(26));
			//context.addParameterValue("report.reportDate", Context.getDateFormat().parse("2008-01-01"));
			//context.addParameterValue("report.location", Context.getLocationService().getLocation(29));
			//context.addParameterValue("report.reportDate", Context.getDateFormat().parse("2008-01-01"));
			
			ReportData reportData = Context.getService(ReportService.class).evaluate(reportSchema, context);
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"indicator-report.csv\"");  
			renderer.render(reportData, null, response.getOutputStream());		
			return "";
				
		}

		ReportDefinition reportSchema = reportService.getReportDefinition(2);
		model.addAttribute("reportSchema", reportSchema);
    	return "/module/reporting/reports/indicatorReportViewer";
		
    }
}
