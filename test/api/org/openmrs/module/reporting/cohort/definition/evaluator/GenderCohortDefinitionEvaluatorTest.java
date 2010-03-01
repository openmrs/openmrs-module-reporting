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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.GenderCohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.util.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * This class tests the evaluation of an GenderCohortDefinition
 * 
 * TODO Cleanup by generating tests cases from the @shoulds with the OpenMRS module plugin.
 */
public class GenderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	public final Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeAllTests() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/include/ReportTestDataset.xml");
		authenticate();	
	}	
	
	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return non voided patients", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnNonVoidedPatients() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender(null);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		Assert.assertEquals("Should contain 9 patients", 9, cohort.getSize());
		Assert.assertTrue("Should not include patient 999 whose record has been voided", !cohort.contains(999));
	}

	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return all patients when gender is null", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWhenGenderIsNull() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender(null);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		Assert.assertEquals("Should include 9 patient who has a null gender", 9, cohort.getSize());
	}

	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return male patients when gender equals male", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnMalePatientsWhenGenderEqualsMale() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender("M");		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		log.warn("Cohort: " + cohort);
		Assert.assertEquals("Should include 4 patients who are male", 4, cohort.getSize());
	}

	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return female patients when gender equals female", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnFemalePatientsWhenGenderEqualsFemale() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender("F");		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		log.warn("Cohort: " + cohort);
		Assert.assertEquals("Should include 5 patients who are female", 5, cohort.getSize());
	}
	
	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return all patients when gender equals empty string", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWhenGenderEqualsEmptyString() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender("");		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);		
		log.warn("Cohort: " + cohort);
		Assert.assertEquals("Should return 2 patient whose gender equals empty string", 2, cohort.getSize());
	}
	
	/**
	 * @see GenderCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 */
	@Test
	@Verifies(value = "should return patients with an unknown gender when gender equals unknown", method = "evaluate(CohortDefinition, EvaluationContext)")
	public void evaluate_shouldReturnPatientsWithUnknownGenderWhenGenderEqualsUnknown() throws Exception {
		GenderCohortDefinition genderCohortDefinition = new GenderCohortDefinition();
		genderCohortDefinition.setGender("unknown");		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(genderCohortDefinition, null);
		log.warn("Cohort: " + cohort);
		Assert.assertEquals("Should return 3 patients whose gender is unknown", 2, cohort.getSize());		
	}	

}
