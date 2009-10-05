/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
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
import org.openmrs.module.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsActiveCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsCompletedCohortDefinition;
import org.openmrs.module.cohort.definition.DrugsStartedCohortDefinition;
import org.openmrs.module.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.cohort.definition.ObsCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimensionCategory;
import org.openmrs.module.indicator.dimension.CohortDimension;
import org.openmrs.module.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.module.indicator.dimension.DimensionCategory;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.tracnet.report.definition.TracNetReportDefinition;
import org.openmrs.module.util.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * Test class that tries to run a portion of the Pepfar monthly report
 */
public class TracNetReportTest extends BaseContextSensitiveTest {
	
	Log log = LogFactory.getLog(getClass());
	
	DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	/**
	 * Auto generated method comment
	 * 
	 * @param params
	 * @return
	 * @throws ParseException
	 */
	Map<Parameter, Object> getUserEnteredParameters(Collection<Parameter> params) throws ParseException {
		Map<Parameter, Object> ret = new HashMap<Parameter, Object>();
		
		if (params != null)
			for (Parameter p : params) {
				if (p.getName().equals("startDate"))
					ret.put(p, ymd.parse("2007-09-01"));
				else if (p.getName().equals("endDate"))
					ret.put(p, ymd.parse("2007-09-30"));
			}
		return ret;
	}

	
	@Before 
	public void beforeTest() throws Exception { 		
		authenticate();		
	}
	
	@Test
	public void shouldGenerateTracNetReport() throws Exception { 
		//initializeInMemoryDatabase();
		//executeDataSet("org/openmrs/report/include/TracNetReportTest.xml");
				

		// ===============================================================================			
		//
		//		 Initialize data
		//
		// ===============================================================================		

		
		Calendar calendar = Calendar.getInstance();		
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2009);
		
		Date reportStartDate = DateUtil.getStartOfMonth(calendar.getTime());
		Date reportEndDate = DateUtil.getEndOfMonth(calendar.getTime());
		
