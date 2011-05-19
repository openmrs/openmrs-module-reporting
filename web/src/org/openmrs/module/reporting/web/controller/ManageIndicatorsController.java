package org.openmrs.module.reporting.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageIndicatorsController {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 * @param includeRetired
	 * @param model
	 * @return
	 */
    @RequestMapping("/module/reporting/indicators/manageIndicators")
    public void manageIndicators(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	 	
    	List<Indicator> indicators = 
    		Context.getService(IndicatorService.class).getAllDefinitions(false);

    	model.addAttribute("indicators", indicators);
    }
    
    /**
     * 
     * @param includeRetired
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/indicators/selectCohort")
    public void selectCohort(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	 	
       	// Add all saved CohortDefinitions
    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	model.addAttribute("cohortDefinitions", cds.getAllDefinitions(retired));
    	
    	// Add all available CohortDefinitions
    	model.addAttribute("types", cds.getDefinitionTypes());
    }
    
    
    /**
     * 
     * @param uuid
     * @param action
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/indicators/editIndicator")
    public String editIndicator(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="action") String action,
    		ModelMap model) {

    	// Get cohort definitions 
    	CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
    	model.addAttribute("cohortDefinitions", 
    			cohortDefinitionService.getAllDefinitions(false));
    	
    	
    	// Get indicator by UUID
    	Indicator indicator = 
    		Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
    	
     	model.addAttribute("indicator", indicator);
        return "/module/reporting/indicators/indicatorEditor";
    }
    
    /**
     * Saves the indicator.
     * 
     * This was one of the first annotated controllers, so it's a bit ugly.
     * 
     * TODO Cleanup - should be more like the indicator report editor or cohort definition editor.
     * 
     * @param uuid
     * @param name
     * @param description
     * @param cohortDefinitionName
     * @param logicQuery
     * @param aggregator
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/indicators/saveIndicator")
    public String saveIndicator(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="action") String action,
            @RequestParam(required=false, value="name") String name,
            @RequestParam(required=false, value="description") String description,
            @RequestParam(required=false, value="cohortDefinition.name") String cohortDefinitionName,
            @RequestParam(required=false, value="cohortDefinition.uuid") String cohortDefinitionUuid,
            @RequestParam(required=false, value="aggregator") String aggregator,            
    		ModelMap model) {
    	
    	if ("cancel".equalsIgnoreCase(action))  {
    		return "redirect:/module/reporting/indicators/manageIndicators.form";
    	}
    	
    	// Find indicator, if one already exists
    	CohortIndicator indicator = (CohortIndicator)
    		Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
    	
    	// Create a new indicator if one does not exist
    	if (indicator == null) { 
    		indicator = new CohortIndicator();
    	}    	
    	
    	// Set the common attributes
    	indicator.setName(name);
    	indicator.setDescription(description);
    	
    	
    	// Find the selected cohort definition by UUID
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	log.debug("Looking up cohort definition with uuid " + cohortDefinitionUuid);
    	CohortDefinition cohortDefinition = service.getDefinitionByUuid(cohortDefinitionUuid);
    	
    	// If we don't find the cohort definition by UUID, then we look it up by name
    	if (cohortDefinition == null) { 
	    	List<CohortDefinition> cohortDefinitions = 
	    		service.getDefinitions(cohortDefinitionName, true);
    	
	    	// Require cohort definition or logic criteria 
	    	if (cohortDefinitions == null || cohortDefinitions.isEmpty()) { 
	    		throw new APIException("Cohort Definition is required");
	    	}    	
	    	cohortDefinition = cohortDefinitions.get(0);
    	}
    	log.debug("Setting cohort definition: " + cohortDefinition);
    	
    	// TODO We need to map the indicator 
    	indicator.setCohortDefinition(cohortDefinition, "");
    	
    	// Save the indicator to the database
    	Context.getService(IndicatorService.class).saveDefinition(indicator);

    	// When a cohort definition is selected, there's an implicit form submit
    	// We want users to be redirected to the form in this case.
    	if (action == null || action.equals("")) { 
    		return "redirect:/module/reporting/indicators/editIndicator.form?uuid="+uuid;
    	}    	
    	
    	// If user clicks "save" they will be taken back to the indicator list
        return "redirect:/module/reporting/indicators/manageIndicators.form";
    }
    
    
    /**
     * 
     * @param uuid
     * @return
     */
    @RequestMapping("/module/reporting/indicators/purgeIndicator")
    public String saveIndicator(
    		@RequestParam(required=false, value="uuid") String uuid) {
    	
    	log.debug("Looking up indicator by uuid " + uuid);

    	Indicator indicator =
    		Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
    	
    	log.debug("Indicator: " + indicator);
    	if (indicator != null) {     		
    		log.debug("Purging indicator: " + indicator);
    		Context.getService(IndicatorService.class).purgeDefinition(indicator);
    	}     	
    	
        return "redirect:/module/reporting/indicators/manageIndicators.form";
    }
    
    
    
}
