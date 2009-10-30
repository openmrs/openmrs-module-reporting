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
package org.openmrs.module.reporting.web.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.util.HandlerUtil;

/**
 * Utility library for Widgets
 */
public class WidgetUtil {

	/**
	 * Return the object value of the passed type, given it's String representation
	 * @param input the String representation
	 * @param type the type to return
	 * @return the Object of the passed type, given the passed input
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T parseInput(String input, Class<? extends T> type) {
		try {
			WidgetHandler handler = HandlerUtil.getPreferredHandler(WidgetHandler.class, type);
			return (T) handler.parse(input, type);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to convert input <" + input + "> to type " + type.getSimpleName(), e);
		}
	}
	
	/**
	 * Return the object value of the passed type, given it's String representation
	 * @param input the String representation
	 * @param type the type to return
	 * @return the Object of the passed type, given the passed input
	 */
	@SuppressWarnings("unchecked")
	public static Object getFromRequest(String paramName, Parameterizable p, HttpServletRequest request) {
		
		Object ret = null;
		
		if (p != null && p.getParameters() != null) { 
			
    		Parameter param = p.getParameter(paramName);
    		Class<? extends Collection> collectionType = param.getCollectionType();
    		Class<?> fieldType = param.getType();
    		
			if (collectionType != null) {
				String[] paramVals = request.getParameterValues(paramName);				
				if (paramVals != null) {
					Collection defaultValue = Set.class.isAssignableFrom(collectionType) ? new HashSet() : new ArrayList();
					for (String val : paramVals) {
						if (StringUtils.isNotEmpty(val)) {
							WidgetHandler h = HandlerUtil.getPreferredHandler(WidgetHandler.class, fieldType);
							defaultValue.add(h.parse(val, fieldType));
						}
					}
					ret = defaultValue;
				}
			}
			else {
				String paramVal = request.getParameter(paramName);
				if (StringUtils.isNotEmpty(paramVal)) {
					WidgetHandler h = HandlerUtil.getPreferredHandler(WidgetHandler.class, fieldType);
					ret = h.parse(paramVal, fieldType);
				}
			}
		}
		return ret;
	}
}