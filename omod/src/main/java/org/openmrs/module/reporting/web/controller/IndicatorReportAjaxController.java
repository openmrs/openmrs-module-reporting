package org.openmrs.module.reporting.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
						Context.getService(ReportDefinitionService.class).getDefinitionByUuid(propertyColonUuid[1]);				
					if ("name".equalsIgnoreCase(propertyColonUuid[0])) { 
						log.warn("update report name: " + value);
						reportDefinition.setName(value);
					}
					else if ("description".equalsIgnoreCase(propertyColonUuid[0])) { 
						log.warn("update report description: " + value);
						reportDefinition.setDescription(value);
					}
					log.warn("save report definition: ");
					Context.getService(ReportDefinitionService.class).saveDefinition(reportDefinition);
				}
			}
			response.getWriter().write(value);
		} 
		catch (Exception e) { 
			log.error("Error occurred while writing to response: ", e);
		}
			
	}
	
	
	
	@RequestMapping("/module/reporting/indicators/evaluateIndicator")
	public void evaluateIndicator(
			@RequestParam(value = "uuid", required=false) String uuid,
			HttpServletResponse response) { 
		
		try { 
			EvaluationContext context = new EvaluationContext();			
			CohortIndicator indicator = 
				(CohortIndicator) Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
			IndicatorResult result = Context.getService(IndicatorService.class).evaluate(indicator, context);
	
			response.getWriter().write(result.getValue().toString());		

		} 
		catch (Exception e) { 
			log.error("Error occurred while writing to response: ", e);
		}
	}
	

	@RequestMapping("/module/reporting/indicators/evaluatePeriodIndicator")
	public void evaluatePeriodIndicator(
			@RequestParam(value = "uuid", required=false) String uuid,
			HttpServletResponse response) { 

		
		try { 
			EvaluationContext context = new EvaluationContext();
	
			// FIXME - We need a way to pass in these values into this method
			context.addParameterValue("startDate", new Date());
			context.addParameterValue("endDate", new Date());
			context.addParameterValue("location", new Location());
			
			CohortIndicator indicator = 
				(CohortIndicator) Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
			IndicatorResult result = Context.getService(IndicatorService.class).evaluate(indicator, context);
	
			response.getWriter().write(result.getValue().toString());		

		} 
		catch (Exception e) { 
			log.error("Error occurred while writing to response: ", e);
		}
	}
	
	
}
