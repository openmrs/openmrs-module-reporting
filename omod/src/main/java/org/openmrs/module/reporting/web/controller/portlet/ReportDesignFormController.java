package org.openmrs.module.reporting.web.controller.portlet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.handler.WidgetHandler;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.HandlerUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class ReportDesignFormController {

	protected static Log log = LogFactory.getLog(ParameterPortletFormController.class);
	
	/**
	 * Default Constructor
	 */
	public ReportDesignFormController() { }
    
    /**
     * Saves report design
     */
    @RequestMapping("/module/reporting/reports/saveReportDesign")
    @SuppressWarnings("unchecked")
    public String saveReportDesign(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=false, value="uuid") String uuid,
    		@RequestParam(required=true, value="name") String name,
    		@RequestParam(required=false, value="description") String description,
    		@RequestParam(required=true, value="reportDefinition") String reportDefinitionUuid,
    		@RequestParam(required=true, value="rendererType") Class<? extends ReportRenderer> rendererType,
    		@RequestParam(required=false, value="properties") String properties,
    		@RequestParam(required=true, value="successUrl") String successUrl
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
    	
    	WidgetHandler propHandler = HandlerUtil.getPreferredHandler(WidgetHandler.class, Properties.class);
    	Properties props = (Properties)propHandler.parse(properties, Properties.class);
    	design.setProperties(props);
    	
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
    
    /**
     * Delete report design
     */
    @RequestMapping("/module/reporting/reports/deleteReportDesign")
    public String deleteReportDesign(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="uuid") String uuid,
            @RequestParam(required=false, value="returnUrl") String returnUrl) {
    	
    	ReportService rs = Context.getService(ReportService.class);
    	ReportDesign design = rs.getReportDesignByUuid(uuid);
    	rs.purgeReportDesign(design);
    	
    	String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (StringUtils.isEmpty(returnUrl)) {
    		returnUrl = "/module/reporting/reports/manageReportDesigns.form";
    	}
    	else if (returnUrl.startsWith(pathToRemove)) {
    		returnUrl = returnUrl.substring(pathToRemove.length());
    	}

    	return "redirect:"+returnUrl;
    }
}
