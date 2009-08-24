package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ParameterPortletFormController {

	protected static Log log = LogFactory.getLog(ParameterPortletFormController.class);
	
	/**
	 * Default Constructor
	 */
	public ParameterPortletFormController() { }
    
    /**
     * Saves mapped parameters
     */
    @RequestMapping("/module/reporting/parameters/saveParameter")
    public String saveParameter(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="uuid") String parentUuid,
    		@RequestParam(required=true, value="currentName") String currentName,
            @RequestParam(required=true, value="newName") String newName,
            @RequestParam(required=true, value="parameterType") Class<?> parameterType,
            @RequestParam(required=false, value="label") String label,
            @RequestParam(required=false, value="collectionType") Class<? extends Collection<?>> collectionType) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
    	Parameter p = new Parameter(newName, label, parameterType, collectionType, null);
    	
    	if (StringUtils.hasText(currentName)) {
    		int index = parent.getParameters().indexOf(parent.getParameter(currentName));
    		parent.getParameters().set(index, p);
    	}
    	else {
    		parent.addParameter(p);
    	}
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	return "redirect:/module/reporting/closeWindow.htm";
    }
    
    /**
     * Remove mapped property
     */
    @RequestMapping("/module/reporting/parameters/deleteParameter")
    public String deleteParameter(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="uuid") String parentUuid,
            @RequestParam(required=true, value="name") String name,
            @RequestParam(required=true, value="returnUrl") String returnUrl) {
    	
       	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
       	parent.removeParameter(name);
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (returnUrl.startsWith(pathToRemove)) {
    		returnUrl = returnUrl.substring(pathToRemove.length());
    	}
    	
    	return "redirect:"+returnUrl;
    }
}
