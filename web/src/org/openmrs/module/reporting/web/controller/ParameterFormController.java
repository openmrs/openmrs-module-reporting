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
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.module.reporting.web.widget.html.Option;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
@RequestMapping("/module/reporting/parameter")
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
		
		model.addObject("supportedTypes", getSupportedTypes());
		model.addObject("supportedCollectionTypes", getSupportedCollectionTypes());
		
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
			@RequestParam(value = "redirectUrl", required=false) String redirectUrl,
			@ModelAttribute("parameter") Parameter parameter, 
			BindingResult bindingResult) {
					
		if (bindingResult.hasErrors()) {
			return setupForm();
		}

		Parameterizable parameterizable = getParameterizable(uuid, type);		
		if (parameterizable != null) { 
			parameterizable.addParameter(parameter);
			saveParameterizable(parameterizable);		
		}		
		return new ModelAndView("redirect:" + redirectUrl);

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
		Parameterizable parameterizable = getParameterizable(uuid, type);	
		Parameter parameter = (parameterName != null) ?		 
			parameterizable.getParameter(parameterName) : null;
		
		// Otherwise create a new parameter
		if (parameter == null) { 
			parameter = new Parameter();
		}

		return parameter;
	}

	/**
	 * Retrieves a parameterizable with the given uuid and parameterizable class.
	 * 
	 * @param uuid
	 * @return
	 */
	public Parameterizable getParameterizable(String uuid, Class<Parameterizable> parameterizableClass) { 
		
		if (DataSetDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(DataSetDefinitionService.class).getDataSetDefinitionByUuid(uuid);			
		} 
		else if (CohortDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(uuid);
		}
		else if (ReportDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);						
		}
		else if (Indicator.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);	
		}
		else { 
			throw new APIException("Unable to save parameterizable type " + parameterizableClass);
		}
		
	}


	/**
	 * Saves the given parameterizable.
	 * 
	 * @param parameterizable
	 * @return
	 */
	public Parameterizable saveParameterizable(Parameterizable parameterizable) { 

		if (DataSetDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(DataSetDefinitionService.class).saveDataSetDefinition((DataSetDefinition)parameterizable);			
		} 
		else if (CohortDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(CohortDefinitionService.class).saveCohortDefinition((CohortDefinition)parameterizable);
		}
		else if (ReportDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(ReportService.class).saveReportDefinition((ReportDefinition)parameterizable);						
		}
		else if (Indicator.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(IndicatorService.class).saveIndicator((Indicator)parameterizable);	
		}
		else { 
			throw new APIException("Unable to save parameterizable type " + parameterizable.getClass());
		}
		//return parameterizable;
	}

	
	/** 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Option> getSupportedCollectionTypes() {
		List<Option> collectionTypes = new ArrayList<Option>();
		collectionTypes.add(new Option(null, List.class.getSimpleName(), null, List.class.getName()));						
		collectionTypes.add(new Option(null, Set.class.getSimpleName(), null, Set.class.getName()));	
		return collectionTypes;
	}
	
	
	/** 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Option> getSupportedTypes() {
		List<Option> ret = new ArrayList<Option>();
		for (WidgetHandler e : HandlerUtil.getHandlersForType(WidgetHandler.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {						
						ret.add(new Option(null, type.getSimpleName(), null, type.getName()));						
					}
				}
			}
		}
		return ret;
	}
	
	
}
