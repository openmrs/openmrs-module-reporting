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
