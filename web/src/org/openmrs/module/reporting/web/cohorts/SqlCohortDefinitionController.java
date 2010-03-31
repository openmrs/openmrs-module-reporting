package org.openmrs.module.reporting.web.cohorts;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.query.definition.SqlQueryDefinition;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class SqlCohortDefinitionController {
	
	@RequestMapping("/module/reporting/cohorts/sqlCohortDefinition")
	public void showForm(ModelMap model,
	                     @RequestParam(value="uuid", required=false) String uuid,
	                     @RequestParam(value="copyFromUuid", required=false) String copyFromUuid) {
		if (uuid == null) {			
			SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
			sqlCohortDefinition.setQueryDefinition(
					new SqlQueryDefinition("SELECT patient_id FROM patient WHERE patient.voided = false"));
			model.addAttribute("definition", sqlCohortDefinition);			
		} else {
			CohortDefinition def = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
			if (def instanceof SqlCohortDefinition) {
				SqlCohortDefinition definition = (SqlCohortDefinition) def;
				model.addAttribute("definition", definition);
			} else {
				throw new RuntimeException("This definition is not of the right class");
			} 
		}
	}
	
	@RequestMapping("/module/reporting/cohorts/sqlCohortDefinitionAssignQueryString")
	public String saveQueryString(
			HttpSession httpSession,
			WebRequest webRequest,
			@RequestParam("uuid") String uuid,
	        @RequestParam("queryString") String queryString) {
		CohortDefinition def = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
		SqlCohortDefinition definition = (SqlCohortDefinition) def;
		definition.setQueryDefinition(new SqlQueryDefinition(queryString));
		
		Context.getService(CohortDefinitionService.class).saveDefinition(definition);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "reporting.SqlCohortDefinition.success");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, queryString);				
		webRequest.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "The SQL query [" + queryString + "] has been saved!", 
				WebRequest.SCOPE_SESSION);
		return "redirect:sqlCohortDefinition.form?uuid=" + uuid;
		//return "redirect:manageCohortDefinitions.form";
	}
	
	/**
	 * Copies the SQL cohort definition with the given uuid into another one with the same
	 * parameters and searches, but blank name/description and SQL string
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/module/reporting/cohorts/sqlCohortDefinitionClone")
	public String cloneDefinition(WebRequest request,
	                              @RequestParam("name") String name,
	                              @RequestParam(value="description", required=false) String description,
	                              @RequestParam("copyFromUuid") String copyFromUuid) {
		CohortDefinition def = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(copyFromUuid);
		SqlCohortDefinition from = (SqlCohortDefinition) def;

		SqlCohortDefinition clone = new SqlCohortDefinition();
		clone.setId(null);
		clone.setUuid(null);
		clone.setName(name);
		clone.setDescription(description);
		clone.setParameters(from.getParameters());
		clone.setQueryDefinition(from.getQueryDefinition());
		Context.getService(CohortDefinitionService.class).saveDefinition(clone);
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:sqlCohortDefinition.form?uuid=" + clone.getUuid();
	}

}
