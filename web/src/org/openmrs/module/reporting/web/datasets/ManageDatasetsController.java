package org.openmrs.module.reporting.web.datasets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetException;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.renderer.XlsReportRenderer;
import org.openmrs.module.report.renderer.XmlReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.CohortUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageDatasetsController {

	protected Log log = LogFactory.getLog(this.getClass());
	
    @RequestMapping("/module/reporting/datasets/manageDataSets")
    public void manageDatasetDefinition(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	
    	DataSetDefinitionService service = 
    		Context.getService(DataSetDefinitionService.class);
    	
    	List<DataSetDefinition> datasetDefinitions = new ArrayList<DataSetDefinition>();
    	
    	// Get all data set definitions
    	try {         	
        	boolean retired = includeRetired != null && includeRetired.booleanValue();
    		datasetDefinitions = service.getAllDataSetDefinitions(retired);
    	} 
    	catch (Exception e) { 
    		log.error("Could not fetch dataset definitions", e);
    	}
    	
    	model.addAttribute("types", service.getDataSetDefinitionTypes());    	
    	model.addAttribute("dataSetDefinitions", datasetDefinitions);
    }
    
    /**
     * Shows a dataset definition given a uuid (or an id and class name). 
     * 
     * @param id
     * 			The identifier of the dataset definition
     * @param uuid
     * 			The universally unique identifier of the dataset definition
     * @param className
     * 			The class name of the dataset to be created/retrieved
     * @param cohortSize
     * 			The size of the cohort used to preview the dataset
     * @param action
     * 			The action to be taken
     * @param model
     * 			The model map used to hold the dataset definition and preview dataset
     * @return
     * 		The name of the JSP to present to the user
     */
    @SuppressWarnings("unchecked")
	@RequestMapping("/module/reporting/datasets/editDataSet")
    public String showDatasetDefinition(
    		@RequestParam(required=false, value="id") Integer id,
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="cohortSize") Integer cohortSize,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model) {

    	DataSetDefinition dataSetDefinition = 
    		getDataSetDefinition(uuid, type, id);
    	    	
    	if (dataSetDefinition != null) { 
    		EvaluationContext context = new EvaluationContext();
    		context.setBaseCohort(CohortUtil.getRandomCohort(cohortSize != null ? cohortSize : 10));
        	DataSet dataSet = 
        		Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context);        
        	model.addAttribute("dataSet", dataSet);
    	}
    		
    	    	
    	model.addAttribute("dataSetDefinition", dataSetDefinition);
        return "/module/reporting/datasets/datasetEditor";
    }
    
    
    
	@RequestMapping("/module/reporting/datasets/removeDataSet")
    public String showDatasetDefinition(
    		@RequestParam(required=false, value="id") Integer id,
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") String type,
    		ModelMap model) {

    	DataSetDefinition dataSetDefinition = 
    		getDataSetDefinition(uuid, type, id);
    	
    	if (dataSetDefinition != null) 
    		Context.getService(DataSetDefinitionService.class).purgeDataSetDefinition(dataSetDefinition);
    		    	
    	return "redirect:/module/reporting/datasets/manageDataSets.list";    	    	
    }
        
    
    
    /**
     * Adds a column to the given dataset.  
     * @return
     */
    @RequestMapping("/module/reporting/datasets/addConceptColumn")
    public String addColumn(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="type") String type,
    		@RequestParam("conceptId") Integer conceptId,
    		@RequestParam("columnName") String columnName,
    		@RequestParam("modifier") String modifier,
    		@RequestParam(required=false, value="modifierNum") Integer modifierNum,
    		@RequestParam(required=false, value="extras") String [] extras,
    		ModelMap model) {

    	
    	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type, id);

    	if (dataSetDefinition instanceof DataExportDataSetDefinition) { 
    		DataExportDataSetDefinition instance = 
    			(DataExportDataSetDefinition) dataSetDefinition;  
    		
    		instance.getDataExportReportObject().addConceptColumn(
    				columnName, 
    				modifier, 
    				modifierNum, 
    				conceptId.toString(), 
    				extras);
    	}
    	else if (dataSetDefinition instanceof PatientDataSetDefinition) {
    		PatientDataSetDefinition instance = 
    			(PatientDataSetDefinition) dataSetDefinition;
        	//Concept concept = Context.getConceptService().getConcept(conceptId);    		
        	// TODO Implement concept column in patient dataset 
    		throw new DataSetException("Patient Data Set Definition does not currently support additional columns");
    	}    		
    	return "";
    }
    
    
    /**
     * Adds a column to the given dataset.  
     * @return
     */
    @RequestMapping("/module/reporting/datasets/removeColumn")
    public String removeColumn(
    		@RequestParam(required=false, value="uuid") String uuid,
    		ModelMap model) {

    	return "";
    }
    	
    
    /**
     * Retrieve an existing dataset or create a new dataset given the type.
     * 
     * @param uuid
     * @param type
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/datasets/newDataSet")
    public String editCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
    		ModelMap model) {
    	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition datasetDefinition = service.getDataSetDefinition(uuid, type);
     	model.addAttribute("dataSetDefinition", datasetDefinition);
     	     	
        return "/module/reporting/datasets/datasetEditor";
    }    
    
    
    
    /**
     * 
     * @param uuid
     * @param type
     * @param name
     * @param description
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/datasets/saveDataSet")
    public String saveDatasetDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam(required=true, value="name") String name,
            @RequestParam("description") String description,
    		ModelMap model
    ) {
    	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDataSetDefinition(uuid, type);
    	dataSetDefinition.setName(name);
    	dataSetDefinition.setDescription(description);
    	
    	dataSetDefinition = 
    		service.saveDataSetDefinition(dataSetDefinition);

    	return "redirect:/module/reporting/datasets/manageDataSets.list";
        //return "redirect:/module/reporting/editDataSet.form?uuid="+dataSetDefinition.getUuid();
    }
    
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/datasets/viewDataSet")
    public String showDatasetPreview(
       		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="cohortUuid") String cohortUuid,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model
     	) {
    	
    	List<CohortDefinition> cohortDefinitions = 
    		Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
    	
    	
    	
       	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	
       	
       	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type, id);
    	
    	// If it's still null, then we should use a default dataset definition
    	if (dataSetDefinition == null) { 
    		dataSetDefinition = new PatientDataSetDefinition();
    	}
    	
    	
    	if ("preview".equalsIgnoreCase(action)) { 
	    	EvaluationContext context = new EvaluationContext();
	    	
	    	Cohort baseCohort = new Cohort();
	    	CohortDefinition cohortDefinition = null;

	    	// If a cohort name was specified, we'll use that cohort
	    	if (cohortUuid != null) { 
	    		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
	    		cds.getCohortDefinitionByUuid(cohortUuid);
	    		baseCohort = cds.evaluate(cohortDefinition, context);
	    	} 
	    	
	    	// Otherwise, use a random cohort from saved cohorts
	    	// TODO This should be removed once we get serialization working	    	
	    	if (baseCohort == null) { 
		    	List<Cohort> cohorts = Context.getCohortService().getAllCohorts();
	    		baseCohort = cohorts.get(new Random().nextInt(cohorts.size()));
	    	}
	    	context.setBaseCohort(baseCohort);	    		
	    	
	    	// Evaluate the dataset
	    	DataSet<Object> dataSet = service.evaluate(dataSetDefinition, context);
	    	
	    	// We usually render, but for now we're just going to return it
	    	// TODO Render as an html table and pass that back to the JSP
	    	
	    	model.addAttribute("dataSet", dataSet);
	    	model.addAttribute("cohort", baseCohort);
	    	model.addAttribute("cohortDefinition", cohortDefinition);
    	}    	
    	model.addAttribute("cohortDefinitions", cohortDefinitions); 
    	model.addAttribute("dataSetDefinition", dataSetDefinition);
    	
        return "/module/reporting/datasets/datasetViewer";
    }    
    
    /**
     * 
     * @param model
     * @return
     */
    @SuppressWarnings("deprecation")
	@RequestMapping("/module/reporting/datasets/downloadDataSet")
    public void downloadDataset(
    		@RequestParam(required=false, value="id") Integer id,
       		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="format") String format,
            @RequestParam(required=false, value="username") String username,
            @RequestParam(required=false, value="password") String password,
            HttpServletResponse response) {
    	 
       	try { 
       		if (username != null && password != null) { 
       			Context.authenticate(username, password);
       		}
       		// Retrieve the dataset definition
           	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type, id); 
	
           	// Create evaluation context
    		EvaluationContext context = new EvaluationContext();	    	
    		
    		// Set the cohort to use when evaluating the dataset
    		Cohort baseCohort = Context.getPatientSetService().getAllPatients();
	    	context.setBaseCohort(baseCohort);	 
	    	context.setLimit(100);
	    	
	    	
	    	// Evaluate the dataset
	    	//DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
           	//DataSet dataSet = service.evaluate(dataSetDefinition, context);

	    	// Evaluate dataset report
	    	ReportDefinition reportDefinition = new ReportDefinition();
	    	reportDefinition.addDataSetDefinition("test", dataSetDefinition, null);
	    	ReportData reportData = Context.getService(ReportService.class).evaluate(reportDefinition, context);

	    	ReportRenderer renderer = new CsvReportRenderer();
	    		    	
	    	// We usually render, but for now we're just going to return it
	    	if ("xml".equalsIgnoreCase(format)) { 
	    		renderer = new XmlReportRenderer();
	    	} 
	    	else if ("tsv".equalsIgnoreCase(format)) { 
	    		renderer = new TsvReportRenderer();
	    	}
	    	else if ("xls".equalsIgnoreCase(format)) { 
	    		renderer = new XlsReportRenderer();
	    	}
			response.setContentType(renderer.getRenderedContentType(reportDefinition, null));
			response.setHeader("Content-Disposition", "attachment; filename=\"" + dataSetDefinition.getName() + "." + format + "\"");  	    	
	    	renderer.render(reportData, null, response.getOutputStream());
	    	response.getOutputStream().close();
       	} 
       	catch (Exception e) { 
       		log.error("Exception ocurred while downloading dataset ", e);
       	}
       	
       	//return "redirect:/module/reporting/showDataSet.form?uuid="+uuid;
    }    
        
    
    
    
    /**
     * 
     * @param uuid
     * @param className
     * @param id
     * @return
     */
    public DataSetDefinition getDataSetDefinition(String uuid, String className, Integer id) {     	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	
    	DataSetDefinition dataSetDefinition = null;
    	if (uuid != null) { 
			log.info("Retrieving dataset definition by uuid " + uuid);
    		dataSetDefinition = service.getDataSetDefinitionByUuid(uuid);    	
    	}

    	
    	log.info("Dataset definition: " + dataSetDefinition);
    	
    	// If we cannot find the dataset by uuid or if no uuid was specified, then try to retrieve it by ID
    	if (dataSetDefinition == null || dataSetDefinition.getUuid() == null) {     		
			try {
				log.info("Retrieving dataset definition by ID " + id + " and class " + className);
				
				Class<? extends DataSetDefinition> type = 
					(Class<? extends DataSetDefinition>) Class.forName(className);
				dataSetDefinition = service.getDataSetDefinition(type, id); 
			} 
			catch (ClassNotFoundException e) {
				log.error("Unable to retrieve dataset definition by id and type: ", e);
			}    	
    	}
    	return dataSetDefinition;    	
    }
    
    
    
}
