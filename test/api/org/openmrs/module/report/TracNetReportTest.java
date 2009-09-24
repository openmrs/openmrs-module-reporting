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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
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
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.ReportingConstants;
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
		//		 Lookup data that we might use later
		//
		// ===============================================================================		

		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		ProgramWorkflow treatmentStatusWorkflow = hivProgram.getWorkflowByName("TREATMENT STATUS");
		ProgramWorkflowState onAntiretroviralsState = treatmentStatusWorkflow.getStateByName("ON ANTIRETROVIRALS");
		Location rwinkwavu = Context.getLocationService().getLocation(26);
		
		
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
		//		 Create initial cohort queries to be used within indicators
		//
		// ===============================================================================
		
		// Males 
		GenderCohortDefinition males = new GenderCohortDefinition("M");

		// Females 
		GenderCohortDefinition females = new GenderCohortDefinition("F");		
		
		// Adults 
		AgeCohortDefinition adults = new AgeCohortDefinition(15, null, null);		
		adults.addParameter(new Parameter("effectiveDate", "As of:", Date.class));

		// Pediatrics -- the effective parameter 
		AgeCohortDefinition pediatrics = new AgeCohortDefinition(null,15, null);
		pediatrics.addParameter(new Parameter("effectiveDate", "As of:", Date.class));
						
		// Currently in HIV Program  
		ProgramStateCohortDefinition inHivProgram = new ProgramStateCohortDefinition();
		inHivProgram.setProgram(hivProgram);
		inHivProgram.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		inHivProgram.addParameter(new Parameter("untilDate", "Period ends:", Date.class));
		
		// Currently on ARV
		// Was ON ARV as of the last day of the period.  sinceDate and untilDate map to the last day of the period
		ProgramStateCohortDefinition onArvTreatment = new ProgramStateCohortDefinition();
		onArvTreatment.setProgram(hivProgram);
		onArvTreatment.setStateList(new ArrayList<ProgramWorkflowState>());
		onArvTreatment.addParameter(new Parameter("sinceDate", "Period begins:", Date.class));
		onArvTreatment.addParameter(new Parameter("untilDate", "Period begins:", Date.class));
		
		// Currently on First Line Regimen
		
		// Currently on Second Line Regimen
		
		// Started HIV Program during the month
		
		// Started ON ARV during the month
		
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
		genderDimension.addCohortDefinition("males", males, null);		
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
		ageDimension.addCohortDefinition("adults", adults, ageDimensionMapping);
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

		// Configure the mapping between the default parameters and cohort definition attribute
		Map<String,Object> inHivProgramMapping = new HashMap<String, Object>(); 
		inHivProgramMapping.put("sinceDate", "${startDate}");
		inHivProgramMapping.put("untilDate", "${endDate}");		

		// In HIV Program Indicator 
		PeriodCohortIndicator inHivProgramIndicator = new PeriodCohortIndicator();
		inHivProgramIndicator.setName("Number of patients currently enrolled in the HIV Program");
		inHivProgramIndicator.setCohortDefinition(inHivProgram, inHivProgramMapping);

		
		
		
		
		// ===============================================================================			
		//
		//		 Add all indicators to the report with or without dimension options
		//
		// ===============================================================================
				
		reportDefinition.addIndicator("1", "# of patients currently in HIV Program", inHivProgramIndicator);
		reportDefinition.addIndicator("2", "# of male patients currently in HIV Program", inHivProgramIndicator, "gender=males");
		reportDefinition.addIndicator("3", "# of female patients currently in HIV Program", inHivProgramIndicator, "gender=females");
		reportDefinition.addIndicator("4", "# of adult patients currently in HIV Program", inHivProgramIndicator, "age=adults");
		reportDefinition.addIndicator("5", "# of pediatric patients currently in HIV Program", inHivProgramIndicator, "age=pediatrics");
		reportDefinition.addIndicator("6", "# of male adult patients currently in HIV Program", inHivProgramIndicator, "gender=males,age=adults");
		reportDefinition.addIndicator("7", "# of female adult patients currently in HIV Program", inHivProgramIndicator, "gender=females,age=adults");
		reportDefinition.addIndicator("8", "# of male pediatric patients currently in HIV Program", inHivProgramIndicator, "gender=males,age=pediatrics");
		reportDefinition.addIndicator("9", "# of female pediatric patients currently in HIV Program", inHivProgramIndicator, "gender=females,age=pediatrics");
		
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
