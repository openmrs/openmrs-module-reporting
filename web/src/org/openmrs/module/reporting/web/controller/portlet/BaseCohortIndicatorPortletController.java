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

		String uuid = (String)model.get("uuid");
    	if (StringUtils.isNotEmpty(uuid)) {
    		model.put("indicator", Context.getService(IndicatorService.class).getDefinitionByUuid(uuid));
    	}
    	else {
    		CohortIndicator indicator = new CohortIndicator();
			indicator.addParameter(ReportingConstants.START_DATE_PARAMETER);
			indicator.addParameter(ReportingConstants.END_DATE_PARAMETER);
			indicator.addParameter(ReportingConstants.LOCATION_PARAMETER);
			model.put("indicator", indicator);
    	}
	}
}
