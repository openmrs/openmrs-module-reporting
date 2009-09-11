package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.util.ParameterizableUtil;

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
