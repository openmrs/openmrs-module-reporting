package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageDatasetDefinitionController {

	protected Log log = LogFactory.getLog(this.getClass());
	
    @RequestMapping("/module/reporting/manageDatasets")
    public String manageDatasetDefinition(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	DataSetDefinitionService service = 
    		Context.getService(DataSetDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	
    	log.info("Service: " + service);
    	
    	List<DataSetDefinition> datasetDefinitions = new ArrayList<DataSetDefinition>();
    	
    	try { 
    		datasetDefinitions = service.getAllDataSetDefinitions(retired);
    	} catch (Exception e) { 
    		log.error("Could not fetch dataset definitions", e);
    	}
    	
    	model.addAttribute("datasetDefinitions", datasetDefinitions);
    	
        return "/module/reporting/datasets/datasetManager";
    }
    
    /**
     * 
     * @param uuid
     * @param type
     * @param returnUrl
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
	@RequestMapping("/module/reporting/showDataset")
    public String showDatasetDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="className") String className,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model
    ) {

    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDataSetDefinitionByUuid(uuid);    	

    	// If we cannot find the dataset by uuid, then try to retrieve it by ID
    	if (dataSetDefinition == null) {     		
			try {
				Class<? extends DataSetDefinition> type = 
					(Class<? extends DataSetDefinition>) Class.forName(className);
				dataSetDefinition = service.getDataSetDefinition(type, id);    		
			} catch (ClassNotFoundException e) {
				log.error("Unable to retrieve dataset definition by id and type: ", e);
			}
    	
    	}
    	
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
    @RequestMapping("/module/reporting/saveDataset")
    public String saveDatasetDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
    		ModelMap model
    ) {
    	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDataSetDefinitionByUuid(uuid);
    	dataSetDefinition.setName(name);
    	dataSetDefinition.setDescription(description);
    	
    	dataSetDefinition = 
    		service.saveDataSetDefinition(dataSetDefinition);
    	
        return "redirect:/module/reporting/showDataset.form?uuid="+dataSetDefinition.getUuid();
    }
    
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/showDatasetPreview")
    public String showDatasetPreview(
       		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="className") String className,
            @RequestParam(required=false, value="cohortName") String cohortName,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model
     	) {
    	
    	List<CohortDefinition> cohortDefinitions = 
    		Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
    	
    	
    	
       	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDataSetDefinitionByUuid(uuid); 
    	
    	// If we cannot find the dataset by uuid, then try to retrieve it by ID
    	if (dataSetDefinition == null) {     		
			try {
				Class<? extends DataSetDefinition> type = 
					(Class<? extends DataSetDefinition>) Class.forName(className);
				dataSetDefinition = service.getDataSetDefinition(type, id);    		
			} 
			catch (ClassNotFoundException e) {
				log.error("Unable to retrieve dataset definition by id and type: ", e);
			}
    	
    	}
    	
    	if ("preview".equalsIgnoreCase(action)) { 
	    	EvaluationContext context = new EvaluationContext();
	    	
	    	Cohort baseCohort = new Cohort();
	    	CohortDefinition cohortDefinition = null;

	    	// If a cohort name was specified, we'll use that cohort
	    	if (cohortName != null && !cohortName.equals("")) { 
	    		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
	    		cohortDefinition = cds.getCohortDefinitions(cohortName, true).get(0);
	    		baseCohort = cds.evaluate(cohortDefinition, context);
	    	} 
	    	
	    	// Otherwise, use a random cohort from saved cohorts
	    	// TODO This should be removed once we get serialization working
	    	if (baseCohort == null || baseCohort.isEmpty()) { 
	    		Random random = new Random();	    		
		    	List<Cohort> cohorts = Context.getCohortService().getAllCohorts();
	    		baseCohort = cohorts.get(random.nextInt(cohorts.size()));
	    	}

	    	context.setBaseCohort(baseCohort);	    		
	    	
	    	// Evaluate the dataset
	    	DataSet dataSet = service.evaluate(dataSetDefinition, context);
	    	
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
    
    
    
}
