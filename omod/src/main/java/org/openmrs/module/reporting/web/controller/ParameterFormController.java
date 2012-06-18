package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.web.util.ParameterUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/reporting/parameters/parameter")
public class ParameterFormController {

	/* Logger */
	private Log log = LogFactory.getLog(this.getClass());

	/* Date format */
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

	
	/**
	 * Allows us to bind a custom editor for a class.
	 * @param binder
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(ymd, true)); 
    }    
	
	
    /**
     * Shows the form.  This method is called after the formBackingObject()
     * method below.
     * 
     * @return	the form model and view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView setupForm() {

				
		ModelAndView model = 
			new ModelAndView("/module/reporting/parameters/parameterForm");
		
		model.addObject("supportedTypes", ParameterUtil.getSupportedTypes());
		model.addObject("supportedCollectionTypes", ParameterUtil.getSupportedCollectionTypes());
		
		return model; 
	}	
	
	/**
	 * Processes the form when a user submits.  
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 */	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type,
			@RequestParam(value = "action", required=false) String action,
			@RequestParam(value = "originalName", required=false) String originalName,
			@RequestParam(value = "redirectUrl", required=false) String redirectUrl,
			@ModelAttribute("parameter") Parameter parameter, 
			BindingResult bindingResult) {
					
		if (bindingResult.hasErrors()) {
			return setupForm();
		}

		log.debug("Action: " + action);
		
		Parameterizable parameterizable = 
			ParameterizableUtil.getParameterizable(uuid, type);		
		if (parameterizable != null) { 
		
			if (action != null) { 
				if (action.equalsIgnoreCase("delete")) { 
					parameterizable.removeParameter(parameter);
				} 
				else if (action.equalsIgnoreCase("save")) {
					
					// If the name changes, we're essentially adding a new parameter. 
					// There's no way to identify the parameter other than 
					// through the name.  Therefore we need to pass in the original 
					// name in order to remove the parameter whose name was changed.
					if (originalName != null && !originalName.equals(parameter.getName())) { 						
						parameterizable.removeParameter(originalName);
					}
					parameterizable.addParameter(parameter);
				}
				else { 
					
				}
				ParameterizableUtil.saveParameterizable(parameterizable);		
			}
		}
		
		return new ModelAndView("redirect:/module/reporting/closeWindow.htm");
		//return new ModelAndView("/module/reporting/parameters/parameterSuccess");
		//return new ModelAndView("redirect:" + redirectUrl);
	}
	
	/**
	 * Retrieves/creates a form backing object.
	 * 
	 * @param uuid
	 * @param type
	 * @param parameterName
	 * @return
	 */
	@ModelAttribute("parameter")
	public Parameter formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "type", required=false) Class<Parameterizable> type,
			@RequestParam(value = "parameterName", required=false) String parameterName) {
		
		// Get parameter if it already exists
		Parameterizable parameterizable = ParameterizableUtil.getParameterizable(uuid, type);	
		Parameter parameter = (parameterName != null) ?		 
			parameterizable.getParameter(parameterName) : null;
		
		// Otherwise create a new parameter
		if (parameter == null) { 
			parameter = new Parameter();
		}

		return parameter;
	}
	
}
