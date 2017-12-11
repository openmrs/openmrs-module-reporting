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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.controller.mapping.renderers.RendererMappingHandler;
import org.openmrs.util.HandlerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	try {
            Context c = new Context(); 
            Class ls = c.loadClass("LogicService");
            if ("LoigcService".equals(ls.getName())){
                //types.put("Row-Per-Patient Report", "logicReport.form");   
                System.out.println("logc report service here");
            }
            else {
                System.out.println("*******LogicService is installed*******");
            }

        } catch (ClassNotFoundException e){
            System.err.println(e.getMessage());
        }
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
		StringBuilder url = new StringBuilder("/module/reporting/reports/renderers/defaultReportDesignEditor.form?type=" + type.getName());
		if (ObjectUtil.notNull(reportDesignUuid)) {
		    url.append("&reportDesignUuid=" + reportDesignUuid);
        }
        if (ObjectUtil.notNull(reportDefinitionUuid)) {
            url.append("&reportDefinitionUuid=" + reportDefinitionUuid);
        }
        if (ObjectUtil.notNull(returnUrl)) {
            url.append("&returnUrl=" + returnUrl);
        }
		return "redirect:"+url.toString();
    }

    /**
     *
     * Edit page for a report design that does not have a custom editor
     */
    @RequestMapping("/module/reporting/reports/renderers/defaultReportDesignEditor")
    public void defaultReportDesignRenderer(ModelMap model,
                                   @RequestParam(required=true, value="type") Class<? extends ReportRenderer> type,
                                   @RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid,
                                   @RequestParam(required=false, value="reportDefinitionUuid") String reportDefinitionUuid,
                                   @RequestParam(required=false, value="returnUrl") String returnUrl) {

        StringBuilder parameters = new StringBuilder();
        parameters.append("type="+type.getName());
        if (ObjectUtil.notNull(reportDesignUuid)) {
            parameters.append("|reportDesignUuid=" + reportDesignUuid);
        }
        if (ObjectUtil.notNull(reportDefinitionUuid)) {
            parameters.append("|reportDefinitionUuid=" + reportDefinitionUuid);
        }
        if (ObjectUtil.notNull(returnUrl)) {
            parameters.append("|returnUrl=" + returnUrl);
        }
        model.addAttribute("parameters", parameters);
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

    @RequestMapping("/module/reporting/reports/purgeReport")
    public String purgeReportDefinition(@RequestParam(required=false, value="uuid") String uuid) {
        ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
        rds.purgeDefinition(rds.getDefinitionByUuid(uuid));
        return "redirect:/module/reporting/reports/manageReports.form";
    }

    /**
     * Renders a report to the response output stream, given a report definition, rendering mode, and optional patient id
     * @param patientIdOrUuid the id or uuid of patient whose summary you wish to view
     */
    @RequestMapping("/module/reporting/reports/renderReport")
    public void renderReport(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("reportDefinition") String reportDefinitionUuid,
                             @RequestParam("renderingMode") String renderingModeDescriptor,
                             @RequestParam(value="patient", required=false) String patientIdOrUuid,
                             @RequestParam(value="download", required=false) boolean download) throws IOException {
        try {
            ReportDefinition rd = getReportDefinitionService().getDefinitionByUuid(reportDefinitionUuid);
            if (rd == null) {
                throw new IllegalArgumentException("Unable to find report with passed uuid = " + reportDefinitionUuid);
            }
            RenderingMode renderingMode = new RenderingMode(renderingModeDescriptor);
            if (!renderingMode.getRenderer().canRender(rd)) {
                throw new IllegalArgumentException("Rendering mode chosen cannot render passed report definition");
            }

            EvaluationContext context = new EvaluationContext();

            if (StringUtils.isNotBlank(patientIdOrUuid)) {
                Cohort c = new Cohort();
                Patient p = Context.getPatientService().getPatientByUuid(patientIdOrUuid);
                if (p != null) {
                    c.addMember(p.getPatientId());
                }
                else {
                    c.addMember(Integer.parseInt(patientIdOrUuid));
                }
                context.setBaseCohort(c);
            }

            // If the report takes in additional parameters, try to retrieve these from the request
            for (Parameter p : rd.getParameters()) {
                String[] parameterValues = request.getParameterValues(p.getName());
                if (parameterValues != null && parameterValues.length > 0) {
                    Object value = null;
                    if (parameterValues.length == 1) {
                        value = WidgetUtil.parseInput(parameterValues[0], p.getType(), p.getCollectionType());
                    }
                    else {
                        List l = new ArrayList();
                        for (String v : parameterValues) {
                            l.add(WidgetUtil.parseInput(parameterValues[0], p.getType()));
                        }
                        value = l;
                    }
                    context.addParameterValue(p.getName(), value);
                }
            }

            ReportData reportData = getReportDefinitionService().evaluate(rd, context);


            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setReportDefinition(new Mapped<ReportDefinition>(rd, context.getParameterValues()));
            reportRequest.setRenderingMode(renderingMode);
            String contentType = renderingMode.getRenderer().getRenderedContentType(reportRequest);
            String fileName = renderingMode.getRenderer().getFilename(reportRequest);

            response.setHeader("Content-Type", contentType);

            if (download) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            }

            renderingMode.getRenderer().render(reportData, renderingMode.getArgument(), response.getOutputStream());
        }
        catch (Exception e) {
            e.printStackTrace(response.getWriter());
        }
    }

    private ReportDefinitionService getReportDefinitionService() {
        return Context.getService(ReportDefinitionService.class);
    }
}
