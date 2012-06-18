package org.openmrs.module.reporting.validator;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
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
						value = cohortDefinition.getParameter(fieldName);
					}
					
					if (ObjectUtil.isNull(value)) {
						errors.rejectValue(fieldName, errorMessageCode);
					}
					else if (Collection.class.isAssignableFrom(value.getClass())) {
						if (((Collection) value).size() == 0) {
							errors.rejectValue(fieldName, errorMessageCode);
						}
					} 
					else if (Map.class.isAssignableFrom(value.getClass())) {
						if (((Map) value).size() == 0) {
							errors.rejectValue(fieldName, errorMessageCode);
						}
					}
				}
			}
		}
	}
}
