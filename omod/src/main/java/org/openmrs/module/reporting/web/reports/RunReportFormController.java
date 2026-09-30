/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
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
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This controller runs a report (which must be passed in with the reportId parameter) after
 * allowing the user to enter parameters (if any) and to choose a ReportRenderer. If the chosen
 * ReportRenderer is a WebReportRenderer, then the report data is placed in the session and this
 * page redirects to the WebReportRenderer's specified URL. Otherwise the renderer writes to this
 * form's response.
 */
public class RunReportFormController extends SimpleFormController implements Validator {

	private transient Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see BaseCommandController#initBinder(HttpServletRequest, ServletRequestDataBinder)
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		binder.registerCustomEditor(Mapped.class, new MappedEditor());
	}
	
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
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		CommandObject command = new CommandObject();
		if (Context.isAuthenticated()) {
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportService reportService = Context.getService(ReportService.class);
			if (StringUtils.hasText(request.getParameter("copyRequest"))) {
				ReportRequest req = reportService.getReportRequestByUuid(request.getParameter("copyRequest"));
				// avoid lazy init exceptions
				command.setReportDefinition(rds.getDefinitionByUuid(req.getReportDefinition().getParameterizable().getUuid()));
				for (Map.Entry<String, Object> param : req.getReportDefinition().getParameterMappings().entrySet()) {
					Object value = param.getValue();
					if ( value != null && EvaluationUtil.isExpression( value.toString() ) ) {
						command.getExpressions().put( param.getKey(),  ( String ) value );
						value = "";
					} 
					command.getUserEnteredParams().put(param.getKey(), value );
				}
				command.setSelectedRenderer(req.getRenderingMode().getDescriptor());
			}
			else if (StringUtils.hasText(request.getParameter("requestUuid"))) {
				String reqUuid = request.getParameter("requestUuid");
				ReportRequest rr = reportService.getReportRequestByUuid(reqUuid);
				command.setExistingRequestUuid(reqUuid);
				command.setReportDefinition(rr.getReportDefinition().getParameterizable());
				command.setUserEnteredParams(rr.getReportDefinition().getParameterMappings());
				command.setBaseCohort(rr.getBaseCohort());
				command.setSelectedRenderer(rr.getRenderingMode().getDescriptor());
				command.setSchedule(rr.getSchedule());
			}
			else {
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
		return command;
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object commandObject, BindException errors) throws Exception {
		CommandObject command = (CommandObject) commandObject;
		ReportDefinition reportDefinition = command.getReportDefinition();
		
		ReportService rs = Context.getService(ReportService.class);

		// Parse the input parameters into appropriate objects and fail validation if any are invalid
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		if (reportDefinition.getParameters() != null && (command.getUserEnteredParams() != null || command.getExpressions() != null)) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				Object value = null;
				String expression = null;
				if (command.getExpressions() != null && ObjectUtil.notNull(command.getExpressions().get(parameter.getName()))) {
					expression = command.getExpressions().get(parameter.getName());
				}
				else {
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
			return showForm(request, response, errors);
		}
		
		ReportRequest rr = null;
		if (command.getExistingRequestUuid() != null) {
			rr = rs.getReportRequestByUuid(command.getExistingRequestUuid());
		}
		else {
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
		
		return new ModelAndView(new RedirectView("../reports/reportHistoryOpen.form?uuid="+rr.getUuid()));
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object commandObject, Errors errors) throws Exception {
		CommandObject command = (CommandObject) commandObject;
		Map<String, Object> map = new HashMap<String, Object>();
		EvaluationContext ec = new EvaluationContext();
		Set<String> expSupportedTypes = new HashSet<String>();
		Set<String> inputsToToggle = new HashSet<String>();
		for (Object value : ec.getContextValues().values()) {
			expSupportedTypes.add(value.getClass().getName());
		}
		map.put("expSupportedTypes", expSupportedTypes);

		for (Map.Entry<String, Object> e : command.getUserEnteredParams().entrySet()) {
			if (StringUtils.hasText(command.getExpressions().get(e.getKey()))) {
				inputsToToggle.add( e.getKey() );
			}
		}
		map.put( "inputsToToggle", inputsToToggle );
		return map;
	}
	
	public class CommandObject {
		
		private String existingRequestUuid;
		private ReportDefinition reportDefinition;
		private Mapped<CohortDefinition> baseCohort;
		private Map<String, Object> userEnteredParams;			
		private String selectedRenderer; // as RendererClass!Arg
		private String schedule;
		private Map<String, String> expressions;
		
		private List<RenderingMode> renderingModes;	
		
		public CommandObject() {
			userEnteredParams = new LinkedHashMap<String, Object>();
			expressions = new HashMap<String ,String>();
		}
		
		@SuppressWarnings("unchecked")
		public RenderingMode getSelectedMode() {
			if (selectedRenderer != null) {
				try {
					String[] temp = selectedRenderer.split("!");
					Class<? extends ReportRenderer> rc = (Class<? extends ReportRenderer>) Context.loadClass(temp[0]);
					String arg = (temp.length > 1 && StringUtils.hasText(temp[1])) ? temp[1] : null;
					for (RenderingMode mode : renderingModes) {
						if (mode.getRenderer().getClass().equals(rc) && OpenmrsUtil.nullSafeEquals(mode.getArgument(), arg)) {
							return mode;
						}
					}
					log.warn("Could not find requested rendering mode: " + selectedRenderer);
				}
				catch (Exception e) {
					log.warn("Could not load requested renderer", e);
				}
			}
			return null;
		}

		public String getExistingRequestUuid() {
			return existingRequestUuid;
		}

		public void setExistingRequestUuid(String existingRequestUuid) {
			this.existingRequestUuid = existingRequestUuid;
		}

		public List<RenderingMode> getRenderingModes() {
			return renderingModes;
		}
		
		public void setRenderingModes(List<RenderingMode> rendereringModes) {
			this.renderingModes = rendereringModes;
		}
		
		public ReportDefinition getReportDefinition() {
			return reportDefinition;
		}
		
		public void setReportDefinition(ReportDefinition reportDefinition) {
			this.reportDefinition = reportDefinition;
		}

		public Mapped<CohortDefinition> getBaseCohort() {
			return baseCohort;
		}

		public void setBaseCohort(Mapped<CohortDefinition> baseCohort) {
			this.baseCohort = baseCohort;
		}

		public String getSelectedRenderer() {
			return selectedRenderer;
		}
		
		public void setSelectedRenderer(String selectedRenderer) {
			this.selectedRenderer = selectedRenderer;
		}
		
		public Map<String, Object> getUserEnteredParams() {
			return userEnteredParams;
		}
		
		public void setUserEnteredParams(Map<String, Object> userEnteredParams) {
			this.userEnteredParams = userEnteredParams;
		}

		public String getSchedule() {
			return schedule;
		}

		public void setSchedule(String schedule) {
			this.schedule = schedule;
		}
		
		/**
		 * @return the expressions
		 */
		public Map<String, String> getExpressions() {
			return expressions;
		}
		
		/**
		 * @param expressions the expressions to set
		 */
		public void setExpressions(Map<String, String> expressions) {
			this.expressions = expressions;
		}
	}
	
}
