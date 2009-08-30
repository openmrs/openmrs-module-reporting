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
package org.openmrs.module.indicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.GroupMethod;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.LocationCohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.aggregation.CountAggregator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *  Test implemented as a proof-of-concept for producing the Rwanda Tracnet Report
 */
public class TracNetReportTest extends BaseModuleContextSensitiveTest {

	private static final DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void runBeforeAllTests() throws Exception {
		authenticate();
	}	
	
	
	public ReportDefinition setupReport() {
		
		ReportDefinition rs = new ReportDefinition();
		rs.addParameter(new Parameter("report.location", "Report Facility", Location.class));
		rs.addParameter(new Parameter("report.startDate", "Report Start Date", Date.class));
		rs.addParameter(new Parameter("report.endDate", "Report End Date", Date.class));
		
		LocationCohortDefinition initialCohort = new LocationCohortDefinition();
		initialCohort.setCalculationMethod(PatientLocationMethod.PATIENT_HEALTH_CENTER);
		initialCohort.addParameter(new Parameter("location", "location", Location.class));
		rs.setBaseCohortDefinition(initialCohort, ParameterizableUtil.createParameterMappings("location=${report.location}"));

		return rs;
	}
	
	public CohortIndicatorDataSetDefinition setupDataSetDefinition(ReportDefinition rs) {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addParameter(new Parameter("dataSet.startDate", "DataSet Start Date", Date.class));
		dsd.addParameter(new Parameter("dataSet.endDate", "DataSet End Date", Date.class));
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dataSet.startDate", "${report.startDate}");
		paramMap.put("dataSet.endDate", "${report.endDate}");
		
		rs.addDataSetDefinition("test", dsd, paramMap);
		setupDimensions(dsd);
		setupIndicators(dsd);
		
		return dsd;
	}
	
	public void setupDimensions(CohortIndicatorDataSetDefinition dsd) {
		
		//****** GENDER *******
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setGender("M");
		genderDimension.addCohortDefinition("male", males, null);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setGender("F");
		genderDimension.addCohortDefinition("female", females, null);
		
		dsd.addDimension("gender", genderDimension, null);
		
		//****** AGE *******
		CohortDefinitionDimension ageDimension = new CohortDefinitionDimension();
		ageDimension.addParameter(new Parameter("ageDate", "ageDate", Date.class));

		AgeCohortDefinition adult = new AgeCohortDefinition();
		adult.setMinAge(15);
		adult.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		ageDimension.addCohortDefinition("adult", adult, ParameterizableUtil.createParameterMappings("effectiveDate=${ageDate}"));
		
		AgeCohortDefinition child = new AgeCohortDefinition();
		child.setMaxAge(14);
		child.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		ageDimension.addCohortDefinition("child", child, ParameterizableUtil.createParameterMappings("effectiveDate=${ageDate}"));
		
		dsd.addDimension("age", ageDimension, ParameterizableUtil.createParameterMappings("ageDate=${dataSet.startDate}"));
	}
	
	public CohortIndicator setupIndicator(String key, CohortIndicatorDataSetDefinition dsd) {
		
		CohortIndicator indicator = new CohortIndicator();
		indicator.addParameter(new Parameter("indicator.startDate", "Indicator Start Date", Date.class));
		indicator.addParameter(new Parameter("indicator.endDate", "Indicator End Date", Date.class));
		indicator.setLogicCriteria(null);
		indicator.setAggregator(CountAggregator.class);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("indicator.startDate", "${dataSet.startDate}");
		paramMap.put("indicator.endDate", "${dataSet.endDate}");
		
		dsd.addCohortIndicator(key, indicator, paramMap);	
		
		return indicator;
	}
	
	public void setupIndicators(CohortIndicatorDataSetDefinition dsd) {
		
		// ********* ON ARVS AT END OF PERIOD *********
		CohortIndicator i = setupIndicator("onArvsAtEnd", dsd);
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.addDrugSet(Context.getConceptService().getConceptByName("ANTIRETROVIRAL DRUGS"));
		cd.setAnyOrAll(GroupMethod.ANY);
		cd.addParameter(new Parameter("sinceDate", "sinceDate", Date.class));
		cd.addParameter(new Parameter("untilDate", "untilDate", Date.class));
		i.setCohortDefinition(cd, ParameterizableUtil.createParameterMappings("sinceDate=${indicator.endDate},untilDate=${indicator.endDate}"));
	}
	
	@Test
	public void test() throws Exception {
		
		ReportDefinition rs = setupReport();
		CohortIndicatorDataSetDefinition dsd = setupDataSetDefinition(rs);

		CohortIndicator indicator = dsd.getIndicator("onArvsAtEnd").getParameterizable();
		
		// Replaced "onArvsAtEnd" with indicator
		dsd.addColumnSpecification("4.", "Nombre total de patients pediatriques (moins de 15 ans) qui sont actuellement sous ARV", 
								   Number.class, indicator, "age=child");
		
		dsd.addColumnSpecification("9.", "Nombre total de patients adultes (plus de 15 ans) qui sont actuellement sous traitement ARV", 
				   Number.class, indicator, "age=adult");
		
		EvaluationContext context = new EvaluationContext();
		CsvReportRenderer renderer = new CsvReportRenderer();
		ReportData data = null;
		
		System.out.println("Patient on ARVs at the end of 2008 by Location...");
		for (Location l : Context.getLocationService().getAllLocations()) {
			System.out.println("\n" + l.getName() + "\n" + "------------------------");
			context.addParameterValue("report.location", l);
			context.addParameterValue("report.startDate", ymd.parse("2008-12-01"));
			context.addParameterValue("report.endDate", ymd.parse("2008-12-31"));
			data = Context.getService(ReportService.class).evaluate(rs, context);
			renderer.render(data, null, System.out);
		}
	}
	

}
