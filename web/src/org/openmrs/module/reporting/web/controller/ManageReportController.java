package org.openmrs.module.reporting.web.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.LabEncounterDataSet;
import org.openmrs.module.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.renderer.CsvReportRenderer;
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

	Log log = LogFactory.getLog(this.getClass());
	
    @RequestMapping("/module/reporting/manageReports")
    public String manageReports(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model
    ) {
    	
    	log.info("Manage reports");

    	List<Location> locations = 
    		Context.getLocationService().getAllLocations(false);
    	
    	List<CohortDefinition> cohortDefinitions = 
    		Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
    	
    	model.addAttribute("cohortDefinitions", cohortDefinitions);
    	model.addAttribute("locations", locations);
    	
        return "/module/reporting/manageReports";
    }
    
    
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); 
    	dateFormat.setLenient(false); 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false)); 
    }    

    @RequestMapping("/module/reporting/generateReport")
    public void generateReport(
    		@RequestParam(required=false, value="locationId") Integer locationId,
    		@RequestParam(required=false, value="startDate") Date startDate,
    		@RequestParam(required=false, value="endDate") Date endDate,
    		@RequestParam(required=false, value="labSets") String labSets,  
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
    		Integer [] labTests = { 5497, 5089, 1019 };
    		
    		// List<Integer> labTests = splitIdentifiers(labTests);
    		
    		LabEncounterDataSetDefinition ledsDefinition = 
    			new LabEncounterDataSetDefinition(Arrays.asList(labTests));				
    		    		
    		 
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


			// Set headers and content type of report file
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"labreport.csv\"");
            
            new CsvReportRenderer().render(reportData, null, response.getOutputStream());
            
            
    	} catch (IOException e) { 
    		e.printStackTrace();
    	}
        //return "/module/reporting/generateReport";
    }    

    /**
     * Convenience method return a list of integers given a comma-delimited string of values.
     * @param paramValue
     * @return	a list of integers
     */
    public List<Integer> splitIdentifiers(String paramValue) { 
    	List<Integer> identifiers = new ArrayList<Integer>();
    	for (String identifier : paramValue.split(",")) { 
    		identifiers.add(Integer.parseInt(identifier));
    	}    	
    	return identifiers;
    }
    
}
