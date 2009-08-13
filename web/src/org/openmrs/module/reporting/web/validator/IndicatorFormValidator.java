package org.openmrs.module.reporting.web.validator;

import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * 
 */
public class IndicatorFormValidator implements Validator {

	public boolean supports(Class clazz) {
		return IndicatorForm.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		validateIndicatorForm((IndicatorForm) obj, errors);
	}
	
	public void validateIndicatorForm(IndicatorForm indicatorForm, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.name", "NAME_REQUIRED", "name required");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.description", "DESCRIPTION_REQUIRED", "description required");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.cohortDefinition", "COHORT_DEFINITION_REQUIRED", "cohort definition required");		
	}

	public void validateCohortIndicator(IndicatorForm indicatorForm, Errors errors) { 
		// not sure what to validate yet		
	}
	
	public void validateParameterMapping(IndicatorForm indicatorForm, Errors errors) { 
		// not sure what to validate yet
	}
	
	
}
