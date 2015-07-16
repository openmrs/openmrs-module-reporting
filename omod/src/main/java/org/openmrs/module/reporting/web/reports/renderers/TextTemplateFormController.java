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
package org.openmrs.module.reporting.web.reports.renderers;

import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.WebDataBinder;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngineManager;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.web.WebConstants;

@Controller
public class TextTemplateFormController {
	
	
	protected static Log log = LogFactory.getLog(DelimitedTextReportRendererFormController.class);
	
	/**
	 * Default Constructor
	 */
	public TextTemplateFormController() { }
	


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Mapped.class, new MappedEditor());
	}
	
	/**
	 *  prepares a new form for the a TextTemplateRenderer
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/module/reporting/reports/renderers/textTemplateReportRenderer")
	public void textTemplateReportRenderer(ModelMap model, 
								@RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid, 
								@RequestParam(required=false, value="reportDefinitionUuid") String reportDefinitionUuid,
								@RequestParam(required=true,  value="type") Class<? extends TextTemplateRenderer> type,
								@RequestParam(required=false, value="successUrl") String successUrl) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException, InstantiationException, ClassNotFoundException, UnsupportedEncodingException {
		
		ReportService rs = Context.getService(ReportService.class); 
		ReportDesign design = null;
		if (StringUtils.isNotEmpty(reportDesignUuid)) {
			design = rs.getReportDesignByUuid(reportDesignUuid);
		} else {
			design = new ReportDesign();
			design.setRendererType(type);
			if (StringUtils.isNotEmpty(reportDefinitionUuid)) {
				design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
			}
		}
		

		ReportDesignResource resource = design.getResourceByName("template");
		if (resource != null) {
			model.addAttribute("script", new String(resource.getContents(), "UTF-8"));
		}
		
			
		String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (StringUtils.isEmpty(successUrl)) {
    		successUrl = "/module/reporting/reports/manageReportDesigns.form";
    	}
		else if (successUrl.startsWith(pathToRemove)) {
    		successUrl = successUrl.substring(pathToRemove.length());
    	}
		model.addAttribute("design", design );
		model.addAttribute("scriptType", design.getPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, ""));
		model.addAttribute("scriptTypes", TemplateEngineManager.getAvailableTemplateEngineNames());
		model.addAttribute("successUrl", successUrl);
		model.addAttribute("cancelUrl",  successUrl);
	}
	
	
	/**
	 * Saves report design
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/module/reporting/reports/renderers/saveTextTemplateReportRendererDesign")
	public String saveTextTemplateReportRendererDesign(ModelMap model, HttpServletRequest request,
					@RequestParam(required=false, value="uuid") String uuid,
					@RequestParam(required=true,  value="name") String name,
					@RequestParam(required=false, value="description") String description,
					@RequestParam(required=true,  value="reportDefinition") String reportDefinitionUuid,
					@RequestParam(required=true,  value="rendererType") Class<? extends TextTemplateRenderer> rendererType,
					@RequestParam(required=true,  value="script") String script,
					@RequestParam(required=true,  value="scriptType") String scriptType,
					@RequestParam(required=true,  value="successUrl") String successUrl
	) throws UnsupportedEncodingException {
		ReportService rs = Context.getService(ReportService.class);
		ReportDesign design = null;
		ReportDesignResource designResource = new ReportDesignResource();
			
		if (StringUtils.isNotEmpty(uuid)) {
			design = rs.getReportDesignByUuid(uuid);
		}
		if (design == null) {
			design = new ReportDesign();
			design.setRendererType(rendererType);
		}

		design.setName(name);
		design.setDescription(description);
		design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
		design.getProperties().clear();
		design.getResources().clear();
		
		designResource.setReportDesign(design);
		designResource.setName("template");
		designResource.setContentType("text/html");
		designResource.setContents(script.getBytes("UTF-8"));
		
		design.addResource(designResource);
		design.addPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, scriptType);
	
		String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (StringUtils.isEmpty(successUrl)) {
    		successUrl = "/module/reporting/reports/manageReportDesigns.form";
    	}
		else if (successUrl.startsWith(pathToRemove)) {
    		successUrl = successUrl.substring(pathToRemove.length());
    	}
    	
    	design = rs.saveReportDesign(design);
    	return "redirect:" + successUrl;
	}
	
	@ModelAttribute( "expSupportedTypes" )
	public Set<String> expSupportedTypes () {
		EvaluationContext ec = new EvaluationContext();
		Set<String> expSupportedTypes = new HashSet<String>();
		for (Object value : ec.getContextValues().values()) {
			expSupportedTypes.add(value.getClass().getName());
		}
		return expSupportedTypes;
	}
	
	@ModelAttribute( "userParams" )
	public UserParams userParams ( @RequestParam(required=false, value="userEnteredParams") Map<String, Object> userEnteredParams,
			@RequestParam(required=false, value="expressions") Map<String, String> expressions,
			@RequestParam(required=false, value="baseCohort") Mapped<CohortDefinition> baseCohort ) {
		UserParams userParams = new UserParams();
		if ( userEnteredParams != null ) {
			userParams.setUserEnteredParams(userEnteredParams);
		}
		
		if ( expressions != null ) {
			userParams.setExpressions(expressions);
		}
		
		if ( baseCohort != null ) {
			userParams.setBaseCohort(baseCohort);
		}
		
		return userParams;
	}
	
	@RequestMapping( value="/module/reporting/reports/renderers/previewTextTemplateReportRenderer", method=RequestMethod.GET)
	public void initPreviewForm(ModelMap model, HttpServletRequest request,
			@RequestParam(required=true, value="reportDefinition") String reportDefinitionUuid,
			@RequestParam(required=false, value="uuid") String uuid,
			@RequestParam(required=false, value="iframe" ) String iframe,
			@RequestParam(required=true, value="script") String script,
			@RequestParam(required=true, value="scriptType") String scriptType,
			@RequestParam(required=true,  value="rendererType") Class<? extends TextTemplateRenderer> rendererType,
			@ModelAttribute("userParams") UserParams userParams
			) throws UnsupportedEncodingException {
		
		ReportService rs = Context.getService(ReportService.class);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		ReportDefinition reportDefinition = rds.getDefinitionByUuid(reportDefinitionUuid);
		ReportDesignResource designResource = new ReportDesignResource();
		ReportDesign design = null;
		
		if (StringUtils.isNotEmpty(uuid)) {
			design = rs.getReportDesignByUuid(uuid);
		}
		
		// if it is a new Report Design then create an incomplete Report Design Object which will be used by the Preview section
		if (design == null) {
			design = new ReportDesign();
			design.setRendererType(rendererType);
			design.setName(Context.getMessageSourceService().getMessage("reporting.TextTemplateRenderer.incompleteDesign"));
			design.setDescription(new Date().toString());
			model.addAttribute("tempDesignUuid", design.getUuid());
		}
		
		design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
		design.getProperties().clear();
		design.getResources().clear();
		
		designResource.setReportDesign(design);
		designResource.setName("template");
		designResource.setContentType("text/html");
		designResource.setContents(script.getBytes("UTF-8"));
		
		design.addResource(designResource);
		design.addPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, scriptType);
		
		design = rs.saveReportDesign(design);
		
		model.addAttribute("iframe", iframe);
		model.addAttribute("script", script);
		model.addAttribute("scriptType", scriptType);
		model.addAttribute("reportDefinition", reportDefinition);
		model.addAttribute("rendererType", rendererType);
		model.addAttribute("design", design );
		
	}
		
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/module/reporting/reports/renderers/previewTextTemplateReportRenderer", method=RequestMethod.POST)
	public void previewScript(ModelMap model, HttpServletRequest request,
			@RequestParam(required=true, value="reportDefinition") String reportDefinitionUuid,
			@RequestParam(required=true, value="uuid") String uuid,
			@RequestParam(required=false, value="iframe" ) String iframe,
			@RequestParam(required=true, value="script") String script,
			@RequestParam(required=true, value="scriptType") String scriptType,
			@RequestParam(required=true,  value="rendererType") Class<? extends TextTemplateRenderer> rendererType,
			@ModelAttribute("userParams") UserParams userParams,
			BindingResult bindingResult
			) throws EvaluationException, UnsupportedEncodingException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		ReportDefinition reportDefinition = rds.getDefinitionByUuid(reportDefinitionUuid);
				
		// validate parameters
		if ( !reportDefinition.getParameters().isEmpty() ) {
			
			Set<String> requiredParams = new HashSet<String>();
			for (Parameter parameter : reportDefinition.getParameters()) {
				requiredParams.add(parameter.getName());
			}
			for (Map.Entry<String, Object> e : userParams.getUserEnteredParams().entrySet()) {
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
			
			if (requiredParams.size() > 0 && !userParams.getExpressions().isEmpty() &&  !userParams.getUserEnteredParams().isEmpty() ) {
				for (Iterator<String> iterator = requiredParams.iterator(); iterator.hasNext();) {
					String parameterName = (String) iterator.next();
					if (StringUtils.isNotEmpty(userParams.getExpressions().get(parameterName))) {
						String expression = userParams.getExpressions().get(parameterName);
						if (!EvaluationUtil.isExpression(expression)){
							bindingResult.rejectValue("expressions[" + parameterName + "]",
							    "reporting.Report.run.error.invalidParamExpression");
						}
					} else {
						bindingResult.rejectValue("userEnteredParams[" + parameterName + "]", "error.required",
						    new Object[] { "This parameter" }, "{0} is required");
					}
				}
			}
		}
		
		// Try to parse the required parameters into appropriate objects if they are available
		if (!bindingResult.hasErrors()) {

			Map<String, Object> params = new LinkedHashMap<String, Object>();			
			if (reportDefinition.getParameters() != null && (userParams.getUserEnteredParams() != null || userParams.getExpressions() != null)) {
				for (Parameter parameter : reportDefinition.getParameters()) {
					Object value = null;
					String expression = null;
					if(userParams.getExpressions() != null && ObjectUtil.notNull(userParams.getExpressions().get(parameter.getName())))
						expression = userParams.getExpressions().get(parameter.getName());
					else
						value = userParams.getUserEnteredParams().get(parameter.getName());
					
					if (ObjectUtil.notNull(value) || ObjectUtil.notNull(expression)) {
						try {
							if (StringUtils.isNotEmpty(expression))
								value = expression;
							else
								value = WidgetUtil.parseInput(value, parameter.getType(), parameter.getCollectionType());
							
							params.put(parameter.getName(), value);
						}
						catch (Exception ex) {
							bindingResult.rejectValue("userEnteredParams[" + parameter.getName() + "]", ex.getMessage());
						}
					}
				}
			}
						
			// if no errors were found while parsing, retrieve a report design object and renderer the data
			if (!bindingResult.hasErrors()) { 
				String previewResult = "";
				ReportDesign design = null;
				ReportDesignResource designResource = new ReportDesignResource();
				ReportData result = null;
				EvaluationContext ec = new EvaluationContext();
				ReportService rs = Context.getService(ReportService.class);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				if (StringUtils.isNotEmpty(uuid)) {
					design = rs.getReportDesignByUuid(uuid);
					design.setRendererType(rendererType);
					design.setReportDefinition(reportDefinition);
					design.getProperties().clear();
					design.getResources().clear();
					
					designResource.setReportDesign(design);
					designResource.setName("template");
					designResource.setContentType("text/html");
					designResource.setContents(script.getBytes("UTF-8"));
					
					design.addResource(designResource);
					design.addPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, scriptType);
					
					if ( userParams.getBaseCohort() != null ) {
						try {
							Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(userParams.getBaseCohort(), ec);
							ec.setBaseCohort(baseCohort);
							
						}
						catch (Exception ex) {
							throw new EvaluationException("baseCohort", ex);
						}
					}
					
					if ( params != null ) {
						ec.setParameterValues(params);						
					}
					
					Class<?> rt = Context.loadClass(design.getRendererType().getName());
					ReportTemplateRenderer reportRenderer = (ReportTemplateRenderer) rt.newInstance(); 
					Throwable errorDetails = null;				
					result = rds.evaluate(reportDefinition, ec);
					try {
						reportRenderer.render( result, design.getUuid(), out);
					} catch (Throwable e) {
						errorDetails = e;
					}
					previewResult = (out.toByteArray() != null ? new String(out.toByteArray(), "UTF-8") : "");
					StringUtils.deleteWhitespace(previewResult);
					model.addAttribute("previewResult", previewResult);
					model.addAttribute("design", design);
					model.addAttribute("errorDetails", errorDetails);
					
				}			
				
			}
			
		}

			
				
		model.addAttribute("iframe", iframe);
		model.addAttribute("script", script);
		model.addAttribute("scriptType", scriptType);
		model.addAttribute("reportDefinition", reportDefinition);
		model.addAttribute("rendererType", rendererType);
		model.addAttribute("errors", bindingResult);
		
	}
	
	public class UserParams {
		
		private Map<String, Object> userEnteredParams;
		private Map<String, String> expressions;
		private Mapped<CohortDefinition> baseCohort;

		public UserParams() {
			userEnteredParams = new LinkedHashMap<String, Object>();
			expressions = new HashMap<String ,String>();
		}
		
		public Map<String, Object> getUserEnteredParams() {
			return userEnteredParams;
		}

		public void setUserEnteredParams(Map<String, Object> userEnteredParams) {
			this.userEnteredParams = userEnteredParams;
		}

		public Map<String, String> getExpressions() {
			return expressions;
		}

		public void setExpressions(Map<String, String> expressions) {
			this.expressions = expressions;
		}
		
		public Mapped<CohortDefinition> getBaseCohort() {
			return baseCohort;
		}

		public void setBaseCohort(Mapped<CohortDefinition> baseCohort) {
			this.baseCohort = baseCohort;
		}
		
	}


}
