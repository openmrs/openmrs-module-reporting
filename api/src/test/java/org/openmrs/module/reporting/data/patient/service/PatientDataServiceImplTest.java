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
package org.openmrs.module.reporting.data.patient.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Collection;
import java.util.List;

/**
 * Test the PatientDataServiceImpl
 */
public class PatientDataServiceImplTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see PatientDataServiceImpl#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @verifies evaluate a patient query
	 */
	@Test
	public void evaluate_shouldEvaluateAnPatientData() throws Exception {
		PatientDataDefinition definition = new PatientIdDataDefinition();
		PatientData data = Context.getService(PatientDataService.class).evaluate(definition, new EvaluationContext());
		Assert.assertNotNull(data);
	}
	
	/**
	 * @see PatientDataServiceImpl#saveDefinition(org.openmrs.module.reporting.evaluation.Definition)
	 * @verifies save a patient query
	 */
	@Test
	public void saveDefinition_shouldSaveAnPatientData() throws Exception {
		PatientDataDefinition definition = new PatientIdDataDefinition();
		definition.setName("All Patient Ids");
		definition = Context.getService(PatientDataService.class).saveDefinition(definition);
		Assert.assertNotNull(definition.getId());
		Assert.assertNotNull(definition.getUuid());
		PatientDataDefinition loadedDefinition = Context.getService(PatientDataService.class).getDefinitionByUuid(definition.getUuid());
		Assert.assertEquals(definition, loadedDefinition);
	}

	/**
	 * @see PatientDataServiceImpl#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @verifies evaluate a patient query
	 */
	@Test
	public void evaluate_shouldPerformABatchedEvaluation() throws Exception {
		TestUtil.updateGlobalProperty("reporting.dataEvaluationBatchSize", "1");
		PatientDataDefinition definition = new PatientIdDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));

		PatientData data = Context.getService(PatientDataService.class).evaluate(definition, context);
		TestUtil.assertCollectionsEqual(context.getBaseCohort().getMemberIds(), data.getData().values());
	}
}