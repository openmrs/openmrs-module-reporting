package org.openmrs.module.reporting.validator;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link CohortDefinition} class.
 */
@Handler(supports = { CohortDefinition.class }, order = 50)
public class CohortDefinitionValidator implements Validator {
	
	/** Log for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return CohortDefinition.class.isAssignableFrom(c);
	}
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should fail validation if required fields have no values.
	 * @should pass validation if all fields are correct
	 */
	public void validate(Object obj, Errors errors) {
		CohortDefinition cohortDefinition = (CohortDefinition) obj;
		if (cohortDefinition == null) {
			errors.rejectValue("cohortDefinition", "error.general");
		} else {
			
			String errorMessageCode = "error.null";
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", errorMessageCode);
			
			for (Property p : DefinitionUtil.getConfigurationProperties(cohortDefinition)) {
				if (p.getRequired()) {
					String fieldName = p.getField().getName();

					Object value = p.getValue();
					if (value == null) {
						Parameter parameter = cohortDefinition.getParameter(fieldName);
						if (parameter != null) {
							value = parameter.getLabel();
						}
					}
					
					if (value == null)
						errors.rejectValue(fieldName, errorMessageCode);
					else if (value instanceof String && !StringUtils.hasText(value.toString()))
						errors.rejectValue(fieldName, errorMessageCode);
					else if (List.class.isAssignableFrom(value.getClass())) {
						if (((List) value).size() == 0) {
							errors.rejectValue(fieldName, errorMessageCode);
						}
					} else if (Map.class.isAssignableFrom(value.getClass())) {
						if (((Map) value).size() == 0) {
							errors.rejectValue(fieldName, errorMessageCode);
						}
					}
				}
			}
		}
	}
}
