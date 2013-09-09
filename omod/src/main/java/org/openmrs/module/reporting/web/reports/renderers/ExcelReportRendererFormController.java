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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.service.ReportService;

import org.openmrs.web.WebConstants;

@Controller
public class ExcelReportRendererFormController {
	protected static Log log = LogFactory.getLog(NonConfigurableReportRendererFormController.class);

	/**
	 * Default Constructor
	 */
	public ExcelReportRendererFormController() { }

	/**
	 *  prepares a new form for the Excel report renderers
	 */
	@RequestMapping("/module/reporting/reports/renderers/excelReportRenderer")
	public void excelReportRenderer(ModelMap model, 
								@RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid, 
								@RequestParam(required=false, value="reportDefinitionUuid") String reportDefinitionUuid,
								@RequestParam(required=true,  value="type") Class<? extends ReportRenderer> type,
								@RequestParam(required=false, value="successUrl") String successUrl) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException, InstantiationException, ClassNotFoundException {
		
		ReportService rs = Context.getService(ReportService.class); 
		ReportDesign design = null;
		if (StringUtils.isNotEmpty(reportDesignUuid)) {
			design = rs.getReportDesignByUuid(reportDesignUuid);
		}
		else {
			design = new ReportDesign();
			design.setRendererType(type);
			if (StringUtils.isNotEmpty(reportDefinitionUuid)) {
				design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
			}
		}
				
		String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (StringUtils.isEmpty(successUrl)) {
    		successUrl = "/module/reporting/reports/manageReportDesigns.form";
    	}
    	else if (successUrl.startsWith(pathToRemove)) {
    		successUrl = successUrl.substring(pathToRemove.length());
    	}
		model.addAttribute("design", design );
		model.addAttribute("successUrl", successUrl);
		model.addAttribute("cancelUrl",  successUrl);

	}

	/**
	 * Saves report design
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/module/reporting/reports/renderers/saveExcelReportRenderer")
	public String saveExcelReportRenderer(ModelMap model, HttpServletRequest request,
					@RequestParam(required=false, value="uuid") String uuid,
					@RequestParam(required=true,  value="name") String name,
					@RequestParam(required=false, value="description") String description,
					@RequestParam(required=true,  value="reportDefinition") String reportDefinitionUuid,
					@RequestParam(required=true,  value="rendererType") Class<? extends ReportRenderer> rendererType,
					@RequestParam(required=true,  value="successUrl") String successUrl
	){
		ReportService rs = Context.getService(ReportService.class);
		ReportDesign design = null;

		if (StringUtils.isNotEmpty(uuid)) {
			design = rs.getReportDesignByUuid(uuid);
		}
		
		if (design == null) {
			design = new ReportDesign();
		}
			
		design.setName(name);
		design.setDescription(description);
		design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
		design.setRendererType(rendererType);
		
		MultipartHttpServletRequest mpr = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = (Map<String, MultipartFile>)mpr.getFileMap();
    	Set<String> foundResources = new HashSet<String>();
    	for (String paramName : files.keySet()) {
    		try {
	    		String[] split = paramName.split("\\.", 2);
	    		if (split.length == 2 && split[0].equals("resources")) {
	    			ReportDesignResource resource = null;
	    			if (split[1].startsWith("new")) {
	    				resource = new ReportDesignResource();
	    			}
	    			else {
	    				foundResources.add(split[1]);
	    				resource = design.getResourceByUuid(split[1]);
	    			}
	    			MultipartFile file = files.get(paramName);
	    			String fileName = file.getOriginalFilename();
	    			if (StringUtils.isNotEmpty(fileName)) {
		    			int index = fileName.lastIndexOf(".");
		    			resource.setReportDesign(design);
		    			resource.setContentType(file.getContentType());
		    			resource.setName(fileName.substring(0, index));
		    			resource.setExtension(fileName.substring(index+1));
		    			resource.setContents(file.getBytes());
		    			design.getResources().add(resource);
	    			}
	    		}
    		}
    		catch (Exception e) {
    			throw new RuntimeException("Unable to add resource to design.", e);
    		}
    	}

    	for (Iterator<ReportDesignResource> i = design.getResources().iterator(); i.hasNext();) {
    		ReportDesignResource r = i.next();
    		if (r.getId() != null && !foundResources.contains(r.getUuid())) {
    			i.remove();
    		}
    	}
    	
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
