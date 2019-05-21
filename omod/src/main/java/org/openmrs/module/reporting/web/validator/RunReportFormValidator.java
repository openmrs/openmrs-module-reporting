package org.openmrs.module.reporting.web.validator;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.web.reports.RunReportFormController;
import org.quartz.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class RunReportFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> c) {
		return c == RunReportFormController.CommandObject.class;
	}

	@Override
	public void validate(Object commandObject, Errors errors) {
		RunReportFormController.CommandObject command = (RunReportFormController.CommandObject) commandObject;
		ValidationUtils.rejectIfEmpty(errors, "reportDefinition", "reporting.Report.run.error.missingReportID");
		if (command.getReportDefinition() != null) {
			ReportDefinition reportDefinition = command.getReportDefinition();
			Set<String> requiredParams = new HashSet<String>();
			if (reportDefinition.getParameters() != null) {
				for (Parameter parameter : reportDefinition.getParameters()) {
					if (parameter.isRequired()) {
						requiredParams.add(parameter.getName());
					}
				}
			}

			for (Map.Entry<String, Object> e : command.getUserEnteredParams().entrySet()) {
				if (e.getValue() instanceof Iterable || e.getValue() instanceof Object[]) {
					Object iterable = e.getValue();
					if (e.getValue() instanceof Object[]) {
						iterable = Arrays.asList((Object[]) e.getValue());
					}

					boolean hasNull = true;

					for (Object value : (Iterable<Object>) iterable) {
						hasNull = !ObjectUtil.notNull(value);
					}

					if (!hasNull) {
						requiredParams.remove(e.getKey());
					}
				} else if (ObjectUtil.notNull(e.getValue())) {
					requiredParams.remove(e.getKey());
				}
			}
			if (requiredParams.size() > 0) {
				for (Iterator<String> iterator = requiredParams.iterator(); iterator.hasNext(); ) {
					String parameterName = (String) iterator.next();
					if (StringUtils.hasText(command.getExpressions().get(parameterName))) {
						String expression = command.getExpressions().get(parameterName);
						if (!EvaluationUtil.isExpression(expression)) {
							errors.rejectValue("expressions[" + parameterName + "]",
									"reporting.Report.run.error.invalidParamExpression");
						}
					} else {
						errors.rejectValue("userEnteredParams[" + parameterName + "]", "error.required",
								new Object[] { "This parameter" }, "{0} is required");
					}
				}
			}

			if (reportDefinition.getDataSetDefinitions() == null || reportDefinition.getDataSetDefinitions().size() == 0) {
				errors.reject("reporting.Report.run.error.definitionNotDeclared");
			}

			if (ObjectUtil.notNull(command.getSchedule())) {
				if (!CronExpression.isValidExpression(command.getSchedule())) {
					errors.rejectValue("schedule", "reporting.Report.run.error.invalidCronExpression");
				}
			}
		}
		ValidationUtils.rejectIfEmpty(errors, "selectedRenderer", "reporting.Report.run.error.noRendererSelected");

	}
}
