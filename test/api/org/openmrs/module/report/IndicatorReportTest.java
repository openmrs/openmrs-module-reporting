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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that executes tests on Reports
 */
public class IndicatorReportTest extends BaseModuleContextSensitiveTest {
	
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void runBeforeAllTests() throws Exception {
		authenticate();
	}
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	/**
	 * Tests report
	 */
	@Test
	public void shouldEvaluateCohortIndicatorReport() throws Exception {
		
		AgeCohortDefinition cohortDefinition = new AgeCohortDefinition();
		cohortDefinition.setMaxAge(100);
		cohortDefinition.setEffectiveDate(new Date());
		//childOnDate.addParameter(new Parameter("effectiveDate", "Age As of Date", Date.class, null, false, false));
		
		//CohortDataSetDefinition datasetDefinition = new CohortDataSetDefinition();
		//datasetDefinition.addParameter(new Parameter("d1", "Start Date", Date.class, null, true, false));
		//dsd.addParameter(new Parameter("d2", "End Date", Date.class, null, true, false));
		//dsd.addStrategy("Children at Start", new Mapped<CohortDefinition>(childOnDate, "effectiveDate=${d1}"));
		//dsd.addStrategy("Children at End", new Mapped<CohortDefinition>(childOnDate, "effectiveDate=${d2}"));

		//CohortDataSetDefinition datasetDefinition = new CohortDataSetDefinition();
		//datasetDefinition.addStrategy("# Children", cohortDefinition, "");
				

				
		
		CohortIndicatorDataSetDefinition datasetDefinition = new CohortIndicatorDataSetDefinition();
		
		/*
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setGender("M");
		genderDimension.addCohortDefinition("male", males, null);		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setGender("F");
		genderDimension.addCohortDefinition("female", females, null);		
		datasetDefinition.addDimension("gender", genderDimension, null);		
		*/
		
		CohortIndicator indicator = new CohortIndicator();
		indicator.setName(cohortDefinition.getName());
		indicator.setCohortDefinition(cohortDefinition, null);
		/*
		datasetDefinition.addIndicator("Test Indicator", indicator, "");		
		datasetDefinition.addColumnSpecification(
				"1.", 
				"# Adult Patients", 
				Number.class, 
				"Test Indicator", 
				null);
		*/
		
		datasetDefinition.addCohortIndicator(indicator, null);
		datasetDefinition.addColumnSpecification("1.a", "# Adult Patients", Number.class, new Mapped<CohortIndicator>(indicator, null), null);
		
		
		ReportDefinition report = new ReportDefinition();
		//report.addParameter(new Parameter("report.startDate", "Report Start Date", Date.class, null, true, false));
		//report.addParameter(new Parameter("report.endDate", "Report End Date", Date.class, null, true, false));
		//report.addDataSetDefinition(new Mapped<DataSetDefinition>(dsd, "d1=${report.startDate},d2=${report.endDate}"));
		report.addDataSetDefinition("test", datasetDefinition, null);
		
		EvaluationContext evalContext = new EvaluationContext();
		Cohort baseCohort = Context.getPatientSetService().getAllPatients();
		evalContext.setBaseCohort(baseCohort);
		//ec.addParameterValue("report.startDate", ymd.parse("1980-01-01"));
		//ec.addParameterValue("report.endDate", ymd.parse("2008-01-01"));
		
		
		ReportService rs = (ReportService) Context.getService(ReportService.class);
		ReportData data = rs.evaluate(report, evalContext);

		 new CsvReportRenderer().render(data, null, System.out);
	}
	
	
	@Test
	public void shouldEvaluateIndicator() { 

		// Create a new program state cohort definition
		ProgramStateCohortDefinition cohortDefinition = new ProgramStateCohortDefinition();
		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		cohortDefinition.setProgram(program);
		cohortDefinition.addParameter(new Parameter("sinceDate", "Enrolled since", Date.class, null, true));
		cohortDefinition.addParameter(new Parameter("untilDate", "Enrolled until", Date.class, null, true));
		cohortDefinition.addParameter(new Parameter("location", "Enrolled at", Location.class, null, true));
				
		// Create period indicator  
		PeriodCohortIndicator indicator = new PeriodCohortIndicator();
		indicator.addParameter(new Parameter("startDate", "Enter a Start Date", Date.class, null, true));
		indicator.addParameter(new Parameter("endDate", "Enter an End Date", Date.class, null, true));
		indicator.addParameter(new Parameter("location", "Choose a Location", Location.class, null, true));
		
		// Map the cohort definition to the indicator
		indicator.setCohortDefinition(cohortDefinition, 
				ParameterizableUtil.createParameterMappings("sinceDate=${startDate},untilDate=${endDate},location=${location}"));
		
		Calendar calendar = Calendar.getInstance();
		Date endDate = calendar.getTime();
		calendar.set(2009, 8, 1);
		Date startDate = calendar.getTime();
		
		Location location = Context.getLocationService().getAllLocations().get(0);
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", startDate);
		context.addParameterValue("endDate", endDate);
		context.addParameterValue("location", location);
		
		
		IndicatorResult result = Context.getService(IndicatorService.class).evaluate(indicator, context);
		log.info("result: " + result.getValue());
	
	}
	
	
	@Test
	public void shouldCreateIndicatorReport() { 
		
		
		// Get existing dataset definition
		ReportDefinition reportDefinition = new ReportDefinition();
		
		reportDefinition.setName("Test Report");
		reportDefinition.setDescription("Testing whether the report can be saved when it already exists");
		
		reportDefinition = Context.getService(ReportService.class).saveReportDefinition(reportDefinition);
		
		
		// Check whether the report definition is new 
		Boolean isNew = (reportDefinition.getUuid() == null);
		Boolean hasIndicatorDataset = 
			(reportDefinition.getDataSetDefinitions() != null && !reportDefinition.getDataSetDefinitions().isEmpty());
		
		String [] selectedIndicatorIds = { "2e91f5f0-af75-4403-8ba4-9e9b10befad8" };
		
		log.info("Indicators: " + selectedIndicatorIds);
		
		// Add indicators to a report schema
		if (selectedIndicatorIds != null && selectedIndicatorIds.length > 0) { 

			// We just create a new dataset definition each time
			CohortIndicatorDataSetDefinition datasetDefinition = null;
			
			// If the report definition is new, we create a new dataset
			if (!hasIndicatorDataset) { 			
				// Dataset should be created under the covers
				datasetDefinition = new CohortIndicatorDataSetDefinition();
				datasetDefinition.setName(reportDefinition.getName() + " Dataset");
				
				log.info("Create new dataset definition for report " + datasetDefinition.getName() + " (" + datasetDefinition.getUuid() + ")");
			} 
			// Otherwise we just get the first available dataset
			else { 
				Mapped<? extends DataSetDefinition> mappedDatasetDefinition = 
					(Mapped<? extends DataSetDefinition>) reportDefinition.getDataSetDefinitions().get(0);
				
				datasetDefinition = 
					(CohortIndicatorDataSetDefinition) mappedDatasetDefinition.getParameterizable();
				log.info("Get existing dataset definition from report " + datasetDefinition.getName() + " (" + datasetDefinition.getUuid() + ")");
			}
			

			for (String uuid : selectedIndicatorIds) { 
				log.info("Looking up indicator: " + uuid);
				// FIXME Assumes cohort indicators
				CohortIndicator indicator = (CohortIndicator)
					Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
				
				log.info("Found indicator " + indicator);					
				if (indicator != null) { 
					Map<String,Object> mapping = 
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}");
					Mapped<CohortIndicator> mappedIndicator = new Mapped<CohortIndicator>(indicator, mapping);
					// Adding indicator to dataset definition with default parameter mapping
					datasetDefinition.addCohortIndicator(indicator.getName(), mappedIndicator);
					
					// Adding column specification to dataset 
					datasetDefinition.addColumnSpecification(indicator.getName(), 
							indicator.getDescription(), Number.class, mappedIndicator, null);						
											
				}										
			}

			// Save the intermediate dataset definition
			datasetDefinition = 
				(CohortIndicatorDataSetDefinition) Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(datasetDefinition);
			
			log.info("Add dataset definition: " + datasetDefinition.getUuid() + " to the report");
			// Remove all existing dataset definitions
			// FIXME: Adding dataset to report requires mapping
			// (like "location=${report.location},effectiveDate=${report.reportDate}")
			reportDefinition.getDataSetDefinitions().clear();			
			reportDefinition.addDataSetDefinition("test", datasetDefinition,
					ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
			
		}
		
		log.info("Saving report definition " + reportDefinition.getUuid() + ", name=" + reportDefinition.getName());
		log.info("Dataset definition " + reportDefinition.getDataSetDefinitions().size());
		
		Context.getService(ReportService.class).saveReportDefinition(reportDefinition);		
		
	}
	
	@Test
	public void shouldEvaluateCohortIndicator() { 
		
		EvaluationContext context = new EvaluationContext();
		
		CohortIndicator indicator = 
			(CohortIndicator) Context.getService(IndicatorService.class).getIndicatorByUuid("2e91f5f0-af75-4403-8ba4-9e9b10befad8");

		
		IndicatorResult result = Context.getService(IndicatorService.class).evaluate(indicator, context);
		
		log.info("result: " + result.getValue());
		
	}
	
	
}
