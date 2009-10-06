package org.openmrs.module.tracnet.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsActiveCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsCompletedCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsStartedCohortDefinition;
import org.openmrs.module.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.cohort.definition.ObsCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.util.IndicatorUtil;
import org.openmrs.module.report.PeriodIndicatorReportDefinition;
import org.openmrs.module.report.PeriodIndicatorReportUtil;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.reporting.ReportingConstants;


/**
 * A thin wrapper around a ReportDefinition that gives it startDate, endDate, and location parameters,
 * and a single {@link CohortIndicatorDataSetDefinition} by default.
 * 
 * @see CohortIndicatorDataSetDefinition
 * @see PeriodIndicatorReportUtil
 */
public class TracNetReportUtil extends PeriodIndicatorReportDefinition {
	
	public static final String DEFAULT_DATASET_KEY = "tracNetDataSet";		
	
	/**
	 * Ensure this report has a data set definition
	 */
	public void setupDataSetDefinition() {
		// Create new dataset definition 
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName(getName() + " Data Set");
		dataSetDefinition.addParameter(ReportingConstants.START_DATE_PARAMETER);
		dataSetDefinition.addParameter(ReportingConstants.END_DATE_PARAMETER);
		
		// Add dataset definition to report definition
		addDataSetDefinition(DEFAULT_DATASET_KEY, dataSetDefinition, IndicatorUtil.getDefaultParameterMappings());
    }
	

	/**
	 * 
	 * @param drugSetName	represents the drug set (e.g. ANTIRETROVIRAL DRUGS)
	 * @return
	 */
	// 
	public static List<Drug> getDrugsByDrugSetName(String drugSetName) { 		
		List<Drug> firstLineDrugs = new ArrayList<Drug>();
		Concept arvDrugs = Context.getConceptService().getConceptByName(drugSetName);
		List<ConceptSet> drugSets = Context.getConceptService().getConceptSetsByConcept(arvDrugs);				
	    if (drugSets != null) {
	    	for (ConceptSet drugSet : drugSets) {	
	    		List<Drug> drugs = Context.getConceptService().getDrugsByConcept(drugSet.getConcept());
	    		if (drugs != null)
	    			firstLineDrugs.addAll(drugs);
			}
	    }
	    return firstLineDrugs;
	}			
	
	/**
	 * 
	 * @param drugName	represents the actual drug (e.g LOPINAVIR AND RITONAVIR)
	 * @return
	 */
	public static List<Drug> getDrugByConceptName(String conceptName) { 		
		Concept secondLineDrugConcept = Context.getConceptService().getConceptByName(conceptName);
		return Context.getConceptService().getDrugsByConcept(secondLineDrugConcept);
	}
	
	
	
