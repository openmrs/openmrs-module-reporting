/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.LogicCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class LogicCohortDefinitionValidatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if logic is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfLogicIsNull() throws Exception {
		LogicCohortDefinition logicCohortDefinition = new LogicCohortDefinition();
		logicCohortDefinition.setLogic(null);
		
		Errors errors = new BindException(logicCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(logicCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if logic is empty string", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfLogicIsEmptyString() throws Exception {
		LogicCohortDefinition logicCohortDefinition = new LogicCohortDefinition();
		logicCohortDefinition.setLogic(" ");
		
		Errors errors = new BindException(logicCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(logicCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		LogicCohortDefinition logicCohortDefinition = new LogicCohortDefinition();
		logicCohortDefinition.setName("Test CD");
		logicCohortDefinition.setLogic("Some Value");
		
		Errors errors = new BindException(logicCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(logicCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
