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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

@Controller
public class DelimitedTextReportRendererFormController {

	protected static Log log = LogFactory.getLog(DelimitedTextReportRendererFormController.class);
	
	/**
	 * Default Constructor
	 */
	public DelimitedTextReportRendererFormController() { }
	
    /**
     *  
     * 
     */
    @RequestMapping("/module/reporting/reports/renderers/delimitedTextReportRenderer")
    public ModelMap delimitedTextReportRenderer(ModelMap model, 
    												@RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid, 
    												@RequestParam(required=false, value="reportDefinitionUuid") String reportDefinitionUuid) {
    	
    	ReportService rs = Context.getService(ReportService.class);
    	ReportDesign design = null;
		if (StringUtils.isNotEmpty(reportDesignUuid)) {
			design = rs.getReportDesignByUuid(reportDesignUuid);
		}
		else {
			design = new ReportDesign();
			if (StringUtils.isNotEmpty(reportDefinitionUuid)) {
				design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinitionUuid));
			}
			
		}
		model.addAttribute( "design", design );
		return model;
    }

        /**
     * Saves report design
     */
    @RequestMapping("/module/reporting/reports/renderers/saveDelimitedTextReportDesign")
    @SuppressWarnings("unchecked")
    public String saveDelimitedTextReportDesign(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=true, value="name") String name,
    		@RequestParam(required=false, value="description") String description,
    		@RequestParam(required=true, value="reportDefinition") String reportDefinitionUuid,
    		@RequestParam(required=true, value="rendererType") Class<? extends ReportRenderer> rendererType,
            @RequestParam(required=false, value="filenameExtension") String filenameExtension,
            @RequestParam(required=false, value="afterColumnDelimiter") String afterColumnDelimiter
    ) {

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
    	return "redirect:/module/reporting/reports/manageReportDesigns.form";
    }
}