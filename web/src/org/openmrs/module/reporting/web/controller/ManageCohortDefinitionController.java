package org.openmrs.module.reporting.web.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ManageCohortDefinitionController {
	private Log log = LogFactory.getLog(this.getClass());
	
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
    	
    	
    	List<Parameter> parameters = cohortDefinition.getAvailableParameters();
    	
    	for (Parameter parameter : parameters) { 
    		String parameterName = parameter.getName();
    		String parameterValue = request.getParameter("parameter." + parameterName + ".defaultValue");

    		// Worst case scenario, we assign the default value to the string value passed in
    		Object defaultValue = parameterValue;

    		// Check to see if the user wants the parameter to be included
    		Boolean parameterIncluded = 
    			ServletRequestUtils.getBooleanParameter(request, "parameter." + parameterName + ".include", false);
    		
    		if (parameterIncluded) {     			    			    			
    			Parameter parameterAdded = new Parameter();
    			
    			parameterAdded.setName(parameter.getName());
    			parameterAdded.setLabel(parameter.getLabel());
    			parameterAdded.setClazz(parameter.getClazz());
    			parameterAdded.setAllowMultiple(ServletRequestUtils.getBooleanParameter(request, "parameter." + parameterName + ".allowMultiple", false));
    			parameterAdded.setAllowUserInput(ServletRequestUtils.getBooleanParameter(request, "parameter." + parameterName + ".allowUserInput", true));
    			parameterAdded.setRequired(ServletRequestUtils.getBooleanParameter(request, "parameter." + parameterName + ".required", true));

    			if (parameter.getClazz().isAssignableFrom(Date.class)) { 
    				try { 
    					defaultValue = Context.getDateFormat().parse(parameterValue);
    				} 
    				catch (ParseException e) { 
    					log.error("Error while parsing date: " + parameterValue, e); 
    				}
    			} 
    			else if (parameter.getClazz().isAssignableFrom(Integer.class)) { 
    				defaultValue = Integer.parseInt(parameterValue);    				
    			}
    			else { 
    				// TODO Essentially, this means we don't know how to handle this value  Should we throw an error here?  
    				// TODO If so, then we need a "string" handler
    				defaultValue = parameterValue;
    			}
    			
    			log.info("Setting parameter value: " + defaultValue + " " + defaultValue.getClass());
    			
    			parameterAdded.setDefaultValue(defaultValue);
    			
    			cohortDefinition.addParameter(parameterAdded);
    		}
    	}
    	
    	
    	

    	Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);

        return "redirect:/module/reporting/manageCohortDefinitions.list";
    }
    

}
