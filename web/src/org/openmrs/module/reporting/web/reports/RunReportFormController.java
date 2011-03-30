/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.web.reports;

import java.util.HashSet;
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
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
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
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c == CommandObject.class;
	}
	
	public void validate(Object commandObject, Errors errors) {
		CommandObject command = (CommandObject) commandObject;
		ValidationUtils.rejectIfEmpty(errors, "reportDefinition", "Missing reportId or report not found");
		if (command.getReportDefinition() != null) {
			ReportDefinition reportDefinition = command.getReportDefinition();
			Set<String> requiredParams = new HashSet<String>();
			if (reportDefinition.getParameters() != null) {
				for (Parameter parameter : reportDefinition.getParameters()) {
					requiredParams.add(parameter.getName());
				}
			}
			
			for (Map.Entry<String, Object> e : command.getUserEnteredParams().entrySet()) {
				if (ObjectUtil.notNull(e.getValue()))
					requiredParams.remove(e.getKey());
			}
			if (requiredParams.size() > 0) {
				errors.rejectValue("userEnteredParams", "Enter all parameter values");
			}
			
			if (reportDefinition.getDataSetDefinitions() == null || reportDefinition.getDataSetDefinitions().size() == 0)
				errors.rejectValue("reportDefinition", "A report definition must declare some data set definitions");
		}
		ValidationUtils.rejectIfEmpty(errors, "selectedRenderer", "Pick a renderer");
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
					command.getUserEnteredParams().put(param.getKey(), param.getValue());
				}
				command.setSelectedRenderer(req.getRenderingMode().getDescriptor());
			} else {
				String uuid = request.getParameter("reportId");
				ReportDefinition reportDefinition = rds.getDefinitionByUuid(uuid);
				command.setReportDefinition(reportDefinition);
			}
			command.setRenderingModes(reportService.getRenderingModes(command.getReportDefinition()));
		}
		return command;
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object commandObject,
	                                BindException errors) throws Exception {
		CommandObject command = (CommandObject) commandObject;
		ReportDefinition reportDefinition = command.getReportDefinition();
		ReportService reportService = (ReportService) Context.getService(ReportService.class);
		
		EvaluationContext evalContext = new EvaluationContext();
		
		if (reportDefinition.getParameters() != null) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				if (command.getUserEnteredParams() != null) {
					Object value = command.getUserEnteredParams().get(parameter.getName());
					if (ObjectUtil.notNull(value)) {
						try {
							value = WidgetUtil.parseInput(value.toString(), parameter.getType());
							evalContext.addParameterValue(parameter.getName(), value);
						}
						catch (Exception ex) {
							errors.rejectValue("userEnteredParams", parameter.getLabel() + ": " + ex.getMessage());
						}
					}
				}
			}
		}
		if (errors.hasErrors())
			return showForm(request, response, errors);
		
		String renderClass = command.getSelectedRenderer();
		String renderArg = "";
		if (renderClass.indexOf("!") > 0) {
			int ind = renderClass.indexOf("!");
			renderArg = renderClass.substring(ind + 1);
			renderClass = renderClass.substring(0, ind);
		}
		ReportRenderer renderer = reportService.getReportRenderer(renderClass);

		// Check to make sure the renderer can render this report 
		if (!renderer.canRender(reportDefinition))  
			throw new RenderingException("Unable to render report definition " + reportDefinition.getName());
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		if (reportDefinition.getParameters() != null) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				if (command.getUserEnteredParams() != null) {
					Object value = command.getUserEnteredParams().get(parameter.getName());
					if (ObjectUtil.notNull(value)) {
						try {
							value = WidgetUtil.parseInput(value.toString(), parameter.getType());
							params.put(parameter.getName(), value);
						}
						catch (Exception ex) {
							// this was already checked above
						}
					}
				}
			}
		}
		
		ReportRequest run = new ReportRequest(new Mapped<ReportDefinition>(reportDefinition, params), command.getBaseCohort(), command.getSelectedMode(), ReportRequest.Priority.HIGHEST);
		Report report;
		try {
			report = reportService.runReport(run);
		} catch (Exception ex) {
			errors.rejectValue("reportDefinition", null, formatEvaluationError(ex));
			return showForm(request, response, errors);
		}
		
		// If we're supposed to use a web report renderer, then we just redirect to the appropriate URL 
		if (renderer instanceof WebReportRenderer) {
			WebReportRenderer webRenderer = (WebReportRenderer) renderer;
			if (webRenderer.getLinkUrl(reportDefinition) != null) {
				request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_DATA, report.getRawData());
				request.getSession().setAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT, renderArg);
				String url = webRenderer.getLinkUrl(reportDefinition);
				if (!url.startsWith("/"))
					url = "/" + url;
				url = request.getContextPath() + url;
				request.getSession().setAttribute(ReportingConstants.OPENMRS_LAST_REPORT_URL, url);
				return new ModelAndView(new RedirectView(url));
			}
		}
		// Otherwise, just render the report 
		else { 
			// TODO it's possible that a web renderer will handle this -- is that ok?
			String filename = renderer.getFilename(reportDefinition, renderArg).replace(" ", "_");
			response.setContentType(renderer.getRenderedContentType(reportDefinition, renderArg));
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setHeader("Pragma", "no-cache");
			response.getOutputStream().write(report.getRenderedOutput());
		}
		return null;
	}

	
	private String formatEvaluationError(Exception ex) {
		if (ex == null) {
			return "";
		} else {
			return ex.getMessage().replaceAll("\\n", "<br/>");
		}
    }
	

	public class CommandObject {
		
		private ReportDefinition reportDefinition;
		private Mapped<CohortDefinition> baseCohort;
		private Map<String, Object> userEnteredParams;		
		private List<RenderingMode> renderingModes;		
		private String selectedRenderer; // as RendererClass!Arg
		
		public CommandObject() {
			userEnteredParams = new LinkedHashMap<String, Object>();
		}
		
		public RenderingMode getSelectedMode() throws ClassNotFoundException {
			if (selectedRenderer == null)
				return null;
			String[] temp = selectedRenderer.split("!");
			Class<? extends ReportRenderer> rc = (Class<? extends ReportRenderer>) Context.loadClass(temp[0]);
			String arg = (temp.length > 1 && StringUtils.hasText(temp[1])) ? temp[1] : null;
			for (RenderingMode mode : renderingModes) {
				if (mode.getRenderer().getClass().equals(rc)
						&& OpenmrsUtil.nullSafeEquals(mode.getArgument(), arg)) {
					return mode;
				}
			}
			log.warn("Could not find requested rendering mode: " + selectedRenderer);
			return null;
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
	}	
	
}
