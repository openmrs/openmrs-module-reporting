package org.openmrs.module.reporting.validator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class PatientStateCohortDefinitionValidatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if states is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStatesIsNull() throws Exception {
		PatientStateCohortDefinition patientStateCohortDefinition = new PatientStateCohortDefinition();
		patientStateCohortDefinition.setStates(null);
		
		Errors errors = new BindException(patientStateCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(patientStateCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if states is empty", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfStatesIsEmpty() throws Exception {
		PatientStateCohortDefinition patientStateCohortDefinition = new PatientStateCohortDefinition();
		patientStateCohortDefinition.setStates(new ArrayList<ProgramWorkflowState>());
		
		Errors errors = new BindException(patientStateCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(patientStateCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		ProgramWorkflowState programWorkflowState = new ProgramWorkflowState();
		programWorkflowState.setName("Name");
		programWorkflowState.setConcept(Context.getConceptService().getConcept(10));
		states.add(programWorkflowState);
		
		PatientStateCohortDefinition patientStateCohortDefinition = new PatientStateCohortDefinition();
		patientStateCohortDefinition.setName("Test CD");
		patientStateCohortDefinition.setStates(states);
		
		Errors errors = new BindException(patientStateCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(patientStateCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
