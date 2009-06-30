package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageCohortDefinitionController {
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 * @param includeRetired
	 * @param model
	 * @return
	 */
    @RequestMapping("/module/reporting/manageCohortDefinitions")
    public String viewCohortDefinitions(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model
    ) {
    	// Add all saved CohortDefinitions
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	model.addAttribute("cohortDefinitions", service.getAllCohortDefinitions(retired));
    	
    	// Add all available CohortDefinitions
    	model.addAttribute("types", service.getCohortDefinitionTypes());
    	
        return "/module/reporting/cohorts/cohortDefinitionManager";
    }
    
    
    /**
     * 
     * @param uuid
     * @param type
     * @param returnUrl
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/editCohortDefinition")
    public String editCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
            @RequestParam(required=false, value="returnUrl") String returnUrl,
    		ModelMap model
    ) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cd = service.getCohortDefinition(uuid, type);
     	model.addAttribute("cohortDefinition", cd);
     	
     	
        return "/module/reporting/cohorts/cohortDefinitionEditor";
    }
    
    @RequestMapping("/module/reporting/saveCohortDefinition")
    public String saveCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
    		ModelMap model
    ) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cohortDefinition = service.getCohortDefinition(uuid, type);
    	cohortDefinition.setName(name);
    	cohortDefinition.setDescription(description);
    	//cohortDefinition.addParameter(parameter);
    	
    	Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);

        return "redirect:/module/reporting/manageCohortDefinitions.list";
    }
    

}
