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
package org.openmrs.module.reporting.query.encounter.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the EncounterQueryServiceImpl
 */
public class EncounterQueryServiceImplTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see EncounterQueryServiceImpl#evaluate(EncounterQuery,EvaluationContext)
	 * @verifies evaluate an encounter query
	 */
	@Test
	public void evaluate_shouldEvaluateAnEncounterQuery() throws Exception {
		EncounterQuery q = new SqlEncounterQuery("select encounter_id from encounter where voided = 0");
		EncounterQueryResult r = Context.getService(EncounterQueryService.class).evaluate(q, new EvaluationContext());
		Assert.assertNotNull(r);
	}
	
	/**
	 * @see EncounterQueryServiceImpl#saveDefinition(EncounterQuery)
	 * @verifies save an encounter query
	 */
	@Test
	public void saveDefinition_shouldSaveAnEncounterQuery() throws Exception {
		EncounterQuery q = new SqlEncounterQuery("select encounter_id from encounter where voided = 0");
		q.setName("Non voided encounters");
		q = Context.getService(EncounterQueryService.class).saveDefinition(q);
		Assert.assertNotNull(q.getId());
		Assert.assertNotNull(q.getUuid());
		EncounterQuery loadedQuery = Context.getService(EncounterQueryService.class).getDefinitionByUuid(q.getUuid());
		Assert.assertEquals(q, loadedQuery);
	}
	
}