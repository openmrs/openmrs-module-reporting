package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("indicatorForm")
public class PreviewPeriodIndicatorController {

	/* Logger */
	private Log log = LogFactory.getLog(this.getClass());

	/* Date format */
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Allows us to bind a custom editor for a class.
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(ymd, true));
	}
	
	/**
	 * Populates the form backing object for the 
	 * 
	 * @param uuid
	 * @param className
	 * @return
	 */
	@ModelAttribute("indicatorForm")
	public IndicatorForm formBackingObject(	
			@RequestParam(value = "uuid", required=false) String uuid) { 
		
		IndicatorForm form = new IndicatorForm();
	
		Indicator indicator = Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
		log.debug("indicator = " + indicator);

		// If indicator does not exist, we just create a new one
		if (indicator != null) { 
			//if (!indicator.getClass().isAssignableFrom(PeriodCohortIndicator.class)) 
			form.setCohortIndicator((CohortIndicator)indicator);
		} 
		// Otherwise, we populate the form bean with the indicator
		else {			
			form.setCohortIndicator(new CohortIndicator());			
		}		
		return form;
	}
	
	/**
	 * Sets up the form for previewing a period indicator.
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value="/module/reporting/indicators/previewPeriodIndicator", method=RequestMethod.GET)
	public ModelAndView showForm() { 				
		ModelAndView model = new ModelAndView("/module/reporting/indicators/previewPeriodIndicator");
		// Nothing to do right now except forward to the JSP
		return model;
	}	
	
	/**
	 * Processes the evaluation of the period indicator.
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 * @throws EvaluationException 
	 */
	@RequestMapping(value="/module/reporting/indicators/previewPeriodIndicator",method = RequestMethod.POST)
	public ModelAndView processForm(
		@ModelAttribute("indicatorForm") IndicatorForm form,
		BindingResult bindingResult) throws EvaluationException {
		
		log.debug("POST /module/reporting/indicators/previewPeriodIndicator");
		
		if (bindingResult.hasErrors()) {
			return showForm();
		}
		
		ModelAndView model = new ModelAndView("/module/reporting/indicators/previewPeriodIndicator");

		if (form != null) { 
			log.debug("Evaluating period indicator ");
			EvaluationContext context = new EvaluationContext();
			
			Map<String, Object> parameterValues = 
				getParameterValues(form.getCohortIndicator(), form.getParameterValues());
			context.setParameterValues(parameterValues);
			
			IndicatorResult indicatorResult = 
				Context.getService(IndicatorService.class).evaluate(form.getCohortIndicator(), context);

			log.debug("indicatorResult: " + indicatorResult);		
			model.addObject("indicatorResult", indicatorResult);
			
		}			
		return model;
		
		//return new ModelAndView("redirect:/module/reporting/closeWindow.htm");
	}
			
	
	/**
	 * TODO Move WidgetUtil into core reporting, rather than web. 
	 *  
	 * @param parameterizable
	 * @param parameterValues
	 * @return
	 */
	public static Map<String, Object> getParameterValues(Parameterizable parameterizable, Map<String,String> parameterStrings) { 
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		if (parameterizable != null && parameterizable.getParameters() != null) { 
			for (Parameter parameter : parameterizable.getParameters()) {
				String textValue = parameterStrings.get(parameter.getName());			
				Object objectValue = WidgetUtil.parseInput(textValue, parameter.getClass());
				parameterValues.put(parameter.getName(), objectValue);								
			}
		}
		return parameterValues;
	}	
	


}
