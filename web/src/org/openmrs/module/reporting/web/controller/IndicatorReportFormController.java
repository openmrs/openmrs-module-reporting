package org.openmrs.module.reporting.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportSchema;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
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
	
	@RequestMapping("/module/reporting/evaluateReport")		
	public void evaluateReport( 
   		@RequestParam(required=false, value="uuid") String uuid,
		@RequestParam(required=false, value="renderAs") String renderType,
		HttpServletRequest request,
		HttpServletResponse response,
		ModelMap model) throws Exception {
		
			
		ReportService reportService = (ReportService) Context.getService(ReportService.class);

		ReportSchema reportSchema = reportService.getReportSchemaByUuid(uuid);				
	
		if (reportSchema == null)
			throw new APIException("Unable to locate report schema with UUID " + uuid);
	
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.setBaseCohort(Context.getPatientSetService().getAllPatients());
		for (Parameter param : reportSchema.getParameters() ) { 
			log.info("Setting parameter " + param.getName() + " of class " + param.getClazz() + " = " + request.getParameter(param.getName()) );
			String paramValue = request.getParameter(param.getName());
			// TODO Need to convert from string to object
			// TODO Parameter needs a data type property
			// We don't have enough information at this point
			evalContext.addParameterValue(param.getName(), paramValue);
		}

		// Evaluate the report
		ReportData reportData = reportService.evaluate(reportSchema, evalContext);
	
		// Render the report
		ReportRenderer renderer = null;
		if ("csv".equalsIgnoreCase(renderType)) { 
			renderer = new CsvReportRenderer();
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + reportSchema.getName() + ".csv\"");  
		} 
		else if ("tsv".equalsIgnoreCase(renderType)) { 
			renderer = new TsvReportRenderer();
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + reportSchema.getName() + ".tsv\"");  
		} 
		else if ("xls".equalsIgnoreCase(renderType)) { 
			renderer = new TsvReportRenderer();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + reportSchema.getName() + ".xls\"");  
		} 
		else { 
			throw new APIException("Unknown rendering type");
		}
		renderer.render(reportData, null, response.getOutputStream()); 

		model.addAttribute("reportSchema", reportSchema);
	}
	
	
	@SuppressWarnings("unused")
	@ModelAttribute("reportSchema")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			HttpServletRequest request,
			ReportSchema reportSchema, 
			BindingResult bindingResult) {
		log.info("Inside submit() method");
		
			
		if (bindingResult.hasErrors()) {
			return showForm();
		}
		
		String action = request.getParameter("action");

		// Add indicators to a report schema
		if ("addIndicators".equalsIgnoreCase(action)) { 
						
			// We just create a new dataset definition each time
			CohortIndicatorDataSetDefinition datasetDefinition = new CohortIndicatorDataSetDefinition();
			
			// Dataset should be created implicitly (without the user knowing)
			datasetDefinition.setName(reportSchema.getName() + " Dataset");

			String [] selectedIndicatorUuids = request.getParameterValues("indicatorUuid");
			log.info("Indicators to add: " + selectedIndicatorUuids);
			if (selectedIndicatorUuids!=null) { 
				for (String uuid : selectedIndicatorUuids) { 
					
					// FIXME Assumes cohort indicators
					CohortIndicator indicator = (CohortIndicator)
						Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
					
					log.info("Found indicator" + indicator);					
					if (indicator != null) { 
						// FIXME: Adding indicator to dataset requires mapping from indicator to cohort definition					

						datasetDefinition.addIndicator(indicator.getName(), indicator, "");
						datasetDefinition.addColumnSpecification(indicator.getName(), 
								indicator.getDescription(), Number.class, indicator.getName(), null);						
												
						// Default behavior
						// Add all parameters to the indicator
						for (Parameter parameter : indicator.getCohortDefinition().getParameterizable().getParameters()) {
							indicator.addParameter(parameter);							
							datasetDefinition.addParameter(parameter);	
							reportSchema.addParameter(parameter);
						}						
					}										
				}
			}

			log.info("Add dataset definition: " + datasetDefinition);
			// Remove all existing dataset definitions
			// FIXME: Adding dataset to report requires mapping
			// (like "location=${report.location},effectiveDate=${report.reportDate}")
			reportSchema.getDataSetDefinitions().clear();			
			reportSchema.addDataSetDefinition(datasetDefinition, "");
		}
		
		
		Context.getService(ReportService.class).saveReportSchema(reportSchema);

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
