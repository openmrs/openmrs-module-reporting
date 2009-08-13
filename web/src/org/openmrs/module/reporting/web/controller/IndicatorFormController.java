package org.openmrs.module.reporting.web.controller;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PatientIdentifierTypeEditor;



/**
 * 
 * @author jmiranda
 *
 */
public class IndicatorFormController extends AbstractWizardFormController {

	private Log log = LogFactory.getLog(this.getClass());
	
	public IndicatorFormController() {
		setCommandName("indicatorForm");
		setPages(new String[] {
				"/module/reporting/indicators/wizard/chooseIndicatorTypeForm", 
				"/module/reporting/indicators/wizard/chooseCohortDefinitionForm", 
				"/module/reporting/indicators/wizard/mapParametersForm", 
				"/module/reporting/indicators/wizard/submitIndicatorForm"
		});		
	}
	
	/**
	 * Allows for other Objects to be used as values in input tags. Normally, only strings and lists
	 * are expected
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
	 *      org.springframework.web.bind.ServletRequestDataBinder)
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		
		// add more when necessary
	}	
	

	protected Object formBackingObject(HttpServletRequest request) throws ModelAndViewDefiningException {
		log.info("formBackingObject");
		return new IndicatorForm();
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) {
		log.info("onBindAndValidate(): " + page);
		
		// Step 2 - select a cohort definition
		IndicatorForm indicatorForm = (IndicatorForm) command;		
		String selectedUuid = indicatorForm.getCohortDefinitionUuid();		
		if (indicatorForm.getCohortDefinition()==null && selectedUuid!=null) { 
			CohortDefinition cohortDefinition = 
				Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(selectedUuid);			
			indicatorForm.setCohortDefinition(cohortDefinition);
		}
		
		
		// Step 3 - map parameters
		/*
		CohortDefinition selectedCohortDefinition = indicatorForm.getCohortDefinition();
		for (Parameter parameter : selectedCohortDefinition.getParameters()) { 
			String value = indicatorForm.getParameters().get(parameter.getName());
			if (value )
		}
		*/
		
		
	}

	protected Map<String,Object> referenceData(HttpServletRequest request, int page) {
		log.info("referenceData(): " + page);

    	// Add cohort definitions  		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false));			
    	
    	
    	

		return model;
	}

	/*
	protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
		log.info("getTargetPage(): " + currentPage);

		IndicatorForm indicatorForm = (IndicatorForm) command;

		// FIXME This should be done for us, but if we need to add 
		if (currentPage == 0) {		
			return 1;
		} 
		else if (currentPage == 1) {
			return 2;
		}
		else if (currentPage == 2) {
			return 3;
		}
		return 0;		
	}*/

	protected void validatePage(Object command, Errors errors, int page) {
		log.info("onBindAndValidate(): " + page);

		//IndicatorForm indicatorForm = (IndicatorForm) command;
		//IndicatorFormValidator indicatorFormValidator = (IndicatorFormValidator) getValidator();
		//errors.setNestedPath("indicatorForm");
		switch (page) {
			case 0:
				//orderValidator.validateIndicatorForm(indicatorForm, errors);
				break;
			case 1:
				// FIXME	Invalid property 'indicatorForm' of bean class [org.openmrs.module.reporting.web.model.IndicatorForm]: 
				// 			Bean property 'indicatorForm' is not readable or has an invalid getter method: 
				//			Does the return type of the getter match the parameter type of the setter?


				//indicatorFormValidator.validateIndicatorForm(indicatorForm, errors);
				break;
			case 2: 

				break;
		}
		errors.setNestedPath("");
	}

	protected ModelAndView processFinish(
			HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) {
		log.info("processFinish()");
		
		IndicatorForm indicatorForm = (IndicatorForm) command;
		CohortIndicator cohortIndicator = indicatorForm.getCohortIndicator();
		//Context.getService(IndicatorService.class).saveIndicator(cohortIndicator);
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("cohortIndicator", cohortIndicator);
		model.put("message", "Thank you, your indicator has been created.");
		return new ModelAndView("/module/reporting/indicators/wizard/viewIndicatorPage", model);
	}

}