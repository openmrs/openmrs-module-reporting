package org.openmrs.module.reporting.web.validator;

import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * TODO Rename validate methods to specify what data is being validated. 
 * 	
 * Step 1 - validateIndicatorType()
 * Step 2 - validateCohortDefinition()
 * Step 3 - validateParameterMapping()
 * Step 4 - validateCohortIndicator() or validate()
 *
 * 
 */
public class IndicatorFormValidator implements Validator {

	public boolean supports(Class clazz) {
		return IndicatorForm.class.isAssignableFrom(clazz);
	}

	/**
	 * Should validate all data in the form.
	 */
	public void validate(Object obj, Errors errors) {
		validateStep1((IndicatorForm) obj, errors);
		validateStep2((IndicatorForm) obj, errors);
		validateStep3((IndicatorForm) obj, errors);
	}

	/**
	 * Should validate all data submitted in step 1.
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateStep1(IndicatorForm indicatorForm, Errors errors) {
		// errors, field, errorKey, defaultMessage
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.name", "cohortIndicator.name.required", "Name required");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.description", "cohortIndicator.description.required", "Description required");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.cohortDefinition", "cohortIndicator.cohortDefinition.required", "Cohort definition required");		
	}

	/**
	 * Should validate all data submitted in step 2.
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateStep2(IndicatorForm indicatorForm, Errors errors) { 
		// TODO Add validation logic
	}
	
	/**
	 * Should validate all data submitted in step 3.
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateStep3(IndicatorForm indicatorForm, Errors errors) { 
		// TODO Add validation logic
	}
	
	
}
