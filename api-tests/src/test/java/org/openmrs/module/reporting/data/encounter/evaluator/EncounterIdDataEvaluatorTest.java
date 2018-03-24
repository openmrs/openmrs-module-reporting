/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
		Assert.assertEquals(10, ed.getData().size());
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