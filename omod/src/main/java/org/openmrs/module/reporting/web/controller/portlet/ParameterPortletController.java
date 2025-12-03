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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.web.util.ParameterUtil;

/**
 * This Controller loads a Parameter from a Parameterizable given the passed parameters
 */
public class ParameterPortletController extends ParameterizablePortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		super.populateModel(request, model);

		Parameterizable obj = (Parameterizable)model.get("obj");
		String name = (String)model.get("name");
		model.put("parameter", obj.getParameter(name));
		model.put("supportedTypes", ParameterUtil.getSupportedTypes());
		model.put("supportedCollectionTypes", ParameterUtil.getSupportedCollectionTypes());
	}
}
