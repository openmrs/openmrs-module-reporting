package org.openmrs.module.reporting.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.service.ReportService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportDashboardController {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Constructor
	 */
	public ReportDashboardController() { }
	
	/**
	 * Registers custom editors for fields of the command class.
	 * 
	 * @param binder
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), false)); 
    }    

    /**
     * 
     * @param cohort
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/dashboard/manageCohortDashboard")
    public String manageCohortDashboard(
    		@RequestParam(required=false, value="cohort") String cohort,
    		ModelMap model) { 
    	
		model.addAttribute("selected", cohort);
    	EvaluationContext evaluationContext = new EvaluationContext();
    	if ("males".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getGenderCohort(evaluationContext, "M"));    		
    	}
    	else if ("females".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getGenderCohort(evaluationContext, "F"));    		
    	}
    	else if ("adults".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getAgeCohort(evaluationContext, 15, 150, new Date()));    		
    	}
    	else if ("children".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getAgeCohort(evaluationContext, 0, 14, new Date()));    		
    	}
    	else { 
    		
    		if (cohort != null) { 
	    		Program program = Context.getProgramWorkflowService().getProgramByName(cohort);
	    		if (program != null) 
	    			model.addAttribute("cohort", getProgramStateCohort(evaluationContext, program));
	    		else 
	    			model.addAttribute("cohort", Context.getPatientSetService().getAllPatients());    		
    		}
    	}
    	return "/module/reporting/dashboard/cohortDashboard";
    	
    }
    
	
    /**
     * Manage reporting dashboard.
     * 
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/dashboard/manageDashboard")
    public String manageDashboard(ModelMap model) {
    	    	
    	// Get all reporting objects
    	model.addAttribute("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false));

    	model.addAttribute("datasetDefinitions", 
    			Context.getService(DataSetDefinitionService.class).getAllDataSetDefinitions(false));
    	
    	
    	model.addAttribute("indicators", 
    			Context.getService(IndicatorService.class).getAllIndicators(false));
    	model.addAttribute("reportDefinitions", 
    			Context.getService(ReportService.class).getReportDefinitions());
    	model.addAttribute("reportRenderers", 
    			Context.getService(ReportService.class).getReportRenderers());
    	
    	// Get all static data
    	List<Program> programs = Context.getProgramWorkflowService().getAllPrograms();
    	
    	model.addAttribute("programs", programs);
    	model.addAttribute("encounterTypes", Context.getEncounterService().getAllEncounterTypes());
    	model.addAttribute("identifierTypes", Context.getPatientService().getAllPatientIdentifierTypes());
    	model.addAttribute("attributeTypes", Context.getPersonService().getAllPersonAttributeTypes());
    	model.addAttribute("drugs", Context.getConceptService().getAllDrugs());
    	//model.addAttribute("concepts", Context.getConceptService().getAllConcepts());
    	//model.addAttribute("tokens", Context.getLogicService().getTokens());
    	//model.addAttribute("tags", Context.getLogicService().findTags(""));
    	model.addAttribute("locations", Context.getLocationService().getAllLocations());
    	//model.addAttribute("locationTags", Context.getLocationService().getAllLocationTags());
    	model.addAttribute("forms", Context.getFormService().getAllForms());    	
    	model.addAttribute("relationshipTypes", Context.getPersonService().getAllRelationshipTypes());
    	//model.addAttribute("relationshipTypes", Context.getPatientSetService().getAllPatients());

    	EvaluationContext evaluationContext = new EvaluationContext();
    	
    	// These should be defined explicitly and configured via global properties
		model.addAttribute("males", getGenderCohort(evaluationContext, "M"));
		model.addAttribute("females", getGenderCohort(evaluationContext, "F"));    	
		model.addAttribute("adults", getAgeCohort(evaluationContext, 15, 150, new Date()));
		model.addAttribute("children", getAgeCohort(evaluationContext, 0, 14, new Date()));				
		model.addAttribute("all", Context.getPatientSetService().getAllPatients());

		Map<Program, Cohort> programCohortMap = new HashMap<Program, Cohort>();
		for (Program program : programs) {
			Cohort cohort = getProgramStateCohort(evaluationContext, program);
			log.info("Program: " + program.getName() + " " + cohort.getSize());
			programCohortMap.put(program, cohort);
		}
		model.addAttribute("programCohortMap", programCohortMap);
		
		return "/module/reporting/dashboard/dashboardManager";
    }    
    
    /**
     * Get program cohort.
     * 
     * @param evaluationContext
     * @param program
     * @return
     */
    public Cohort getProgramStateCohort(EvaluationContext evaluationContext, Program program) { 
    	ProgramStateCohortDefinition programStateCohortDefinition = new ProgramStateCohortDefinition();    	
    	programStateCohortDefinition.setProgram(program);
    	programStateCohortDefinition.setStateList(null);
    	return Context.getService(CohortDefinitionService.class).evaluate(programStateCohortDefinition, evaluationContext);     	
    }
        
    /**
     * Get program cohort.
     * 
     * @param evaluationContext
     * @param program
     * @return
     */
    public Cohort getGenderCohort(EvaluationContext evaluationContext, String gender) {     	
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender(gender);
		return Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, evaluationContext); 
    }

    /**
     * Get an adult cohort 
     * 
     * @param evaluationContext
     * @param minAge
     * @param maxAge
     * @param effectiveDate
     * @return
     */
    public Cohort getAgeCohort(EvaluationContext evaluationContext, Integer minAge, Integer maxAge, Date effectiveDate) {     	
    	AgeCohortDefinition ageCohortDefinition = new AgeCohortDefinition();
    	ageCohortDefinition.setMinAge(minAge);
    	ageCohortDefinition.setMaxAge(maxAge);
    	ageCohortDefinition.setEffectiveDate(effectiveDate);		
    	return Context.getService(CohortDefinitionService.class).evaluate(ageCohortDefinition, evaluationContext);    
    }
}
