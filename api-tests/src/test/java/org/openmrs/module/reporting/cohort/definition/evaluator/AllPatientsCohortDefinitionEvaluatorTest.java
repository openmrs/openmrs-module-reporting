/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
