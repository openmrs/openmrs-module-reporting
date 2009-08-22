package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.util.ParameterizableUtil;
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
    public String saveMappedProperty(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
    		@RequestParam(required=true, value="currentName") String currentName,
            @RequestParam(required=true, value="newName") String newName,
            @RequestParam(required=true, value="clazz") Class<?> clazz,
            @RequestParam(required=false, value="label") String label,
            @RequestParam(required=false, value="collectionType") Class<? extends Collection<?>> collectionType) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);;
    	Parameter p = new Parameter(newName, label, clazz);
    	
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
    public String removeParameter(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
            @RequestParam(required=true, value="name") String name,
            @RequestParam(required=true, value="returnUrl") String returnUrl) {
    	
       	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
       	parent.removeParameter(name);
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	return "redirect:"+returnUrl;
    }
}
