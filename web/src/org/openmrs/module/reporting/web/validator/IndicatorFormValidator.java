package org.openmrs.module.reporting.web.validator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Parameter;
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

	
	private Log log = LogFactory.getLog(this.getClass());
	
	public boolean supports(Class type) {
		return IndicatorForm.class.isAssignableFrom(type);
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
	 * Should validate whether all parameter mappings have been specified.
	 * 
	 * @param indicatorForm
	 * @param errors
	 * 
	 */
	public void validateParameterMapping(IndicatorForm indicatorForm, Errors errors) {
		// errors, field, errorKey, defaultMessage
		if (indicatorForm != null) { 
			
			List<Parameter> parameters = 
				indicatorForm.getCohortDefinition().getParameters();
			for(Parameter parameter : parameters) {
				
				String value = indicatorForm.getParameterMapping().get(parameter.getName());
				log.info("value = '" + value + "'");
				if (value == null || value.equals("")) { 
					ValidationUtils.rejectIfEmpty(errors, "parameterMapping", "parameterMapping.required", "Must map each parameter");
				}				
			}			
		}
	}
	
	
	/**
	 * Should validate all data submitted in step 1.
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateStep1(IndicatorForm indicatorForm, Errors errors) {
		// errors, field, errorKey, defaultMessage
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.name", "cohortIndicator.name.required", "Must specify indicator name");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.description", "cohortIndicator.description.required", "Must specify description");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.cohortDefinition", "cohortIndicator.cohortDefinition.required", "Must specify cohort definition");		
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
