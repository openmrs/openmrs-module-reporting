package org.openmrs.module.reporting.web.reports;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.EvaluationUtil;
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
public class MappedEditor {

	protected static Log log = LogFactory.getLog(MappedEditor.class);
	
	/**
	 * Default Constructor
	 */
	public MappedEditor() { }
    
    /**
     * Retrieves either an existing or new Mapped property to edit
     */
    @RequestMapping("/module/reporting/reports/mappedPropertyEditor")
    @SuppressWarnings("unchecked")
    public ModelMap mapParameters(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
            @RequestParam(required=true, value="mappedProperty") String mappedProperty,
            @RequestParam(required=false, value="collectionKey") String collectionKey,
            @RequestParam(required=false, value="childUuid") String childUuid) {
    	
    	// Retrieve the parent object
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
    	
    	// Retrieve the child property, or null
       	Parameterizable child = null;
       	Map<String, String> mappings = new HashMap<String, String>();
       	Class<? extends Parameterizable> childType = ParameterizableUtil.getMappedType(parentType, mappedProperty);
       	
       	if (StringUtils.isEmpty(childUuid)) {
       		Mapped<Parameterizable> mapped = ParameterizableUtil.getMappedProperty(parent, mappedProperty, collectionKey);
       		if (mapped != null) {
       			child = mapped.getParameterizable();
       			mappings = mapped.getParameterMappings();
       		}
       	}
       	else if (childUuid != null) {
       		child = ParameterizableUtil.getParameterizable(childUuid, childType);
       	}

       	Map<String, String> mappedParams = new HashMap<String, String>();
       	Map<String, String> complexParams = new HashMap<String, String>();
       	Map<String, String> fixedParams = new HashMap<String, String>();
       	Map<String, Set<String>> allowedParams = new HashMap<String, Set<String>>();
       	
       	if (child != null) {
			for (Parameter p : child.getParameters()) {
				String mappedVal = mappings.get(p.getName());
				
				Set<String> allowed  = new HashSet<String>();
				for (Parameter parentParam : parent.getParameters()) {
					if (p.getClazz() == parentParam.getClazz()) {
						allowed.add(parentParam.getName());
					}
				}
				allowedParams.put(p.getName(), allowed);
				
				if (mappedVal != null) {
					if (EvaluationUtil.isExpression(mappedVal)) {
						mappedVal = EvaluationUtil.stripExpression(mappedVal);
						if (parent.getParameter(mappedVal) != null) {
							mappedParams.put(p.getName(), mappedVal);
						}
						else {
							complexParams.put(p.getName(), mappedVal);
						}
					}
					else {
						fixedParams.put(p.getName(), mappedVal);
					}
				}
			}
       	}
		
		// Populate model
		
    	model.put("parentType", parentType);
    	model.put("parentUuid", parentUuid);
    	model.put("parent", parent);
    	
    	model.put("mappedProperty", mappedProperty);
    	model.put("collectionKey", collectionKey);
    	model.put("childType", childType);
       	model.put("child", child);
		
		model.put("allowedParams", allowedParams);
		model.put("mappedParams", mappedParams);
		model.put("complexParams", complexParams);
		model.put("fixedParams", fixedParams);

        return model;
    }
    
    /**
     * Saves mapped parameters
     */
    @RequestMapping("/module/reporting/reports/saveMappedParameters")
    @SuppressWarnings("unchecked")
    public String saveMappedParameters(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
            @RequestParam(required=true, value="mappedProperty") String mappedProperty,
            @RequestParam(required=false, value="collectionKey") String collectionKey,
            @RequestParam(required=false, value="childUuid") String childUuid) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
    	Field f = ReflectionUtil.getField(parentType, mappedProperty);
    	
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
        		String type = request.getParameterValues("valueType_"+p.getName())[0];
        		String[] value = request.getParameterValues(type+"Value_"+p.getName());
        		if (value != null && value.length > 0) {
    	    		String paramValue = null;
    	    		if (StringUtils.isEmpty(type) || type.equals("fixed")) {
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
