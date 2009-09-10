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

import java.util.HashMap;
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
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.report.Report;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.ReportRequest;
import org.openmrs.module.report.renderer.RenderingException;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.module.reporting.web.widget.WidgetUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
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
			
			for (Map.Entry<String, String> e : command.getUserEnteredParams().entrySet()) {
				if (StringUtils.hasText(e.getValue()))
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
			String uuid = request.getParameter("reportId");
			ReportService reportService = (ReportService) Context.getService(ReportService.class);
			ReportDefinition reportDefinition = reportService.getReportDefinitionByUuid(uuid);
			command.setReportDefinition(reportDefinition);
			command.setRenderingModes(reportService.getRenderingModes(reportDefinition));
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
					String valString = command.getUserEnteredParams().get(parameter.getName());
					Object value;
					if (StringUtils.hasText(valString)) {
						try {
							value = WidgetUtil.parseInput(valString, parameter.getType());
							
							//value = OpenmrsUtil.parse(valString, parameter.getType());
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
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (reportDefinition.getParameters() != null) {
			for (Parameter parameter : reportDefinition.getParameters()) {
				if (command.getUserEnteredParams() != null) {
					String valString = command.getUserEnteredParams().get(parameter.getName());
					Object value;
					if (StringUtils.hasText(valString)) {
						try {
							value = WidgetUtil.parseInput(valString, parameter.getType());
							params.put(parameter.getName(), value);
						}
						catch (Exception ex) {
							// this was already checked above
						}
					}
				}
			}
		}
		
		ReportRequest run = new ReportRequest(reportDefinition, null, params, command.getSelectedMode(), ReportRequest.Priority.HIGHEST);
		Report report = reportService.runReport(run);
		
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
	
		
	public class CommandObject {
		
		private ReportDefinition reportDefinition;		
		private Map<String, String> userEnteredParams;		
		private List<RenderingMode> renderingModes;		
		private String selectedRenderer; // as RendererClass!Arg
		
		public CommandObject() {
			userEnteredParams = new LinkedHashMap<String, String>();
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
		
		public String getSelectedRenderer() {
			return selectedRenderer;
		}
		
		public void setSelectedRenderer(String selectedRenderer) {
			this.selectedRenderer = selectedRenderer;
		}
		
		public Map<String, String> getUserEnteredParams() {
			return userEnteredParams;
		}
		
		public void setUserEnteredParams(Map<String, String> userEnteredParams) {
			this.userEnteredParams = userEnteredParams;
		}
	}	
	
}
