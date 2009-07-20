package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
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
	public ModelAndView showForm() {
		log.info("Inside show() method");

		return new ModelAndView(
				"/module/reporting/cohorts/cohortDefinitionForm");
	}	

	
	/**
	 * Processes the form when a user submits.  
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 */
	@ModelAttribute("cohortDefinition")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			CohortDefinition cohortDefinition, 
			BindingResult bindingResult) {
		log.info("Inside submit() method");
		
		log.info("Setting ID " + cohortDefinition.getId());
		log.info("Setting UUID " + cohortDefinition.getUuid());
		log.info("Setting name " + cohortDefinition.getName());
		
		log.info("Setting parameters " + cohortDefinition.getAvailableParameters());
		log.info("Setting bound parameters " + cohortDefinition.getParameters());
		
		if (cohortDefinition instanceof PatientCharacteristicCohortDefinition) { 
			PatientCharacteristicCohortDefinition pccd = 
				(PatientCharacteristicCohortDefinition) cohortDefinition;
			log.info("getGender: " + pccd.getGender() );
			log.info("getEffectiveDate: " + pccd.getEffectiveDate() );
			log.info("getAliveOnly: " + pccd.getAliveOnly() );
			log.info("getDeadOnly: " + pccd.getDeadOnly() );
			log.info("getMaxAge: " + pccd.getMaxAge());
			log.info("getMinAge: " + pccd.getMinAge() );
			log.info("getMaxBirthdate: " + pccd.getMaxBirthdate() );
			log.info("getMinBirthdate: " + pccd.getMinBirthdate() );
		}
			
		if (bindingResult.hasErrors()) {
			log.info("# errors: " + bindingResult.getErrorCount());
			log.info("errors: " + bindingResult.getAllErrors());
			return showForm();
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
			@RequestParam(value = "className", required=false) String className
	) {
		log.info("Inside formBackingObject(String, String) method with ");
		log.info("UUID=" + uuid + ", className=" + className);
		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);		
		CohortDefinition cohortDefinition = service.getCohortDefinitionByUuid(uuid);
		
		if (cohortDefinition == null) { 		
			log.info("Could not find cohort definition ... creating new one");
			try { 
				cohortDefinition = (CohortDefinition) Class.forName(className).newInstance();
				cohortDefinition.setName("New " + className);
			} 
			catch (Exception e) { 
				log.error("Could not instantiate cohort definition of class " + className );
			}
		} else { 
			log.info("Found cohort definition with uuid " + cohortDefinition.getUuid());			
		}

		
		
		return cohortDefinition;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private boolean isCancelButton(HttpServletRequest request) {		
		log.info("isCancelButton() method");
		if (WebUtils.hasSubmitParameter(request, "cancel")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	protected boolean suppressValidation(HttpServletRequest request) {
		log.info("suppressValidation() method");
		return isCancelButton(request);
	}	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	protected boolean suppressBinding(HttpServletRequest request) {
		log.info("suppressBinding() method");
		if("cancel".equals(request.getParameter("dispatch"))){
			return true;
		} else {
			return false;
		}
	}	
	
	
	
}
