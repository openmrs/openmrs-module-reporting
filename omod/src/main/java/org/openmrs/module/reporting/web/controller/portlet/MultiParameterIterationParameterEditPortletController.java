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
package org.openmrs.module.reporting.web.controller.portlet;

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.util.OpenmrsClassLoader;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public class MultiParameterIterationParameterEditPortletController extends ReportingPortletController {
	
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		model.put("portletUUID", UUID.randomUUID().toString().replace("-", ""));

		int iteration = Integer.parseInt(request.getParameter("iteration"));
		String paramName = request.getParameter("paramName");

		String dsdUuid = (String)model.get("dsdUuid");	
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		MultiParameterDataSetDefinition pdsd = (MultiParameterDataSetDefinition)dsd;

		model.put("dsdUuid", pdsd.getUuid());
		model.put("dsd", pdsd);

		model.put("iteration", iteration);

		Object paramValue = pdsd.getIterations().get(iteration).get(paramName);
		model.put("param", pdsd.getBaseDefinition().getParameter(paramName));
		model.put("paramValue", paramValue);
		model.put("allowedParams", ParameterizableUtil.getAllowedMappings(pdsd, pdsd.getBaseDefinition()).get(paramName));

		Parameter mappedParam = null;
		if (pdsd.getIterations().get(iteration).get(pdsd.getBaseDefinition().getParameter(paramName)) instanceof Parameter) {
			mappedParam = (Parameter) pdsd.getIterations().get(iteration).get(pdsd.getBaseDefinition().getParameter(paramName));
		}
		model.put("mappedParam", mappedParam);
		model.putAll(ParameterizableUtil.getCategorizedMappings(dsd, pdsd.getBaseDefinition(), pdsd.getIterations().get(iteration)));
	}
}
