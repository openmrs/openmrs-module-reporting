package org.openmrs.module.reporting.web.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.ParameterUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageCohortDefinitionController {
	
	protected static Log log = LogFactory.getLog(ManageCohortDefinitionController.class);
	
	/**
	 * Lists the cohort definitions.
	 * 
	 * @param includeRetired
	 * @param model
	 * @return
	 */
    @RequestMapping("/module/reporting/manageCohortDefinitions")
    public String manageCohortDefinitions(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	// Add all saved CohortDefinitions
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	model.addAttribute("cohortDefinitions", service.getAllCohortDefinitions(retired));
    	
    	// Add all available CohortDefinitions
    	model.addAttribute("types", service.getCohortDefinitionTypes());
    	
        return "/module/reporting/cohorts/cohortDefinitionManager";
    }
    
    
    /**
     * Basically acts as the formBackingObject() method for saving a 
     * cohort definition.
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
    		ModelMap model) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cd = service.getCohortDefinition(uuid, type);
     	model.addAttribute("cohortDefinition", cd);
     	
     	
        return "/module/reporting/cohorts/cohortDefinitionEditor";
    }
    
    
    
    /**
     * Purges a cohort definitions
     * 
     * @param uuid
     * @param type
     * @param returnUrl
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/purgeCohortDefinition")
    public String purgeCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cohortDefinition = service.getCohortDefinition(uuid, null);
     	
    	service.purgeCohortDefinition(cohortDefinition);
    	     	
        return "redirect:/module/reporting/manageCohortDefinitions.list";
    }    

    /**
     * Evaluates a cohort definition given a uuid.
     * 
     * @param uuid
     * @param type
     * @param returnUrl
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/evaluateCohortDefinition")
    public String evaluateCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
    		ModelMap model) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cohortDefinition = service.getCohortDefinition(uuid, type);
     	
    	// Evaluate the cohort definition
    	EvaluationContext context = new EvaluationContext();
    	Cohort cohort = service.evaluate(cohortDefinition, context);
    	
    	// create the model and view to return
     	model.addAttribute("cohort", cohort);
     	model.addAttribute("cohortDefinition", cohortDefinition);
     	
        return "/module/reporting/cohortDefinitionEvaluator";
    }    

    
    /**
     * Saves a cohort definition.
     * 
     * @param uuid
     * @param type
     * @param name
     * @param description
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/saveCohortDefinition")
    @SuppressWarnings("unchecked")
    public String saveCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            HttpServletRequest request,
    		ModelMap model
    ) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	
    	
    	// Locate or create cohort definition
    	CohortDefinition cohortDefinition = service.getCohortDefinition(uuid, type);
    	cohortDefinition.setName(name);
    	cohortDefinition.setDescription(description);
    	cohortDefinition.getParameters().clear();
    	
    	for (Parameter p : cohortDefinition.getAvailableParameters()) {
    		
    		String valParamName = "parameter." + p.getName() + ".defaultValue";
    		boolean isParameter = "t".equals(request.getParameter("parameter."+p.getName()+".allowAtEvaluation"));
    		Object valToSet = null;
    		
			if (p.getCollectionType() != null) {
				String[] paramVals = request.getParameterValues(valParamName);
				if (paramVals != null) {
					Collection defaultValue = ParameterUtil.getNewCollection(p.getCollectionType());
					for (String val : paramVals) {
						if (StringUtils.hasText(val)) {
							defaultValue.add(ParameterUtil.convertStringToObject(val, p.getClazz()));
						}
					}
					valToSet = defaultValue;
				}
			}
			else {
				String paramVal = request.getParameter(valParamName);
				if (StringUtils.hasText(paramVal)) {
					valToSet = ParameterUtil.convertStringToObject(paramVal, p.getClazz());
				}
			}
			
			if (isParameter) {
				cohortDefinition.enableParameter(p.getName(), valToSet, true);
			}
			else {
				ParameterUtil.setAnnotatedFieldFromParameter(cohortDefinition, p, valToSet);
			}
    	}
    	
    	log.warn("Saving: " + cohortDefinition);
    	Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);

        return "redirect:/module/reporting/manageCohortDefinitions.list";
    }
    

}
