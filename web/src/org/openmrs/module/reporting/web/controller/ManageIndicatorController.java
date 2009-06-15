package org.openmrs.module.reporting.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageIndicatorController {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 
	 * @param includeRetired
	 * @param model
	 * @return
	 */
    @RequestMapping("/module/reporting/manageIndicators")
    public String manageIndicators(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	 	
    	List<Indicator> indicators = 
    		Context.getService(IndicatorService.class).getAllIndicators(true);

    	model.addAttribute("indicators", indicators);
    	
        return "/module/reporting/indicators/indicatorManager";
    }
    
    /**
     * 
     * @param includeRetired
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/selectCohort")
    public String selectCohort(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	 	
       	// Add all saved CohortDefinitions
    	CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	model.addAttribute("cohortDefinitions", cds.getAllCohortDefinitions(retired));
    	
    	// Add all available CohortDefinitions
    	model.addAttribute("types", cds.getCohortDefinitionTypes());
    	
        return "/module/reporting/indicators/selectCohort";
    }
    
    
    /**
     * 
     * @param uuid
     * @param action
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/editIndicator")
    public String editIndicator(
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=false, value="action") String action,
    		ModelMap model) {
    	
    	log.info("Get indicator by uuid");
    	Indicator indicator = 
    		Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
    	
     	model.addAttribute("indicator", indicator);
        return "/module/reporting/indicators/indicatorEditor";
    }
    
    @RequestMapping("/module/reporting/saveIndicator")
    public String saveIndicator(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
    		ModelMap model
    ) {
    	Indicator indicator =
    		Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
    	
    	
    	if (indicator == null) { 
    		indicator = new CohortIndicator();
    	}    	
    	
    	indicator.setName(name);
    	indicator.setDescription(description);
    	
    	indicator = 
    		Context.getService(IndicatorService.class).saveIndicator(indicator);
    	
        return "redirect:/module/reporting/manageIndicators.list";
    }
    
    
    
    @RequestMapping("/module/reporting/purgeIndicator")
    public String saveIndicator(
    		@RequestParam(required=false, value="uuid") String uuid) {

    	Indicator indicator =
    		Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
    	
    	if (indicator != null) { 
    		Context.getService(IndicatorService.class).purgeIndicator(indicator.getUuid());
    	}     	
    	
        return "redirect:/module/reporting/manageIndicators.list";
    }
    
    
    
}
