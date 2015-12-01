package org.openmrs.module.reporting.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class InverseCohortDefinitionValidatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if baseDefinition is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfBaseDefinitionIsNull() throws Exception {
		InverseCohortDefinition inverseCohortDefinition = new InverseCohortDefinition();
		inverseCohortDefinition.setBaseDefinition(null);
		
		Errors errors = new BindException(inverseCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inverseCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		InverseCohortDefinition inverseCohortDefinition = new InverseCohortDefinition();
		inverseCohortDefinition.setName("Test CD");
		inverseCohortDefinition.setBaseDefinition(new SqlCohortDefinition());
		
		Errors errors = new BindException(inverseCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(inverseCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
