package org.openmrs.module.reporting.validator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class InProgramCohortDefinitionValidatorTest  extends BaseModuleContextSensitiveTest {

	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if programs is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfProgramsIsNull() throws Exception {
		InProgramCohortDefinition inProgramCohortDefinition = new InProgramCohortDefinition();
		inProgramCohortDefinition.setPrograms(null);
		
		Errors errors = new BindException(inProgramCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inProgramCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if programs is empty", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfProgramsIsEmpty() throws Exception {
		InProgramCohortDefinition inProgramCohortDefinition = new InProgramCohortDefinition();
		inProgramCohortDefinition.setPrograms(new ArrayList<Program>());
		
		Errors errors = new BindException(inProgramCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inProgramCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		List<Program> programs = new ArrayList<Program>();
		programs.add(new Program());
		
		InProgramCohortDefinition inProgramCohortDefinition = new InProgramCohortDefinition();
		inProgramCohortDefinition.setName("Test CD");
		inProgramCohortDefinition.setPrograms(programs);
		
		Errors errors = new BindException(inProgramCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inProgramCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
