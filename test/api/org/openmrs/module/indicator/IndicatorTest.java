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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.LocationCohortDefinition;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.aggregation.CountAggregator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportSchema;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *
 */
public class IndicatorTest extends BaseModuleContextSensitiveTest {
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void runBeforeAllTests() throws Exception {
		//executeDataSet("org/openmrs/module/indicator/include/IndicatorTest.xml");
		authenticate();
	}
	
	@Test
	public void test() throws Exception {
		
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		
		ReportSchema rs = new ReportSchema();
		rs.addParameter(new Parameter("report.location", "Report Location", Location.class, null, true));
		rs.addParameter(new Parameter("report.reportDate", "Report Date", Date.class, null, true));
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addParameter(new Parameter("location", "Location", Location.class, null, true));
		dsd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class, null, true));
		rs.addDataSetDefinition(dsd, "location=${report.location},effectiveDate=${report.reportDate}");
		
		CohortIndicator indicator = new CohortIndicator();
		indicator.addParameter(new Parameter("indicator.location", "Location", Location.class, null, true));
		indicator.addParameter(new Parameter("indicator.effDate", "Date", Date.class, null, true));
		LocationCohortDefinition atSite = new LocationCohortDefinition();
		atSite.enableParameter("location", null, true);
		indicator.setCohortDefinition(atSite, "location=${indicator.location}");
		indicator.setLogicCriteria(null);
		indicator.setAggregator(CountAggregator.class);
		dsd.addIndicator("patientsAtSite", indicator, "indicator.location=${location},indicator.effDate=${effectiveDate}");
		
		// Dimensions
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();
		
		PatientCharacteristicCohortDefinition males = new PatientCharacteristicCohortDefinition();
		males.setGender("M");
		genderDimension.addCohortDefinition("male", males, null);
		
		PatientCharacteristicCohortDefinition females = new PatientCharacteristicCohortDefinition();
		females.setGender("F");
		genderDimension.addCohortDefinition("female", females, null);
		
		dsd.addDimension("gender", genderDimension, null);
		
		CohortDefinitionDimension ageDimension = new CohortDefinitionDimension();
		ageDimension.addParameter(new Parameter("ageDate", "ageDate", Date.class, null, true));

		PatientCharacteristicCohortDefinition adult = new PatientCharacteristicCohortDefinition();
		adult.setMinAge(15);
		adult.enableParameter("effectiveDate", null, true);
		ageDimension.addCohortDefinition("adult", adult, "effectiveDate=${ageDate}");
		
		PatientCharacteristicCohortDefinition child = new PatientCharacteristicCohortDefinition();
		child.setMaxAge(14);
		child.enableParameter("effectiveDate", null, true);
		ageDimension.addCohortDefinition("child", child, "effectiveDate=${ageDate}");
		
		dsd.addDimension("age", ageDimension, "ageDate=${indicator.effDate}");
		
		dsd.addColumnSpecification("1.A", "Male Adult", Object.class, "patientsAtSite", "gender=male,age=adult");
		dsd.addColumnSpecification("1.B", "Male Child", Object.class, "patientsAtSite", "gender=male,age=child");
		dsd.addColumnSpecification("2.A", "Female Adult", Object.class, "patientsAtSite", "gender=female,age=adult");
		dsd.addColumnSpecification("2.B", "Female Child", Object.class, "patientsAtSite", "gender=female,age=child");
		
		EvaluationContext context = new EvaluationContext();
		CsvReportRenderer renderer = new CsvReportRenderer();
		ReportData data = null;
		
		context.addParameterValue("report.location", Context.getLocationService().getLocation(1));
		context.addParameterValue("report.reportDate", ymd.parse("2007-01-01"));
		data = Context.getService(ReportService.class).evaluate(rs, context);
		renderer.render(data, null, System.out);
		
		context.addParameterValue("report.location", Context.getLocationService().getLocation(1));
		context.addParameterValue("report.reportDate", ymd.parse("2008-01-01"));
		data = Context.getService(ReportService.class).evaluate(rs, context);
		renderer.render(data, null, System.out);
		
		context = new EvaluationContext();
		context.addParameterValue("report.location", Context.getLocationService().getLocation(1));
		context.addParameterValue("report.reportDate", ymd.parse("2007-01-01"));
		data = Context.getService(ReportService.class).evaluate(rs, context);
		renderer.render(data, null, System.out);
		
		context = new EvaluationContext();
		context.addParameterValue("report.location", Context.getLocationService().getLocation(1));
		context.addParameterValue("report.reportDate", ymd.parse("2008-01-01"));
		data = Context.getService(ReportService.class).evaluate(rs, context);
		renderer.render(data, null, System.out);
	}
	
}
