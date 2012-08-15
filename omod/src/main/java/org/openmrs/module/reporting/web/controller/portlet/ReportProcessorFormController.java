package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.handler.WidgetHandler;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration.ProcessorMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.HandlerUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportProcessorFormController {

	protected static Log log = LogFactory.getLog(ReportProcessorFormController.class);
	
	/**
	 * Default Constructor
	 */
	public ReportProcessorFormController() { }
    
    /**
     * Saves report design
     */
    @RequestMapping("/module/reporting/reports/saveReportProcessor")
    public String saveReportProcessor(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=true, value="name") String name,
    		@RequestParam(required=false, value="description") String description,
    		@RequestParam(required=true, value="processorType") String processorType,
    		@RequestParam(required=true, value="processorMode") ProcessorMode processorMode,
    		@RequestParam(required=false, value="configuration") String configuration,
    		@RequestParam(required=false, value="runOnSuccess") String runOnSuccess,
    		@RequestParam(required=false, value="runOnError") String runOnError,
    		@RequestParam(required=false, value="reportDesignUuid") String reportDesignUuid
    ) {
    	
		ReportService rs = Context.getService(ReportService.class);
		
		ReportProcessorConfiguration c = rs.getReportProcessorConfigurationByUuid(uuid);
		if (c == null) {
			c = new ReportProcessorConfiguration();
		}

		c.setName(name);
		c.setDescription(description);
    	c.setProcessorType(processorType);
    	c.setProcessorMode(processorMode);
    	c.setRunOnSuccess("t".equals(runOnSuccess));
    	c.setRunOnError("t".equals(runOnError));
    	c.setReportDesign(rs.getReportDesignByUuid(reportDesignUuid));
    	
    	WidgetHandler propHandler = HandlerUtil.getPreferredHandler(WidgetHandler.class, Properties.class);
    	Properties props = (Properties)propHandler.parse(configuration, Properties.class);
    	c.setConfiguration(props);

    	c = rs.saveReportProcessorConfiguration(c);
    	return "redirect:/module/reporting/closeWindow.htm";
    }
    
    /**
     * Delete report design
     */
    @RequestMapping("/module/reporting/reports/deleteReportProcessor")
    public String deleteReportDesign(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="uuid") String uuid,
            @RequestParam(required=false, value="returnUrl") String returnUrl) {
    	
    	ReportService rs = Context.getService(ReportService.class);
    	ReportProcessorConfiguration c = rs.getReportProcessorConfigurationByUuid(uuid);
    	rs.purgeReportProcessorConfiguration(c);
    	
    	String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (StringUtils.isEmpty(returnUrl)) {
    		returnUrl = "/module/reporting/reports/manageReportProcessors.form";
    	}
    	else if (returnUrl.startsWith(pathToRemove)) {
    		returnUrl = returnUrl.substring(pathToRemove.length());
    	}

    	return "redirect:"+returnUrl;
    }
}
