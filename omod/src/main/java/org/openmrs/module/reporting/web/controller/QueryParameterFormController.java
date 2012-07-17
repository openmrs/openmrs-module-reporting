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
package org.openmrs.module.reporting.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class QueryParameterFormController {

	/* Logger */
	private Log log = LogFactory.getLog(this.getClass());

	
	/**
	 * Allows us to bind a custom editor for a class.
	 * @param binder
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true)); 
    }
	
	/**
	 * Processes the form when a user submits.
	 */	
	@RequestMapping("/module/reporting/parameters/queryParameter")
	public ModelAndView processForm(
			HttpServletRequest request,	
			HttpServletResponse response,	
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type,
			@RequestParam(value = "action", required=false) String action,
			@RequestParam(value = "format", required=false) String format,
			@RequestParam(value = "successView", required=false) String successView,
			@ModelAttribute("parameterizable") Parameterizable parameterizable, 
			BindingResult bindingResult) throws Exception {
		
		if ( parameterizable == null ) {
			parameterizable = ParameterizableUtil.getParameterizable(uuid, type);
		}
		
		if (parameterizable != null && parameterizable.getParameters().isEmpty() && StringUtils.isEmpty(action)) {
			action = "preview";
		}
		
		if (StringUtils.isEmpty(action)) {
			request.getSession().removeAttribute("results");
			return new ModelAndView("/module/reporting/parameters/queryParameterForm");
		}
		else {
		
			Object results = null;
			ModelAndView model = new ModelAndView();		
				
			if (parameterizable != null) {			
				EvaluationContext evaluationContext = new EvaluationContext();
				
				Map<String, Object> parameterValues = new HashMap<String, Object>();
				if (parameterizable != null && parameterizable.getParameters() != null) { 
					for (Parameter p : parameterizable.getParameters()) {
						Object paramVal = WidgetUtil.getFromRequest(request, p.getName(), p.getType(), p.getCollectionType());
						parameterValues.put(p.getName(), paramVal);								
					}
				}
	
				// Set parameter values
				evaluationContext.setParameterValues(parameterValues);		
	
				model.addObject("evaluationContext", evaluationContext);
				try { 
					long startTime = System.currentTimeMillis();
					// Evaluate the parameterizable and populate the model
					results = ParameterizableUtil.evaluateParameterizable(parameterizable, evaluationContext);						
					//model.addObject("results", results);
					request.getSession().setAttribute("results", results);
					long executionTime = System.currentTimeMillis() - startTime;
					model.addObject("executionTime", new Double(executionTime/1000));
					
					// Use the success view if it's given, default view otherwise
					//successView = (!StringUtils.isEmpty(successView)) ? successView : defaultView;
					//successView += "?uuid=" + parameterizable.getUuid() + "&type=" + type + "&format=" + format; 
					model.setViewName("/module/reporting/parameters/queryParameterForm");
					
				} 
				catch (ParameterException e) { 
					log.error("unable to evaluate report: ", e);
					request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Unable to evaluate report: " + e.getMessage());
					request.getSession().removeAttribute("results");
					return new ModelAndView("/module/reporting/parameters/queryParameterForm");
				}
				catch (MissingDependencyException ex) {
					request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Missing dependency: " + ex.getMessage());
					request.getSession().removeAttribute("results");
					return new ModelAndView("/module/reporting/parameters/queryParameterForm");
				}
			}		
			
			log.debug("Returning model with view " + model.getViewName() + " and map " + model.getModelMap());
			return model;
		}
	}
	
	/**
	 * Retrieves/creates a form backing object.
	 * 
	 * @param uuid
	 * @param type
	 * @param parameterName
	 * @return
	 */
	@ModelAttribute("parameterizable")
	public Parameterizable formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type) {
		
		if (type == null || uuid == null)
			return null;
		else
			return ParameterizableUtil.getParameterizable(uuid, type);

	}
	
}
