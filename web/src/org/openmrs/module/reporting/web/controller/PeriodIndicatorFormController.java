package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.PeriodIndicatorReportDefinition;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.module.reporting.web.widget.html.Option;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.openmrs.module.reporting.web.util.ParameterUtil;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.util.HandlerUtil;
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
import org.springframework.web.util.WebUtils;

@Controller
@RequestMapping("/module/reporting/indicators/periodIndicator")
public class PeriodIndicatorFormController {

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
	 * Shows the form. This method is called after the formBackingObject()
	 * method below.
	 * 
	 * @return the form model and view
	 */
	@ModelAttribute("indicatorForm")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView setupForm(
		@RequestParam(value = "uuid", required = false) String uuid) {

				
		ModelAndView model = new ModelAndView(
				"/module/reporting/indicators/periodIndicatorForm");

		model.addObject("cohortDefinitions", Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false));
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
	public String processForm(
			@ModelAttribute("indicatorForm") IndicatorForm indicatorForm,
			BindingResult bindingResult) {

		//new IndicatorFormValidator().validate(indicatorForm, bindResult);

		
		if (bindingResult.hasErrors()) 
			return "periodIndicatorForm";
		
		log.info("indicatorForm.getUuid(): " + indicatorForm.getUuid());
		
		// Find the selected indicator
		PeriodCohortIndicator indicator = (PeriodCohortIndicator) Context
				.getService(IndicatorService.class).getIndicatorByUuid(indicatorForm.getUuid());
		
		// Save the report definition with the new indicator
		Context.getService(IndicatorService.class).saveIndicator(indicator);

		return "redirect:/module/reporting/closeWindow.htm";
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
	
	
		IndicatorForm indicatorForm = new IndicatorForm();

		
		Indicator indicator = Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
		
		// If indicator does not exist, we just create a new one
		if (indicator == null) { 
			indicatorForm.setCohortIndicator(new PeriodCohortIndicator());
		} 
		// Otherwise, we populate the form bean with the indicator
		else {			
			if (!indicator.getClass().isAssignableFrom(PeriodCohortIndicator.class)) 
				indicatorForm.setCohortIndicator((PeriodCohortIndicator)indicator);
			
		}		
		log.info("formBackingObject(): " + indicatorForm.getUuid());
		return indicatorForm;
	}
	
	
}


