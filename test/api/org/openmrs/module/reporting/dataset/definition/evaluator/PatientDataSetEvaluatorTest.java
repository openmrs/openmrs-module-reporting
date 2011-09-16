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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the PatientDataSetDefinition
 */
public class PatientDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(PatientDataSetEvaluatorTest.class);
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/*
	@Test
	public void evaluate_shouldExportASimpleProperty() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumnDefinition(new PersonDataColumnDefinition("Sexe", new GenderDataDefinition()));
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
	}
	
	@Test
	public void evaluate_shouldExportAConvertedProperty() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumnDefinition(new BirthdateColumnDefinition("birthdate", new DateConverter("dd/MMM/yyyy")));
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals("08/Apr/1975", dataset.getColumnValue(2, "birthdate"));
	}
	
	@Test
	public void evaluate_shouldExportAnInternalConvertedProperty() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumnDefinition(new PatientIdColumnDefinition("EMR ID"));
		
		AgeColumnDefinition ageAtStart = new AgeColumnDefinition("Age At Start", new AgeConverter());
		ageAtStart.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		d.addColumnDefinition(ageAtStart, "effectiveDate=${startDate}");

		AgeColumnDefinition ageAtEnd = new AgeColumnDefinition("Age At End", new AgeConverter());
		ageAtEnd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		d.addColumnDefinition(ageAtEnd, "effectiveDate=${endDate}");
		
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals(35, dataset.getColumnValue(2, "Age At Start"));
		Assert.assertEquals(36, dataset.getColumnValue(2, "Age At End"));
		ReportingTestUtils.printDataSetToConsole(dataset);
	}
	
		@Test
	public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {
		
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumnDefinition(new PersonIdColumnDefinition("Person ID"));
		d.addColumnDefinition(new GenderColumnDefinition("Sexe"));
		d.addColumnDefinition(new AgeColumnDefinition("Age"));
		
		EvaluationContext context = new EvaluationContext();
		
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assert.assertEquals("F", dataset.getColumnValue(7, "Sexe"));
		Assert.assertNull(dataset.getColumnValue(501, "Sexe"));
		Assert.assertEquals(9, dataset.getRows().size());
		
		Cohort c = new Cohort("2,6,8");
		context.setBaseCohort(c);
		
		dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assert.assertEquals(3, dataset.getRows().size());
	}
	*/
	
	//***** UTILITY METHODS *****
	
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.getDateTime(2010, 7, 1));
		context.addParameterValue("endDate", DateUtil.getDateTime(2011, 6, 30));
		return context;
	}
}