		//DateUtil.getStartOfMonth(d, monthAdjustment)
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);

		TracNetReportDefinition tracNetReport = new TracNetReportDefinition();
		Cohort cohort = new Cohort();
		EvaluationContext context = new EvaluationContext();
		
		
		// ===============================================================================			
		//
		//		 Lookup data that we might use later
		//
		// ===============================================================================		


		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		Program pmtctProgram = Context.getProgramWorkflowService().getProgramByName("PMTCT PROGRAM");
		ProgramWorkflow treatmentStatusWorkflow = hivProgram.getWorkflowByName("TREATMENT STATUS");
		List<Concept> firstLineDrugSets = Context.getConceptService().getConceptsByName("ANTIRETROVIRAL DRUGS");
		Location rwinkwavu = Context.getLocationService().getLocation(26);
		
		// HIV PROGRAM TREATMENT STATUS
		ProgramWorkflowState onAntiretroviralsState = treatmentStatusWorkflow.getStateByName("ON ANTIRETROVIRALS");
		ProgramWorkflowState treatmentStoppedSideEffectsState = treatmentStatusWorkflow.getStateByName("TREATMENT STOPPED - SIDE EFFECTS");
		ProgramWorkflowState treatmentStoppedPatientRefusedState = treatmentStatusWorkflow.getStateByName("TREATMENT STOPPED - PATIENT REFUSED");
		ProgramWorkflowState treatmentStoppedState = treatmentStatusWorkflow.getStateByName("TREATMENT STOPPED");
		ProgramWorkflowState patientDefaultedState = treatmentStatusWorkflow.getStateByName("PATIENT DEFAULTED");
		ProgramWorkflowState patientDiedState = treatmentStatusWorkflow.getStateByName("PATIENT DIED");
		
		// ON ARV state(s)
		List<ProgramWorkflowState> onArvStates = new ArrayList<ProgramWorkflowState>();
		onArvStates.add(onAntiretroviralsState);
		
		// STOPPED state(s)
		List<ProgramWorkflowState> treatmentStoppedStates = new ArrayList<ProgramWorkflowState>();
		treatmentStoppedStates.add(treatmentStoppedSideEffectsState);
		treatmentStoppedStates.add(treatmentStoppedPatientRefusedState);
		treatmentStoppedStates.add(treatmentStoppedState);
		
		// DEFAULTED state(s)
		List<ProgramWorkflowState> patientDefaultedStates = new ArrayList<ProgramWorkflowState>();
		patientDefaultedStates.add(patientDefaultedState);
		
		// DIED state(s)
		List<ProgramWorkflowState> patientDiedStates = new ArrayList<ProgramWorkflowState>();
		patientDiedStates.add(patientDiedState);
		
		
		
		
		
		List<Drug> allArvDrugs = TracNetReportDefinition.getDrugsByDrugSetName("ANTIRETROVIRAL DRUGS");
		List<Drug> secondLineRegimen = TracNetReportDefinition.getDrugByConceptName("LOPINAVIR AND RITONAVIR");
		for (Drug drug : secondLineRegimen) { 
			log.warn("second line drugs: " + drug.getName());			
		}		
		List<Drug> firstLineRegimen = new ArrayList<Drug>(allArvDrugs);
		firstLineRegimen.removeAll(secondLineRegimen);
		for (Drug drug : firstLineRegimen) { 
			log.warn("first line drugs: " + drug.getName());			
		}

		
		// ===============================================================================			
		//
		//		 Create initial cohort queries to be used within indicators
		//
		// ===============================================================================		
		
		// CURRENTLY IN HIV PROGRAM (Program)  
		ProgramStateCohortDefinition currentlyInHivProgram = new ProgramStateCohortDefinition();
		currentlyInHivProgram.setProgram(hivProgram);
		currentlyInHivProgram.setSinceDate(reportEndDate);
		//currentlyInHivProgram.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		//currentlyInHivProgram.addParameter(new Parameter("untilDate", "Period ends:", Date.class));
		cohort = cds.evaluate(currentlyInHivProgram, context);
		log.warn("currently on any ARV treatment: " + cohort.getSize());
		
		// CURRENTLY ON ARV (ProgramWorkflowState)
		// Was ON ARV as of the last day of the period.  sinceDate and untilDate map to the last day of the period
		ProgramStateCohortDefinition currentlyOnAntiretrovirals = new ProgramStateCohortDefinition();
		currentlyOnAntiretrovirals.setProgram(hivProgram);
		currentlyOnAntiretrovirals.setStateList(onArvStates);
		currentlyOnAntiretrovirals.setSinceDate(reportEndDate);
		//onArvTreatment.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		//onArvTreatment.addParameter(new Parameter("untilDate", "Period begins:", Date.class));
		cohort = cds.evaluate(currentlyOnAntiretrovirals, context);
		log.warn("currently on any ARV treatment: " + cohort.getSize());
		
		// STARTED HIV PROGRAM DURING PERIOD
		ProgramStateCohortDefinition startHivProgram = new ProgramStateCohortDefinition();
		startHivProgram.setProgram(hivProgram);
		startHivProgram.setSinceDate(reportStartDate);
		startHivProgram.setUntilDate(reportEndDate);
		//inHivProgram.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		//inHivProgram.addParameter(new Parameter("untilDate", "Period ends:", Date.class));
		cohort = cds.evaluate(currentlyInHivProgram, context);
		log.warn("started ART during the period: " + cohort.getSize());
		
		// Started ON ARV DURING PERIOD
		ProgramStateCohortDefinition startedOnAntiretrovirals = new ProgramStateCohortDefinition();
		startedOnAntiretrovirals.setProgram(hivProgram);
		startedOnAntiretrovirals.setStateList(onArvStates);
		startedOnAntiretrovirals.setSinceDate(reportEndDate);
		//onArvTreatment.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		//onArvTreatment.addParameter(new Parameter("untilDate", "Period begins:", Date.class));
		cohort = cds.evaluate(currentlyOnAntiretrovirals, context);
		log.warn("started on any ARV treatment during the period: " + cohort.getSize());		
		
		
		// CURRENTLY ON ARVs
		DrugsActiveCohortDefinition currentlyOnArvs = new DrugsActiveCohortDefinition();		
		currentlyOnArvs.setDrugs(allArvDrugs);
		currentlyOnArvs.setAsOfDate(reportEndDate);	// report end date
		cohort = cds.evaluate(currentlyOnArvs, context);
		log.warn("currently on any ARV treatment: " + cohort.getSize());

		
		// CURRENTLY ON FIRST LINE REGIMEN
		DrugsActiveCohortDefinition currentlyOnFirstLineRegimen = new DrugsActiveCohortDefinition();		
		currentlyOnFirstLineRegimen.setDrugs(firstLineRegimen);
		currentlyOnFirstLineRegimen.setAsOfDate(reportEndDate);	// report end date
		cohort = cds.evaluate(currentlyOnFirstLineRegimen, context);
		log.warn("currently on first line regimen: " + cohort.getSize());

		// CURRENTLY ON SECOND LINE REGIMEN
		DrugsActiveCohortDefinition currentlyOnSecondLineRegimen = new DrugsActiveCohortDefinition();		
		currentlyOnSecondLineRegimen.setDrugs(secondLineRegimen);
		currentlyOnSecondLineRegimen.setAsOfDate(reportEndDate);	// report end date
		cohort = cds.evaluate(currentlyOnSecondLineRegimen, context);		
		log.warn("currently on second line regimen: " + cohort.getSize());

		// LAST WHO STAGE 
		ObsCohortDefinition whoStage = new ObsCohortDefinition();
		Concept whoStageQuestion = Context.getConceptService().getConceptByName("WHO STAGE");		
		whoStage.setQuestion(whoStageQuestion);
		whoStage.setModifier(Modifier.EQUAL);
		whoStage.setTimeModifier(TimeModifier.LAST);		
		whoStage.setSinceDate(reportStartDate);
		whoStage.setUntilDate(reportEndDate);
		
		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 1 ADULT"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 1 ADULT: " + cohort.getSize());

		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 2 ADULT"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 2 ADULT: " + cohort.getSize());

		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 3 ADULT"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 3 ADULT: " + cohort.getSize());
		
		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 4 ADULT"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 4 ADULT: " + cohort.getSize());

		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 1 PEDS"));
		cohort = cds.evaluate(whoStage, context);		
		log.warn("WHO STAGE 1 PEDS: " + cohort.getSize());
		
		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 2 PEDS"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 2 PEDS: " + cohort.getSize());
		
		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 3 PEDS"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 3 PEDS: " + cohort.getSize());
		
		whoStage.setValueCoded(Context.getConceptService().getConceptByName("WHO STAGE 4 PEDS"));
		cohort = cds.evaluate(whoStage, context);
		log.warn("WHO STAGE 4 PEDS: " + cohort.getSize());
		

		// AT LEAST ONE ENCOUNTER IN THE LAST THREE MONTHS (needs some work)		
		EncounterCohortDefinition encounterWithinLastThreeMonths = new EncounterCohortDefinition();
		encounterWithinLastThreeMonths.setSinceDate(reportStartDate);
		encounterWithinLastThreeMonths.setUntilDate(reportEndDate);
		//encounterWithinLastThreeMonths.setEncounterType();
		//encounterWithinLastThreeMonths.setWithinLastMonths(3);
		//encounterWithinLastThreeMonths.setUntilDate(reportEndDate-3m);		
		cohort = cds.evaluate(encounterWithinLastThreeMonths, context);
		log.warn("Any encounter in the last month: " + cohort.getSize());
		
		// NO ENCOUNTER IN THE PAST THREE MONTHS
		InverseCohortDefinition noEncounterWithLastThreeMonths = new InverseCohortDefinition();
		noEncounterWithLastThreeMonths.setBaseDefinition(encounterWithinLastThreeMonths);		
		cohort = cds.evaluate(noEncounterWithLastThreeMonths, context);
		log.warn("No encounter in the last month: " + cohort.getSize());
		

		// STARTED TREATMENT DURING PERIOD
		DrugsStartedCohortDefinition drugsStartedCohortDefinition = new DrugsStartedCohortDefinition();
		drugsStartedCohortDefinition.setDrugs(allArvDrugs);
		drugsStartedCohortDefinition.setStartedOnOrAfter(reportStartDate);
		drugsStartedCohortDefinition.setStartedOnOrBefore(reportEndDate);		
		cohort = cds.evaluate(drugsStartedCohortDefinition, context);
		log.warn("Started on ARVs during period: " + cohort.getSize());
		
		
		// STOPPED TREATMENT DURING PERIOD
		DrugsCompletedCohortDefinition drugsStoppedCohortDefinition = new DrugsCompletedCohortDefinition();
		drugsStoppedCohortDefinition.setDrugs(allArvDrugs);
		drugsStoppedCohortDefinition.setCompletedOnOrAfter(reportStartDate);
		drugsStoppedCohortDefinition.setCompletedOnOrBefore(reportEndDate);		
		cohort = cds.evaluate(drugsStoppedCohortDefinition, context);
		log.warn("Stopped ARVs during period: " + cohort.getSize());
		
		// NOT ON ARV AT END OF PERIOD
		log.warn("currently on any ARV treatment: " + cohort.getSize());
		InverseCohortDefinition notOnArvsAtPeriodEnd = new InverseCohortDefinition();
		notOnArvsAtPeriodEnd.setBaseDefinition(currentlyOnArvs);		
		cohort = cds.evaluate(notOnArvsAtPeriodEnd, context);		
		log.warn("Not on ARVs at end of period: " + cohort.getSize());
		
		CompoundCohortDefinition notOnArvButEligible = new CompoundCohortDefinition();
		notOnArvButEligible.addDefinition(new Mapped<CohortDefinition>(notOnArvsAtPeriodEnd, null));
		notOnArvButEligible.addDefinition(new Mapped<CohortDefinition>(currentlyInHivProgram, null));
		cohort = cds.evaluate(notOnArvButEligible, context);		
		log.warn("Not on ARVs at end of period, but eligible: " + cohort.getSize());
		
		
		// HOSPITALIZED DURING PERIOD
		ObsCohortDefinition hospitalizedDuringPeriod = new ObsCohortDefinition();
		Concept hospitalizedQuestion = Context.getConceptService().getConceptByName("PATIENT HOSPITALIZED SINCE LAST VISIT");		
		hospitalizedDuringPeriod.setQuestion(hospitalizedQuestion);
		hospitalizedDuringPeriod.setSinceDate(reportStartDate);
		hospitalizedDuringPeriod.setUntilDate(reportEndDate);
		hospitalizedDuringPeriod.setValueNumeric(1d);
		hospitalizedDuringPeriod.setModifier(Modifier.EQUAL);
		hospitalizedDuringPeriod.setTimeModifier(TimeModifier.ANY);		
		cohort = cds.evaluate(hospitalizedDuringPeriod, context);
		log.warn("Patients that have been hospitalized during the period: " + cohort.getSize());

		
		
		
		if (true)
			return;
		
		


		
		
		// ===============================================================================			
		//
		//		 Create initial cohort queries to be used within indicators
		//
		// ===============================================================================
		
		//Parameter startDateParameter = new Parameter("startDate", "Start Date:", java.util.Date.class);
		//Parameter endDateParameter = new Parameter("endDate", "End Date:", java.util.Date.class);
		//Parameter locationParameter = new Parameter("location", "Health Center:", org.openmrs.Location.class);
		

		
		// ===============================================================================			
		//
		//		 Create the report definition 
		//
		// ===============================================================================

		PeriodIndicatorReportDefinition reportDefinition = new PeriodIndicatorReportDefinition();
		

		// ===============================================================================			
		//
		//		 Create the dimensions that will be used in the report 
		//
		// ===============================================================================
					
		// Define the GENDER dimension as a breakdown of males and females
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();
		GenderCohortDefinition males = new GenderCohortDefinition("M");
		genderDimension.addCohortDefinition("males", males, null);				
		GenderCohortDefinition females = new GenderCohortDefinition("F");		
		genderDimension.addCohortDefinition("females", females, null);		
		genderDimension.addParameter(ReportingConstants.START_DATE_PARAMETER);
		
		// Not fully implemented yet - so we'll continue to use the existing construct
		//CohortDefinitionDimensionCategory maleCategory = new CohortDefinitionDimensionCategory("male", males, null);
		//CohortDefinitionDimensionCategory femaleCategory = new CohortDefinitionDimensionCategory("female", females, null);
		//genderDimension.addDimensionCategory(maleCategory);
		//genderDimension.addDimensionCategory(femaleCategory);
		
		// We need to map the AGE dimension using the effective date (e.g. "adult as of ...")
		Map<String,Object> ageDimensionMapping = new HashMap<String,Object>();
		ageDimensionMapping.put("effectiveDate", "${endDate}");

		// Define the AGE dimension
		CohortDefinitionDimension ageDimension = new CohortDefinitionDimension();		
		AgeCohortDefinition adults = new AgeCohortDefinition(15, null, null);		
		adults.addParameter(new Parameter("effectiveDate", "As of:", Date.class));
		ageDimension.addCohortDefinition("adults", adults, ageDimensionMapping);
		AgeCohortDefinition pediatrics = new AgeCohortDefinition(null,15, null);
		pediatrics.addParameter(new Parameter("effectiveDate", "As of:", Date.class));
		ageDimension.addCohortDefinition("pediatrics", pediatrics, ageDimensionMapping);		
		//ageDimension.addParameter(ReportingConstants.START_DATE_PARAMETER);

		
		
		// Add dimensions to the report 
		reportDefinition.addDimension("gender", genderDimension);
		reportDefinition.addDimension("age", ageDimension);
		
		
		
		// ===============================================================================			
		//
		//		 Create indicators to be added to the report definition
		//
		// ===============================================================================

		// CURRENTLY IN HIV PROGRAM 
		// Configure the mapping between the default parameters and cohort definition attribute
		Map<String,Object> inHivProgramMapping = new HashMap<String, Object>(); 
		inHivProgramMapping.put("sinceDate", "${startDate}");
		inHivProgramMapping.put("untilDate", "${endDate}");
		PeriodCohortIndicator inHivProgramIndicator = new PeriodCohortIndicator();
		inHivProgramIndicator.setName("Number of patients currently enrolled in the HIV Program");
		inHivProgramIndicator.setCohortDefinition(currentlyInHivProgram, inHivProgramMapping);

		
		// CURRENTLY ON ANTIRETROVIRALS
		
		
		// STARTED 
		
		
		// ===============================================================================			
		//
		//		 Add all indicators to the report with or without dimension options
		//
		// ===============================================================================
				
		// Currently enrolled in HIV Program (breakdown by age and gender)
		reportDefinition.addIndicator("1", "# of patients currently in HIV Program", inHivProgramIndicator);
		reportDefinition.addIndicator("2", "# of male patients currently in HIV Program", inHivProgramIndicator, "gender=males");
		reportDefinition.addIndicator("3", "# of female patients currently in HIV Program", inHivProgramIndicator, "gender=females");
		reportDefinition.addIndicator("4", "# of adult patients currently in HIV Program", inHivProgramIndicator, "age=adults");
		reportDefinition.addIndicator("5", "# of pediatric patients currently in HIV Program", inHivProgramIndicator, "age=pediatrics");
		reportDefinition.addIndicator("6", "# of male adult patients currently in HIV Program", inHivProgramIndicator, "gender=males|age=adults");
		reportDefinition.addIndicator("7", "# of female adult patients currently in HIV Program", inHivProgramIndicator, "gender=females|age=adults");
		reportDefinition.addIndicator("8", "# of male pediatric patients currently in HIV Program", inHivProgramIndicator, "gender=males|age=pediatrics");
		reportDefinition.addIndicator("9", "# of female pediatric patients currently in HIV Program", inHivProgramIndicator, "gender=females|age=pediatrics");
		
		// ===============================================================================			
		//
		//		 Evaluate Report Definition
		//
		// ===============================================================================
		
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.addParameterValue("startDate", new Date());
		evalContext.addParameterValue("endDate", new Date());
		evalContext.addParameterValue("location", rwinkwavu);
		
		ReportData reportData = Context.getService(ReportService.class).evaluate(reportDefinition, evalContext);
		for (DataSet<?> dataSet : reportData.getDataSets().values()) { 
			for(DataSetRow<?> row : dataSet) { 
				for(DataSetColumn column : row.getColumnValues().keySet()) { 
					
					CohortIndicatorAndDimensionResult result = 
						(CohortIndicatorAndDimensionResult) row.getColumnValue(column);
					
					log.info(column.getColumnKey() + ".) " + column.getDisplayName() + " = " + result.getValue());
				}
			}			
		}			
	}
	
	
	public void shouldCalculateNewlyEnrolledDuringMonth() { 		
		ProgramStateCohortDefinition pscd = new ProgramStateCohortDefinition();
		pscd.addParameter(ReportingConstants.START_DATE_PARAMETER);
		pscd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		pscd.addParameter(ReportingConstants.LOCATION_PARAMETER);
		
	}
	
	public void shouldDoSomething() throws Exception {
		CohortDefinition female = new GenderCohortDefinition("F");
		CohortDefinition male = new GenderCohortDefinition("M");
		
		CohortDefinitionDimension gender = new CohortDefinitionDimension();
		gender.addCohortDefinition("female", female, null);
		gender.addCohortDefinition("male", male, null);
		
		ProgramStateCohortDefinition inProgram = new ProgramStateCohortDefinition();
		inProgram.setProgram(Context.getProgramWorkflowService().getProgram(1));

		CohortIndicator ind = new CohortIndicator("In HIV Program", null, new Mapped<ProgramStateCohortDefinition>(inProgram, null), null, null);
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addDimension("gender", new Mapped<CohortDefinitionDimension>(gender, null));
		dsd.addColumn("1", "Total in program", new Mapped<CohortIndicator>(ind, null), "");
		dsd.addColumn("1.a", "Males in program", new Mapped<CohortIndicator>(ind, null), "gender=male");
		dsd.addColumn("1.b", "Females in program", new Mapped<CohortIndicator>(ind, null), "gender=female");
		
		MapDataSet<IndicatorResult<CohortIndicator>> ds = (MapDataSet<IndicatorResult<CohortIndicator>>) Context.getService(DataSetDefinitionService.class).evaluate(dsd, null);
		
		int i = 0;
		for (DataSetRow<? extends IndicatorResult<CohortIndicator>> row : ds) {
			System.out.println("Row " + (++i));
			for (Map.Entry<DataSetColumn, ? extends IndicatorResult<CohortIndicator>> col : row.getColumnValues().entrySet()) {
				System.out.println(col.getKey().getDisplayName() + " -> " + col.getValue().getValue());
			}
		}
	}		
		
		
		
	
	
	/**
	 * Auto generated method comment
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldPepfarReport() throws Exception {
		/**
		 * TODO: This is really just here to create a new report in the test
		 * It also usefully shows how some of the classes can be used.
		 * It isn't worth fixing to get it working, so I'm commenting it out for reference
		 * 
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/report/include/PepfarReportTest.xml");
		
		authenticate();
		
		Cohort inputCohort = null;
		
		Parameter startDateParam = new Parameter("report.startDate", "Report Start Date", java.util.Date.class, null);
		Parameter endDateParam = new Parameter("report.endDate", "Report End Date", java.util.Date.class, null);
		
		log.info("Creating basic PatientSearches");
		CohortDefinition male = new PatientCharacteristicCohortDefinition();
		male.setParameterValue("gender", "M");
		
		CohortDefinition female = new PatientCharacteristicCohortDefinition();
		female.setParameterValue("gender", "F");
		
		CohortDefinition adult = new PatientCharacteristicCohortDefinition();
		adult.setParameterValue("minAge", "15");
		
		CohortDefinition child = new PatientCharacteristicCohortDefinition();
		child.setParameterValue("maxAge", "15");
		
		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		if (hivProgram == null) {
			List<Program> programs = Context.getProgramWorkflowService().getAllPrograms();
			for (Program p : programs) {
				hivProgram = p;
			}
		}
		
		assertNotNull(hivProgram);
		
		CohortDefinition enrolledBeforeDate = new ProgramStateCohortDefinition());
		enrolledBeforeDate.setParameterValue("program", hivProgram);
		enrolledBeforeDate.setParameterOverride("untilDate", true);


		log.info("Creating DataSets");
		List<DataSetDefinition> dataSets = new ArrayList<DataSetDefinition>();
		CohortDataSetDefinition dataSetDef = new CohortDataSetDefinition();
		dataSetDef.setName("Cohorts");
		dataSetDef.addStrategy("Cumulative ever enrolled before start of period", enrolledBeforeDate);
		dataSetDef.addStrategy("Male adults ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { male, "and", adult, "and", enrolledBeforeDate }));
		dataSetDef.addStrategy("Feale adults ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { female, "and", adult, "and", enrolledBeforeDate }));
		dataSetDef.addStrategy("Male children ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { male, "and", child, "and", enrolledBeforeDate }));
		dataSetDef.addStrategy("Female children ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { female, "and", child, "and", enrolledBeforeDate }));
		dataSets.add(dataSetDef);
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(startDateParam);
		parameters.add(endDateParam);
		
		log.info("Creating the ReportDefinition");
		ReportDefinition schema = new ReportDefinition();
		schema.setReportDefinitionId(123);
		schema.setName("Pepfar Report");
		schema.setDescription("desc");
		schema.setDataSetDefinitions(dataSets);
		schema.setReportParameters(parameters);
		
		// todo
		// set the xml file on the schema
		
		log.info("Creating EvaluationContext");
		EvaluationContext evalContext = new EvaluationContext();
		
		for (Map.Entry<Parameter, Object> e : getUserEnteredParameters(schema.getReportParameters()).entrySet()) {
			log.info("adding parameter value " + e.getKey());
			evalContext.addParameterValue(e.getKey(), e.getValue());
		}
		
		// TODO figure out about the non-top-level parameters
		
		// run the report
		ReportService rs = (ReportService) Context.getService(ReportService.class);
		ReportData data = rs.evaluate(schema, inputCohort, evalContext);
		
		Serializer serializer = OpenmrsUtil.getSerializer();
		StringWriter writer = new StringWriter();
		serializer.write(data, writer);
		//System.out.println("Serialized report:\n" + writer.toString());
		
		TsvReportRenderer renderer = new TsvReportRenderer();
		
		//System.out.println("Rendering results:");
		//renderer.render(data, null, System.out);
		*/
	}
	
}
