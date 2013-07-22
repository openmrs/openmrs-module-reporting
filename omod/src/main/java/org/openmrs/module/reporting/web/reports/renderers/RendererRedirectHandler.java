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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.openmrs.web.WebConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

/**
 * 
 * This class handles which interface should be presented to the user
 * based on the rendererType
 *
 */
@Controller
public class RendererRedirectHandler {

	protected static Log log = LogFactory.getLog(DelimitedTextReportRendererFormController.class);
	
	/**
	 * Default Constructor
	 */
	public RendererRedirectHandler() { }
	

    @RequestMapping("/module/reporting/reports/renderers/rendererRedirectHandler")
    @SuppressWarnings("unchecked")
    public String rendererRedirectHandler(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid,
    		@RequestParam(required=false, value="reportDefinition") String reportDefinitionUuid,
    		@RequestParam(required=false, value="rendererType") Class<? extends ReportRenderer> rendererType,
            @RequestParam(required=false, value="returnUrl") String returnUrl
    ) {
        String defaultUrl = "/module/reporting/viewPortlet.htm?id=reportDesignPortlet&url=reportDesignForm&parameters=";
        
        // this is hardcoded for now but the idea is that every renderertype has its own url property 
        // which contains the path to the form that is used to edit it
        if ( !ObjectUtil.isNull( rendererType )  ) {
            return "redirect:/module/reporting/reports/renderers/delimitedTextReportRenderer.form";
        }

        if ( StringUtils.hasText( reportDesignUuid ) ) {
            defaultUrl += "reportDesignUuid=" + reportDesignUuid + "|";
        }

        String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
        if (!StringUtils.hasText(returnUrl)) {
            returnUrl = "/module/reporting/reports/manageReportDesigns.form";
        }
        else if (returnUrl.startsWith(pathToRemove)) {
            returnUrl = returnUrl.substring(pathToRemove.length());
        }

        defaultUrl += "returnUrl=" + returnUrl;

    	return "redirect:"+ defaultUrl;
    }
}
