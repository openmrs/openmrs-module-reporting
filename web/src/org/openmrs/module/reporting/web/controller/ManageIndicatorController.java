package org.openmrs.module.reporting.web.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.aggregation.Aggregator;
import org.openmrs.module.indicator.aggregation.CountAggregator;
import org.openmrs.module.indicator.aggregation.DistinctAggregator;
import org.openmrs.module.indicator.aggregation.MaxAggregator;
import org.openmrs.module.indicator.aggregation.MeanAggregator;
import org.openmrs.module.indicator.aggregation.MedianAggregator;
import org.openmrs.module.indicator.aggregation.MinAggregator;
import org.openmrs.module.indicator.aggregation.SumAggregator;
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
    		Context.getService(IndicatorService.class).getAllIndicators(false);

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

    	// Get cohort definitions 
    	CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
    	model.addAttribute("cohortDefinitions", 
    			cohortDefinitionService.getAllCohortDefinitions(false));
    	
    	
    	// Get indicator by UUID
    	Indicator indicator = 
    		Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
    	
     	model.addAttribute("indicator", indicator);
        return "/module/reporting/indicators/indicatorEditor";
    }
    
    /**
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
    @RequestMapping("/module/reporting/saveIndicator")
    public String saveIndicator(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("cohortDefinition.name") String cohortDefinitionName,
            @RequestParam("cohortDefinition.uuid") String cohortDefinitionUuid,
            @RequestParam("aggregator") String aggregator,            
    		ModelMap model) {
    	CohortIndicator indicator = (CohortIndicator)
    		Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
    	
    	
    	if (indicator == null) { 
    		indicator = new CohortIndicator();
    	}    	
    	
    	// Set the common attributes
    	indicator.setName(name);
    	indicator.setDescription(description);
    	
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	// Find the selected cohort definition by name
    	// TODO Should be a lookup by UUID

    	log.info("Looking up cohort definition with uuid " + cohortDefinitionUuid);
    	CohortDefinition cohortDefinition = service.getCohortDefinitionByUuid(cohortDefinitionUuid);
    	
    	// If we don't find the cohort definition by UUID, then we look it up by name
    	if (cohortDefinition == null) { 
	    	List<CohortDefinition> cohortDefinitions = 
	    		service.getCohortDefinitions(cohortDefinitionName, true);
    	
	    	// Require cohort definition or logic criteria 
	    	if (cohortDefinitions == null || cohortDefinitions.isEmpty()) { 
	    		throw new APIException("Cohort Definition is required");
	    	}    	
    	
	    	cohortDefinition = cohortDefinitions.get(0);
    	}
    	log.info("Setting cohort definition: " + cohortDefinition);
    	
    	indicator.setCohortDefinition(cohortDefinition, "");
    	
    	//indicator.setParameters(parameters);
    	//indicator.setLogicCriteria(logicCriteria);

    	
    	// Set the aggregation method 
    	if ("CountAggregator".equalsIgnoreCase(aggregator)) { 
    		indicator.setAggregator(CountAggregator.class);
    	} 
    	else if ("DistinctAggregator".equalsIgnoreCase(aggregator)) { 
    		indicator.setAggregator(DistinctAggregator.class);
    	} 
    	else if ("MaxAggregator".equalsIgnoreCase(aggregator)) { 
    		indicator.setAggregator(MaxAggregator.class);
    	} 
    	else if ("MeanAggregator".equalsIgnoreCase(aggregator)) { 
    		indicator.setAggregator(MeanAggregator.class);
    	} 
    	else if ("MedianAggregator".equalsIgnoreCase(aggregator)) { 
    		indicator.setAggregator(MedianAggregator.class);
    	} 
    	else if ("MinAggregator".equalsIgnoreCase(aggregator)) { 
    		indicator.setAggregator(MinAggregator.class);
    	} 
    	else if ("SumAggregator".equalsIgnoreCase(aggregator)) {     	
    		indicator.setAggregator(SumAggregator.class);
    	} 
    	else { 
    		throw new APIException("Aggregator " + aggregator + " is not currently supported");
    	}
    	
    	Context.getService(IndicatorService.class).saveIndicator(indicator);

        return "redirect:/module/reporting/manageIndicators.list";
    }
    
    
    /**
     * 
     * @param uuid
     * @return
     */
    @RequestMapping("/module/reporting/purgeIndicator")
    public String saveIndicator(
    		@RequestParam(required=false, value="uuid") String uuid) {
    	
    	log.info("Looking up indicator by uuid " + uuid);

    	Indicator indicator =
    		Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
    	
    	log.info("Indicator: " + indicator);
    	if (indicator != null) {     		
    		log.info("Purging indicator: " + indicator);
    		Context.getService(IndicatorService.class).purgeIndicator(indicator);
    	}     	
    	
        return "redirect:/module/reporting/manageIndicators.list";
    }
    
    
    
}
