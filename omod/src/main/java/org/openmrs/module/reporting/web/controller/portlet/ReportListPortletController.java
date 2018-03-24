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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

public class ReportListPortletController extends ReportingPortletController {
	
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		super.populateModel(request, model);
		List<DefinitionSummary> definitionSummaries = Context.getService(ReportDefinitionService.class).getAllDefinitionSummaries(false);
		model.put("definitionSummaries", definitionSummaries);
	}

}
