package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
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
@RequestMapping("/module/reporting/cohortDefinition")
public class CohortDefinitionFormController {

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
	
	
    /**
     * Shows the form.  This method is called after the formBackingObject()
     * method below.
     * 
     * @return	the form model and view
     */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView setupForm() {
		log.info("Inside show() method");

		return new ModelAndView("/module/reporting/cohorts/cohortDefinitionForm");
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
			@ModelAttribute("cohortDefinition") CohortDefinition cohortDefinition, 
			BindingResult bindingResult) {
					
		if (bindingResult.hasErrors()) {
			return setupForm();
		}
		Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);
		return new ModelAndView("redirect:/module/reporting/manageCohortDefinitions.list");

	}
	
	/**
	 * Populates the form backing object for the 
	 * 
	 * @param uuid
	 * @param className
	 * @return
	 */
	@ModelAttribute("cohortDefinition")
	public CohortDefinition formBackingObject(
			@RequestParam(value = "uuid", required=false) String uuid,
			@RequestParam(value = "className", required=false) String className) {
		log.info("Inside formBackingObject(String, String) method with ");
		log.info("UUID=" + uuid + ", className=" + className);
		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);		
		CohortDefinition cohortDefinition = service.getCohortDefinitionByUuid(uuid);
		
		if (cohortDefinition == null) { 		
			try { 
				cohortDefinition = (CohortDefinition) Context.loadClass(className).newInstance();
				cohortDefinition.setName("New " + className);
			} 
			catch (Exception e) { 
				log.error("Could not instantiate cohort definition of class " + className );
			}
		} 
		else { 
			log.info("Found cohort definition with uuid " + cohortDefinition.getUuid());			
		}
		return cohortDefinition;
	}
	
	
	
}
