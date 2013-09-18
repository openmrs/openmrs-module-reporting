package org.openmrs.module.reporting.web.controller.portlet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ScriptedCompositionPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MappedPropertyPortletFormController {

	protected static Log log = LogFactory.getLog(MappedPropertyPortletFormController.class);
	
	/**
	 * Default Constructor
	 */
	public MappedPropertyPortletFormController() { }
    
    /**
     * Saves mapped parameters
     */
    @RequestMapping("/module/reporting/reports/saveMappedProperty")
    @SuppressWarnings("unchecked")
    public String saveMappedProperty(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> type,
    		@RequestParam(required=true, value="uuid") String uuid,
            @RequestParam(required=true, value="property") String property,
            @RequestParam(required=false, value="currentKey") String currentKey,
            @RequestParam(required=false, value="newKey") String newKey,
            @RequestParam(required=false, value="mappedUuid") String mappedUuid) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(uuid, type);
    	Field f = ReflectionUtil.getField(type, property);
    	Class<?> fieldType = ReflectionUtil.getFieldType(f);
    	
		Class<? extends Parameterizable> mappedType = null;
		if (StringUtils.isNotEmpty(property)) {
			mappedType = ParameterizableUtil.getMappedType(type, property);
		}
		
		Mapped m = null;
		Object previousValue = ReflectionUtil.getPropertyValue(parent, property);
		
		if (StringUtils.isNotEmpty(mappedUuid)) {
			Parameterizable valToSet = ParameterizableUtil.getParameterizable(mappedUuid, mappedType);
			
			// TODO We need to find a more generic way of converting data definitions.
			//  If the definition being mapped is of unsupported type, the code should
			//  be able to find a proper adapter class and convert the definition to 
			//  the supported type.
			
			if(parent instanceof ScriptedCompositionPatientDataDefinition && valToSet instanceof PersonDataDefinition)
    		valToSet = new PersonToPatientDataDefinition((PersonDataDefinition) valToSet);
			
			m = new Mapped();
    		m.setParameterizable(valToSet);
    		
        	for (Parameter p : valToSet.getParameters()) {
        		String valueType = request.getParameterValues("valueType_"+p.getName())[0];
        		String[] value = request.getParameterValues(valueType+"Value_"+p.getName());
        		if (value != null && value.length > 0) {
    	    		Object paramValue = null;
    	    		if (StringUtils.isEmpty(valueType) || valueType.equals("fixed")) {
    	    			String fixedValueString = OpenmrsUtil.join(Arrays.asList(value), ",");
    	    			paramValue = WidgetUtil.parseInput(fixedValueString, p.getType());
    	    		}
    	    		else {
    	    			paramValue = "${"+value[0]+"}";
    	    		}
    	    		if (paramValue != null) {
    	    			m.addParameterMapping(p.getName(), paramValue);
    	    		}
        		}
        	}
		}
        	
        if (previousValue != null || m != null) {
        		
    		if (List.class.isAssignableFrom(fieldType)) {
    			List newValue = null;
    			if (previousValue == null) {
    				newValue = new ArrayList();
    				newValue.add(m);
    			}
    			else if (m != null) {
    				newValue = (List)previousValue;
    				if (StringUtils.isEmpty(newKey)) {
    					newValue.add(m);
    				}
    				else {
    					int listIndex = Integer.parseInt(newKey);
    					newValue.set(listIndex, m);
    				}
    			}
    			ReflectionUtil.setPropertyValue(parent, f, newValue);
    		}
    		else if (Map.class.isAssignableFrom(fieldType)) {
    			if (m != null) {
    				Map newValue = (previousValue == null ? new HashMap() : (Map)previousValue);
    				newValue.put(newKey, m);
        			if (!newKey.equals(currentKey) && currentKey != null) {
        				newValue.remove(currentKey);
        			}
       				ReflectionUtil.setPropertyValue(parent, f, newValue);
    			}
    		}
    		else if (Mapped.class.isAssignableFrom(fieldType)) {
    			ReflectionUtil.setPropertyValue(parent, f, m);
    		}
    		else {
    			throw new IllegalArgumentException("Cannot set property of type: " + fieldType + " to " + m);
    		}
    	}
    	
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	return "redirect:/module/reporting/closeWindow.htm";
    }
    
    /**
     * Remove mapped property
     */
    @RequestMapping("/module/reporting/reports/removeMappedProperty")
    @SuppressWarnings("unchecked")
    public String removeMappedProperty(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> type,
    		@RequestParam(required=true, value="uuid") String uuid,
            @RequestParam(required=true, value="property") String property,
            @RequestParam(required=true, value="currentKey") String currentKey,
            @RequestParam(required=true, value="returnUrl") String returnUrl) {
    	
       	Parameterizable parent = ParameterizableUtil.getParameterizable(uuid, type);
    	Field f = ReflectionUtil.getField(type, property);
    	Class<?> fieldType = ReflectionUtil.getFieldType(f);	
		Object previousValue = ReflectionUtil.getPropertyValue(parent, property);
		
        if (List.class.isAssignableFrom(fieldType)) {
        	List v = (List)previousValue;
			int listIndex = Integer.parseInt(currentKey);
			v.remove(listIndex);
        }
		else if (Map.class.isAssignableFrom(fieldType)) {
			Map v = (Map)previousValue;
			v.remove(currentKey);
		}
		else {
        	throw new IllegalArgumentException("Cannot remove property in fieldType: " + fieldType + " with key " + currentKey);
    	}
        
        ReflectionUtil.setPropertyValue(parent, f, previousValue);
    	ParameterizableUtil.saveParameterizable(parent);
    	
    	String pathToRemove = "/" + WebConstants.WEBAPP_NAME;
    	if (returnUrl.startsWith(pathToRemove)) {
    		returnUrl = returnUrl.substring(pathToRemove.length());
    	}
    	
    	return "redirect:"+returnUrl;
    }
}
