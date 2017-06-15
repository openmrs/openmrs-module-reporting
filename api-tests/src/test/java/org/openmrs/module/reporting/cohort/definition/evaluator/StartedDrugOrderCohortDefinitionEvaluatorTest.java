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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.StartedDrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class StartedDrugOrderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	// @Test
	// @Verifies(value = "should return all patients taking any drugs (active or inactive)", method = "evaluate(CohortDefinition,EvaluationContext)")
	// public void evaluate_shouldReturnAllPatientsTakingAnyDrugsActiveOrInactive() throws Exception {
	// 	StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
	// 	cd.setOnlyCurrentlyActive(Boolean.FALSE);
	// 	Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
	// 	Assert.assertEquals(3, c.size());
	// 	Assert.assertTrue(c.contains(7));
	// 	Assert.assertTrue(c.contains(2));
	// 	Assert.assertTrue(c.contains(20));
	// }

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return all patients drugs active on or before a specific time", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingDrugsActiveOnOrBeforeDate() throws Exception {
		StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
		cd.setActiveOnOrBefore(DateUtil.getDateTime(2008, 01, 2));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		System.out.println("onOrBefore Size - " + c.getMemberIds());
		Assert.assertTrue(c.contains(7));
		Assert.assertEquals(1, c.size());
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return all patients drugs active on or after a specific time", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingDrugsActiveOnOrAfterDate() throws Exception {
		StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
		cd.setActiveOnOrAfter(DateUtil.getDateTime(2010, 10, 1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		System.out.println("On or after size - " + c.size());
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(20));
		Assert.assertTrue(c.contains(21));
		Assert.assertTrue(c.contains(999));
	}

}