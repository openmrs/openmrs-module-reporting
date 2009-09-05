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
    

    /**
     * Shows the report form for the lab report.
     * 
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/showLabReport")
    public String showLabReportForm(ModelMap model) {     	
    	// Show user the form 
    	List<Location> locations = Context.getLocationService().getAllLocations(false);
    	
    	List<CohortDefinition> cohortDefinitions = 
    		Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
    	
    	model.addAttribute("cohortDefinitions", cohortDefinitions);
    	model.addAttribute("locations", locations);
    	
        return "/module/reporting/reports/labReportViewer";
    }
    
    /**
     * Render the lab report.
     * 
     * @param action
     * @param locationId
     * @param startDate
     * @param endDate
     * @param conceptIds
     * @param renderType
     * @param response
     */
    @RequestMapping("/module/reporting/renderLabReport")
    public void renderLabReport(
    		@RequestParam(required=false, value="action") String action,
    		@RequestParam(required=false, value="locationId") Integer locationId,
    		@RequestParam(required=false, value="startDate") Date startDate,
    		@RequestParam(required=false, value="endDate") Date endDate,
    		@RequestParam(required=false, value="conceptIds") String conceptIds,  
    		@RequestParam(required=false, value="renderType") String renderType,      		
    		ModelMap model,
    		HttpServletResponse response) {

    	try { 
    		
    		// The base cohort 
    		Cohort baseCohort = new Cohort();
    	    		
    		// The selected location
    		Location location = 
    			Context.getLocationService().getLocation(locationId);

    		//CohortDefinition cohortDefinition = 
    		//	Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(cohortDefinitionUuid);
    		
    		// Populate parameters
    		Map<String,Object> parameterValues = new HashMap<String,Object>();
    		parameterValues.put("startDate", startDate);
    		parameterValues.put("endDate", endDate);
    		parameterValues.put("location", location);

    		log.info("Generating report with location=" + location + ", startDate=" + startDate + ", endDate=" + endDate);
    		
    		// Create a shared evaluation context
    		//Cohort baseCohort = Context.getPatientSetService().getAllPatients();		
    		EvaluationContext sharedContext = new EvaluationContext();
    		sharedContext.setBaseCohort(baseCohort);
    		sharedContext.setParameterValues(parameterValues);
    		
    		// 
    		//EvaluationContext encounterDatasetContext = new EvaluationContext();
    		//encounterDatasetContext.setBaseCohort(baseCohort);		
    		
    		// Create, evaluate, and render the lab dataset (5497, 5089, 1019)
    		// TODO Need to make this a global property or user-entered values
    		Integer [] LAB_CONCEPT_IDS = { 657, 856, 6167, 6168 };

    		// List<Integer> labTests = splitIdentifiers(labTests);
    		
    		LabEncounterDataSetDefinition ledsDefinition = 
    			new LabEncounterDataSetDefinition(Arrays.asList(LAB_CONCEPT_IDS));				
    		    		
    		 
    		DataSet<?> labDataSet = Context.getService(DataSetDefinitionService.class).evaluate(ledsDefinition, sharedContext);	

            //ReportData labReportData = new ReportData();
            //Map<String, DataSet> labDataSets = new HashMap<String, DataSet>();
            //labDataSets.put("encounter", labDataSet);
            //labReportData.setDataSets(labDataSets);        
            //new CsvReportRenderer().render(labReportData, null, System.out);			

    		
    		// ================================================================
    		
    		
    		// FIXME For now we're just creating a patient dataset with all patients
    		// This is very time/memory consuming so we don't want to do this forever.
    		// For the patient dataset, we need to find out what patients were
    		// included in the lab encounter dataset 
    		// MS: Not sure what this was doing before, commenting it out since it no longer works
    		//     -> sharedContext.setBaseCohort(labDataSet.getCohort());
    		
    		// Create, evaluate, and render the patient dataset
    		PatientDataSetDefinition pdsDefinition = new PatientDataSetDefinition();
    		/*
    		DataSet patientDataSet = 
    			Context.getService(
    					DataSetDefinitionService.class).evaluate(
    							pdsDefinition, 
    							sharedContext);
			*/
            //ReportData patientReportData = new ReportData();
            //Map<String, DataSet> patientDataSets = new HashMap<String, DataSet>();
            //patientDataSets.put("patient", patientDataSet);
            //patientReportData.setDataSets(patientDataSets);        
            //new CsvReportRenderer().render(patientReportData, null, System.out);		

    		
    		
    		// Create, evaluate, and render the joined dataset 
            JoinDataSetDefinition jdsDefinition = new JoinDataSetDefinition(
            		pdsDefinition, "patient.", "patient_id", 
            		ledsDefinition, "encounter.", "patient_id");

            // TODO Need to pass a Mapped<DataSetDefinition>
            //reportSchema.addDataSetDefinition(joinDataSetDefinition);

            
            
            
            DataSet joinedDataSet = 
            	Context.getService(DataSetDefinitionService.class).evaluate(
            			jdsDefinition, sharedContext);
                        
            log.info("JoinedDataSet: " + joinedDataSet);
            
            ReportData reportData = new ReportData();
            Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
            dataSets.put("joinDataSet", joinedDataSet);
            reportData.setDataSets(dataSets);

            //
            // FIXME Haven't figure out the perfect solution for this yet.
            //
            // Kind of hacky way to handle displaying certain columns
            // TODO Should be added as a RenderOptions 
            // In fact, we should never instantiate a ReportRenderer, we should 
            // send a RenderOptions to the service layer and let it take care 
            // of rendering and passing us back a Rendered object.
            List<String> displayColumns = new LinkedList<String>();
            List<String> pdsColumns = new LinkedList<String>();
            pdsColumns.addAll(pdsDefinition.getColumnKeys());
            pdsColumns.remove(PatientDataSetDefinition.PATIENT_ID);
            displayColumns.addAll(pdsColumns);

            // FIXME Hack to get the columns to show up as 
            // the joint dataset adds a prefix to the column name
            for(String column : pdsColumns) { 
            	displayColumns.add("patient." + column);
            }
            
            List<String> ledsColumns = new LinkedList<String>();
            ledsColumns.addAll(ledsDefinition.getColumnKeys());
            ledsColumns.remove(LabEncounterDataSetDefinition.ENCOUNTER_ID);
            ledsColumns.remove(LabEncounterDataSetDefinition.PATIENT_ID);
            
            // FIXME Hack to get the columns to show up as 
            // the joint dataset adds a prefix to the column name
            for(String column : ledsColumns) { 
            	displayColumns.add("encounter." + column);
            }
            
            
            if ("XLS".equalsIgnoreCase(renderType)) { 
            	log.info("Rendering as xls");
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=\"lab-order-report.xls\"");            
				XlsReportRenderer renderer = new XlsReportRenderer();
				//renderer.setDisplayColumns(displayColumns); MS: No longer supported in renderer...move to DSD
				renderer.render(reportData, null, response.getOutputStream());
            }
            else {
            	log.info("Rendering as csv");
				// Set headers and content type of report file
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"lab-order-report.csv\"");            
	            CsvReportRenderer renderer = new CsvReportRenderer();
				//renderer.setDisplayColumns(displayColumns); MS: No longer supported in renderer...move to DSD
	            renderer.render(reportData, null, response.getOutputStream());
            }
    	} catch (IOException e) { 
    		e.printStackTrace();
    	}
    	
    	// TODO Refactor controller -- this should never happen, not should it be necessary
    	//return "redirect:/module/reporting/reports/reportManager.list";
    } 
        
}
