package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.web.model.IndicatorParameterBean;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
		log.info("showForm()");
		return new ModelAndView("/module/reporting/indicators/indicatorParameterEditor");
	}	

	@ModelAttribute
	public void referenceData(ModelMap model){ 
		log.info("referenceData()");
	}	
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			@ModelAttribute("indicatorParameter") IndicatorParameterBean indicatorParameter,
			BindingResult bindingResult) {
		
		log.info("Processing form ..." + indicatorParameter.getCohortIndicator().getUuid());
		
		//parameter.setAllowUserInput(allowUserInput);
		//parameter.setTyp(type);
		//parameter.setDefaultValue(defaultValue);
		//parameter.setLabel(label);
		//parameter.setName(name);
		//parameter.setRequired(required);

		
		if (bindingResult.hasErrors()) {
			log.info("There are " + bindingResult.getErrorCount() + " errors in the form");
			log.info("\t" + bindingResult);
			return showForm();
		}
		CohortIndicator cohortIndicator = indicatorParameter.getCohortIndicator();
		Parameter parameter = indicatorParameter.getParameter();
		
		cohortIndicator.addParameter(parameter);
		
		log.info("Added parameter: " + parameter.getName() + " to " + cohortIndicator.getUuid());
		
		log.info("name: " + cohortIndicator.getName());
		log.info("description: " + cohortIndicator.getDescription());
		log.info("cohort definition: " + cohortIndicator.getCohortDefinition());
		log.info("parameters: " + cohortIndicator.getParameters());

		Context.getService(IndicatorService.class).saveIndicator(cohortIndicator);
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
		log.info("formBackingObject() " + uuid);
		
		IndicatorService service = Context.getService(IndicatorService.class);
		
		IndicatorParameterBean indicatorParameter = new IndicatorParameterBean();
		CohortIndicator cohortIndicator = (CohortIndicator) service.getIndicatorByUuid(uuid);

		
		if (cohortIndicator == null) { 
			log.info("Creating new cohort indicator");
			cohortIndicator = new CohortIndicator();
		} else { 
			log.info("Found cohort indicator with uuid " + uuid);
		}
		indicatorParameter.setCohortIndicator(cohortIndicator);
		indicatorParameter.setParameter(new Parameter());
		log.info("Indicator: " + cohortIndicator);
		
		return indicatorParameter;
	}


	
	
	
}
