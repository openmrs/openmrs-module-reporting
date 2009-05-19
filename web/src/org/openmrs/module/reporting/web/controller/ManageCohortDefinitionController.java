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
            @RequestParam("uuid") String uuid,
            @RequestParam("type") Class<? extends CohortDefinition> type,
            @RequestParam(required=false, value="returnUrl") String returnUrl,
    		ModelMap model
    ) {
    	CohortDefinition cd = null;
    	System.out.println("In controller");
    	if (StringUtils.hasText(uuid)) {
    		System.out.println("UUID = " + uuid);
	    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
	    	cds.getCohortDefinitionByUuid(uuid);
    	}
    	else {
    		System.out.println("Type = " + type);
    		try {
    			cd = type.newInstance();
    		}
    		catch (Exception e) {
    			System.out.println("Exception: " + e);
    			throw new IllegalArgumentException("Unable to instantiate a CohortDefinition of type: " + type);
    		}
    	}
    	System.out.println("Adding def: " + cd);
    	model.addAttribute(cd);
        return "/module/reporting/editCohortDefinition";
    }
}
