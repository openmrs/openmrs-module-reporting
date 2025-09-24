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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * This class tests the evaluation of an GenderCohortDefinition
 */
public class GenderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	public final Log log = LogFactory.getLog(this.getClass());
	
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
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return all non voided patients when all are included", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnAllNonVoidedPatientsWhenAllAreIncluded() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setMaleIncluded(true);
		genderCohortDefinition.setFemaleIncluded(true);
		genderCohortDefinition.setUnknownGenderIncluded(true);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		Assert.assertEquals(9, cohort.getSize());
		Assert.assertTrue("Should not include patient 999 whose record has been voided", !cohort.contains(999));
	}

	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return male patients when males are included", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnMalePatientsWhenMalesAreIncluded() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setMaleIncluded(true);		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		log.warn("Cohort: " + cohort);
		Assert.assertEquals(3, cohort.getSize());
	}
	
	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return female patients when females are included", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnFemalePatientsWhenFemalesAreIncluded() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setFemaleIncluded(true);	
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		log.warn("Cohort: " + cohort);
		Assert.assertEquals(5, cohort.getSize());
	}

	@Test
	@Verifies(value = "should return patients with unknown gender when unknown are included", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnPatientsWithUnknownGenderWhenUnknownAreIncluded() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setUnknownGenderIncluded(true);	
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		log.warn("Cohort: " + cohort);
		Assert.assertEquals(1, cohort.getSize());
	}	
	
	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "@should return no patients when none are included", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnNoPatientsWhenNoneAreIncluded() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);		
		log.warn("Cohort: " + cohort);
		Assert.assertEquals(0, cohort.getSize());
	}
}
