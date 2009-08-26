package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.PeriodIndicatorReportDefinition;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.web.model.IndicatorForm;
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
public class PeriodIndicatorManagerController {

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
	@RequestMapping(value="/module/reporting/indicators/managePeriodIndicator", method=RequestMethod.GET)
	public ModelAndView showForm() {
		ModelAndView model = new ModelAndView("/module/reporting/indicators/periodIndicatorManager");
		model.addObject("indicators", 
				Context.getService(IndicatorService.class).getAllIndicators(false));
		return model;
	}

	
	
	
	/**
	 * Processes the form when a user submits.
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/module/reporting/indicators/managePeriodIndicator",method = RequestMethod.POST)
	public ModelAndView managePeriodIndicator(
		@ModelAttribute("indicatorForm") IndicatorForm indicatorForm,
		@RequestParam(required=false,value="action") String action,
		@RequestParam(required=false,value="indicatorKey") String indicatorKey,
		@RequestParam(required=false,value="columnKey") String columnKey) {
		
		log.info("POST /module/reporting/indicators/managePeriodIndicator " + action);
		
		//if (bindingResult.hasErrors()) {
		//	return showForm();
		//}

	
		ReportService reportService = Context.getService(ReportService.class);

		// Locate report definition
		ReportDefinition reportDefinition = reportService
				.getReportDefinitionByUuid(indicatorForm.getReportUuid());

		log.info("Report definition: " + reportDefinition);
		if (reportDefinition != null) { 
			
			PeriodIndicatorReportDefinition indicatorReportDefinition = 
				(PeriodIndicatorReportDefinition) reportDefinition;

			if ("add".equalsIgnoreCase(action)) { 
				
				// Find the selected indicator
				CohortIndicator cohortIndicator = (CohortIndicator)
					Context.getService(IndicatorService.class).getIndicatorByUuid(indicatorForm.getUuid());
								
				// Add cohort indicator to the report definition
				indicatorReportDefinition.addCohortIndicator(
						indicatorForm.getColumnKey(), 
						indicatorForm.getDisplayName(), 
						cohortIndicator,
						"startDate=${startDate},endDate=${endDate},location=${location}");
			} 
			else if ("remove".equalsIgnoreCase(action)) { 	
				
				log.info("Removing indicator " + indicatorKey + " from report " + indicatorReportDefinition);				
				indicatorReportDefinition.removeIndicator(indicatorKey);

				log.info("Removing column specification " + indicatorKey + " from report " + indicatorReportDefinition);				
				indicatorReportDefinition.removeColumnSpecification(columnKey);
	
				// We have to serialize everything individually
				DataSetDefinition dataSetDefinition = 
					indicatorReportDefinition.getDataSetDefinition();				
				Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);			
			}
				
			// Save the report definition with the new indicator
			reportService.saveReportDefinition(indicatorReportDefinition);
			
		}
		return new ModelAndView("redirect:/module/reporting/closeWindow.htm");
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
			if (indicator.getClass().isAssignableFrom(PeriodCohortIndicator.class)) 
				indicatorForm.setCohortIndicator((PeriodCohortIndicator)indicator);			
		}		
		return indicatorForm;
	}
	
}
