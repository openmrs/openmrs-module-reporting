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

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * This tests the evaluation of an AllPatientsCohortDefinition
 */
public class AllPatientsCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void evaluate_shouldReturnAllNonVoidedPatientsOptionallyLimitedToThoseInThePassedContext() throws Exception {
		
		AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
		EvaluationContext context = new EvaluationContext();
		
		// Should return all 9 non-voided patients without a base cohort defined
		EvaluatedCohort allPats = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		Assert.assertEquals(9, allPats.size());
		
		// Should return all patients in the base cohort if it is defined
		context.setBaseCohort(new Cohort("2,7,20"));
		allPats = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		Assert.assertEquals(3, allPats.size());
	}
}
