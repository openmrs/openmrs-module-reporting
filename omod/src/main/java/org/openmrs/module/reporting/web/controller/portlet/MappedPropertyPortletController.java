/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.controller.portlet;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * This Controller loads a Mapped property given the passed parameters
 */
public class MappedPropertyPortletController extends ParameterizablePortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		super.populateModel(request, model);

		String uuid = (String)model.get("uuid");
		String property = (String)model.get("property");
		String currentKey = (String)model.get("currentKey");
		String mappedUuid = (String) model.get("mappedUuid");
		
		Class<?> typeClass = (Class<?>)model.get("typeClass");
		Parameterizable obj = (Parameterizable)model.get("obj");
		
		// Get generic type of the Mapped property, if specified
		Field f = ReflectionUtil.getField(typeClass, property);
		if (f != null) {
			Class<?> fieldType = ReflectionUtil.getFieldType(f);
			if (List.class.isAssignableFrom(fieldType)) {
				model.put("multiType", "list");
			}
			else if (Map.class.isAssignableFrom(fieldType)) {
				model.put("multiType", "map");
				if (obj != null) {
					Object propertyValue = ReflectionUtil.getPropertyValue(obj, property);
					if (propertyValue != null && propertyValue instanceof Map) {
						model.put("existingKeys", ((Map<?, ?>)propertyValue).keySet());
					}
				}
			}
		}
		
		Class<? extends Parameterizable> mappedType = null;
		if (StringUtils.isNotEmpty(property)) {
			mappedType = ParameterizableUtil.getMappedType(typeClass, property);
		}
		model.put("mappedType", mappedType);

		if (StringUtils.isNotEmpty(uuid)) {

	    	// Retrieve the child property, or null
	       	Parameterizable mappedObj = null;
	       	Map<String, Object> mappings = new HashMap<String, Object>();
	       	
	       	if (StringUtils.isEmpty(mappedUuid)) {
	       		Mapped<Parameterizable> mapped = ParameterizableUtil.getMappedProperty(obj, property, currentKey);
	       		if (mapped != null) {
	       			model.put("mapped", mapped);
	       			mappedObj = mapped.getParameterizable();
	       			mappings = mapped.getParameterMappings();
	       		}
	       	}
	       	else if (mappedUuid != null) {
	       		mappedObj = ParameterizableUtil.getParameterizable(mappedUuid, mappedType);
	       	}
	       	model.put("mappedObj", mappedObj);
	       	model.put("mappings", mappings);
	       	
	       	Map<String, String> mappedParams = new HashMap<String, String>();
	       	Map<String, String> complexParams = new HashMap<String, String>();
	       	Map<String, Object> fixedParams = new HashMap<String, Object>();
	       	Map<String, Map<String, String>> allowedParams = new HashMap<String, Map<String, String>>();
       	
	       	if (mappedObj != null) {
				for (Parameter p : mappedObj.getParameters()) {
					Object mappedObjVal = mappings.get(p.getName());
					
					Map<String, String> allowed  = new HashMap<String, String>();
					for (Parameter parentParam : obj.getParameters()) {
						if (p.getType() == parentParam.getType()) {
							allowed.put(parentParam.getName(), parentParam.getLabelOrName());
						}
					}
					allowedParams.put(p.getName(), allowed);
					
					if (mappedObjVal != null && mappedObjVal instanceof String) {
						String mappedVal = (String) mappedObjVal;
						if (EvaluationUtil.isExpression(mappedVal)) {
							mappedVal = EvaluationUtil.stripExpression(mappedVal);
							if (obj.getParameter(mappedVal) != null) {
								mappedParams.put(p.getName(), mappedVal);
							}
							else {
								complexParams.put(p.getName(), mappedVal);
							}
						}
					}
					else {
						fixedParams.put(p.getName(), mappedObjVal);
					}
				}
	       	}
			model.put("allowedParams", allowedParams);
			model.put("mappedParams", mappedParams);
			model.put("complexParams", complexParams);
			model.put("fixedParams", fixedParams);
		}
		
		// Handle customizations for look and feel
		
		model.put("keyLabel", ObjectUtil.nvlStr(model.get("keyLabel"), "Key"));
		model.put("typeLabel", ObjectUtil.nvlStr(model.get("typeLabel"), mappedType.getSimpleName()));
	}
}
