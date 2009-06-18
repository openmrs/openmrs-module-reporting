package org.openmrs.module.reporting.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.LabEncounterDataSet;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportSchema;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.XlsReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageReportController {

	private Log log = LogFactory.getLog(this.getClass());
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	// TODO Switch this to the Context.getDateFormat()
    	//SimpleDateFormat dateFormat = Context.getDateFormat();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); 
    	dateFormat.setLenient(false); 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false)); 
    }    

	
    @RequestMapping("/module/reporting/manageReports")
    public String manageReports(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	
    	List<ReportSchema> reportSchemas = 
    		Context.getService(ReportService.class).getReportSchemas();

    	model.addAttribute("reportSchemas", reportSchemas);
    	
        return "/module/reporting/reports/reportManager";
    }
    
    @RequestMapping("/module/reporting/editReportSchema")
    public String editReportSchema(
    		@RequestParam(required=false, value="uuid") String uuid,
    		ModelMap model) {
		ReportService reportService = 
			Context.getService(ReportService.class);
    	
    	List<ReportSchema> reportSchemas = reportService.getReportSchemas();
    	
    	ReportSchema reportSchema = reportService.getReportSchemaByUuid(uuid);
    	
    	model.addAttribute("reportSchema", reportSchema);    	
    	model.addAttribute("reportSchemas", reportSchemas);
    	
        return "/module/reporting/reports/reportEditor";
    }    
    
    
    /**
     * Get the lab encounter report 
     * 
     * @param model		the model 
     * @return	a string representing the lab 
     */
    @RequestMapping("/module/reporting/getSimpleLabReport")
    public String getSimpleLabReport(ModelMap model) {
    	
    	List<Location> locations = Context.getLocationService().getAllLocations(false);
    	
    	List<CohortDefinition> cohortDefinitions = 
    		Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
    	
    	model.addAttribute("cohortDefinitions", cohortDefinitions);
    	model.addAttribute("locations", locations);
    	
        return "/module/reporting/reports/simpleLabReportForm";
    }
    
    /**
     * Get a simple cohort report form.
     * 
     * @param model		the model 
     * @return	jsp used to render this report form
     */
    @RequestMapping("/module/reporting/getSimpleCohortReport")
    public String getSimpleCohortReport(ModelMap model) {
    	
		ReportService reportService = (ReportService) Context.getService(ReportService.class);

		// Will add the first report		    	
    	model.addAttribute("reportSchema", reportService.getReportSchemas().get(0));

    	
    	return "/module/reporting/reports/simpleCohortReportForm";
    }    
    
    /**
     * Get a simple indicator report.
     * 
     * @param model		the model 
     * @return	jsp used to render this report form
     */
    @RequestMapping("/module/reporting/getSimpleIndicatorReport")
    public String getSimpleIndicatorReport(ModelMap model) {
    	
		ReportService reportService = (ReportService) Context.getService(ReportService.class);

		// Will add the first report		    	
    	model.addAttribute("reportSchema", reportService.getReportSchemas().get(1));

    	
    	return "/module/reporting/reports/simpleIndicatorReportForm";
    }    
    
    
    
    
    /**
     * Generate the lab report 
     * @param locationId
     * @param startDate
     * @param endDate
     * @param conceptIds
     * @param renderType
     * @param response
     */
    @RequestMapping("/module/reporting/generateSimpleLabReport")
    public void generateLabReport(
    		@RequestParam(required=false, value="locationId") Integer locationId,
    		@RequestParam(required=false, value="startDate") Date startDate,
    		@RequestParam(required=false, value="endDate") Date endDate,
    		@RequestParam(required=false, value="conceptIds") String conceptIds,  
    		@RequestParam(required=false, value="renderType") String renderType,      		
    		HttpServletResponse response) {

    	try { 
    		Cohort baseCohort = new Cohort();
    	
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
    		    		
    		 
    		LabEncounterDataSet labDataSet = (LabEncounterDataSet) 
    			Context.getService(
    				DataSetDefinitionService.class).evaluate(
    						ledsDefinition, sharedContext);	
    		
    		
    		
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
    		sharedContext.setBaseCohort(labDataSet.getCohort());
    		
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
            
            if ("XLS".equalsIgnoreCase(renderType)) { 
            	log.info("Rendering as xls");
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=\"laboratory-report.xls\"");            
				new XlsReportRenderer().render(reportData, null, response.getOutputStream());
            }
            else {
            	log.info("Rendering as csv");
				// Set headers and content type of report file
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"laboratory-report.csv\"");            
	            new CsvReportRenderer().render(reportData, null, response.getOutputStream());
            }
    	} catch (IOException e) { 
    		e.printStackTrace();
    	}
    }    

    
    /**
     * 
     * @param response
     * @throws Exception
     */
    @RequestMapping("/module/reporting/generateSimpleCohortReport")
    public void generateSimpleCohortReport(HttpServletResponse response) throws Exception { 		
		ReportService rs = (ReportService) Context.getService(ReportService.class);

		// Will return the first instance of a report
		ReportSchema reportSchema = rs.getReportSchema(1);
		
		EvaluationContext ec = new EvaluationContext();
		ec.addParameterValue("report.startDate", ymd.parse("1980-01-01"));
		ec.addParameterValue("report.endDate", ymd.parse("2008-01-01"));
		
		ReportData reportData = rs.evaluate(reportSchema, ec);

		CsvReportRenderer renderer = new CsvReportRenderer();
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"cohort-report.csv\"");  
		renderer.render(reportData, null, response.getOutputStream());    	
    }
    
    
    /**
     * 
     * @param response
     * @throws Exception
     */
    @RequestMapping("/module/reporting/generateSimpleIndicatorReport")
    public void generateSimpleIndicatorReport(HttpServletResponse response) throws Exception { 
    	
		EvaluationContext context = new EvaluationContext();
		CsvReportRenderer renderer = new CsvReportRenderer();
		
		ReportService rs = (ReportService) Context.getService(ReportService.class);
		ReportSchema reportSchema = rs.getReportSchema(2);
		
		context = new EvaluationContext();
		context.addParameterValue("report.location", Context.getLocationService().getLocation(26));
		context.addParameterValue("report.reportDate", ymd.parse("2007-01-01"));

		//context.addParameterValue("report.location", Context.getLocationService().getLocation(29));
		//context.addParameterValue("report.reportDate", ymd.parse("2007-01-01"));
		//context.addParameterValue("report.location", Context.getLocationService().getLocation(26));
		//context.addParameterValue("report.reportDate", ymd.parse("2008-01-01"));
		//context.addParameterValue("report.location", Context.getLocationService().getLocation(29));
		//context.addParameterValue("report.reportDate", ymd.parse("2008-01-01"));
		
		ReportData reportData = Context.getService(ReportService.class).evaluate(reportSchema, context);

		
		
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"indicator-report.csv\"");  
		renderer.render(reportData, null, response.getOutputStream());		
    	
    	
    }
        
}