	public static ReportDefinition getTracNetReportDefinition() { 
		
		// HIV PROGRAMS 
		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		Program pmtctProgram = Context.getProgramWorkflowService().getProgramByName("PMTCT PROGRAM");
		
		// HIV PROGRAM TREATMENT STATUS
		//ProgramWorkflow treatmentStatusWorkflow = hivProgram.getWorkflowByName("TREATMENT STATUS");
		//ProgramWorkflowState onAntiretroviralsState = treatmentStatusWorkflow.getStateByName("ON ANTIRETROVIRALS");
		//ProgramWorkflowState treatmentStoppedSideEffectsState = treatmentStatusWorkflow.getStateByName("TREATMENT STOPPED - SIDE EFFECTS");
		//ProgramWorkflowState treatmentStoppedPatientRefusedState = treatmentStatusWorkflow.getStateByName("TREATMENT STOPPED - PATIENT REFUSED");
		//ProgramWorkflowState treatmentStoppedState = treatmentStatusWorkflow.getStateByName("TREATMENT STOPPED");
		//ProgramWorkflowState patientDefaultedState = treatmentStatusWorkflow.getStateByName("PATIENT DEFAULTED");
		//ProgramWorkflowState patientDiedState = treatmentStatusWorkflow.getStateByName("PATIENT DIED");
		
		// ON ARV state(s)
		//List<ProgramWorkflowState> onArvStates = new ArrayList<ProgramWorkflowState>();
		//onArvStates.add(onAntiretroviralsState);
		
		// STOPPED state(s)
		//List<ProgramWorkflowState> treatmentStoppedStates = new ArrayList<ProgramWorkflowState>();
		//treatmentStoppedStates.add(treatmentStoppedSideEffectsState);
		//treatmentStoppedStates.add(treatmentStoppedPatientRefusedState);
		//treatmentStoppedStates.add(treatmentStoppedState);
		
		// DEFAULTED state(s)
		//List<ProgramWorkflowState> patientDefaultedStates = new ArrayList<ProgramWorkflowState>();
		//patientDefaultedStates.add(patientDefaultedState);
		
		// DIED state(s)
		//List<ProgramWorkflowState> patientDiedStates = new ArrayList<ProgramWorkflowState>();
		//patientDiedStates.add(patientDiedState);
				
		// ARV DRUGS 
		List<Drug> cotrimoxazole = TracNetReportUtil.getDrugByConceptName("COTRIMOXAZOLE");
		List<Drug> allArvDrugs = TracNetReportUtil.getDrugsByDrugSetName("ANTIRETROVIRAL DRUGS");
		List<Drug> secondLineRegimen = TracNetReportUtil.getDrugByConceptName("LOPINAVIR AND RITONAVIR");

		List<Drug> firstLineRegimen = new ArrayList<Drug>(allArvDrugs);
		firstLineRegimen.removeAll(secondLineRegimen);
		
		// ===============================================================================			
		//
		//		 Create initial cohort queries to be used within indicators
		//
		// ===============================================================================		
		
		// =====  CURRENTLY IN HIV PROGRAM (Program)  ===============================================================================

		ProgramStateCohortDefinition currentlyInHivProgram = new ProgramStateCohortDefinition();
		currentlyInHivProgram.setName("Currently in HIV Program");
		currentlyInHivProgram.setProgram(hivProgram);
		currentlyInHivProgram.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
				
		Map<String,Object> currentlyInHivProgramMappings = new HashMap<String, Object>(); 
		currentlyInHivProgramMappings.put("sinceDate", "${endDate}");
		
		PeriodCohortIndicator currentlyInHivProgramIndicator = new PeriodCohortIndicator();
		currentlyInHivProgramIndicator.setName("Number of patients currently enrolled in the HIV Program");
		currentlyInHivProgramIndicator.setCohortDefinition(currentlyInHivProgram, currentlyInHivProgramMappings);

		// =====  CURRENTLY IN HIV PROGRAM (Program)  ===============================================================================

		ProgramStateCohortDefinition currentlyInPmtctProgram = new ProgramStateCohortDefinition();
		currentlyInPmtctProgram.setName("Currently in PMTCT Program");
		currentlyInPmtctProgram.setProgram(pmtctProgram);
		currentlyInPmtctProgram.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
				
		Map<String,Object> currentlyInPmtctProgramMappings = new HashMap<String, Object>(); 
		currentlyInPmtctProgramMappings.put("sinceDate", "${endDate}");
		
		PeriodCohortIndicator currentlyInPmtctProgramIndicator = new PeriodCohortIndicator();
		currentlyInPmtctProgramIndicator.setName("Number of patients currently enrolled in the PMTCT Program");
		currentlyInPmtctProgramIndicator.setCohortDefinition(currentlyInPmtctProgram, currentlyInPmtctProgramMappings);
		
		
		// =====  CURRENTLY ON ARV (ProgramWorkflowState)  ===============================================================================

		/*
		// Was ON ARV as of the last day of the period.  sinceDate and untilDate map to the last day of the period
		ProgramStateCohortDefinition currentlyOnAntiretrovirals = new ProgramStateCohortDefinition();
		currentlyOnAntiretrovirals.setProgram(hivProgram);
		currentlyOnAntiretrovirals.setStateList(onArvStates);
		currentlyOnAntiretrovirals.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		
		Map<String,Object> currentlyOnAntiretroviralsMappings = new HashMap<String, Object>(); 
		currentlyOnAntiretroviralsMappings.put("sinceDate", "${endDate}");
		
		PeriodCohortIndicator currentlyOnAntiretroviralsIndicator = new PeriodCohortIndicator();
		currentlyOnAntiretroviralsIndicator.setName("Number of patients currently on antiretrovirals");
		currentlyOnAntiretroviralsIndicator.setCohortDefinition(currentlyOnAntiretrovirals, currentlyOnAntiretroviralsMappings);
		*/
		
		// =====  STARTED HIV PROGRAM DURING PERIOD  =========================================================================
		
		ProgramStateCohortDefinition startedHivProgram = new ProgramStateCohortDefinition();
		startedHivProgram.setProgram(hivProgram);
		startedHivProgram.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		startedHivProgram.addParameter(new Parameter("untilDate", "Period ends:", Date.class));
		
		Map<String,Object> startedHivProgramMappings = new HashMap<String, Object>(); 
		startedHivProgramMappings.put("sinceDate", "${startDate}");
		startedHivProgramMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator startedHivProgramIndicator = new PeriodCohortIndicator();
		startedHivProgramIndicator.setName("Number of patients that started in HIV Program during month");
		startedHivProgramIndicator.setCohortDefinition(startedHivProgram, startedHivProgramMappings);
		
		
		// =====  STARTED ON ARV DURING PERIOD  =============================================================================
		/*
		ProgramStateCohortDefinition startedOnAntiretrovirals = new ProgramStateCohortDefinition();
		startedOnAntiretrovirals.setProgram(hivProgram);
		startedOnAntiretrovirals.setStateList(onArvStates);
		startedOnAntiretrovirals.setSinceDate(endDate);
		startedOnAntiretrovirals.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		startedOnAntiretrovirals.addParameter(new Parameter("untilDate", "Period begins:", Date.class));

		Map<String,Object> startedOnAntiretroviralsMappings = new HashMap<String, Object>(); 
		startedOnAntiretroviralsMappings.put("sinceDate", "${startDate}");
		startedOnAntiretroviralsMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator startedOnAntiretroviralsIndicator = new PeriodCohortIndicator();
		startedOnAntiretroviralsIndicator.setName("Number of patients that started ON ARVs during month");
		startedOnAntiretroviralsIndicator.setCohortDefinition(startedOnAntiretrovirals, startedOnAntiretroviralsMappings);
		*/
		
		
		// =====  ON ARVS DURING THE PERIOD  ===========================================================
		
		/*
		DrugOrderCohortDefinition onArvsDuringPeriod = new DrugOrderCohortDefinition();
		onArvsDuringPeriod.setAnyOrAll(GroupMethod.ANY);
		onArvsDuringPeriod.setDrugList(allArvDrugs);
		onArvsDuringPeriod.addParameter(new Parameter("sinceDate", "sinceDate", Date.class));
		onArvsDuringPeriod.addParameter(new Parameter("untilDate", "untilDate", Date.class));
		onArvsDuringPeriod.setName("Taking any arv drugs Between Dates");		

		Map<String,Object> onArvsDuringPeriodMappings = new HashMap<String, Object>(); 
		onArvsDuringPeriodMappings.put("sinceDate", "${startDate}");
		onArvsDuringPeriodMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator onArvsDuringPeriodIndicator = new PeriodCohortIndicator();
		onArvsDuringPeriodIndicator.setName("Number of patients ON ARVs during month");
		onArvsDuringPeriodIndicator.setCohortDefinition(onArvsDuringPeriod, onArvsDuringPeriodMappings);
		*/	
		
		// =====  CURRENTLY ON ARVs  ===============================================================================
		
		DrugsActiveCohortDefinition onArvsDuringPeriod = new DrugsActiveCohortDefinition();		
		onArvsDuringPeriod.setDrugs(allArvDrugs);
		onArvsDuringPeriod.addParameter(new Parameter("asOfDate", "Actively on ARVs as of when?", Date.class));

		Map<String,Object> onArvsDuringPeriodMappings = new HashMap<String, Object>(); 
		onArvsDuringPeriodMappings.put("asOfDate", "${endDate}");

		PeriodCohortIndicator onArvsDuringPeriodIndicator = new PeriodCohortIndicator();
		onArvsDuringPeriodIndicator.setName("On ARVs During Period");
		onArvsDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(onArvsDuringPeriod, onArvsDuringPeriodMappings));

				
		// =====  CURRENTLY ON FIRST LINE REGIMEN  ===============================================================================
		
