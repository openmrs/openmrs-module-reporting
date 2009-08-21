package org.openmrs.module.reporting.web.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.module.util.ReflectionUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MappedPropertyFormController {

	protected static Log log = LogFactory.getLog(MappedPropertyFormController.class);
	
	/**
	 * Default Constructor
	 */
	public MappedPropertyFormController() { }
    
    /**
     * Saves mapped parameters
     */
    @RequestMapping("/module/reporting/reports/saveMappedParameters")
    @SuppressWarnings("unchecked")
    public String saveMappedParameters(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> type,
    		@RequestParam(required=true, value="uuid") String uuid,
            @RequestParam(required=true, value="property") String property,
            @RequestParam(required=false, value="collectionKey") String collectionKey,
            @RequestParam(required=false, value="mappedUuid") String childUuid) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(uuid, type);
    	Field f = ReflectionUtil.getField(type, property);
    	
    	if (StringUtils.isEmpty(childUuid)) {
    		ReflectionUtil.setPropertyValue(parent, f, null);
    	}
    	else {
        	Type[] genericTypes = ReflectionUtil.getGenericTypes(f);
        	Class<? extends Parameterizable> childType = (Class<? extends Parameterizable>) genericTypes[0];
    		Parameterizable child = ParameterizableUtil.getParameterizable(childUuid, childType);
    		
    		Mapped m = new Mapped();
    		m.setParameterizable(child);
    		
        	for (Parameter p : child.getParameters()) {
        		String valueType = request.getParameterValues("valueType_"+p.getName())[0];
        		String[] value = request.getParameterValues(valueType+"Value_"+p.getName());
        		if (value != null && value.length > 0) {
    	    		String paramValue = null;
    	    		if (StringUtils.isEmpty(valueType) || valueType.equals("fixed")) {
    	    			paramValue = OpenmrsUtil.join(Arrays.asList(value), ",");
    	    		}
    	    		else {
    	    			paramValue = "${"+value[0]+"}";
    	    		}
    	    		m.addParameterMapping(p.getName(), paramValue);
        		}
        	}
        	ReflectionUtil.setPropertyValue(parent, f, m);
    	}
    	
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	return "redirect:/module/reporting/closeWindow.htm";
    }
}
