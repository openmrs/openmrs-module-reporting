package org.openmrs.module.reporting.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;



/**
 * 
 * @author jmiranda
 *
 */
public class IndicatorWizardFormController extends AbstractWizardFormController {

	private Log log = LogFactory.getLog(this.getClass());
	
	public IndicatorWizardFormController() {
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
		log.debug("formBackingObject");
		return new IndicatorForm();
	}

	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors, int page) {
		log.debug("onBindAndValidate(): " + page);
		
		// Step 2 - select a cohort definition
		if (page == 1) { 
			
			// Set the selected cohort definition on the form object			
			// FIXME This should have been done using spring:bind but 
			// I didn't want to write a CohortDefinitionEditor
			IndicatorForm indicatorForm = (IndicatorForm) command;		
			String selectedUuid = indicatorForm.getCohortDefinitionUuid();		
			if (indicatorForm.getCohortDefinition()==null && selectedUuid!=null) { 
				CohortDefinition cohortDefinition = 
					Context.getService(CohortDefinitionService.class).getDefinitionByUuid(selectedUuid);			
				indicatorForm.setCohortDefinition(cohortDefinition);
			}
			
			// Also add some default parameters to the indicator (might be used in the next step).
			// TODO Not sure if this goes here or in an earlier step
			CohortIndicator indicator = indicatorForm.getCohortIndicator();
			indicator.addParameter(new Parameter("startDate", "Start Date", Date.class, null, false));
			indicator.addParameter(new Parameter("endDate", "End Date", Date.class, null, false));
			indicator.addParameter(new Parameter("location", "Location", Location.class, null, false));
						
		}
		// Step 3 - map parameters		
		else if (page == 2) { 
						
			IndicatorForm indicatorForm = (IndicatorForm) command;	
			Map<String, Object> parameterMapping = new HashMap<String,Object>();
			// Iterate over the cohort definitions parameters and create a parameter mapping
			// By default we map the cohort definition parameters directly to the 
			for (Parameter parameter : indicatorForm.getCohortDefinition().getParameters()) { 								
				parameterMapping.put(parameter.getName(), "${" + parameter.getName() + "}");				
			}	
			// Add the cohort definition to the indicator with mapped parameters
			indicatorForm.getCohortIndicator().setCohortDefinition(
					indicatorForm.getCohortDefinition(), parameterMapping);
		}
		
		
	}

	protected Map<String,Object> referenceData(HttpServletRequest request, int page) {
		log.debug("referenceData(): " + page);

    	// Add cohort definitions  		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllDefinitions(false));			
    	
    	
    	

		return model;
	}


	protected void validatePage(Object command, Errors errors, int page) {
		log.debug("validatePage(): " + page);

		// TODO When validators have been written, we can uncomment the following code
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
		log.debug("processFinish()");
		
		IndicatorForm indicatorForm = (IndicatorForm) command;
		CohortIndicator cohortIndicator = indicatorForm.getCohortIndicator();
		
		// TODO At this point, the cohortIndicator should have everything it needs
		//Context.getService(IndicatorService.class).saveIndicator(cohortIndicator);

		Map<String,Object> model = new HashMap<String,Object>();
		model.put("cohortIndicator", cohortIndicator);
		model.put("message", "Thank you, your indicator has been created.");
		return new ModelAndView("/module/reporting/indicators/wizard/viewIndicatorPage", model);
	}

}