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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * This Controller loads a Parameterizable class and object given the passed parameters
 */
public class ParameterizablePortletController extends ReportingPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings("unchecked")
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		super.populateModel(request, model);

		String type = (String)model.get("type");
		String uuid = (String)model.get("uuid");
		
		// Get Parameterizable class from the passed type
		Class<? extends Parameterizable> typeClass = null;
		try {
			typeClass = (Class<? extends Parameterizable>)Context.loadClass(type);
			model.put("typeClass", typeClass);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Type " + type + " cannot be loaded", e);
		}

    	if (StringUtils.isNotEmpty(uuid)) {
    		model.put("obj", ParameterizableUtil.getParameterizable(uuid, typeClass));
    	}
	}
}
