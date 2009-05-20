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

    @RequestMapping("/module/reporting/cohortDefinitions")
    public String viewCohortDefinitions(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model
    ) {
    	// Add all saved CohortDefinitions
    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	model.addAttribute(cds.getAllCohortDefinitions(retired));
    	
    	// Add all available CohortDefinitions
    	model.addAttribute("types", cds.getCohortDefinitionTypes());
    	
        return "/module/reporting/cohortDefinitions";
    }
    
    @RequestMapping("/module/reporting/editCohortDefinition")
    public String editCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
            @RequestParam(required=false, value="returnUrl") String returnUrl,
    		ModelMap model
    ) {
    	CohortDefinition cd = null;
    	if (StringUtils.hasText(uuid)) {
	    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
	    	cds.getCohortDefinitionByUuid(uuid);
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
     	model.addAttribute("cohortDefinition", cd);
        return "/module/reporting/editCohortDefinition";
    }
}
