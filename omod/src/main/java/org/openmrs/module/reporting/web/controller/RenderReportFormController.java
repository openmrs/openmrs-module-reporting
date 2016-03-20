package org.openmrs.module.reporting.web.controller;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class RenderReportFormController {

	/* Date format */
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(ymd, true)); 
    }    
	
    @RequestMapping("/module/reporting/reports/renderDefaultReport")
	public ModelAndView renderDefaultReport() {
    	return new ModelAndView("/module/reporting/reports/renderDefaultReport");    	
    }

}
