/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.data.BaseSqlDataDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
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
			@RequestParam(value="type", required=false) Class<? extends Definition> type,
			@RequestParam("uuid") String uuid,
	        @RequestParam("queryString") String queryString) {

		DefinitionService svc = DefinitionContext.getDefinitionService(type);

		Definition definition = null;
		if (BaseSqlQuery.class.isAssignableFrom(type)) {
            BaseSqlQuery sqlQuery = (BaseSqlQuery) svc.getDefinitionByUuid(uuid);
            sqlQuery.setQuery(queryString);
            definition = sqlQuery;
        }
        else if (BaseSqlDataDefinition.class.isAssignableFrom(type)) {
            BaseSqlDataDefinition sqlDataDefinition = (BaseSqlDataDefinition) svc.getDefinitionByUuid(uuid);
            sqlDataDefinition.setQuery(queryString);
            definition = sqlDataDefinition;
        }
        else {
		    throw new IllegalArgumentException("Only able to save Sql Query and Sql Data Definition types");
        }
		
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
								  @RequestParam(value="type", required=false) Class<? extends Definition> type,
	                              @RequestParam("copyFromUuid") String copyFromUuid) throws Exception {

		DefinitionService svc = DefinitionContext.getDefinitionService(type);

		Definition newDefinition = null;

        if (BaseSqlQuery.class.isAssignableFrom(type)) {
            BaseSqlQuery from = (BaseSqlQuery) svc.getDefinitionByUuid(copyFromUuid);
            BaseSqlQuery clone = (BaseSqlQuery)type.newInstance();
            clone.setQuery(from.getQuery());
            clone.setParameters(from.getParameters());
            newDefinition = clone;
        }
        else if (BaseSqlDataDefinition.class.isAssignableFrom(type)) {
            BaseSqlDataDefinition from = (BaseSqlDataDefinition) svc.getDefinitionByUuid(copyFromUuid);
            BaseSqlDataDefinition clone = (BaseSqlDataDefinition)type.newInstance();
            clone.setQuery(from.getQuery());
            clone.setParameters(from.getParameters());
            newDefinition = clone;
        }
        else {
            throw new IllegalArgumentException("Only able to clone Sql Query and Sql Data Definition types");
        }

        newDefinition.setName(name);
        newDefinition.setDescription(description);
        svc.saveDefinition(newDefinition);

		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:sqlDefinition.form?type="+type.getName()+"&uuid=" + newDefinition.getUuid();
	}

}
