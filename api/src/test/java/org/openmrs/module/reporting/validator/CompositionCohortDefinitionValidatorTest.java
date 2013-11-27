package org.openmrs.module.reporting.validator;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link CohortDefinitionValidator} class.
 */
public class CompositionCohortDefinitionValidatorTest  extends BaseModuleContextSensitiveTest {

	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if searches is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfSearchesIsNull() throws Exception {
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setSearches(null);
		compositionCohortDefinition.setCompositionString("Some Composition String");
		
		Errors errors = new BindException(compositionCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(compositionCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if searches is empty", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfSearchesIsEmpty() throws Exception {
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setSearches(new HashMap<String, Mapped<CohortDefinition>>());
		compositionCohortDefinition.setCompositionString("Some Composition String");
		
		Errors errors = new BindException(compositionCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(compositionCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if composition string is null", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfCompositionStringIsNull() throws Exception {
		HashMap<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("Some Key", new Mapped());
		
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString(null);
		
		Errors errors = new BindException(compositionCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(compositionCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if composition string is empty", method = "validate(Object,Errors)")
	public void validate_shouldFailValidationIfCompositionStringIsEmpty() throws Exception {
		HashMap<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("Some Key", new Mapped());
		
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString(" ");
		
		Errors errors = new BindException(compositionCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(compositionCohortDefinition, errors);
		
		Assert.assertTrue(errors.hasErrors());
	}
	
	/**
	 * @see {@link CohortDefinitionValidator#validate(Object,Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object,Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		HashMap<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("Some Key", new Mapped<CohortDefinition>());
		
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("Test CD");
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString("Some Composition String");
		
		Errors errors = new BindException(compositionCohortDefinition, "cohortDefinition");
		new CohortDefinitionValidator().validate(compositionCohortDefinition, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
