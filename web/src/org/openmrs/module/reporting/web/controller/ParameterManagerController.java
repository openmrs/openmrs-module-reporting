package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ParameterManagerController {

	protected static Log log = LogFactory.getLog(ParameterManagerController.class);
	
	/**
	 * Default Constructor
	 */
	public ParameterManagerController() { }

	/**
	 * Processes the form when a user submits.  
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 */	
	@RequestMapping("/module/reporting/deleteParameter")
	public ModelAndView removeParameter(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type,
			@RequestParam(value = "parameterName", required=false) String parameterName,
			@RequestParam(value = "redirectUrl", required=false) String redirectUrl) { 	
		
		Parameterizable parameterizable = ParameterizableUtil.getParameterizable(uuid, type);		
		if (parameterizable != null) { 		
			parameterizable.removeParameter(parameterName);
			ParameterizableUtil.saveParameterizable(parameterizable);	
		}
		return new ModelAndView("redirect:" + redirectUrl);
	}
}
