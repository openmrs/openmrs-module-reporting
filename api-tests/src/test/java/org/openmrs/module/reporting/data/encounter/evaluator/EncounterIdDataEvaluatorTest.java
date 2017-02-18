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
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EncounterIdDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see EncounterIdDataEvaluator#evaluate(EncounterDataDefinition,EvaluationContext)
	 * @verifies return encounterIds for the patients given an EvaluationContext
	 */
	@Test
	public void evaluate_shouldReturnEncounterIdsForThePatientsGivenAnEvaluationContext() throws Exception {
		EncounterIdDataDefinition d = new EncounterIdDataDefinition();
		EvaluationContext context = new EvaluationContext();
		EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);
		Assert.assertEquals(12, ed.getData().size());
		for (Integer eId : ed.getData().keySet()) {
			Assert.assertEquals(eId, ed.getData().get(eId));
		}
		
		// Test for a limited base cohort of patients
		context.setBaseCohort(new Cohort("7,20"));
		ed = Context.getService(EncounterDataService.class).evaluate(d, context);
		Assert.assertEquals(4, ed.getData().size());
	}

	/**
	 * @see EncounterIdDataEvaluator#evaluate(EncounterDataDefinition,EvaluationContext)
	 * @verifies return encounterIds for the encounters given an EncounterEvaluationContext
	 */
	@Test
	public void evaluate_shouldReturnEncounterIdsForTheEncountersGivenAnEncounterEvaluationContext() throws Exception {
		EncounterIdDataDefinition d = new EncounterIdDataDefinition();
		EncounterEvaluationContext context = new EncounterEvaluationContext();
		context.setBaseCohort(new Cohort("7,20"));
		context.setBaseEncounters(new EncounterIdSet(3,4,6));
		EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);
		Assert.assertEquals(3, ed.getData().size());
	}
}