		DrugsActiveCohortDefinition onFirstLineArvsDuringPeriod = new DrugsActiveCohortDefinition();	
		onFirstLineArvsDuringPeriod.setName("Currently on first line regimen");
		onFirstLineArvsDuringPeriod.setDrugs(firstLineRegimen);
		onFirstLineArvsDuringPeriod.addParameter(new Parameter("asOfDate", "Actively on ARVs as of when?", Date.class));

		Map<String,Object> onFirstLineArvsDuringPeriodMappings = new HashMap<String, Object>(); 
		onFirstLineArvsDuringPeriodMappings.put("asOfDate", "${endDate}");

		PeriodCohortIndicator onFirstLineArvsDuringPeriodIndicator = new PeriodCohortIndicator();
		onFirstLineArvsDuringPeriodIndicator.setName("Actively On First Line ARVs During Period");
		onFirstLineArvsDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(onFirstLineArvsDuringPeriod, onFirstLineArvsDuringPeriodMappings));
		
		// =====  CURRENTLY ON SECOND LINE REGIMEN  ===============================================================================

		DrugsActiveCohortDefinition onSecondLineArvsDuringPeriod = new DrugsActiveCohortDefinition();		
		onSecondLineArvsDuringPeriod.setName("Currently on second line regimen");
		onSecondLineArvsDuringPeriod.setDrugs(secondLineRegimen);
		onSecondLineArvsDuringPeriod.addParameter(new Parameter("asOfDate", "Actively on ARVs as of when?", Date.class));
		
		Map<String,Object> onSecondLineArvsDuringPeriodMappings = new HashMap<String, Object>(); 
		onSecondLineArvsDuringPeriodMappings.put("asOfDate", "${endDate}");

		PeriodCohortIndicator onSecondLineArvsDuringPeriodIndicator = new PeriodCohortIndicator();
		onSecondLineArvsDuringPeriodIndicator.setName("Actively On Second Line ARVs During Period");
		onSecondLineArvsDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(onSecondLineArvsDuringPeriod, onSecondLineArvsDuringPeriodMappings));
		
		// =====  STARTED TREATMENT DURING PERIOD  ===========================================================
				
		DrugsStartedCohortDefinition startedOnArvsDuringPeriod = new DrugsStartedCohortDefinition();
		startedOnArvsDuringPeriod.setDrugs(allArvDrugs);
		startedOnArvsDuringPeriod.addParameter(new Parameter("startedOnOrAfter", "Started on or after", Date.class));
		startedOnArvsDuringPeriod.addParameter(new Parameter("startedOnOrBefore", "Started on or before", Date.class));
		
		Map<String,Object> startedOnArvsDuringPeriodMappings = new HashMap<String, Object>(); 
		startedOnArvsDuringPeriodMappings.put("startedOnOrAfter", "${startDate}");
		startedOnArvsDuringPeriodMappings.put("startedOnOrBefore", "${endDate}");

		PeriodCohortIndicator startedOnArvsDuringPeriodIndicator = new PeriodCohortIndicator();
		startedOnArvsDuringPeriodIndicator.setName("Started on ARVs During Period");
		startedOnArvsDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(startedOnArvsDuringPeriod, startedOnArvsDuringPeriodMappings));

		
		// =====  WHO STAGE 4 CHILDREN  ===============================================================================
		
		Concept whoStageQuestion = Context.getConceptService().getConceptByName("WHO STAGE");		

		ObsCohortDefinition whoStageFourChildren = new ObsCohortDefinition();
		whoStageFourChildren.setQuestion(whoStageQuestion);
		whoStageFourChildren.setModifier(Modifier.EQUAL);
		whoStageFourChildren.setTimeModifier(TimeModifier.LAST);		
		whoStageFourChildren.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 4 PEDS"));

		Map<String,Object> whoStageFourChildrenMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageFourChildrenIndicator = new PeriodCohortIndicator();
		whoStageFourChildrenIndicator.setName("Patients who are WHO Stage 4");
		whoStageFourChildrenIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageFourChildren, whoStageFourChildrenMappings));

		// =====  WHO STAGE 3 CHILDREN  ===============================================================================
		
		ObsCohortDefinition whoStageThreeChildren = new ObsCohortDefinition();
		whoStageThreeChildren.setQuestion(whoStageQuestion);
		whoStageThreeChildren.setModifier(Modifier.EQUAL);
		whoStageThreeChildren.setTimeModifier(TimeModifier.LAST);		
		whoStageThreeChildren.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 3 PEDS"));

		Map<String,Object> whoStageThreeChildrenMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageThreeChildrenIndicator = new PeriodCohortIndicator();
		whoStageThreeChildrenIndicator.setName("Patients who are WHO Stage 3");
		whoStageThreeChildrenIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageThreeChildren, whoStageThreeChildrenMappings));

		// =====  WHO STAGE 2 CHILDREN  ===============================================================================
		
		ObsCohortDefinition whoStageTwoChildren = new ObsCohortDefinition();
		whoStageTwoChildren.setQuestion(whoStageQuestion);
		whoStageTwoChildren.setModifier(Modifier.EQUAL);
		whoStageTwoChildren.setTimeModifier(TimeModifier.LAST);		
		whoStageTwoChildren.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 2 PEDS"));

		Map<String,Object> whoStageTwoChildrenMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageTwoChildrenIndicator = new PeriodCohortIndicator();
		whoStageTwoChildrenIndicator.setName("Patients who are WHO Stage 2");
		whoStageTwoChildrenIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageTwoChildren, whoStageTwoChildrenMappings));

		// =====  WHO STAGE 1 CHILDREN  ===============================================================================

		ObsCohortDefinition whoStageOneChildren = new ObsCohortDefinition();
		whoStageOneChildren.setQuestion(whoStageQuestion);
		whoStageOneChildren.setModifier(Modifier.EQUAL);
		whoStageOneChildren.setTimeModifier(TimeModifier.LAST);		
		whoStageOneChildren.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 1 PEDS"));

		Map<String,Object> whoStageOneChildrenMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageOneChildrenIndicator = new PeriodCohortIndicator();
		whoStageOneChildrenIndicator.setName("Patients who are WHO Stage 1");
		whoStageOneChildrenIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageOneChildren, whoStageOneChildrenMappings));


		// =====  WHO STAGE 4 ADULT  ===============================================================================
		
		ObsCohortDefinition whoStageFourAdult = new ObsCohortDefinition();
		whoStageFourAdult.setQuestion(whoStageQuestion);
		whoStageFourAdult.setModifier(Modifier.EQUAL);
		whoStageFourAdult.setTimeModifier(TimeModifier.LAST);		
		whoStageFourAdult.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 4 ADULT"));

		Map<String,Object> whoStageFourAdultMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageFourAdultIndicator = new PeriodCohortIndicator();
		whoStageFourAdultIndicator.setName("Patients who are WHO Stage 4");
		whoStageFourAdultIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageFourAdult, whoStageFourAdultMappings));

		// =====  WHO STAGE 3 ADULT  ===============================================================================
		
		ObsCohortDefinition whoStageThreeAdult = new ObsCohortDefinition();
		whoStageThreeAdult.setQuestion(whoStageQuestion);
		whoStageThreeAdult.setModifier(Modifier.EQUAL);
		whoStageThreeAdult.setTimeModifier(TimeModifier.LAST);		
		whoStageThreeAdult.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 3 ADULT"));

		Map<String,Object> whoStageThreeAdultMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageThreeAdultIndicator = new PeriodCohortIndicator();
		whoStageThreeAdultIndicator.setName("Patients who are WHO Stage 3");
		whoStageThreeAdultIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageThreeAdult, whoStageThreeAdultMappings));

		// =====  WHO STAGE 2 ADULT  ===============================================================================
		
		ObsCohortDefinition whoStageTwoAdult = new ObsCohortDefinition();
		whoStageTwoAdult.setQuestion(whoStageQuestion);
		whoStageTwoAdult.setModifier(Modifier.EQUAL);
		whoStageTwoAdult.setTimeModifier(TimeModifier.LAST);		
		whoStageTwoAdult.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 2 ADULT"));

		Map<String,Object> whoStageTwoAdultMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageTwoAdultIndicator = new PeriodCohortIndicator();
		whoStageTwoAdultIndicator.setName("Patients who are WHO Stage 2");
		whoStageTwoAdultIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageTwoAdult, whoStageTwoAdultMappings));

		// =====  WHO STAGE 1 ADULT  ===============================================================================

		ObsCohortDefinition whoStageOneAdult = new ObsCohortDefinition();
		whoStageOneAdult.setQuestion(whoStageQuestion);
		whoStageOneAdult.setModifier(Modifier.EQUAL);
		whoStageOneAdult.setTimeModifier(TimeModifier.LAST);		
		whoStageOneAdult.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 1 ADULT"));

		Map<String,Object> whoStageOneAdultMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageOneAdultIndicator = new PeriodCohortIndicator();
		whoStageOneAdultIndicator.setName("Patients who are WHO Stage 1");
		whoStageOneAdultIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageOneAdult, whoStageOneAdultMappings));		
		
		// =====  WHO STAGE UNKNOWN  ===============================================================================

		ObsCohortDefinition whoStageUnknown = new ObsCohortDefinition();
		whoStageUnknown.setQuestion(whoStageQuestion);
		whoStageUnknown.setModifier(Modifier.EQUAL);
		whoStageUnknown.setTimeModifier(TimeModifier.LAST);		
		whoStageUnknown.setValueCoded(Context.getConceptService().getConceptByName("UNKNOWN"));

		Map<String,Object> whoStageUnknownMappings = new HashMap<String, Object>(); 
		
		PeriodCohortIndicator whoStageUnknownIndicator = new PeriodCohortIndicator();
		whoStageUnknownIndicator.setName("Patients who have an unknown WHO STAGE");
		whoStageUnknownIndicator.setCohortDefinition(new Mapped<CohortDefinition>(whoStageUnknown, whoStageUnknownMappings));		
		
		// =====  NO ENCOUNTER IN THE PAST THREE MONTHS  ======================================================

		EncounterCohortDefinition encounterWithinLastThreeMonths = new EncounterCohortDefinition();
		encounterWithinLastThreeMonths.addParameter(new Parameter("sinceDate", "", Date.class));
		encounterWithinLastThreeMonths.addParameter(new Parameter("untilDate", "", Date.class));
		encounterWithinLastThreeMonths.setWithinLastMonths(3);
		
		InverseCohortDefinition lostToFollowupDuringPeriod = new InverseCohortDefinition();
		lostToFollowupDuringPeriod.setBaseDefinition(encounterWithinLastThreeMonths);		
		lostToFollowupDuringPeriod.addParameter(new Parameter("sinceDate", "", Date.class));
		lostToFollowupDuringPeriod.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> lostToFollowupDuringPeriodMappings = new HashMap<String, Object>(); 
		lostToFollowupDuringPeriodMappings.put("sinceDate", "${startDate-3m}");
		lostToFollowupDuringPeriodMappings.put("untilDate", "${endDate-3m}");
		
		PeriodCohortIndicator lostToFollowupIndicator = new PeriodCohortIndicator();
		lostToFollowupIndicator.setName("Patients who are lost to followup");
		lostToFollowupIndicator.setCohortDefinition(lostToFollowupDuringPeriod, lostToFollowupDuringPeriodMappings);
		
		// =====  STOPPED TREATMENT DURING PERIOD  ===========================================================

		DrugsCompletedCohortDefinition stoppedDuringPeriod = new DrugsCompletedCohortDefinition();
		stoppedDuringPeriod.setDrugs(allArvDrugs);
		stoppedDuringPeriod.addParameter(new Parameter("completedOnOrAfter", "", Date.class));
		stoppedDuringPeriod.addParameter(new Parameter("completedOnOrBefore", "", Date.class));

		Map<String,Object> stoppedDuringPeriodMappings = new HashMap<String, Object>(); 
		stoppedDuringPeriodMappings.put("completedOnOrAfter", "${startDate}");
		stoppedDuringPeriodMappings.put("completedOnOrBefore", "${endDate}");
		
		PeriodCohortIndicator stoppedDuringPeriodIndicator = new PeriodCohortIndicator();
		stoppedDuringPeriodIndicator.setName("Patients who have stopped treatment during the period");
		stoppedDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(stoppedDuringPeriod, stoppedDuringPeriodMappings));		
		
		// =====  NOT ON ARV AT END OF PERIOD    ===========================================================

		InverseCohortDefinition notOnArvsAtPeriodEnd = new InverseCohortDefinition();
		notOnArvsAtPeriodEnd.setBaseDefinition(onArvsDuringPeriod);		
		
		CompoundCohortDefinition notOnArvButEligible = new CompoundCohortDefinition();
		notOnArvButEligible.addDefinition(new Mapped<CohortDefinition>(notOnArvsAtPeriodEnd, null));
		notOnArvButEligible.addDefinition(new Mapped<CohortDefinition>(currentlyInHivProgram, null));
		
		
		// =====  HOSPITALIZED DURING PERIOD  ===========================================================
		
		ObsCohortDefinition hospitalizedDuringPeriod = new ObsCohortDefinition();
		Concept hospitalizedQuestion = Context.getConceptService().getConceptByName("PATIENT HOSPITALIZED SINCE LAST VISIT");		
		hospitalizedDuringPeriod.setQuestion(hospitalizedQuestion);
		hospitalizedDuringPeriod.setValueNumeric(1d);
		hospitalizedDuringPeriod.setModifier(Modifier.EQUAL);
		hospitalizedDuringPeriod.setTimeModifier(TimeModifier.ANY);		
		hospitalizedDuringPeriod.addParameter(new Parameter("sinceDate", "", Date.class));
		hospitalizedDuringPeriod.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> hospitalizedDuringPeriodMappings = new HashMap<String, Object>(); 
		hospitalizedDuringPeriodMappings.put("sinceDate", "${startDate}");
		hospitalizedDuringPeriodMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator hospitalizedDuringPeriodIndicator = new PeriodCohortIndicator();
		hospitalizedDuringPeriodIndicator.setName("Patients who have stopped treatment during the period");
		hospitalizedDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(hospitalizedDuringPeriod, hospitalizedDuringPeriodMappings));		
		
		
		//  =====  MODE OF ADMISSION  ===========================================================
		
		ObsCohortDefinition modeOfAdmission = new ObsCohortDefinition();
		Concept modeOfAdmissionQuestion = Context.getConceptService().getConceptByName("MODE OF ADMISSION");	
		Concept modeOfAdmissionAnswer = Context.getConceptService().getConceptByName("VCT PROGRAM");
		modeOfAdmission.setQuestion(modeOfAdmissionQuestion);
		modeOfAdmission.setValueCoded(modeOfAdmissionAnswer);
		modeOfAdmission.setModifier(Modifier.EQUAL);
		modeOfAdmission.setTimeModifier(TimeModifier.ANY);
		modeOfAdmission.addParameter(new Parameter("sinceDate", "", Date.class));
		modeOfAdmission.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> modeOfAdmissionMappings = new HashMap<String, Object>(); 
		modeOfAdmissionMappings.put("sinceDate", "${startDate}");
		modeOfAdmissionMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator modeOfAdmissionIndicator = new PeriodCohortIndicator();
		modeOfAdmissionIndicator.setName("Patients who have stopped treatment during the period");
		modeOfAdmissionIndicator.setCohortDefinition(new Mapped<CohortDefinition>(modeOfAdmission, modeOfAdmissionMappings));		
		
		//  =====  DIED DURING PERIOD  ===========================================================
		
		ObsCohortDefinition diedDuringPeriod = new ObsCohortDefinition();
		Concept diedDuringPeriodQuestion = Context.getConceptService().getConceptByName("REASON FOR EXITING CARE");	
		Concept diedDuringPeriodAnswer = Context.getConceptService().getConceptByName("PATIENT DIED");
		diedDuringPeriod.setQuestion(diedDuringPeriodQuestion);
		diedDuringPeriod.setValueCoded(diedDuringPeriodAnswer);
		diedDuringPeriod.setModifier(Modifier.EQUAL);
		diedDuringPeriod.setTimeModifier(TimeModifier.ANY);
		diedDuringPeriod.addParameter(new Parameter("sinceDate", "", Date.class));
		diedDuringPeriod.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> diedDuringPeriodMappings = new HashMap<String, Object>(); 
		modeOfAdmissionMappings.put("sinceDate", "${startDate}");
		modeOfAdmissionMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator diedDuringPeriodIndicator = new PeriodCohortIndicator();
		diedDuringPeriodIndicator.setName("Patients who have died during the period");
		diedDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(diedDuringPeriod, diedDuringPeriodMappings));		

		
		//  =====  ON CTX DURING PERIOD  ===========================================================
		
		DrugsActiveCohortDefinition onCtxDuringPeriod = new DrugsActiveCohortDefinition();		
		onCtxDuringPeriod.setDrugs(cotrimoxazole);
		onCtxDuringPeriod.addParameter(new Parameter("asOfDate", "Actively on CTX as of when?", Date.class));

		Map<String,Object> onCtxDuringPeriodMappings = new HashMap<String, Object>(); 
		onCtxDuringPeriodMappings.put("asOfDate", "${endDate}");

		PeriodCohortIndicator onCtxDuringPeriodIndicator = new PeriodCohortIndicator();
		onCtxDuringPeriodIndicator.setName("On CTX During Period");
		onCtxDuringPeriodIndicator.setCohortDefinition(new Mapped<CohortDefinition>(onCtxDuringPeriod, onCtxDuringPeriodMappings));
		
		//  =====  SEXUALLY TRANSMITTED SERVICES  ===========================================================
		
		ObsCohortDefinition sti = new ObsCohortDefinition();
		//Concept modeOfAdmissionQuestion = Context.getConceptService().getConceptByName("MODE OF ADMISSION");	
		Concept stiAnswer = Context.getConceptService().getConceptByName("STI");
		//sti.setQuestion(stiQuestion);
		sti.setValueCoded(stiAnswer);
		sti.setModifier(Modifier.EQUAL);
		sti.setTimeModifier(TimeModifier.ANY);
		sti.addParameter(new Parameter("sinceDate", "", Date.class));
		sti.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> stiMappings = new HashMap<String, Object>(); 
		stiMappings.put("sinceDate", "${startDate}");
		stiMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator stiIndicator = new PeriodCohortIndicator();
		stiIndicator.setName("Patients who have been treated for STI during the period");
		stiIndicator.setCohortDefinition(new Mapped<CohortDefinition>(sti, stiMappings));		
		
		// =====  STARTED TREATMENT 12 MONTHS AGO  ===========================================================
		
		/*
		DrugsStartedCohortDefinition startedOnArvsTwelveMonthsAgo = new DrugsStartedCohortDefinition();
		startedOnArvsTwelveMonthsAgo.setDrugs(allArvDrugs);
		startedOnArvsTwelveMonthsAgo.addParameter(new Parameter("startedOnOrBefore", "Started on or before", Date.class));
		
		Map<String,Object> startedOnArvsTwelveMonthsAgoMappings = new HashMap<String, Object>(); 
		startedOnArvsTwelveMonthsAgoMappings.put("startedOnOrBefore", "${endDate - 12m}");
		
		CompoundCohortDefinition onArvsAfterTwelveMonths = new CompoundCohortDefinition();
		onArvsAfterTwelveMonths.addDefinition(new Mapped<CohortDefinition>(startedOnArvsTwelveMonthsAgo, startedOnArvsTwelveMonthsAgoMappings));
		onArvsAfterTwelveMonths.addDefinition(new Mapped<CohortDefinition>(onArvsDuringPeriod, onArvsDuringPeriodMappings));
				
		PeriodCohortIndicator onArvsAfterTwelveMonthsIndicator = new PeriodCohortIndicator();
		onArvsAfterTwelveMonthsIndicator.setName("Patients who have been on ARVs for 12 months");
		onArvsAfterTwelveMonthsIndicator.setCohortDefinition(new Mapped<CohortDefinition>(onArvsAfterTwelveMonths, startedOnArvsTwelveMonthsAgoMappings));		
		*/

		//  =====  OPPORTUNISTIC INFECTION (NOT TB)  ===========================================================
		
		ObsCohortDefinition opportunisticInfection = new ObsCohortDefinition();
		Concept oiQuestion = Context.getConceptService().getConceptByName("CURRENT OPPORTUNISTIC INFECTION OR COMORBIDITY, CONFIRMED OR PRESUMED");
		Concept oiAnswer = Context.getConceptService().getConceptByName("TUBERCULOSIS");	
		opportunisticInfection.setQuestion(oiQuestion);
		opportunisticInfection.setValueCoded(oiAnswer);
		opportunisticInfection.setModifier(Modifier.EQUAL);
		opportunisticInfection.setTimeModifier(TimeModifier.NO);
		opportunisticInfection.addParameter(new Parameter("sinceDate", "", Date.class));
		opportunisticInfection.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> opportunisticInfectionMappings = new HashMap<String, Object>(); 
		opportunisticInfectionMappings.put("sinceDate", "${startDate}");
		opportunisticInfectionMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator opportunisticInfectionIndicator = new PeriodCohortIndicator();
		opportunisticInfectionIndicator.setName("Patients who have been treated for OIs during the period");
		opportunisticInfectionIndicator.setCohortDefinition(new Mapped<CohortDefinition>(opportunisticInfection, opportunisticInfectionMappings));		
		
		//  =====  OPPORTUNISTIC INFECTION (JUST TB)  ===========================================================
		
		ObsCohortDefinition opportunisticInfectionTuberculosis = new ObsCohortDefinition();		
		opportunisticInfectionTuberculosis.setQuestion(oiQuestion);
		opportunisticInfectionTuberculosis.setValueCoded(oiAnswer);
		opportunisticInfectionTuberculosis.setModifier(Modifier.EQUAL);
		opportunisticInfectionTuberculosis.setTimeModifier(TimeModifier.ANY);
		opportunisticInfectionTuberculosis.addParameter(new Parameter("sinceDate", "", Date.class));
		opportunisticInfectionTuberculosis.addParameter(new Parameter("untilDate", "", Date.class));
		
		Map<String,Object> opportunisticInfectionTuberculosisMappings = new HashMap<String, Object>(); 
		opportunisticInfectionTuberculosisMappings.put("sinceDate", "${startDate}");
		opportunisticInfectionTuberculosisMappings.put("untilDate", "${endDate}");
		
		PeriodCohortIndicator opportunisticInfectionTuberculosisIndicator = new PeriodCohortIndicator();
		opportunisticInfectionTuberculosisIndicator.setName("Patients who have been treated for OIs during the period");
		opportunisticInfectionTuberculosisIndicator.setCohortDefinition(new Mapped<CohortDefinition>(opportunisticInfectionTuberculosis, opportunisticInfectionTuberculosisMappings));		
		

		
		
		
		// ===============================================================================			
		//
		//		 Create the report definition and dimensions to be used in report 
		//
		// ===============================================================================

		PeriodIndicatorReportDefinition reportDefinition = new PeriodIndicatorReportDefinition();
							
		// Define the GENDER dimension as a breakdown of males and females
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();
		
		GenderCohortDefinition males = new GenderCohortDefinition("M");
		genderDimension.addCohortDefinition("males", males, null);				

		GenderCohortDefinition females = new GenderCohortDefinition("F");		
		genderDimension.addCohortDefinition("females", females, null);		
		
		// We need to map the AGE dimension using the effective date (e.g. "adult as of ...")
		Map<String,Object> ageDimensionMapping = new HashMap<String,Object>();
		ageDimensionMapping.put("effectiveDate", "${endDate}");

		// Define the AGE dimension
		CohortDefinitionDimension ageDimension = new CohortDefinitionDimension();		
		//ageDimension.addParameter(ReportingConstants.START_DATE_PARAMETER);

		AgeCohortDefinition adults = new AgeCohortDefinition(15, null, null);		
		adults.addParameter(new Parameter("effectiveDate", "As of:", Date.class));
		ageDimension.addCohortDefinition("adults", adults, ageDimensionMapping);
		
		AgeCohortDefinition pediatrics = new AgeCohortDefinition(null,15, null);
		pediatrics.addParameter(new Parameter("effectiveDate", "As of:", Date.class));
		ageDimension.addCohortDefinition("pediatrics", pediatrics, ageDimensionMapping);		
				
		// Add dimensions to the report 
		reportDefinition.addDimension("gender", genderDimension);
		reportDefinition.addDimension("age", ageDimension);
		
	
		
		// ===============================================================================			
		//
		//		 Add all indicators to the report with or without dimension options
		//
		// ===============================================================================
				
		// Currently enrolled in HIV Program (breakdown by age and gender)
		//reportDefinition.addIndicator("1", "# of patients currently in HIV Program", currentlyInHivProgramIndicator);
		//reportDefinition.addIndicator("2", "# of male patients currently in HIV Program", currentlyInHivProgramIndicator, "gender=males");
		//reportDefinition.addIndicator("3", "# of female patients currently in HIV Program", currentlyInHivProgramIndicator, "gender=females");
		//reportDefinition.addIndicator("4", "# of adult patients currently in HIV Program", currentlyInHivProgramIndicator, "age=adults");
		//reportDefinition.addIndicator("5", "# of pediatric patients currently in HIV Program", currentlyInHivProgramIndicator, "age=pediatrics");
		//reportDefinition.addIndicator("6", "# of male adult patients currently in HIV Program", currentlyInHivProgramIndicator, "gender=males|age=adults");
		//reportDefinition.addIndicator("7", "# of female adult patients currently in HIV Program", currentlyInHivProgramIndicator, "gender=females|age=adults");
		//reportDefinition.addIndicator("8", "# of male pediatric patients currently in HIV Program", currentlyInHivProgramIndicator, "gender=males|age=pediatrics");
		//reportDefinition.addIndicator("9", "# of female pediatric patients currently in HIV Program", currentlyInHivProgramIndicator, "gender=females|age=pediatrics");
		
		reportDefinition.addIndicator("1", "Nombre total de patients pédiatriques (moins de 15 ans) qui sont actuellement sous ARV:", onArvsDuringPeriodIndicator, "age=pediatrics"); 
		reportDefinition.addIndicator("2", "Nombre total de patients pédiatriques (sexe masculin) qui sont au Régime de Première Ligne:", onFirstLineArvsDuringPeriodIndicator, "gender=males"); 
		reportDefinition.addIndicator("3", "Nombre total de patientes pédiatriques (sexe féminin) qui sont au Régime de Première Ligne:", onFirstLineArvsDuringPeriodIndicator, "gender=females");
		reportDefinition.addIndicator("4", "Nombre total de patients pédiatriques (sexe masculin) qui sont au Régime de Deuxième Ligne:", onSecondLineArvsDuringPeriodIndicator, "gender=males");
		reportDefinition.addIndicator("5", "Nombre total de patientes pédiatriques (sexe féminin) qui sont au Régime de Deuxième Ligne:", onSecondLineArvsDuringPeriodIndicator, "gender=females");
		reportDefinition.addIndicator("6", "Nombre total de patients adultes (plus de 15 ans) qui sont actuellement sous traitement ARV:", onArvsDuringPeriodIndicator, "age=adults"); 
		reportDefinition.addIndicator("7", "Nombre total de patients adultes (sexe masculin) qui sont au Régime de Première Ligne:", onFirstLineArvsDuringPeriodIndicator, "gender=males");
		reportDefinition.addIndicator("8", "Nombre total de patientes adultes (sexe féminin) qui sont au Régime de Première Ligne:", onFirstLineArvsDuringPeriodIndicator, "gender=females");
		reportDefinition.addIndicator("9", "Nombre total de patients adultes (sexe masculin) qui sont au Régime de Deuxième Ligne:", onSecondLineArvsDuringPeriodIndicator, "gender=males");
		reportDefinition.addIndicator("10", "Nombre total de patientes adultes (sexe féminin) qui sont au Régime de Deuxième Ligne:", onSecondLineArvsDuringPeriodIndicator, "gender=females");
		reportDefinition.addIndicator("11", "Nombre de nouveaux patients péds (moins de 15 ans) ayant commencé les ARV au cours de ce mois:", startedOnArvsDuringPeriodIndicator, "age=pediatrics"); 
		reportDefinition.addIndicator("12", "Nombre de nouveaux patients pédiatriques au stade 4 OMS ce mois:", whoStageFourChildrenIndicator, "age=pediatrics");
		reportDefinition.addIndicator("13", "Nombre de nouveaux patients pédiatriques au stade 3 OMS ce mois:", whoStageThreeChildrenIndicator, "age=pediatrics");
		reportDefinition.addIndicator("14", "Nombre de nouveaux patients pédiatriques au stade 2 OMS ce mois:", whoStageTwoChildrenIndicator, "age=pediatrics");
		reportDefinition.addIndicator("15", "Nombre de nouveaux patients pédiatriques au stade 1 OMS ce mois:", whoStageOneChildrenIndicator, "age=pediatrics");
		reportDefinition.addIndicator("16", "Nombre de nouveaux patients pédiatriques au stade OMS inconnu ce mois:", whoStageUnknownIndicator, "age=pediatrics");
		reportDefinition.addIndicator("17", "Nombre de nouveaux patients adultes (plus de15ans)ayant commencé les ARVs au cours de ce mois:", startedOnArvsDuringPeriodIndicator, "age=adults");
		reportDefinition.addIndicator("18", "Nombre de nouveaux patients adultes qui sont au stade 4 OMS ce mois:", whoStageFourAdultIndicator, "age=adults");
		reportDefinition.addIndicator("19", "Nombre de nouveaux patients adultes qui sont au stade 3 OMS ce mois:", whoStageThreeAdultIndicator, "age=adults");
		reportDefinition.addIndicator("20", "Nombre de nouveaux patients adultes qui sont au stade 2 OMS ce mois:", whoStageTwoAdultIndicator, "age=adults");
		reportDefinition.addIndicator("21", "Nombre de nouveaux patients adultes qui sont au stade 1 OMS ce mois:", whoStageOneAdultIndicator, "age=adults");
		reportDefinition.addIndicator("22", "Nombre de nouveaux patients adultes qui sont au stade OMS inconnu ce mois:", whoStageUnknownIndicator, "age=adults");
		reportDefinition.addIndicator("23", "Nombre de patients (sexe masculin) sous ARV dont le traitement a été interrompu ce mois:", stoppedDuringPeriodIndicator, "gender=males");
		reportDefinition.addIndicator("24", "Nombre de patientes (sexe féminin) sous ARV dont le traitement a été interrompu ce mois:", stoppedDuringPeriodIndicator, "gender=females"); 
		reportDefinition.addIndicator("25", "Nombre de patients (sexe masculin) sous ARV hospitalisés ce mois:", hospitalizedDuringPeriodIndicator, "gender=males"); 
		reportDefinition.addIndicator("26", "Nombre de patientes (sexe féminin) sous ARV hospitalisés ce mois:", hospitalizedDuringPeriodIndicator, "gender=females");  
		reportDefinition.addIndicator("27", "Nombre de patients (sexe masculin) sous ARV décédés ce mois:", diedDuringPeriodIndicator, "gender=males"); 
		reportDefinition.addIndicator("28", "Nombre de patientes (sexe féminin) sous ARV décédées ce mois:", diedDuringPeriodIndicator, "gender=females");
		reportDefinition.addIndicator("29", "Number of male ARV patients lost to follow-up (>3 months)?:", lostToFollowupIndicator, "gender=males");
		reportDefinition.addIndicator("30", "Number of female ARV patients lost to follow-up (>3 months)?:", lostToFollowupIndicator, "gender=females");  
		
		// TODO Cannot get these two to work
		//reportDefinition.addIndicator("31", "Nombre de patients (sexe masculin) sous ARV depuis plus de 12 mois:", onArvsAfterTwelveMonthsIndicator, "gender=males");
		//reportDefinition.addIndicator("32", "Nombre de patientes (sexe féminin) sous ARV depuis plus de 12 mois:", onArvsAfterTwelveMonthsIndicator, "gender=females");
		
		reportDefinition.addIndicator("33", "Nombre des femmes venant du PTME recevant les ARV ce mois:", currentlyInPmtctProgramIndicator, "gender=females"); 		
		reportDefinition.addIndicator("34", "Nombre de patients (sexe♂) recevant les services IST ce mois quelque soit leur statut sérologique:", stiIndicator, "gender=males");
		reportDefinition.addIndicator("35", "Nombre de patients (sexe♀) recevant les services IST ce mois quelque soit leur statut sérologique:", stiIndicator, "gender=females");
		reportDefinition.addIndicator("36", "Nombre de nouveaux patients venant du VCT recevant les ARV ce mois:", modeOfAdmissionIndicator, "");
		reportDefinition.addIndicator("37", "Nombre de patients sur prophylaxie au cotrimoxazole ce mois:", onCtxDuringPeriodIndicator, "");

		reportDefinition.addIndicator("38", "Nombre de patients VIH positifs traités contre des IOs ce mois, à l'exception de la TB:", opportunisticInfectionIndicator, "");
		reportDefinition.addIndicator("39", "Nombre de patients VIH positifs traités contre la tuberculose ce mois:", opportunisticInfectionTuberculosisIndicator, "");
		
		// TODO We don't store this information in the EMR 
		//reportDefinition.addIndicator("40", "Nombre de patients VIH positifs recevant des soins paliatifs (A la formation sanitaire) ce mois:", null, "");
	
		return reportDefinition;
		
	}
	
	
	
	
	
}
