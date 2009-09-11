package org.openmrs.module.reporting.web.datasets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSetException;
import org.openmrs.module.dataset.column.LogicDataSetColumn;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.renderer.XlsReportRenderer;
import org.openmrs.module.report.renderer.XmlReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageDatasetsController {

	protected Log log = LogFactory.getLog(this.getClass());
	
	
	/**
	 * 
	 * @param includeRetired
	 * @param model
	 */
    @RequestMapping("/module/reporting/datasets/manageDataSets")
    public void manageDataSets(
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
    public String editDataSet(
    		@RequestParam(required=false, value="id") Integer id,
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="cohortSize") Integer cohortSize,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model) {

    	DataSetDefinition dataSetDefinition = 
    		getDataSetDefinition(uuid, type, id);
    	    	
    	model.addAttribute("dataSetDefinition", dataSetDefinition);
        return "/module/reporting/datasets/datasetEditor";
    }
    
    
    
	@RequestMapping("/module/reporting/datasets/removeDataSet")
    public String removeDataSet(
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
    @RequestMapping("/module/reporting/datasets/addLogicColumn")
    public String addLogicColumn(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="type") String type,
    		@RequestParam("columnName") String columnName,
    		@RequestParam("logicQuery") String logicQuery,
    		ModelMap model) {

    	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type, id);
    	if (dataSetDefinition instanceof PatientDataSetDefinition) { 
    		PatientDataSetDefinition instance = (PatientDataSetDefinition) dataSetDefinition;
    		instance.addLogicColumn(new LogicDataSetColumn(columnName, String.class, logicQuery));
    		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(instance);
    	}    	   
    	
    	return "redirect:/module/reporting/datasets/editDataSet.form?uuid=" + uuid;
    }
	
	
    
    /**
     * Adds a column to the given dataset.  
     * @return
     */
    @RequestMapping("/module/reporting/datasets/addConceptColumn")
    public String addConceptColumn(
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
    		
    		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(instance);
    	}
    	else if (dataSetDefinition instanceof PatientDataSetDefinition) {
    		PatientDataSetDefinition instance = 
    			(PatientDataSetDefinition) dataSetDefinition;
        	//Concept concept = Context.getConceptService().getConcept(conceptId);    		
        	// TODO Implement concept column in patient dataset 
    		throw new DataSetException("Patient Data Set Definition does not currently support additional columns");
    	}    		
    	return "redirect:/module/reporting/datasets/editDataSet.form?uuid=" + uuid;
    }
    
    @RequestMapping("/module/reporting/datasets/removeColumn")
    public String removeColumn(
    		@RequestParam(required=false, value="id") Integer id,
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") String type,
    		@RequestParam("columnKey") String columnKey,
    		ModelMap model) {

    	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type, id);
    	
    	if (dataSetDefinition instanceof PatientDataSetDefinition) {
    		PatientDataSetDefinition instance = 
    			(PatientDataSetDefinition) dataSetDefinition;
    		instance.removeLogicColumn(columnKey);
    		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);
    	}

    	
    	return "redirect:/module/reporting/datasets/editDataSet.form?uuid=" + uuid;
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
    public String newDataSet(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
    		ModelMap model) {
    	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDataSetDefinition(uuid, type);
    	
    	dataSetDefinition.setName("(untitled dataset definition)");
    	dataSetDefinition = service.saveDataSetDefinition(dataSetDefinition);
    	
     	model.addAttribute("dataSetDefinition", dataSetDefinition);
     	     	
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
    public String saveDataSet(
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
    @SuppressWarnings("unchecked")
	@RequestMapping("/module/reporting/datasets/viewDataSet")
    public String viewDataset(
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="cohortId") String cohortId,
            @RequestParam(required=false, value="dataSetId") String dataSetId,
            @RequestParam(required=false, value="indicatorId") String indicatorId,
            @RequestParam(required=false, value="limit") Integer limit,
    		ModelMap model
     	) {

    	// 
    	if (cohortId != null && dataSetId != null) { 
	    	
	    	// Step 1 
	    	// Find the dataset definition and if it's still null, 
	       	// then we should use a default dataset definition
	       	DataSetDefinition dataSetDefinition = getDataSetDefinition(dataSetId, type, id);    	
	    	model.addAttribute("dataSetDefinition", 
	    			dataSetDefinition == null ? new PatientDataSetDefinition() : dataSetDefinition);
	
	    	// Step 2 
	    	// Find the cohort definition and evaluate the base cohort
    		// Set the cohort to use when evaluating the dataset
    		Cohort cohort = evaluateCohort(cohortId);
	    	    		
    		log.info("evaluated cohort: " + cohort.size());
    		
    		model.addAttribute("cohort", cohort);
	    	model.addAttribute("cohortDefinition", 
	    			Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(cohortId));    		    	

	    	// If we don't have a cohort yet, just get all patients
	    	if (cohort == null || cohortId.equals("all")) 
	    		cohort = Context.getPatientSetService().getAllPatients();

	    	
	    	// Step 3 Set up the evaluation context
	    	EvaluationContext context = new EvaluationContext();
	    	context.setLimit(limit);
	    	context.setBaseCohort(cohort);	 
	    	
    		log.info("just before dataset evaluation: " + context.getBaseCohort().size());

    		// Step 4 Evaluate the dataset
	    	model.addAttribute("dataSet", 
	    			Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context));
	    	
	    	// TODO Put dataset in the session so we can download it later
	    	
    	}
    	
    	// The following two attributes should ALWAYS be added to the request 
    	
    	// Add all cohort definition to the request (allow user to choose)
    	model.addAttribute("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false)); 

    	// Add all dataset definition to the request (allow user to choose)
    	model.addAttribute("dataSetDefinitions", 
    			Context.getService(DataSetDefinitionService.class).getAllDataSetDefinitions(false)); 
    	
    	
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
            @RequestParam(required=false, value="cohortId") String cohortId,
            @RequestParam(required=false, value="dataSetId") String dataSetId,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="format") String format,
            @RequestParam(required=false, value="limit") Integer limit,
            @RequestParam(required=false, value="username") String username,
            @RequestParam(required=false, value="password") String password,
            HttpServletResponse response) {
    	 
       	try { 
       		
       		// Hack to allow external client to download data 
       		// (need to find better solution for this)
       		if (username != null && password != null) 
       			Context.authenticate(username, password);
       		
       		
       		// Step 1 
       		// Retrieve the dataset definition
           	DataSetDefinition dataSetDefinition = getDataSetDefinition(dataSetId, type, id); 
           	if (dataSetDefinition == null)
           		throw new APIException("The dataset definition that you selected could not be found.");           	
           	
    		
    		// Step 2
    		// Evaluate cohort
    		Cohort baseCohort = evaluateCohort(cohortId);

           	// Step 3
           	// Create evaluation context for running report
    		EvaluationContext context = new EvaluationContext();
    		context.setBaseCohort(baseCohort);	 
    		context.setLimit(limit);
    		
	    		    
    		// Step 4
	    	// Evaluate dataset report
	    	ReportDefinition reportDefinition = new ReportDefinition();
	    	reportDefinition.addDataSetDefinition(dataSetDefinition.getName(), dataSetDefinition, null);
	    	ReportData reportData = Context.getService(ReportService.class).evaluate(reportDefinition, context);

	    	// Step 5 
	    	// Render using one of the given formats
	    	// FIXME This should be taken care of by the rendering handler framework
	    	ReportRenderer renderer = new CsvReportRenderer();
	    	if ("xml".equalsIgnoreCase(format)) { 
	    		renderer = new XmlReportRenderer();
	    	} else if ("tsv".equalsIgnoreCase(format)) { 
	    		renderer = new TsvReportRenderer();
	    	} else if ("xls".equalsIgnoreCase(format)) { 
	    		renderer = new XlsReportRenderer();
	    	} else if ("html".equalsIgnoreCase(format)) { 
	    		renderer = new SimpleHtmlReportRenderer();
	    	}
	    	
	    	// Step 6
	    	// Write rendered data to response
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

    	// FIXME This is used because we current cannot use UUIDs to locate data export dataset definitions
    	// If we cannot find the dataset by uuid or if no uuid was specified, then try to retrieve it by ID
    	if (dataSetDefinition == null || dataSetDefinition.getUuid() == null) {     		
			try {
				if (className != null) { 
					Class<? extends DataSetDefinition> type = 
						(Class<? extends DataSetDefinition>) Class.forName(className);
					dataSetDefinition = service.getDataSetDefinition(type, id); 
				}
			} 
			catch (ClassNotFoundException e) {
				log.error("Unable to retrieve dataset definition by id and type: ", e);
			}    	
    	}
    	return dataSetDefinition;    	
    }
    
    
    
    /**
     * Evaluates a cohort 
     * 
     * TODO Move to service layer 
     * 
     * @param uuid
     * @param evaluationContext
     * @return
     */
    public Cohort evaluateCohort(String uuid) {
    	EvaluationContext evaluationContext = new EvaluationContext();
		Cohort cohort = Context.getPatientSetService().getAllPatients();
		evaluationContext.setBaseCohort(cohort);
    	CohortDefinition cohortDefinition = null;    	
    	if (StringUtils.isNotEmpty(uuid)) { 
    		cohortDefinition = 
    			Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(uuid);
    		if (cohortDefinition != null) {
    			cohort = 
    				Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);
    		}
    	}     		    	
    	return cohort;
    }
    
    
}
