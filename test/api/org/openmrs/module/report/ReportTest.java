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
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
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
		
		PatientCharacteristicCohortDefinition childOnDate = new PatientCharacteristicCohortDefinition();
		childOnDate.setMaxAge(14);
		childOnDate.addParameter(new Parameter("effectiveDate", "Age As of Date", Date.class, null, false));
		
		CohortDataSetDefinition dsd = new CohortDataSetDefinition();
		dsd.addParameter(new Parameter("d1", "Start Date", Date.class, null, true));
		dsd.addParameter(new Parameter("d2", "End Date", Date.class, null, true));
		dsd.addStrategy("Children at Start", new Mapped<CohortDefinition>(childOnDate, "effectiveDate=${d1}"));
		dsd.addStrategy("Children at End", new Mapped<CohortDefinition>(childOnDate, "effectiveDate=${d2}"));
		
		ReportSchema report = new ReportSchema();
		report.addParameter(new Parameter("report.startDate", "Report Start Date", Date.class, null, true));
		report.addParameter(new Parameter("report.endDate", "Report End Date", Date.class, null, true));
		report.addDataSetDefinition(new Mapped<DataSetDefinition>(dsd, "d1=${report.startDate},d2=${report.endDate}"));
		
		EvaluationContext ec = new EvaluationContext();
		ec.addParameterValue("report.startDate", ymd.parse("1980-01-01"));
		ec.addParameterValue("report.endDate", ymd.parse("2008-01-01"));
		
		ReportService rs = (ReportService) Context.getService(ReportService.class);
		ReportData data = rs.evaluate(report, ec);

		CsvReportRenderer renderer = new CsvReportRenderer();
		renderer.render(data, null, System.out);
	}
}
