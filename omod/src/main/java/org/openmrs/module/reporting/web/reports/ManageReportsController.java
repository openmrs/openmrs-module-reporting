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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.controller.mapping.renderers.RendererMappingHandler;
import org.openmrs.util.HandlerUtil;
import org.openmrs.api.APIException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageReportsController {

	protected static Log log = LogFactory.getLog(ManageReportsController.class);
	
	/**
	 * Default Constructor
	 */
	public ManageReportsController() { }
	
	
    /**
     * Provide all reports, optionally including those that are retired, to a page 
     * that lists them and provides options for working with these reports.
     */
    @RequestMapping("/module/reporting/reports/manageReports")
    public ModelMap manageReports(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	// Get list of existing reports
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<ReportDefinition> reportDefinitions = Context.getService(ReportDefinitionService.class).getAllDefinitions(includeRet);
    	model.addAttribute("reportDefinitions", reportDefinitions);
    	
    	// Get possible new reports to create
    	Map<String, String> types = new LinkedHashMap<String, String>();
    	types.put("Period Indicator Report", "periodIndicatorReport.form");
    	types.put("Row-Per-Patient Report", "logicReport.form");
    	types.put("Custom Report (Advanced)", "reportEditor.form?type=" + ReportDefinition.class.getName());
    	model.addAttribute("createLinks", types);
    	
        return model;
    }
    
    /**
     * Provide all reports designs, optionally including those that are retired, to a page 
     * that lists them and provides options for working with them.
     */
    @RequestMapping("/module/reporting/reports/manageReportDesigns")
    public ModelMap manageReportDesigns(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	// Get list of existing reports
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<ReportDesign> reportDesigns = Context.getService(ReportService.class).getAllReportDesigns(includeRet);
    	ReportDesign reportDesign = new ReportDesign();
    	model.addAttribute("reportDesigns", reportDesigns);
    	model.addAttribute("reportDesign", reportDesign);
    	
        return model;
    }
    
    /**
     *  
     * to edit a reportDefinition based on its rendererType.
     */
	@RequestMapping("/module/reporting/reports/renderers/editReportDesign")
    public String editReportDesign(ModelMap model, 
    		@RequestParam(required=true, value="type") Class<? extends ReportRenderer> type,
    		@RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid,
    		@RequestParam(required=false, value="reportDefinitionUuid") String reportDefinitionUuid,
    		@RequestParam(required=false, value="returnUrl") String returnUrl) {
    
		if ( !ObjectUtil.isNull(type) ) {
			try {
				RendererMappingHandler handler = HandlerUtil.getPreferredHandler(RendererMappingHandler.class, type);
				String redirectParameters = ObjectUtil.isNull(reportDefinitionUuid) ? "" : "&reportDefinitionUuid=" + reportDefinitionUuid;
				redirectParameters += ObjectUtil.isNull(returnUrl) ? "" : "&successUrl=" + returnUrl;
				if (ObjectUtil.isNull(reportDesignUuid)) {
					return "redirect:" + handler.getCreateUrl( type ) + redirectParameters;
				}
				else {
					ReportService rs = Context.getService(ReportService.class);
					ReportDesign design = rs.getReportDesignByUuid(reportDesignUuid);
					return "redirect:" + handler.getEditUrl( design ) + redirectParameters;
				}
			} catch ( APIException e ) {
				log.error( "No handler found" );
			}			
		}
		return "redirect:/module/reporting/reports/renderers/defaultReportDesignEditor.htm?parameters=" 
				+ ( ObjectUtil.isNull( type ) || !ObjectUtil.isNull(reportDesignUuid) ? "" : "type=" + type.getName() + "|" )
				+ ( ObjectUtil.isNull(reportDesignUuid) ? "" : "reportDesignUuid=" + reportDesignUuid + "|" )  
				+ ( ObjectUtil.isNull(reportDefinitionUuid) ? "" : "reportDefinitionUuid=" + reportDefinitionUuid  + "|"  )
				+ ( ObjectUtil.isNull(returnUrl) ? "" : "returnUrl=" + returnUrl );
    	
    }
    
    /**
     * Provide all reports processor configurations, to a page that lists them and provides options for working with them.
     */
    @RequestMapping("/module/reporting/reports/manageReportProcessors")
    public void manageReportProcessors(ModelMap model) {
    	List<ReportProcessorConfiguration> configs = Context.getService(ReportService.class).getAllReportProcessorConfigurations(false);
    	model.addAttribute("reportProcessorConfigurations", configs);
    }
    
    /**
     * Provide all reports designs, optionally including those that are retired, to a page 
     * that lists them and provides options for working with them.
     */
    @RequestMapping("/module/reporting/reports/viewReportDesignResource")
    public void viewDesignContent(ModelMap model, 
    									HttpServletResponse response,
    									@RequestParam(required=true, value="designUuid") String designUuid,
    									@RequestParam(required=true, value="resourceUuid") String resourceUuid) {
    	
    	ReportDesign d = Context.getService(ReportService.class).getReportDesignByUuid(designUuid);
    	ReportDesignResource r = d.getResourceByUuid(resourceUuid);
    	
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Disposition", "attachment; filename=" + r.getResourceFilename());
		try {
			response.getOutputStream().write(r.getContents());
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to render contents of file", e);
		}
    }
}
