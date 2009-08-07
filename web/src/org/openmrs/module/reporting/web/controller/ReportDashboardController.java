package org.openmrs.module.reporting.web.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.LabEncounterDataSet;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportSchema;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.renderer.XlsReportRenderer;
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

    
    @RequestMapping("/module/reporting/viewDemographicData")
    public void viewDemographicData(ModelMap model) {     

    	// not implemented yet
    	
    }

    @RequestMapping("/module/reporting/manageCohortDashboard")
    public String manageCohortDashboard(
    		@RequestParam(required=false, value="cohort") String cohort,
    		ModelMap model) { 
    	
    	EvaluationContext evaluationContext = new EvaluationContext();
    	if ("males".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getMaleCohort(evaluationContext));    		
    	}
    	else if ("females".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getFemaleCohort(evaluationContext));    		
    	}
    	else if ("adults".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getAdultCohort(evaluationContext));    		
    	}
    	else if ("children".equalsIgnoreCase(cohort)) { 
    		model.addAttribute("cohort", getChildCohort(evaluationContext));    		
    	}
    	else { 
    		model.addAttribute("cohort", Context.getPatientSetService().getAllPatients());    		
    	}
    	
    	
    	
    	
    	return "/module/reporting/dashboard/cohortDashboard";
    	
    }
    
	
    /**
     * Manage reporting dashboard.
     * 
     * @param model
     * @return
     */
    @RequestMapping("/module/reporting/manageDashboard")
    public String manageDashboard(ModelMap model) {
    	    	
    	// Get all reporting objects
    	model.addAttribute("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false));

    	model.addAttribute("datasetDefinitions", 
    			Context.getService(DataSetDefinitionService.class).getAllDataSetDefinitions(false));
    	
    	
    	model.addAttribute("indicators", 
    			Context.getService(IndicatorService.class).getAllIndicators(false));
    	model.addAttribute("reportDefinitions", 
    			Context.getService(ReportService.class).getReportSchemas());
    	model.addAttribute("reportRenderers", 
    			Context.getService(ReportService.class).getReportRenderers());
    	
    	// Get all static data
    	model.addAttribute("programs", Context.getProgramWorkflowService().getAllPrograms());
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
    	
		model.addAttribute("males", getMaleCohort(evaluationContext));
		model.addAttribute("females", getFemaleCohort(evaluationContext));    	
		model.addAttribute("adults", getAdultCohort(evaluationContext));
		model.addAttribute("children", getChildCohort(evaluationContext));				
		model.addAttribute("all", Context.getPatientSetService().getAllPatients());

		
		return "/module/reporting/dashboard/dashboardManager";
    }    
    
    
    
    public Cohort getMaleCohort(EvaluationContext evaluationContext) {     	
    	// Male filter
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender("M");
		return Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, evaluationContext);    	
    
    }

    public Cohort getFemaleCohort(EvaluationContext evaluationContext) {     	
    	// Female filter
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender("F");
		return Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, evaluationContext);    	
    
    }
    
    public Cohort getAdultCohort(EvaluationContext evaluationContext) {     	
    	// Adult filter
    	AgeCohortDefinition adultCohortDefinition = new AgeCohortDefinition();
    	adultCohortDefinition.setMinAge(15);
    	adultCohortDefinition.setMaxAge(150);
    	adultCohortDefinition.setEffectiveDate(new Date());		
    	return Context.getService(CohortDefinitionService.class).evaluate(adultCohortDefinition, evaluationContext);    
    }
    
    public Cohort getChildCohort(EvaluationContext evaluationContext) {     	    
		AgeCohortDefinition childCohortDefinition = new AgeCohortDefinition();
		childCohortDefinition.setMinAge(0);
		childCohortDefinition.setMaxAge(15);
		childCohortDefinition.setEffectiveDate(new Date());		
		return Context.getService(CohortDefinitionService.class).evaluate(childCohortDefinition, evaluationContext);
    }
    
    
    
    
    
    
    
        
}
