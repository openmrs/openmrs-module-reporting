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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingTestUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.ExpectedException;

/**
 * Test the evaluation of the PatientDataSetDefinition
 */
public class PatientDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(PatientDataSetEvaluatorTest.class);
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}

	@Test
	public void evaluate_shouldExportPersonData() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumn("Sexe", new GenderDataDefinition(), null, null);
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
	}
	
	@Test
	public void evaluate_shouldExportPatientData() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumn("EMR ID", new PatientIdDataDefinition(), null, null);
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals(2, dataset.getColumnValue(2, "EMR ID"));
	}
	
	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void evaluate_shouldFailToExportEncounterData() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumn("Encounter Date", new EncounterDatetimeDataDefinition(), null, null);
	}
	
	@Test
	public void evaluate_shouldExportConvertedData() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumn("birthdate", new BirthdateDataDefinition(), null, new DateConverter("dd/MMM/yyyy"));
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals("08/Apr/1975", dataset.getColumnValue(2, "birthdate"));
	}
	
	@Test
	public void evaluate_shouldExportParameterizedData() throws Exception {
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumn("EMR ID", new PatientIdDataDefinition(), null, null);
		
		AgeDataDefinition ageOnDate = new AgeDataDefinition();
		ageOnDate.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		
		d.addColumn("Age At Start", ageOnDate, "effectiveDate=${startDate}", new AgeConverter());
		d.addColumn("Age At End", ageOnDate, "effectiveDate=${endDate}", new AgeConverter());
		
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals(35, dataset.getColumnValue(2, "Age At Start"));
		Assert.assertEquals(36, dataset.getColumnValue(2, "Age At End"));
		ReportingTestUtils.printDataSetToConsole(dataset);
	}
	
	@Test
	public void evaluate_shouldEvaluateAgainstALimitedPatientSet() throws Exception {
		
		PatientDataSetDefinition d = new PatientDataSetDefinition();
		d.addColumn("Sexe", new GenderDataDefinition(), null, null);
		
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
	
	//***** UTILITY METHODS *****
	
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.getDateTime(2010, 7, 1));
		context.addParameterValue("endDate", DateUtil.getDateTime(2011, 6, 30));
		return context;
	}
}