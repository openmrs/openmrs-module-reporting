package org.openmrs.module.reporting.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class SqlCohortDefinitionValidatorTest  extends BaseModuleContextSensitiveTest {

	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if query is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfQuerytIsNull() throws Exception {
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
		sqlCohortDefinition.setQuery(null);
		
		Errors errors = new BindException(sqlCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(sqlCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if query is empty string", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfLogicIsEmptyString() throws Exception {
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
		sqlCohortDefinition.setQuery(" ");
		
		Errors errors = new BindException(sqlCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(sqlCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
		sqlCohortDefinition.setName("Test CD");
		sqlCohortDefinition.setQuery("Some Query");
		
		Errors errors = new BindException(sqlCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(sqlCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
