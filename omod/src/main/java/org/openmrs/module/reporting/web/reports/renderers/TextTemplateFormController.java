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

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngineManager;

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

}
