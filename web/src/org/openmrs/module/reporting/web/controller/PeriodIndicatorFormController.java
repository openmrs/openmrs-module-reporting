package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.propertyeditor.CohortDefinitionEditor;
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
		log.info("Setup form");
		return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
	}

	/**
	 * Populate the cohort definitions that are available to this indicator.
	 * 
	 * @return	a list of cohort definitions
	 */
	@ModelAttribute("cohortDefinitions")
    public Collection<CohortDefinition> populateAllCohortDefinitions() {
		log.info("Populate cohort definitions");
        return Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
    }
	
	/**
	 * Populate the location cohort definitions that are available to this indicator.
	 * 
	 * @return	a list of cohort definitions
	 */
	@ModelAttribute("locationFilters")
    public Collection<CohortDefinition> populateLocationFilters() {
		log.info("Populate all location filters");
        return Context.getService(CohortDefinitionService.class).getCohortDefinitions("location", false);
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

		Boolean isSave = "save".equalsIgnoreCase(action);
		CohortIndicator cohortIndicator = indicatorForm.getCohortIndicator();
				
		if (isSave) { 			
			
			// validate the parameter mapping
			// TODO we actually need to validate the entire indicator 
			new IndicatorFormValidator().validateParameterMapping(indicatorForm, bindingResult);
			
			// if there are errors return to the form
			if (bindingResult.hasErrors()) 
				return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
			
			// Assign the cohort definition and parameter mapping for the numerator
			// TODO should validate above
			if (indicatorForm.getCohortDefinition() != null) { 
				cohortIndicator.setCohortDefinition(
						indicatorForm.getCohortDefinition(), indicatorForm.getParameterMapping());
			}
			
			// Assign the cohort definition and parameter mapping for the denominator
			//cohortIndicator.setDenominator(
			//		indicatorForm.getDenominatorCohortDefinition(), indicatorForm.getDenominatorParameterMapping());
						
			// Assign the location filter  
			// TODO Should validate above
			if (indicatorForm.getLocationFilter() != null) { 
				cohortIndicator.setLocationFilter(
						indicatorForm.getLocationFilter(), indicatorForm.getLocationFilterParameterMapping());
			}
			
			// Save the cohort indicator definition to the database 
			Context.getService(IndicatorService.class).saveIndicator(cohortIndicator);			

			// Redirect to the close window page in order to close the modal dialog
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
	public IndicatorForm populateFormBackingObject(	
			@RequestParam(value = "uuid", required=false) String uuid) { 

		log.info("formBackingObject(): ");		
		
		log.info("Lookup indicator by UUID: " + uuid);
		IndicatorForm indicatorForm = new IndicatorForm();
		Indicator indicator = 
			Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);

		log.info("Found indicator: " + indicator);
		
		// If indicator does not exist, we just create a new one
		if (indicator != null ) { 
			log.info("formBackingObject(): found indicator " + indicator);	
			if (CohortIndicator.class.isAssignableFrom(indicator.getClass())) {
				CohortIndicator cohortIndicator = (CohortIndicator)indicator;
				indicatorForm.setCohortIndicator(cohortIndicator);
				if (cohortIndicator.getCohortDefinition() != null) { 
					indicatorForm.setCohortDefinition(cohortIndicator.getCohortDefinition().getParameterizable());
					indicatorForm.setParameterMapping(cohortIndicator.getCohortDefinition().getParameterMappings());
				}
			}
			
			else 
				throw new APIException("Unable to edit a <" + indicator.getClass().getName() + "> indicator using the period indicator form");
		} 
		// Otherwise, we populate the form bean with the indicator
		else {			
			log.info("formBackingObject(): creating new indicator ");		
			if (indicatorForm.getCohortIndicator() == null)  { 
				CohortIndicator ci = new CohortIndicator();
				ci.addParameter(ReportingConstants.START_DATE_PARAMETER);
				ci.addParameter(ReportingConstants.END_DATE_PARAMETER);
				indicatorForm.setCohortIndicator(ci);
			}
		}		
		return indicatorForm;
	}	
}


