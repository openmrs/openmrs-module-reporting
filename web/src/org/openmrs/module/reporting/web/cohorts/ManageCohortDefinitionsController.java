package org.openmrs.module.reporting.web.cohorts;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageCohortDefinitionsController {
	
	protected static Log log = LogFactory.getLog(ManageCohortDefinitionsController.class);
	
	@ModelAttribute("customPages")
	public Map<Class<? extends CohortDefinition>, String> getCustomPages() {
		Map<Class<? extends CohortDefinition>, String> ret = new LinkedHashMap<Class<? extends CohortDefinition>, String>();
		ret.put(CompositionCohortDefinition.class, "compositionCohortDefinition.form");
		return ret;
	}
	
	/**
	 * Lists the cohort definitions.
	 * 
	 * @param includeRetired
	 * @param model
	 * @return
	 */
    @RequestMapping("/module/reporting/cohorts/manageCohortDefinitions")
    public void manageCohortDefinitions(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	// Add all saved CohortDefinitions
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	
    	// Get all cohort definitions that are not static cohort definition
    	List<CohortDefinition> cohortDefinitions = service.getAllDefinitions(retired);
    	for (Iterator<CohortDefinition> iter = cohortDefinitions.iterator(); iter.hasNext(); ) {
    		if (StaticCohortDefinition.class.isAssignableFrom(iter.next().getClass())) {
    			iter.remove();
    		}
    	}

    	model.addAttribute("cohortDefinitions", cohortDefinitions);
    	
    	// Add all available cohort definition types 
    	List<Class<? extends CohortDefinition>> types = service.getDefinitionTypes();
    	Collections.sort(types, new Comparator<Class<? extends CohortDefinition>>() {
    		public int compare(Class<? extends CohortDefinition> left, Class<? extends CohortDefinition> right) {
	            return left.getSimpleName().compareTo(right.getSimpleName());
            }
    	});
    	model.addAttribute("types", types);
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
    @RequestMapping("/module/reporting/cohorts/editCohortDefinition")
    public String editCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
    		ModelMap model) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cd = service.getDefinition(uuid, type);
     	model.addAttribute("cohortDefinition", cd);
     	model.addAttribute("configurationProperties", DefinitionUtil.getConfigurationProperties(cd));
	
        return "/module/reporting/cohorts/cohortDefinitionEditor";
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
    @RequestMapping("/module/reporting/cohorts/saveCohortDefinition")
    @SuppressWarnings("unchecked")
    public String saveCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
            @RequestParam(required=true, value="name") String name,
            @RequestParam(required=false, value="description") String description,
            HttpServletRequest request,
    		ModelMap model
    ) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	    	
    	// Locate or create cohort definition
    	CohortDefinition cohortDefinition = service.getDefinition(uuid, type);
    	cohortDefinition.setName(name);
    	cohortDefinition.setDescription(description);
    	cohortDefinition.getParameters().clear();
    	
    	for (Property p : DefinitionUtil.getConfigurationProperties(cohortDefinition)) {
    		String fieldName = p.getField().getName();
    		String prefix = "parameter." + fieldName;
    		String valParamName =  prefix + ".value"; 
    		boolean isParameter = "t".equals(request.getParameter(prefix+".allowAtEvaluation"));
    		
    		Object valToSet = WidgetUtil.getFromRequest(request, valParamName, p.getField());
    		
    		Class<? extends Collection<?>> collectionType = null;
    		Class<?> fieldType = p.getField().getType();   		
			if (ReflectionUtil.isCollection(p.getField())) {
				collectionType = (Class<? extends Collection<?>>)p.getField().getType();
				fieldType = (Class<?>)ReflectionUtil.getGenericTypes(p.getField())[0];
			}
			
			if (isParameter) {
				ReflectionUtil.setPropertyValue(cohortDefinition, p.getField(), null);
				String paramLabel = ObjectUtil.nvlStr(request.getParameter(prefix + ".label"), fieldName);
				Parameter param = new Parameter(fieldName, paramLabel, fieldType, collectionType, valToSet);
				cohortDefinition.addParameter(param);
			}
			else {
				ReflectionUtil.setPropertyValue(cohortDefinition, p.getField(), valToSet);
			}
    	}
    	
    	log.warn("Saving: " + cohortDefinition);
    	Context.getService(CohortDefinitionService.class).saveDefinition(cohortDefinition);

        return "redirect:/module/reporting/cohorts/manageCohortDefinitions.form";
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
    @RequestMapping("/module/reporting/cohorts/evaluateCohortDefinition")
    public String evaluateCohortDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends CohortDefinition> type,
    		ModelMap model) {
    	
    	CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    	CohortDefinition cohortDefinition = service.getDefinition(uuid, type);
     	
    	// Evaluate the cohort definition
    	EvaluationContext context = new EvaluationContext();
    	Cohort cohort = service.evaluate(cohortDefinition, context);
    	
    	// create the model and view to return
     	model.addAttribute("cohort", cohort);
     	model.addAttribute("cohortDefinition", cohortDefinition);
     	
        return "/module/reporting/cohorts/cohortDefinitionEvaluator";
    }    
    
    
    /**
     * Purges the cohort definition represented by the given uuid.
     * 
     * @param uuid
     * 		a universally unique identifier used to identify the cohort definition.
     * @return	
     * 		the name or URL that represents the view
     */
    @RequestMapping("/module/reporting/cohorts/purgeCohortDefinition")
    public String purgeCohortDefinition(@RequestParam(required=false, value="uuid") String uuid) {
    	CohortDefinitionService service = 
    		Context.getService(CohortDefinitionService.class);
    	CohortDefinition cohortDefinition = service.getDefinitionByUuid(uuid);
    	service.purgeDefinition(cohortDefinition);	
        return "redirect:/module/reporting/cohorts/manageCohortDefinitions.form";
    }    
    

}
