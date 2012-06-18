package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.web.model.IndicatorParameterBean;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/reporting/indicatorParameter")
public class IndicatorParameterFormController {

	private static Log log = LogFactory.getLog(IndicatorParameterFormController.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Class.class, new ClassEditor());
    }    
	
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm() {		
		log.debug("showForm()");
		return new ModelAndView("/module/reporting/indicators/indicatorParameterEditor");
	}	

	@ModelAttribute
	public void referenceData(ModelMap model){ 
		log.debug("referenceData()");
	}	
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			@ModelAttribute("indicatorParameter") IndicatorParameterBean indicatorParameter,
			BindingResult bindingResult) {
		
		log.debug("Processing form ..." + indicatorParameter.getCohortIndicator().getUuid());
		
		//parameter.setAllowUserInput(allowUserInput);
		//parameter.setTyp(type);
		//parameter.setDefaultValue(defaultValue);
		//parameter.setLabel(label);
		//parameter.setName(name);
		//parameter.setRequired(required);

		
		if (bindingResult.hasErrors()) {
			log.debug("There are " + bindingResult.getErrorCount() + " errors in the form");
			log.debug("\t" + bindingResult);
			return showForm();
		}
		CohortIndicator cohortIndicator = indicatorParameter.getCohortIndicator();
		Parameter parameter = indicatorParameter.getParameter();
		
		cohortIndicator.addParameter(parameter);
		
		log.debug("Added parameter: " + parameter.getName() + " to " + cohortIndicator.getUuid());
		
		log.debug("name: " + cohortIndicator.getName());
		log.debug("description: " + cohortIndicator.getDescription());
		log.debug("cohort definition: " + cohortIndicator.getCohortDefinition());
		log.debug("parameters: " + cohortIndicator.getParameters());

		Context.getService(IndicatorService.class).saveDefinition(cohortIndicator);
		return new ModelAndView("redirect:/module/reporting/editIndicator.form#indicator-advanced-tab");
	}
	
	/**
	 * The @ModelAttribute annotation on this class tells Spring to call this 
	 * on every request on the controller.
	 * 
	 * @param uuid
	 * @return
	 */
	@ModelAttribute("indicatorParameter")
	public IndicatorParameterBean formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid) {	
		log.debug("formBackingObject() " + uuid);
		
		IndicatorService service = Context.getService(IndicatorService.class);
		
		IndicatorParameterBean indicatorParameter = new IndicatorParameterBean();
		CohortIndicator cohortIndicator = (CohortIndicator) service.getDefinitionByUuid(uuid);

		
		if (cohortIndicator == null) { 
			log.debug("Creating new cohort indicator");
			cohortIndicator = new CohortIndicator();
		} else { 
			log.debug("Found cohort indicator with uuid " + uuid);
		}
		indicatorParameter.setCohortIndicator(cohortIndicator);
		indicatorParameter.setParameter(new Parameter());
		log.debug("Indicator: " + cohortIndicator);
		
		return indicatorParameter;
	}


	
	
	
}
