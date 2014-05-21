package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @throws EvaluationException 
     */
    @RequestMapping("/module/reporting/dashboard/viewCohortDataSet")
    public String viewCohortDataSet(
    		@RequestParam(required=false, value="savedDataSetKey") String savedDataSetKey,
    		@RequestParam(required=false, value="savedColumnKey") String savedColumnKey,   		
    		@RequestParam(required=false, value="applyDataSetId") String applyDataSetId,
    		@RequestParam(required=false, value="limit") Integer limit,
    		HttpServletRequest request,
    		ModelMap model) throws EvaluationException { 
    	    
    	
		ReportData reportData = (ReportData) request.getSession().getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
    
		for (Map.Entry<String, DataSet> e : reportData.getDataSets().entrySet()) {
			if (e.getKey().equals(savedDataSetKey)) { 
				
				MapDataSet mapDataSet = (MapDataSet) e.getValue();
				
				DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
				model.addAttribute("selectedColumn", dataSetColumn);
				
				Object result = mapDataSet.getData(dataSetColumn);
				Cohort selectedCohort = null;
				if (result instanceof CohortIndicatorAndDimensionResult) {
					CohortIndicatorAndDimensionResult cidr = (CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
					selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();
				}
				else if (result instanceof Cohort) {
					selectedCohort = (Cohort) result;
				} 

				model.addAttribute("selectedCohort", selectedCohort);
				
				model.addAttribute("patients", Context.getPatientSetService().getPatients(selectedCohort.getMemberIds()));	

				// Evaluate the default patient dataset definition
				DataSetDefinition dsd = null;
				if (applyDataSetId != null) {
					try {
						dsd = Context.getService(DataSetDefinitionService.class).getDefinition(applyDataSetId, null);
					} catch (Exception ex) { 
						log.error("exception getting dataset definition", ex);				
					}
				}
				
				if (dsd == null) {
					SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
					d.addPatientProperty("patientId");
					List<PatientIdentifierType> types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
					if (!types.isEmpty()) {
						d.setIdentifierTypes(types);
					}
					d.addPatientProperty("givenName");
					d.addPatientProperty("familyName");
					d.addPatientProperty("age");
					d.addPatientProperty("gender");
					dsd = d;
				}
				
				EvaluationContext evalContext = new EvaluationContext();
				if (limit != null && limit > 0) 
					evalContext.setLimit(limit);
				evalContext.setBaseCohort(selectedCohort);
				
				DataSet patientDataSet = Context.getService(DataSetDefinitionService.class).evaluate(dsd, evalContext);
				model.addAttribute("dataSet", patientDataSet);
		    	model.addAttribute("dataSetDefinition", dsd);
				
			}
		}
    	// Add all dataset definition to the request (allow user to choose)
    	model.addAttribute("dataSetDefinitions", 
    			Context.getService(DataSetDefinitionService.class).getAllDefinitions(false)); 			
		
    	return "/module/reporting/dashboard/cohortDataSetDashboard";
    	
    }
    
    
    /**
     * 
     * @param cohort
     * @param model
     * @return
     * @throws EvaluationException 
     */
    @RequestMapping("/module/reporting/dashboard/manageCohortDashboard")
    public String manageCohortDashboard(
    		@RequestParam(required=false, value="cohort") String cohort,
    		@RequestParam(required=false, value="ageCohort") String ageCohort,
    		@RequestParam(required=false, value="genderCohort") String genderCohort,
    		@RequestParam(required=false, value="locationCohort") String locationCohort,
    		@RequestParam(required=false, value="programCohort") String programCohort,
    		
    		ModelMap model) throws EvaluationException { 
    	
    	Cohort selectedCohort = new Cohort();
    	//Cohort selectedCohort = Context.getPatientSetService().getAllPatients();
		
		/*
		model.addAttribute("selected", cohort);
    	EvaluationContext evaluationContext = new EvaluationContext();
    	if ("males".equalsIgnoreCase(cohort)) {
    		selectedCohort = getGenderCohort(evaluationContext, "M");
    	}
    	else if ("females".equalsIgnoreCase(cohort)) { 
    		selectedCohort = getGenderCohort(evaluationContext, "F");    		
    	}
    	else if ("adults".equalsIgnoreCase(cohort)) { 
    		selectedCohort = getAgeCohort(evaluationContext, 15, 150, new Date());    		
    	}
    	else if ("children".equalsIgnoreCase(cohort)) { 
    		selectedCohort = getAgeCohort(evaluationContext, 0, 14, new Date());    		
    	}
    	else if ("all".equalsIgnoreCase(cohort)) { 
    		selectedCohort = Context.getPatientSetService().getAllPatients();
    	}
    	else { 
    		
    		if (cohort != null) { 
	    		Program program = Context.getProgramWorkflowService().getProgramByName(cohort);
	    		if (program != null) 
	    			selectedCohort = getProgramStateCohort(evaluationContext, program);    		
	    		else {  
	    			selectedCohort = CohortUtil.limitCohort(Context.getPatientSetService().getAllPatients(), 100);
	    		}
    		}
    	}
    	*/
    	
    	List<String> selectedCohorts = new ArrayList<String>();
    	//CompoundCohortDefinition cohortDefinition = new CompoundCohortDefinition();
    	EvaluationContext evalContext = new EvaluationContext();
    	Cohort tempCohort = null;
    	if (genderCohort != null || ageCohort != null) {
    		selectedCohort = CohortUtil.limitCohort(Cohorts.allPatients(evalContext), 238);
    		if (genderCohort != null && genderCohort.equals("male")) {
	    		tempCohort = getGenderCohort(evalContext, "M");
	    		selectedCohort = Cohort.intersect(selectedCohort, tempCohort);
	    		selectedCohorts.add("Males");
	    	}
	    	if (genderCohort != null && genderCohort.equals("female")) {
	    		tempCohort = getGenderCohort(evalContext, "F");
	    		selectedCohort = Cohort.intersect(selectedCohort, tempCohort);
	    		selectedCohorts.add("Females");
	    	}
	    	if (ageCohort != null && ageCohort.equals("infant")) {
	    		tempCohort = getAgeCohort(evalContext, 0, 2, new Date());
	    		selectedCohort = Cohort.intersect(selectedCohort, tempCohort);
	    		selectedCohorts.add("Infant (0 - 2)");
	    	}
	    	if (ageCohort != null && ageCohort.equals("child")) {
	    		tempCohort = getAgeCohort(evalContext, 3, 15, new Date());
	    		selectedCohort = Cohort.intersect(selectedCohort, tempCohort);
	    		selectedCohorts.add("Child (3 - 15)");
	    	}
	    	if (ageCohort != null && ageCohort.equals("adult")) {
	    		tempCohort = getAgeCohort(evalContext, 15, 200, new Date());
	    		selectedCohort = Cohort.intersect(selectedCohort, tempCohort);
	    		selectedCohorts.add("Adult (15+)");
	    	}
    	}    	
    	else if (ageCohort == null && genderCohort == null) { 
    		selectedCohort = CohortUtil.limitCohort(Cohorts.allPatients(evalContext), 238);
    		selectedCohorts.add("All patients");
    	}
    	
    	
    	if (selectedCohorts == null || selectedCohorts.isEmpty()) { 
    		selectedCohorts.add("None");
    	}
		model.addAttribute("selectedCohorts", selectedCohorts);    		
    	
    	//Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, context);
    	
    	/*
    	if (selectedCohort == null || selectedCohort.isEmpty()) { 
    		selectedCohort = CohortUtil.limitCohort(Context.getPatientSetService().getAllPatients(), 100);
    	}*/
    	
    	
    	if (selectedCohort != null && !selectedCohort.isEmpty()) { 
    		// Evaluate on the fly report
    		/*
    		EvaluationContext evalContext = new EvaluationContext();
    		evalContext.setBaseCohort(selectedCohort);
	    	ReportDefinition reportDefinition = new ReportDefinition();
	    	DataSetDefinition dataSetDefinition = new PatientDataSetDefinition();
	    	reportDefinition.addDataSetDefinition("patientDataSet", dataSetDefinition, null);
	    	ReportData reportData = Context.getService(ReportService.class).evaluate(reportDefinition, evalContext);
			model.addAttribute("reportData", reportData);    	
	    	*/
    		
	    	// Add generated report, patients, and cohort to request
	    	model.addAttribute("patients", Context.getPatientSetService().getPatients(selectedCohort.getMemberIds()));
	    	
    	}    	
    	
    	
    	manageDashboard(model);
    	
    	return "/module/reporting/dashboard/cohortDashboard";
    	
    }
    
	
    /**
     * Manage reporting dashboard.
     * 
     * @param model
     * @return
     * @throws EvaluationException 
     */
    //@RequestMapping("/module/reporting/dashboard/manageDashboard")
    public String manageDashboard(ModelMap model) throws EvaluationException {
    	    	
    	// Get all reporting objects
    	model.addAttribute("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllDefinitions(false));
    	model.addAttribute("datasetDefinitions", 
    			Context.getService(DataSetDefinitionService.class).getAllDefinitions(false));
    	model.addAttribute("indicators", 
    			Context.getService(IndicatorService.class).getAllDefinitions(false));
    	model.addAttribute("reportDefinitions", 
    			Context.getService(ReportDefinitionService.class).getAllDefinitions(false));
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
		model.addAttribute("all", Cohorts.allPatients(evaluationContext));

		Map<Program, Cohort> programCohortMap = new HashMap<Program, Cohort>();
		for (Program program : programs) {
			Cohort cohort = getProgramStateCohort(evaluationContext, program);
			log.debug("Program: " + program.getName() + " " + cohort.getSize());
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
     * @throws EvaluationException 
     */
    public Cohort getProgramStateCohort(EvaluationContext evaluationContext, Program program) throws EvaluationException { 
    	ProgramEnrollmentCohortDefinition programStateCohortDefinition = new ProgramEnrollmentCohortDefinition();    	
    	programStateCohortDefinition.setPrograms(Collections.singletonList(program));
    	return Context.getService(CohortDefinitionService.class).evaluate(programStateCohortDefinition, evaluationContext);     	
    }
        
    /**
     * Get program cohort.
     * 
     * @param evaluationContext
     * @param program
     * @return
     * @throws EvaluationException 
     */
    public Cohort getGenderCohort(EvaluationContext evaluationContext, String gender) throws EvaluationException {     	
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setMaleIncluded("M".equals(gender));
		genderCohortDefinition.setFemaleIncluded("F".equals(gender));
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
     * @throws EvaluationException 
     */
    public Cohort getAgeCohort(EvaluationContext evaluationContext, Integer minAge, Integer maxAge, Date effectiveDate) throws EvaluationException {     	
    	AgeCohortDefinition ageCohortDefinition = new AgeCohortDefinition();
    	ageCohortDefinition.setMinAge(minAge);
    	ageCohortDefinition.setMaxAge(maxAge);
    	ageCohortDefinition.setEffectiveDate(effectiveDate);		
    	return Context.getService(CohortDefinitionService.class).evaluate(ageCohortDefinition, evaluationContext);    
    }
}
