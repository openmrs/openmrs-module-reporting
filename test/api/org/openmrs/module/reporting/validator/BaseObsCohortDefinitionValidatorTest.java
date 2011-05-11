package org.openmrs.module.reporting.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.TextObsCohortDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class BaseObsCohortDefinitionValidatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if time modifier is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfTimeModifierIsNullForCodedObsCohortDefinition() throws Exception {
		CodedObsCohortDefinition codedObsCohortDefinition = new CodedObsCohortDefinition();
		codedObsCohortDefinition.setTimeModifier(null);
		codedObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(codedObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(codedObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if question is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfQuestionIsNullForCodedObsCohortDefinition() throws Exception {
		CodedObsCohortDefinition codedObsCohortDefinition = new CodedObsCohortDefinition();
		codedObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		codedObsCohortDefinition.setQuestion(null);
		
		Errors errors = new BindException(codedObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(codedObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrectForCodedObsCohortDefinition() throws Exception {
		CodedObsCohortDefinition codedObsCohortDefinition = new CodedObsCohortDefinition();
		codedObsCohortDefinition.setName("Test CD");
		codedObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		codedObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(codedObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(codedObsCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if time modifier is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfTimeModifierIsNullForDateObsCohortDefinition() throws Exception {
		DateObsCohortDefinition dateObsCohortDefinition = new DateObsCohortDefinition();
		dateObsCohortDefinition.setTimeModifier(null);
		dateObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(dateObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(dateObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if question is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfQuestionIsNullForDateObsCohortDefinition() throws Exception {
		DateObsCohortDefinition dateObsCohortDefinition = new DateObsCohortDefinition();
		dateObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		dateObsCohortDefinition.setQuestion(null);
		
		Errors errors = new BindException(dateObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(dateObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrectForDateObsCohortDefinition() throws Exception {
		DateObsCohortDefinition dateObsCohortDefinition = new DateObsCohortDefinition();
		dateObsCohortDefinition.setName("Test CD");
		dateObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		dateObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(dateObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(dateObsCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if time modifier is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfTimeModifierIsNullForNumericObsCohortDefinition() throws Exception {
		NumericObsCohortDefinition numericObsCohortDefinition = new NumericObsCohortDefinition();
		numericObsCohortDefinition.setTimeModifier(null);
		numericObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(numericObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(numericObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if question is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfQuestionIsNullForNumericObsCohortDefinition() throws Exception {
		NumericObsCohortDefinition numericObsCohortDefinition = new NumericObsCohortDefinition();
		numericObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		numericObsCohortDefinition.setQuestion(null);
		
		Errors errors = new BindException(numericObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(numericObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrectForNumericObsCohortDefinition() throws Exception {
		NumericObsCohortDefinition numericObsCohortDefinition = new NumericObsCohortDefinition();
		numericObsCohortDefinition.setName("Test CD");
		numericObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		numericObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(numericObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(numericObsCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if time modifier is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfTimeModifierIsNullForTextObsCohortDefinition() throws Exception {
		TextObsCohortDefinition textObsCohortDefinition = new TextObsCohortDefinition();
		textObsCohortDefinition.setTimeModifier(null);
		textObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(textObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(textObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if question is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfQuestionIsNullForTextObsCohortDefinition() throws Exception {
		TextObsCohortDefinition textObsCohortDefinition = new TextObsCohortDefinition();
		textObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		textObsCohortDefinition.setQuestion(null);
		
		Errors errors = new BindException(textObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(textObsCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrectForTextObsCohortDefinition() throws Exception {
		TextObsCohortDefinition textObsCohortDefinition = new TextObsCohortDefinition();
		textObsCohortDefinition.setName("Test CD");
		textObsCohortDefinition.setTimeModifier(TimeModifier.ANY);
		textObsCohortDefinition.setQuestion(new Concept(10));
		
		Errors errors = new BindException(textObsCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(textObsCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
