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
package org.openmrs.module.reporting.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that executes tests on Reports
 */
public class ReportTest extends BaseModuleContextSensitiveTest {
	
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void runBeforeAllTests() throws Exception {
		executeDataSet("org/openmrs/module/report/include/ReportTest.xml");
	}
	
	/**
	 * Tests report
	 */
	@Test
	public void testReport() throws Exception {
		
		AgeCohortDefinition childOnDate = new AgeCohortDefinition();
		childOnDate.setMaxAge(14);
		childOnDate.addParameter(new Parameter("effectiveDate", "Age As of Date", Date.class));
		
		/*
		CohortDataSetDefinition dsd = new CohortDataSetDefinition();
		dsd.addParameter(new Parameter("d1", "Start Date", Date.class));
		dsd.addParameter(new Parameter("d2", "End Date", Date.class));
		dsd.addDefinition("childAtStart", "Children at Start", 
				new Mapped<CohortDefinition>(childOnDate, ParameterizableUtil.createParameterMappings("effectiveDate=${d1}")));
		dsd.addDefinition("childAtEnd", "Children at End", 
				new Mapped<CohortDefinition>(childOnDate, ParameterizableUtil.createParameterMappings("effectiveDate=${d2}")));
		
		ReportDefinition report = new ReportDefinition();
		report.addParameter(new Parameter("report.startDate", "Report Start Date", Date.class));
		report.addParameter(new Parameter("report.endDate", "Report End Date", Date.class));
		report.addDataSetDefinition("test", 
				new Mapped<DataSetDefinition>(dsd, ParameterizableUtil.createParameterMappings("d1=${report.startDate},d2=${report.endDate}")));
		
		EvaluationContext ec = new EvaluationContext();
		ec.addParameterValue("report.startDate", ymd.parse("1980-01-01"));
		ec.addParameterValue("report.endDate", ymd.parse("2008-01-01"));
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, ec);

		CsvReportRenderer renderer = new CsvReportRenderer();
		renderer.render(data, null, System.out);
		*/
	}
}
