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
package org.openmrs.module.reporting.cohort;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * This tests the evaluation of a PatientCharacteristicCohortDefinition
 */
public class CohortDefinitionEvaluatorTest extends BaseContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionEvaluatorTest#evaluate(CohortDefinition, EvaluationContext)}
	 */
	@Test
	public void getHandlerForType_shouldReturnAnEmptyListIfNoClassesCanHandleThePassedType() throws Exception {
		GenderCohortDefinition d = new GenderCohortDefinition();
		d.setGender("M");
		Cohort males = Context.getService(CohortDefinitionService.class).evaluate(d, new EvaluationContext());
		System.out.println("Males: " + males.getSize());
		d.setGender("F");
		Cohort females = Context.getService(CohortDefinitionService.class).evaluate(d, new EvaluationContext());
		System.out.println("Females: " + females.getSize());
	}
}
