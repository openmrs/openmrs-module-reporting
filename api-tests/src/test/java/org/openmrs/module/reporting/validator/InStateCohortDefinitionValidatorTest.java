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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class InStateCohortDefinitionValidatorTest  extends BaseModuleContextSensitiveTest {

	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if states is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfBaseDefinitionIsNull() throws Exception {
		InStateCohortDefinition inStateCohortDefinition = new InStateCohortDefinition();
		inStateCohortDefinition.setStates(null);
		
		Errors errors = new BindException(inStateCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inStateCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if states is empty", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStateIsEmpty() throws Exception {
		InStateCohortDefinition inStateCohortDefinition = new InStateCohortDefinition();
		inStateCohortDefinition.setStates(new ArrayList<ProgramWorkflowState>());
		
		Errors errors = new BindException(inStateCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inStateCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		ProgramWorkflowState programWorkflowState = new ProgramWorkflowState();
		programWorkflowState.setName("Name");
		programWorkflowState.setConcept(Context.getConceptService().getConcept(10));
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states.add(programWorkflowState);
		
		InStateCohortDefinition inStateCohortDefinition = new InStateCohortDefinition();
		inStateCohortDefinition.setName("Name");
		inStateCohortDefinition.setStates(states);
		
		Errors errors = new BindException(inStateCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inStateCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
