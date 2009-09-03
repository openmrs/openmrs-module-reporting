package org.openmrs.module.reporting.web.controller;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.ParameterException;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.IndicatorReportRenderer;
import org.openmrs.module.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.renderer.XmlReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RenderReportFormController {

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
	
    @RequestMapping("/module/reporting/reports/renderIndicatorReportData")
	public ModelAndView renderIndicatorReport() {     	
    	return new ModelAndView("/module/reporting/reports/renderIndicatorReportData");    	
    }
    
	/**
	 * 
	 * @return
	 */	
    @RequestMapping("/module/reporting/reports/renderReport")
	public ModelAndView renderReport(
			HttpServletRequest request,	
			HttpServletResponse response,	
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "format", required=false) String format) throws Exception {
					
		ReportDefinition reportDefinition = 
			Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);
		
		ModelAndView model = new ModelAndView("/module/reporting/reports/renderReportForm");
	
		if (reportDefinition == null) { 
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Report does not exist");		
			// TODO What page should we redirect to in this case
			return model;
		}
		
		try { 
			EvaluationContext context = new EvaluationContext();
			
			ReportData reportData = null;
			// Get report data from the session 
			Object results = request.getSession().getAttribute("results");
			if (results != null && results instanceof ReportData) { 
				reportData = (ReportData) results;
			}
			
			// If session does not contain report data, we need to re-evaluate the report definition
			if (reportData == null) { 		
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Report data does not exist");		
				// TODO Figure out What page we should redirect to in this case
				return model;

			}
						
			// Render the report definition
			if (reportData != null) { 	
				String filename = reportDefinition.getName() + "." + format;
				
				log.info("Rendering report data as " + format + " file named " + filename);
				StringWriter writer = new StringWriter();					
				
				if ("csv".equalsIgnoreCase(format)) { 
					response.setContentType("text/csv");				
					response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");  				
					new CsvReportRenderer().render(reportData, response.getOutputStream());
				} 
				else if ("tsv".equalsIgnoreCase(format)) { 
					response.setContentType("text/tsv");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");  				
					new TsvReportRenderer().render(reportData, response.getOutputStream());					
				}
				else if ("xml".equalsIgnoreCase(format)) { 
					response.setContentType("text/xml");				
					response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");  				
					new XmlReportRenderer().render(reportData, response.getOutputStream());					
				}
				else if ("indicator".equalsIgnoreCase(format)) {  
					model = new ModelAndView("/module/reporting/reports/renderReportData");
					new IndicatorReportRenderer().render(reportData, writer);
					model.addObject("renderedData", writer.toString());
				}
				else { 
					new SimpleHtmlReportRenderer().render(reportData, response.getOutputStream());
				}							
				request.getSession().removeAttribute("results");
				
			}
		}
		catch (ParameterException e) { 				
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Unable to render report: " + e.getMessage());
			// By default send back to the query parameter form
			response.sendRedirect(request.getContextPath() + 
					"/module/reporting/parameters/queryParameter.form?uuid=" + uuid + "&type=" + reportDefinition.getClass().getName());
			return null;
		}
		return model;
	}	
}
