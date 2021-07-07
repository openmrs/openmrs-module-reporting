/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsUtil;
import org.quartz.CronExpression;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller runs a report (which must be passed in with the reportId parameter) after
 * allowing the user to enter parameters (if any) and to choose a ReportRenderer. If the chosen
 * ReportRenderer is a WebReportRenderer, then the report data is placed in the session and this
 * page redirects to the WebReportRenderer's specified URL. Otherwise the renderer writes to this
 * form's response.
 */

/**
 * This controller runs a report (which must be passed in with the reportId parameter) after
 * allowing the user to enter parameters (if any) and to choose a ReportRenderer. If the chosen
 * ReportRenderer is a WebReportRenderer, then the report data is placed in the session and this
 * page redirects to the WebReportRenderer's specified URL. Otherwise the renderer writes to this
 * form's response.
 */
@Controller
public class RunReportFormController implements Validator {

	private transient Log log = LogFactory.getLog(this.getClass());

	@SuppressWarnings("rawtypes")
	public boolean supports(Class c) {
		return c == CommandObject.class;
	}

	@Override
	public void validate(Object commandObject, Errors errors) {
		CommandObject command = (CommandObject) commandObject;
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
				for (Iterator<String> iterator = requiredParams.iterator(); iterator.hasNext();) {
					String parameterName = (String) iterator.next();
					if (StringUtils.hasText(command.getExpressions().get(parameterName))) {
						String expression = command.getExpressions().get(parameterName);
						if (!EvaluationUtil.isExpression(expression)){
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


	@InitBinder
	private void initBinder(WebDataBinder binder) throws Exception {
		binder.registerCustomEditor(Mapped.class, new MappedEditor());
	}

	@RequestMapping(value = "/module/reporting/run/runReport.form", method = RequestMethod.GET)
	protected String initializeForm(HttpServletRequest request, ModelMap model) throws Exception {
		CommandObject command = new CommandObject();

		fillCommandObjectData(command, request);

		model.addAttribute("report", command);
		addReferenceData(model, command);
		return "/module/reporting/run/runReportForm";
	}

	@RequestMapping(value = "/module/reporting/run/runReport.form", method = RequestMethod.POST)
	protected String onSubmit(@ModelAttribute("report") CommandObject commandObject, BindingResult errors,
			HttpServletRequest request) throws Exception {

		CommandObject command = (CommandObject) commandObject;
		fillCommandObjectData(command, request);

		validate(commandObject, errors);

		if (errors.hasErrors()) {
			return "/module/reporting/run/runReportForm";
		}

		ReportDefinition reportDefinition = command.getReportDefinition();

		ReportService rs = Context.getService(ReportService.class);

		// Parse the input parameters into appropriate objects and fail validation if any are invalid
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		if (reportDefinition.getParameters() != null && (command.getUserEnteredParams() != null
				|| command.getExpressions() != null)) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				Object value = null;
				String expression = null;
				if (command.getExpressions() != null && ObjectUtil
						.notNull(command.getExpressions().get(parameter.getName()))) {
					expression = command.getExpressions().get(parameter.getName());
				} else {
					value = command.getUserEnteredParams().get(parameter.getName());
				}
				if (ObjectUtil.notNull(value) || ObjectUtil.notNull(expression)) {
					try {
						if (StringUtils.hasText(expression))
							value = expression;
						else
							value = WidgetUtil.parseInput(value, parameter.getType(), parameter.getCollectionType());

						params.put(parameter.getName(), value);
					}
					catch (Exception ex) {
						errors.rejectValue("userEnteredParams[" + parameter.getName() + "]", ex.getMessage());
					}
				}
			}
		}

		// Ensure that the chosen renderer is valid for this report
		RenderingMode renderingMode = command.getSelectedMode();
		if (!renderingMode.getRenderer().canRender(reportDefinition)) {
			errors.rejectValue("selectedRenderer", "reporting.Report.run.error.invalidRenderer");
		}

		if (errors.hasErrors()) {
			return "/module/reporting/run/runReportForm";
		}

		ReportRequest rr = null;
		if (command.getExistingRequestUuid() != null) {
			rr = rs.getReportRequestByUuid(command.getExistingRequestUuid());
		} else {
			rr = new ReportRequest();
		}
		rr.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
		rr.setBaseCohort(command.getBaseCohort());
		rr.setRenderingMode(command.getSelectedMode());
		rr.setPriority(Priority.NORMAL);
		rr.setSchedule(command.getSchedule());

		// TODO: We might want to check here if this exact same report request is already queued and just re-direct if so

		rr = rs.queueReport(rr);
		rs.processNextQueuedReports();

		return "redirect:/module/reporting/reports/reportHistoryOpen.form?uuid=" + rr.getUuid();
	}

	private void addReferenceData(ModelMap model, CommandObject command)
			throws Exception {
		EvaluationContext ec = new EvaluationContext();
		Set<String> expSupportedTypes = new HashSet<String>();
		Set<String> inputsToToggle = new HashSet<String>();
		for (Object value : ec.getContextValues().values()) {
			expSupportedTypes.add(value.getClass().getName());
		}
		model.addAttribute("expSupportedTypes", expSupportedTypes);

		for (Map.Entry<String, Object> e : command.getUserEnteredParams().entrySet()) {
			if (StringUtils.hasText(command.getExpressions().get(e.getKey()))) {
				inputsToToggle.add(e.getKey());
			}
		}
		model.addAttribute("inputsToToggle", inputsToToggle);
	}

	private void fillCommandObjectData(CommandObject command, HttpServletRequest request) {
		if (Context.isAuthenticated()) {
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportService reportService = Context.getService(ReportService.class);
			if (StringUtils.hasText(request.getParameter("copyRequest"))) {
				ReportRequest req = reportService.getReportRequestByUuid(request.getParameter("copyRequest"));
				// avoid lazy init exceptions
				command.setReportDefinition(
						rds.getDefinitionByUuid(req.getReportDefinition().getParameterizable().getUuid()));
				for (Map.Entry<String, Object> param : req.getReportDefinition().getParameterMappings().entrySet()) {
					Object value = param.getValue();
					if (value != null && EvaluationUtil.isExpression(value.toString())) {
						command.getExpressions().put(param.getKey(), (String) value);
						value = "";
					}
					command.getUserEnteredParams().put(param.getKey(), value);
				}
				command.setSelectedRenderer(req.getRenderingMode().getDescriptor());
			} else if (StringUtils.hasText(request.getParameter("requestUuid"))) {
				String reqUuid = request.getParameter("requestUuid");
				ReportRequest rr = reportService.getReportRequestByUuid(reqUuid);
				command.setExistingRequestUuid(reqUuid);
				command.setReportDefinition(rr.getReportDefinition().getParameterizable());
				command.setUserEnteredParams(rr.getReportDefinition().getParameterMappings());
				command.setBaseCohort(rr.getBaseCohort());
				command.setSelectedRenderer(rr.getRenderingMode().getDescriptor());
				command.setSchedule(rr.getSchedule());
			} else {
				String uuid = request.getParameter("reportId");
				ReportDefinition reportDefinition = rds.getDefinitionByUuid(uuid);
				command.setReportDefinition(reportDefinition);
				for (Parameter p : reportDefinition.getParameters()) {
					if (p.getDefaultValue() != null) {
						command.getUserEnteredParams().put(p.getName(), p.getDefaultValue());
					}
				}
			}
			command.setRenderingModes(reportService.getRenderingModes(command.getReportDefinition()));
		}
	}
}
