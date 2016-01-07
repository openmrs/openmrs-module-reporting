package org.openmrs.module.reporting.web.controller;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.IndicatorReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.XmlReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
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
	
    @RequestMapping("/module/reporting/reports/renderDefaultReport")
	public ModelAndView renderIndicatorReport() {     	
    	return new ModelAndView("/module/reporting/reports/renderDefaultReport");    	
    }

}
