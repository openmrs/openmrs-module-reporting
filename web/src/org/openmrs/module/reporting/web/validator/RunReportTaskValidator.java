package org.openmrs.module.reporting.web.validator;

import org.openmrs.module.reporting.report.task.RunReportTask;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class RunReportTaskValidator implements Validator {
	
	public boolean supports(Class clazz) {
		return RunReportTask.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "reportDefinition", "error.null");
		ValidationUtils.rejectIfEmpty(errors, "renderingMode", "error.null");
		ValidationUtils.rejectIfEmpty(errors, "priority", "error.null");
		ValidationUtils.rejectIfEmpty(errors, "startTime", "error.null");
		ValidationUtils.rejectIfEmpty(errors, "repeatInterval", "error.null");
		// RunReportTask task = (RunReportTask) target;
	}
	
}
