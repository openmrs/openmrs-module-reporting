package org.openmrs.module.reporting.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class StaticCohortDefinitionValidatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if cohort is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfCohortIsNull() throws Exception {
		StaticCohortDefinition staticCohortDefinition = new StaticCohortDefinition();
		staticCohortDefinition.setCohort(null);
		
		Errors errors = new BindException(staticCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(staticCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
}
