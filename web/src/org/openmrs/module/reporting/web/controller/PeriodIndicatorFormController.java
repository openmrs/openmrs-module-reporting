package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.propertyeditor.CohortDefinitionEditor;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.openmrs.module.reporting.web.validator.IndicatorFormValidator;
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
		binder.registerCustomEditor(CohortDefinition.class, new CohortDefinitionEditor());
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

		return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
	}

	@ModelAttribute("cohortDefinitions")
    public Collection<CohortDefinition> populateCohortDefinitions() {
        return Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
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
			@RequestParam(value = "action", required = false) String action,
			@ModelAttribute("indicatorForm") IndicatorForm indicatorForm,
			BindingResult bindingResult) {

		log.info("action: " + action);
		
		Boolean isSave = "save".equalsIgnoreCase(action);
		CohortIndicator cohortIndicator = indicatorForm.getCohortIndicator();
		
		
		log.info("Parameter mapping: " + indicatorForm.getParameterMapping());
		
		if (isSave) { 
			log.info("saving indicator" + cohortIndicator);
			
			// validate the parameter mapping
			new IndicatorFormValidator().validateParameterMapping(indicatorForm, bindingResult);

			if (bindingResult.hasErrors()) 
				return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
			
			
			CohortDefinition cohortDefinition = indicatorForm.getCohortDefinition();
			Map<String, String> parameterMapping = indicatorForm.getParameterMapping();	
			
			cohortIndicator.setCohortDefinition(cohortDefinition, parameterMapping);
			
			// Save the report definition with the new indicator
			Context.getService(IndicatorService.class).saveIndicator(indicatorForm.getCohortIndicator());			
			return new ModelAndView("redirect:/module/reporting/closeWindow.htm");
		}

		return this.setupForm(cohortIndicator.getUuid());
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

		log.info("formBackingObject(): ");		
		
		IndicatorForm indicatorForm = new IndicatorForm();

		
		Indicator indicator = 
			Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
		
		// If indicator does not exist, we just create a new one
		if (indicator != null ) { 
			log.info("formBackingObject(): found indicator ");		
			if (!indicator.getClass().isAssignableFrom(PeriodCohortIndicator.class)) 
				indicatorForm.setCohortIndicator((PeriodCohortIndicator)indicator);
		} 
		// Otherwise, we populate the form bean with the indicator
		else {			
			log.info("formBackingObject(): creating new indicator ");		
			if (indicatorForm.getCohortIndicator() == null)  	
				indicatorForm.setCohortIndicator(new PeriodCohortIndicator());			
		}		
		return indicatorForm;
	}	
}


