package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
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
@RequestMapping("/module/reporting/indicatorDataset")
public class IndicatorDatasetFormController {

	private Log log = LogFactory.getLog(this.getClass());

	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	// TODO Switch this to the Context.getDateFormat()
    	//SimpleDateFormat dateFormat = Context.getDateFormat();
    	//SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); 
    	//dateFormat.setLenient(false); 
    	//binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("true", "false", true)); 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(ymd, true)); 
    }    
	
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm() {
		log.info("Inside show() method");
		
		return new ModelAndView("/module/reporting/datasets/indicatorDatasetEditor");
	}	

	@ModelAttribute
	public void referenceData(ModelMap model){
		log.info("Building the reference data for all requests");
		
		List<CohortDefinition> cohortDefinitions = 
			Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false);
		
		List<Indicator> indicators = 
			Context.getService(IndicatorService.class).getAllIndicators(false);
		
		model.addAttribute("cohortDefinitions", cohortDefinitions);
		model.addAttribute("indicators", indicators);
	}	
	
	/**
	 * As
	 * @param request
	 * @param dataSetDefinition
	 * @param bindingResult
	 * @return
	 */
	@ModelAttribute("datasetDefinition")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			HttpServletRequest request,
			CohortIndicatorDataSetDefinition dataSetDefinition, 
			BindingResult bindingResult) {
		log.info("Inside submit() method");
					
		if (bindingResult.hasErrors()) {
			log.info("# errors: " + bindingResult.getErrorCount());
			log.info("errors: " + bindingResult.getAllErrors());
			return showForm();
		}
		
		String action = request.getParameter("action");

		// Add indicators to a report schema
		if ("addIndicators".equalsIgnoreCase(action)) { 
			if (dataSetDefinition == null) 
				dataSetDefinition = new CohortIndicatorDataSetDefinition();
			

			String [] selectedUuids = request.getParameterValues("selectedUuid");
			if (selectedUuids!=null) { 				
				for (String uuid : selectedUuids) { 
					
					Indicator indicator = (CohortIndicator) Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
					if (indicator != null) { 					
						// FIXME Hacked the mapping
						Map<String, Object> parameterMapping = new HashMap<String, Object>();
						parameterMapping.put("indicator.location", "${dataset.location}");
						parameterMapping.put("indicator.date", "${dataset.date}");
						
						// FIXME Assumes CohortIndicatorDataSetDefinition and CohortIndicator
						dataSetDefinition.addCohortIndicator(
								indicator.getName(),(CohortIndicator) indicator, parameterMapping);
										
					}										
				}
			}

			// Dataset needs to know what parameters it needs
			dataSetDefinition.addParameter(new Parameter("dataset.location", "Location Parameter", Location.class));
			dataSetDefinition.addParameter(new Parameter("dataset.date", "Date Parameter", Date.class));			
			
		}
		
		// Otherwise this is a simple save operation		
		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);
		
		return new ModelAndView("redirect:/module/reporting/manageDatasets.list");
	}
	
	
	@ModelAttribute("datasetDefinition")
	public DataSetDefinition formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "className", required=false) String className
	) {
		log.info("Inside formBackingObject(String, String) method with ");
		log.info("UUID=" + uuid + ", className=" + className);
		
		DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);		
		DataSetDefinition dataSetDefinition = service.getDataSetDefinitionByUuid(uuid);
		
		// TODO We can actually return 'null' by default and handle the instantiation
		// of the appropriate type in the process form controller
		if (dataSetDefinition == null) { 		
			dataSetDefinition = new CohortIndicatorDataSetDefinition();
		} else { 
			log.info("Found reportDefinition with uuid " + dataSetDefinition.getUuid());			
		}		
		
		return dataSetDefinition;
	}


	
	
	
}
