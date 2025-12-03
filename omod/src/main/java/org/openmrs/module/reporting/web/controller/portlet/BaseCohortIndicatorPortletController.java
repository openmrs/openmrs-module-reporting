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
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;

/**
 * This Controller loads a CohortIndicator class and object given the passed parameters
 */
public class BaseCohortIndicatorPortletController extends ReportingPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		super.populateModel(request, model);

		CohortIndicator indicator = null;
		String uuid = (String)model.get("uuid");
    	if (StringUtils.isNotEmpty(uuid)) {
    		indicator = (CohortIndicator) Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
    	}
    	if (indicator != null) {
    		if (indicator.getAggregator() != null) {
    			try {
    				model.put("aggregatorName", indicator.getAggregator().newInstance().getName());
    			}
    			catch (Exception e) {
    				model.put("aggregatorName", indicator.getAggregator().getSimpleName());
    			}
    		}
    	}
    	else {
    		indicator = new CohortIndicator();
			indicator.addParameter(ReportingConstants.START_DATE_PARAMETER);
			indicator.addParameter(ReportingConstants.END_DATE_PARAMETER);
			indicator.addParameter(ReportingConstants.LOCATION_PARAMETER);
    	}
    	model.put("indicator", indicator);
	}
}
