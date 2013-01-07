package org.openmrs.module.reporting.web.indicator;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.SqlIndicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class EditSqlIndicatorController {
	
	@RequestMapping("/module/reporting/indicators/editSqlIndicator")
	public void showForm(ModelMap model,
	                     @RequestParam(value="uuid", required=false) String uuid,
	                     @RequestParam(value="copyFromUuid", required=false) String copyFromUuid) {
		if (uuid == null) {			
			model.addAttribute("definition", new SqlIndicator());			
		} 
		else {
			Indicator def = Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
			if (def instanceof SqlIndicator) {
				SqlIndicator definition = (SqlIndicator) def;
				model.addAttribute("definition", definition);
			} 
			else {
				throw new RuntimeException("This definition is not of the right class");
			} 
		}
	}
	
	@RequestMapping("/module/reporting/indicators/saveSqlIndicatorQueryString")
	public String saveQueryString(
			HttpSession httpSession,
			WebRequest webRequest,
			@RequestParam("uuid") String uuid,
	        @RequestParam("queryString") String queryString) {
		
		Indicator def = Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
		SqlIndicator definition = (SqlIndicator) def;
		definition.setSql(queryString);
		
		List<Parameter> parameters =  Context.getService(CohortQueryService.class).getNamedParameters(queryString);
		for (Parameter parameter : parameters) {
			if (definition.getParameter(parameter.getName()) == null) {
				definition.addParameter(parameter);
			}
		}

		// Save the definition
		Context.getService(IndicatorService.class).saveDefinition(definition);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "reporting.saved");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, queryString);				
		return "redirect:editSqlIndicator.form?uuid=" + uuid;
	}
	
	/**
	 * Copies the SQL Indicator with the given uuid into another one with the same
	 * parameters and searches, but blank name/description and SQL string
	 */
	@RequestMapping("/module/reporting/indicators/cloneSqlIndicator")
	public String cloneDefinition(WebRequest request,
	                              @RequestParam("name") String name,
	                              @RequestParam(value="description", required=false) String description,
	                              @RequestParam("copyFromUuid") String copyFromUuid) {
		Indicator def = Context.getService(IndicatorService.class).getDefinitionByUuid(copyFromUuid);
		SqlIndicator from = (SqlIndicator) def;

		SqlIndicator clone = new SqlIndicator();
		clone.setId(null);
		clone.setUuid(null);
		clone.setName(name);
		clone.setDescription(description);
		clone.setParameters(from.getParameters());
		clone.setSql(from.getSql());
		Context.getService(IndicatorService.class).saveDefinition(clone);
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:editSqlIndicator.form?uuid=" + clone.getUuid();
	}

}
