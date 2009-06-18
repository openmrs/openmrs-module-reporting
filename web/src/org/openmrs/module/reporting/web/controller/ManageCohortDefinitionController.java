package org.openmrs.module.reporting.web.controller;

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

    @RequestMapping("/module/reporting/manageCohortDefinitions")
    public String viewCohortDefinitions(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model
    ) {
    	// Add all saved CohortDefinitions
    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	model.addAttribute("cohortDefinitions", cds.getAllCohortDefinitions(retired));
    	
    	// Add all available CohortDefinitions
    	model.addAttribute("types", cds.getCohortDefinitionTypes());
    	
        return "/module/reporting/cohorts/cohortDefinitionManager";
    }
    
    @RequestMapping("/module/reporting/editCohortDefinition")
    public String editCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
            @RequestParam(required=false, value="returnUrl") String returnUrl,
    		ModelMap model
    ) {
    	CohortDefinition cd = getCohortDefinition(uuid, type);
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
    	CohortDefinition cd = getCohortDefinition(uuid, type);
    	cd.setName(name);
    	cd.setDescription(description);
    	
    	cd = Context.getService(CohortDefinitionService.class).saveCohortDefinition(cd);
    	
        return "redirect:/module/reporting/cohorts/manageCohortDefinition.list";
    }
    
    /**
     * Helper method which checks that either uuid or type is passed, and returns either the
     * saved CohortDefinition with the passed uuid, or a new instance of the CohortDefinition
     * represented by the passed type.  Throws an IllegalArgumentException if any of this is invalid.
     */
    protected CohortDefinition getCohortDefinition(String uuid, Class<? extends CohortDefinition> type) {
    	CohortDefinition cd = null;
    	if (StringUtils.hasText(uuid)) {
	    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
	    	cd = cds.getCohortDefinitionByUuid(uuid);
    	}
    	else if (type != null) {
     		try {
    			cd = type.newInstance();
    		}
    		catch (Exception e) {
    			throw new IllegalArgumentException("Unable to instantiate a CohortDefinition of type: " + type);
    		}
    	}
    	else {
    		throw new IllegalArgumentException("You must supply either a uuid or a type");
    	}
    	return cd;
    }
}
