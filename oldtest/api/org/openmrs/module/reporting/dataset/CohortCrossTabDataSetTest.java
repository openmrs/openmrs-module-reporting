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
package org.openmrs.module.reporting.dataset;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.cohort.definition.util.CohortExpressionParser;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 *
 */
public class CohortCrossTabDataSetTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void register() {
		SerializedObjectDAO dao = Context.getRegisteredComponents(SerializedObjectDAO.class).iterator().next();
		dao.registerSupportedType(CohortDefinition.class);
		dao.registerSupportedType(DataSetDefinition.class);
	}
	
	/**
	 * TODO Add javadoc What the heck is this for?
	 * @param text
	 * @return
	 */
	public CohortDefinition getStrategy(String text) {
		return CohortExpressionParser.parse(text);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldTest() throws Exception {
		
		CohortDefinitionService cohortDefinitionService = Context.getService(CohortDefinitionService.class);
		ReportDefinitionService reportService = Context.getService(ReportDefinitionService.class);
		
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/report/include/ReportTests-patients.xml");
		authenticate();
		
		ReportDefinition schema = new ReportDefinition();
		schema.setName("Test Report for Table");
		schema.setDescription("A test description");
		
		Parameter dateParam = new Parameter("report.startDate", "Date of report", Date.class, null, new Date());
		schema.addParameter(dateParam);
		
		GenderCohortDefinition maleDef = new GenderCohortDefinition();
		maleDef.setName("Male");
		maleDef.setMaleIncluded(true);
		cohortDefinitionService.saveDefinition(maleDef);
		
		GenderCohortDefinition femaleDef = new GenderCohortDefinition();
		femaleDef.setName("Female");
		femaleDef.setFemaleIncluded(true);
		cohortDefinitionService.saveDefinition(femaleDef);
		
		Parameter effDateParam = new Parameter("effectiveDate", "Effective Date", Date.class);
		
		AgeCohortDefinition adultOnDate = new AgeCohortDefinition();
		adultOnDate.setName("AdultOnDate");
		adultOnDate.setMinAge(15);
		adultOnDate.addParameter(effDateParam);
		cohortDefinitionService.saveDefinition(adultOnDate);
		
		AgeCohortDefinition childOnDate = new AgeCohortDefinition();
		childOnDate.setName("ChildOnDate");
		childOnDate.setMaxAge(14);
		childOnDate.addParameter(effDateParam);
		cohortDefinitionService.saveDefinition(childOnDate);

		CohortDataSetDefinition genderDef = new CohortDataSetDefinition();
		genderDef.setName("gender");
		genderDef.addDefinition("male", "male", maleDef, null);
		genderDef.addDefinition("female", "female", femaleDef, null);
		
		CohortDataSetDefinition ageDef = new CohortDataSetDefinition();
		ageDef.setName("age");
		ageDef.addDefinition("adult", "adult", adultOnDate, null);
		ageDef.addDefinition("child", "adult", childOnDate, null);
		
		CohortCrossTabDataSetDefinition def = new CohortCrossTabDataSetDefinition();
		def.setName("test");
		def.setRowCohortDataSetDefinition(ageDef, null);
		def.setColumnCohortDataSetDefinition(genderDef, null);
		schema.addDataSetDefinition("test", def, null);
		
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.addParameterValue(dateParam.getName(), new Date());
		
		ReportData data = reportService.evaluate(schema, evalContext);
		System.out.println("Result=");

		new CsvReportRenderer().render(data, null, System.out);
	}
	
}
