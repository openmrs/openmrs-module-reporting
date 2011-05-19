package org.openmrs.module.reporting.web.validator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
		
		IndicatorForm indicatorForm = (IndicatorForm) obj;
		validateCohortIndicator(indicatorForm, errors);
		
		if ("COUNT".equals(indicatorForm.getIndicatorType())) {
			validateCountIndicator(indicatorForm, errors);
		}
		else if ("FRACTION".equals(indicatorForm.getIndicatorType())) { 
			validateFractionIndicator(indicatorForm, errors);
		}
		else if ("LOGIC".equals(indicatorForm.getIndicatorType())) { 
			//validateLogicIndicator(indicatorForm, errors);
		}
		else { 
			// unknown indicator type
			errors.rejectValue("indicatorType", "cohortIndicator.errors.unknownIndicatorType", 
					new Object[] { indicatorForm.getIndicatorType() }, "Unknown indicator type");
		}

	}

	/**
	 * Should validate all data submitted in step 1.
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateCohortIndicator(IndicatorForm indicatorForm, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.name", "cohortIndicator.errors.name.required", "Must specify indicator name");
		ValidationUtils.rejectIfEmpty(errors, "indicatorType", "cohortIndicator.errors.type.required", "Must specify indicator calculation type");
		ValidationUtils.rejectIfEmpty(errors, "cohortIndicator.description", "cohortIndicator.errors.description.required", "Must specify description");
	}
	
	/**
	 * 
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateLocationFilter(IndicatorForm indicatorForm, Errors errors) { 		
		List<Parameter> parameters = indicatorForm.getLocationFilter().getParameters();			
		for(Parameter parameter : parameters) {				
			Object value = indicatorForm.getLocationFilterParameterMapping().get(parameter.getName());
			if (value == null || value.equals("")) { 
				errors.rejectValue("locationFilterParameterMapping", "cohortIndicator.errors.parameterMapping.required", 
						new Object[] { value }, "Must map each parameter on the location filter");					
			}				
		}			

	}
	
	
	/**
	 * Should validate whether all parameter mappings have been specified.
	 * 
	 * @param indicatorForm
	 * @param errors
	 * 
	 */
	public void validateCountIndicator(IndicatorForm indicatorForm, Errors errors) {
		if (indicatorForm != null) { 			
			validateCohortIndicator(indicatorForm, errors);
			
			if (indicatorForm.getCohortDefinition() == null) { 
				// errors, field, errorKey, defaultMessage
				ValidationUtils.rejectIfEmpty(errors, "cohortDefinition", "cohortDefinition.required", "Must specify a cohort definition for simple indiator");
			}
			List<Parameter> parameters = indicatorForm.getCohortDefinition().getParameters();			
			for(Parameter parameter : parameters) {				
				Object value = indicatorForm.getParameterMapping().get(parameter.getName());
				log.debug("value = '" + value + "'");
				if (value == null || value.equals("")) { 					
					errors.rejectValue("denominatorParameterMapping", "cohortIndicator.errors.parameterMapping.required", 
							new Object[] { value }, "Must map each parameter on the count indicator");					
				}				
			}			
		}
	}
	
	
	
	/**
	 * 
	 * @param indicatorForm
	 * @param errors
	 */
	public void validateFractionIndicator(IndicatorForm indicatorForm, Errors errors) {
		
		if (indicatorForm.getNumerator() == null) { 
			ValidationUtils.rejectIfEmpty(errors, "numerator", "cohortDefinition.required", "Must specify a numerator for fractional indicator");
		}
		if (indicatorForm.getDenominator() == null) { 
			ValidationUtils.rejectIfEmpty(errors, "denominator", "cohortDefinition.required", "Must specify a denominator for fractional indicator");
		}
		
		List<Parameter> parameters = indicatorForm.getNumerator().getParameters();			
		for(Parameter parameter : parameters) {				
			Object value = indicatorForm.getNumeratorParameterMapping().get(parameter.getName());
			log.debug("value = '" + value + "'");
			if (value == null || value.equals("")) { 
				errors.rejectValue("numeratorParameterMapping", "cohortIndicator.errors.parameterMappingRequired", 
						new Object[] { value }, "Must map each parameter on the numerator");
			}				
		}			
		parameters = indicatorForm.getDenominator().getParameters();			
		for(Parameter parameter : parameters) {				
			Object value = indicatorForm.getDenominatorParameterMapping().get(parameter.getName());
			log.debug("value = '" + value + "'");
			if (value == null || value.equals("")) { 
				errors.rejectValue("denominatorParameterMapping", "cohortIndicator.errors.parameterMappingRequired", 
						new Object[] { value }, "Must map each parameter on the denominator");
			}				
		}		
	}
	
	

	
}
