package org.openmrs.module.reporting.web.datasets;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class SqlDataSetEditor {
	
	@RequestMapping("/module/reporting/datasets/sqlDataSetEditor")
	public void showForm(ModelMap model,
	                     @RequestParam(value="uuid", required=false) String uuid,
	                     @RequestParam(value="copyFromUuid", required=false) String copyFromUuid) {
		if (uuid == null) {			
			model.addAttribute("definition", new SqlDataSetDefinition());			
		} 
		else {
			DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
			if (def instanceof SqlDataSetDefinition) {
				SqlDataSetDefinition definition = (SqlDataSetDefinition) def;
				model.addAttribute("definition", definition);
			} 
			else {
				throw new RuntimeException("This definition is not of the right class");
			} 
		}
	}
	
	@RequestMapping("/module/reporting/datasets/sqlDataSetDefinitionAssignQueryString")
	public String saveQueryString(
			HttpSession httpSession,
			WebRequest webRequest,
			@RequestParam("uuid") String uuid,
	        @RequestParam("queryString") String queryString) {
		
		DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
		SqlDataSetDefinition definition = (SqlDataSetDefinition) def;
		definition.setSqlQuery(queryString);
		
		List<Parameter> parameters =  Context.getService(CohortQueryService.class).getNamedParameters(queryString);
		for (Parameter parameter : parameters) {
			if (definition.getParameter(parameter.getName()) == null)
				definition.addParameter(parameter);
		}

		// Save the definition
		Context.getService(DataSetDefinitionService.class).saveDefinition(definition);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "reporting.SqlDataSetDefinition.saved");
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, queryString);				
		return "redirect:sqlDataSetEditor.form?uuid=" + uuid;
	}
	
	/**
	 * Copies the SQL DataSet definition with the given uuid into another one with the same
	 * parameters and searches, but blank name/description and SQL string
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/module/reporting/datasets/sqlDataSetDefinitionClone")
	public String cloneDefinition(WebRequest request,
	                              @RequestParam("name") String name,
	                              @RequestParam(value="description", required=false) String description,
	                              @RequestParam("copyFromUuid") String copyFromUuid) {
		DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(copyFromUuid);
		SqlDataSetDefinition from = (SqlDataSetDefinition) def;

		SqlDataSetDefinition clone = new SqlDataSetDefinition();
		clone.setId(null);
		clone.setUuid(null);
		clone.setName(name);
		clone.setDescription(description);
		clone.setParameters(from.getParameters());
		clone.setSqlQuery(from.getSqlQuery());
		Context.getService(DataSetDefinitionService.class).saveDefinition(clone);
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:sqlDataSetEditor.form?uuid=" + clone.getUuid();
	}

}
