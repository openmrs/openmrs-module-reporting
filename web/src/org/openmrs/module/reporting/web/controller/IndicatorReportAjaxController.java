package org.openmrs.module.reporting.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.openmrs.module.reporting.web.model.IndicatorReportForm;
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
public class IndicatorReportAjaxController {

	private Log log = LogFactory.getLog(this.getClass());

	
	@RequestMapping("/module/reporting/reports/editIndicatorReport")
	public void editIndicatorReport(
			@RequestParam(value = "id", required=false) String id,
			@RequestParam(value = "value", required=false) String value,
			HttpServletResponse response) { 
		log.error("Inside editIndicatorReport() method: id=" + id + ", value=" + value );

		try { 
			if (id != null) { 
				String [] propertyColonUuid = id.split(":");
				if (propertyColonUuid.length == 2) { 
					ReportDefinition reportDefinition = 
						Context.getService(ReportService.class).getReportDefinitionByUuid(propertyColonUuid[1]);				
					if ("name".equalsIgnoreCase(propertyColonUuid[0])) { 
						log.warn("update report name: " + value);
						reportDefinition.setName(value);
					}
					else if ("description".equalsIgnoreCase(propertyColonUuid[0])) { 
						log.warn("update report description: " + value);
						reportDefinition.setDescription(value);
					}
					log.warn("save report definition: ");
					Context.getService(ReportService.class).saveReportDefinition(reportDefinition);
				}
			}
			response.getWriter().write(value);
		} 
		catch (Exception e) { 
			log.error("Error occurred while writing to response: ", e);
		}
			
	}
	
	@RequestMapping("/module/reporting/reports/evaluateIndicator")
	public void evaluateIndicator(
			@RequestParam(value = "uuid", required=false) String uuid,
			HttpServletResponse response) { 

		
		try { 
			EvaluationContext context = new EvaluationContext();
			
			CohortIndicator indicator = 
				(CohortIndicator) Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
			IndicatorResult result = Context.getService(IndicatorService.class).evaluate(indicator, context);
	
			response.getWriter().write(result.getValue().toString());		

		} 
		catch (Exception e) { 
			log.error("Error occurred while writing to response: ", e);
		}
	}
	
	
}
