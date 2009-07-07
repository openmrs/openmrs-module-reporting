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
import org.openmrs.module.report.ReportSchema;
import org.openmrs.module.report.service.ReportService;
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
@RequestMapping("/module/reporting/indicatorReport")
public class IndicatorReportFormController {

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
		
		return new ModelAndView("/module/reporting/reports/indicatorReportEditor");
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
	
	
	@ModelAttribute("reportSchema")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			HttpServletRequest request,
			ReportSchema reportSchema, 
			BindingResult bindingResult) {
		log.info("Inside submit() method");
		
		log.info("Setting ID " + reportSchema.getId());
		log.info("Setting UUID " + reportSchema.getUuid());
		log.info("Setting name " + reportSchema.getName());
		
		log.info("Setting parameters " + reportSchema.getParameters());
			
		if (bindingResult.hasErrors()) {
			log.info("# errors: " + bindingResult.getErrorCount());
			log.info("errors: " + bindingResult.getAllErrors());
			return showForm();
		}
		
		String action = request.getParameter("action");

		// Add indicators to a report schema
		if ("addIndicators".equalsIgnoreCase(action)) { 
			CohortIndicatorDataSetDefinition dataSetDefinition = 
				new CohortIndicatorDataSetDefinition();
			
			// Dataset should be created implicitly (without the user knowing)
			dataSetDefinition.setName(reportSchema.getName() + " Cohort Indicator Dataset");

			String [] selectedUuids = request.getParameterValues("selectedUuid");
			if (selectedUuids!=null) { 				
				for (String uuid : selectedUuids) { 
					
					// FIXME Assumes cohort indicators
					CohortIndicator indicator = (CohortIndicator)
						Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
					if (indicator != null) { 
					
						//CohortDefinition cohortDefinition = 
						//	indicator.getCohortDefinition().getParameterizable();
						//cohortDefinition.getParameters();
						
						// FIXME 
						String mapping = 
							"indicator.location=${dataset.location},indicator.date=${dataset.date}";
						dataSetDefinition.addIndicator(indicator.getName(), indicator, mapping);
										
					}										
				}
			}

			// Dataset needs to know what parameters it needs
			dataSetDefinition.addParameter(new Parameter("dataset.location", "Location Parameter", Location.class, null, true, false));
			dataSetDefinition.addParameter(new Parameter("dataset.date", "Date Parameter", Date.class, null, true, false));
			

			
			// Remove all existing dataset definitions
			reportSchema.getDataSetDefinitions().clear();
			reportSchema.addDataSetDefinition(dataSetDefinition, "location=${report.location},effectiveDate=${report.reportDate}");
			
			
		}
		// Otherwise this is a simple save operation
		else { 
			Context.getService(ReportService.class).saveReportSchema(reportSchema);
		}
		return new ModelAndView("redirect:/module/reporting/manageReports.list");
	}
	
	
	@ModelAttribute("reportSchema")
	public ReportSchema formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "className", required=false) String className
	) {
		log.info("Inside formBackingObject(String, String) method with ");
		log.info("UUID=" + uuid + ", className=" + className);
		
		ReportService service = Context.getService(ReportService.class);		
		ReportSchema reportSchema = service.getReportSchemaByUuid(uuid);
		
		if (reportSchema == null) { 		
			reportSchema = new ReportSchema();
		} else { 
			log.info("Found reportSchema with uuid " + reportSchema.getUuid());			
		}		
		
		return reportSchema;
	}


	
	
	
}
