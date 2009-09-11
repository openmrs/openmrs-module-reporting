package org.openmrs.module.reporting.web.controller.portlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Parameterizable;
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
