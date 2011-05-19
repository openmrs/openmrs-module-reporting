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
import org.openmrs.module.reporting.indicator.CohortIndicator.IndicatorType;
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
		return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
	}

	/**
	 * Populate the cohort definitions that are available to this indicator.
	 * 
	 * @return	a list of cohort definitions
	 */
	@ModelAttribute("cohortDefinitions")
    public Collection<CohortDefinition> populateAllCohortDefinitions() {
        return Context.getService(CohortDefinitionService.class).getAllDefinitions(false);
    }
	
	/**
	 * Populate the location cohort definitions that are available to this indicator.
	 * 
	 * @return	a list of cohort definitions
	 */
	@ModelAttribute("locationFilters")
    public Collection<CohortDefinition> populateLocationFilters() {
        return Context.getService(CohortDefinitionService.class).getDefinitions("location", false);
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
			
			log.debug("Processing cohort indicator save");
			
			IndicatorFormValidator validator = new IndicatorFormValidator();			
			
			if ("COUNT".equals(indicatorForm.getIndicatorType())) { 
			
				// form has not been completed yet
				if (indicatorForm.getCohortDefinition() == null) 
					return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
				
				// validate the count indicator
				validator.validateCountIndicator(indicatorForm, bindingResult);

				// if there are errors return to the form
				if (bindingResult.hasErrors()) 
					return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
				
				// if successful, set cohort definition on indicator
				cohortIndicator.setType(IndicatorType.COUNT);
				cohortIndicator.setCohortDefinition(
						indicatorForm.getCohortDefinition(), indicatorForm.getParameterMapping());				
			} 
			else if ("FRACTION".equals(indicatorForm.getIndicatorType())) { 

				// form has not been completed yet
				if (indicatorForm.getNumerator() == null || indicatorForm.getDenominator() == null) 
					return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
				
				
				// Validate the fractional indicator
				validator.validateFractionIndicator(indicatorForm, bindingResult);

				// if there are errors return to the form
				if (bindingResult.hasErrors()) 
					return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");

				// Set the indicatory type
				cohortIndicator.setType(IndicatorType.FRACTION);

				// Set the numerator and parameter mapping for the denominator
				cohortIndicator.setCohortDefinition(
						indicatorForm.getNumerator(), indicatorForm.getNumeratorParameterMapping());
				
				// Set the denominator and parameter mapping for the denominator
				cohortIndicator.setDenominator(
						indicatorForm.getDenominator(), indicatorForm.getDenominatorParameterMapping());				
			}			
			
			// If specified, validate and set the location filter on the indicator	
			if (indicatorForm.getLocationFilter() != null) { 				
				
				// validate the location filter
				validator.validateLocationFilter(indicatorForm, bindingResult);

				// if there are errors return to the form
				if (bindingResult.hasErrors()) 
					return new ModelAndView("/module/reporting/indicators/periodIndicatorForm");
				
				// if successful, set the location filter 
				cohortIndicator.setLocationFilter(
						indicatorForm.getLocationFilter(), indicatorForm.getLocationFilterParameterMapping());
			}			
			
			// Otherwise, we save the cohort indicator definition to the database 
			Context.getService(IndicatorService.class).saveDefinition(cohortIndicator);			

			// Redirect to the close window page in order to close the modal dialog
			return new ModelAndView("redirect:/module/reporting/indicators/manageIndicators.form");
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

		log.debug("formBackingObject(): ");		
		
		log.debug("Lookup indicator by UUID: " + uuid);
		IndicatorForm indicatorForm = new IndicatorForm();
		Indicator indicator = 
			Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);

		log.debug("Found indicator: " + indicator);
		
		// If indicator does not exist, we just create a new one
		if (indicator != null ) { 
			
			
			
			log.debug("formBackingObject(): found indicator " + indicator);	
			if (CohortIndicator.class.isAssignableFrom(indicator.getClass())) {
				
				
				CohortIndicator cohortIndicator = (CohortIndicator)indicator;
				
				indicatorForm.setIndicatorType(cohortIndicator.getType().toString());				
				indicatorForm.setCohortIndicator(cohortIndicator);
				
				if (cohortIndicator.getLocationFilter() != null) { 
					indicatorForm.setLocationFilter(cohortIndicator.getLocationFilter().getParameterizable());
					indicatorForm.setLocationFilterParameterMapping(cohortIndicator.getLocationFilter().getParameterMappings());
				}
				
			
				if ("COUNT".equals(indicatorForm.getIndicatorType())) { 
					indicatorForm.setCohortDefinition(cohortIndicator.getCohortDefinition().getParameterizable());
					indicatorForm.setParameterMapping(cohortIndicator.getCohortDefinition().getParameterMappings());
				} 
				else if ("FRACTION".equals(indicatorForm.getIndicatorType())) { 
					indicatorForm.setNumerator(cohortIndicator.getCohortDefinition().getParameterizable());
					indicatorForm.setNumeratorParameterMapping(cohortIndicator.getCohortDefinition().getParameterMappings());
					indicatorForm.setDenominator(cohortIndicator.getDenominator().getParameterizable());
					indicatorForm.setDenominatorParameterMapping(cohortIndicator.getDenominator().getParameterMappings());				
				} 
				else { 
					throw new APIException("Unsupported indicator type: " + indicatorForm.getIndicatorType());
				}
			}
			
			else 
				throw new APIException("Unable to edit a <" + indicator.getClass().getName() + "> indicator using the period indicator form");
		} 
		// Otherwise, we populate the form bean with the indicator
		else {			
			log.debug("formBackingObject(): creating new indicator ");		
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


