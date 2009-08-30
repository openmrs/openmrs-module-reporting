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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.LocationCohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.aggregation.CountAggregator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *
 */
public class IndicatorTest extends BaseModuleContextSensitiveTest {
	
	/* Logger */
	private static Log log = LogFactory.getLog(IndicatorTest.class);	
	
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
	public void evaluateIndicator() throws Exception { 
		
		String uuid = "79e204e8-0360-4058-9966-072f371b5e6c"; 
		Indicator indicator = Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
		log.info("indicator = " + indicator);
		
		//Map<String, Object> parameterValues = new HashMap<String, Object>();
		//parameterValues.put("startDate", new Date());
		//parameterValues.put("endDate", new Date());
		//parameterValues.put("location", new Location());
		
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", new Date());
		
		
		//context.setParameterValues(parameterValues);
		
		IndicatorResult result = 
			Context.getService(IndicatorService.class).evaluate(indicator, context);		
	
		log.info("Result: " + result);
	}
	
	
	@Ignore
	public void evaluteIndicatorReport() throws Exception {
		
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		
		ReportDefinition rs = new ReportDefinition();
		rs.addParameter(new Parameter("report.location", "Report Location", Location.class));
		rs.addParameter(new Parameter("report.reportDate", "Report Date", Date.class));
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.addParameter(new Parameter("location", "Location", Location.class));
		dsd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		rs.addDataSetDefinition("test", dsd, 
				ParameterizableUtil.createParameterMappings("location=${report.location},effectiveDate=${report.reportDate}"));
		
		CohortIndicator indicator = new CohortIndicator();
		indicator.addParameter(new Parameter("indicator.location", "Location", Location.class));
		indicator.addParameter(new Parameter("indicator.effDate", "Date", Date.class));
		indicator.setLogicCriteria(null);
		indicator.setAggregator(CountAggregator.class);
		
		LocationCohortDefinition atSite = new LocationCohortDefinition();
		atSite.addParameter(new Parameter("location", "location", Location.class));
		indicator.setCohortDefinition(atSite, ParameterizableUtil.createParameterMappings("location=${indicator.location}"));
		dsd.addCohortIndicator("patientsAtSite", indicator, 
				ParameterizableUtil.createParameterMappings("indicator.location=${location},indicator.effDate=${effectiveDate}"));
		
		// Dimensions
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();		
		genderDimension.addCohortDefinition("male", new GenderCohortDefinition("M"), null);		
		genderDimension.addCohortDefinition("female", new GenderCohortDefinition("F"), null);		
		dsd.addDimension("gender", genderDimension, null);
		
		CohortDefinitionDimension ageDimension = new CohortDefinitionDimension();
		ageDimension.addParameter(new Parameter("ageDate", "ageDate", Date.class));

		AgeCohortDefinition adult = new AgeCohortDefinition(15, null, null);
		adult.setMinAge(15);
		adult.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		ageDimension.addCohortDefinition("adult", adult, ParameterizableUtil.createParameterMappings("effectiveDate=${ageDate}"));
		
		AgeCohortDefinition child = new AgeCohortDefinition(null, 14, null);
		child.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		ageDimension.addCohortDefinition("child", child, ParameterizableUtil.createParameterMappings("effectiveDate=${ageDate}"));
		
		dsd.addDimension("age", ageDimension, ParameterizableUtil.createParameterMappings("ageDate=${indicator.effDate}"));
		
		// Replace "patientsAtSite" with indicator
		dsd.addColumnSpecification("1.A", "Male Adult", Object.class, indicator, "gender=male,age=adult");
		dsd.addColumnSpecification("1.B", "Male Child", Object.class, indicator, "gender=male,age=child");
		dsd.addColumnSpecification("2.A", "Female Adult", Object.class, indicator, "gender=female,age=adult");
		dsd.addColumnSpecification("2.B", "Female Child", Object.class, indicator, "gender=female,age=child");
		
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
