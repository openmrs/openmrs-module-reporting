package org.openmrs.module.reporting.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.BaseSqlQuery;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class SqlDefinitionController {
	
	@RequestMapping("/module/reporting/definition/sqlDefinition")
	public void showForm(ModelMap model,
						 @RequestParam(value="type", required=true) Class<? extends BaseSqlQuery> type,
	                     @RequestParam(value="uuid", required=false) String uuid,
	                     @RequestParam(value="copyFromUuid", required=false) String copyFromUuid) throws Exception {

		Object definition = null;
		if (uuid == null) {
			definition = type.newInstance();
		}
		else {
			definition = DefinitionContext.getDefinitionService(type).getDefinitionByUuid(uuid);
		}
		model.addAttribute("definition", definition);
		model.addAttribute("type", type);
	}
	
	@RequestMapping("/module/reporting/definition/sqlDefinitionAssignQueryString")
	public String saveQueryString(
			HttpSession httpSession,
			WebRequest webRequest,
			@RequestParam(value="type", required=false) Class<? extends BaseSqlQuery> type,
			@RequestParam("uuid") String uuid,
	        @RequestParam("queryString") String queryString) {

		DefinitionService svc = DefinitionContext.getDefinitionService(type);
		BaseSqlQuery definition = (BaseSqlQuery)svc.getDefinitionByUuid(uuid);
		definition.setQuery(queryString);
		
		// Add all new named parameters to the definition before saving.
		List<Parameter> parameters =  
			Context.getService(CohortQueryService.class).getNamedParameters(queryString);
		for (Parameter parameter : parameters) {
			if (definition.getParameter(parameter.getName()) == null)
				definition.addParameter(parameter);
		}

		// Save the sql cohort definition
		svc.saveDefinition(definition);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "reporting.saved");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, queryString);				
		return "redirect:sqlDefinition.form?type="+type.getName()+"&uuid=" + uuid;
	}
	
	/**
	 * Copies the SQL cohort definition with the given uuid into another one with the same
	 * parameters and searches, but blank name/description and SQL string
	 */
	@RequestMapping("/module/reporting/definition/sqlDefinitionClone")
	public String cloneDefinition(WebRequest request,
	                              @RequestParam("name") String name,
	                              @RequestParam(value="description", required=false) String description,
								  @RequestParam(value="type", required=false) Class<? extends BaseSqlQuery> type,
	                              @RequestParam("copyFromUuid") String copyFromUuid) throws Exception {

		DefinitionService svc = DefinitionContext.getDefinitionService(type);
		BaseSqlQuery from = (BaseSqlQuery)svc.getDefinitionByUuid(copyFromUuid);

		BaseSqlQuery clone = type.newInstance();
		clone.setName(name);
		clone.setDescription(description);
		clone.setParameters(from.getParameters());
		clone.setQuery(from.getQuery());
		svc.saveDefinition(clone);
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:sqlDefinition.form?type="+type.getName()+"&uuid=" + clone.getUuid();
	}

}
