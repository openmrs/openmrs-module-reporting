/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.datasets;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.IterableSqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IterableSqlDataSetEditor {

    @RequestMapping("/module/reporting/datasets/iterableSqlDataSetEditor")
    public void showForm(ModelMap model,
                         @RequestParam(value = "uuid", required = false) String uuid,
                         @RequestParam(value = "copyFromUuid", required = false) String copyFromUuid) {
        if (uuid == null) {
            model.addAttribute("definition", new IterableSqlDataSetDefinition());
        } else {
            DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
            if (def instanceof IterableSqlDataSetDefinition) {
                IterableSqlDataSetDefinition definition = (IterableSqlDataSetDefinition) def;
                model.addAttribute("definition", definition);
            } else {
                throw new RuntimeException("This definition does not correspond to an IterableSqlDataSetDefinition.");
            }
        }
    }

    @RequestMapping("/module/reporting/datasets/iterableSqlDataSetDefinitionAssignQueryString")
    public String saveQueryString(
            HttpSession httpSession,
            WebRequest webRequest,
            @RequestParam("uuid") String uuid,
            @RequestParam("queryString") String queryString) {

        DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
        IterableSqlDataSetDefinition definition = (IterableSqlDataSetDefinition) def;
        definition.setSql(queryString);

        List<Parameter> parameters = Context.getService(CohortQueryService.class).getNamedParameters(queryString);
        for (Parameter parameter : parameters) {
            if (definition.getParameter(parameter.getName()) == null)
                definition.addParameter(parameter);
        }

        // Save the definition
        Context.getService(DataSetDefinitionService.class).saveDefinition(definition);
        httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "reporting.IterableSqlDataSetDefinition.saved");
        httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, queryString);
        return "redirect:iterableSqlDataSetEditor.form?uuid=" + uuid;
    }

    /**
     * Copies the SQL DataSet definition with the given uuid into another one with the same
     * parameters and searches, but blank name/description and SQL string
     *
     * @return
     */
    @RequestMapping("/module/reporting/datasets/iterableSqlDataSetDefinitionClone")
    public String cloneDefinition(WebRequest request,
                                  @RequestParam("name") String name,
                                  @RequestParam(value = "description", required = false) String description,
                                  @RequestParam("copyFromUuid") String copyFromUuid) {
        DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(copyFromUuid);
        IterableSqlDataSetDefinition from = (IterableSqlDataSetDefinition) def;

        IterableSqlDataSetDefinition clone = new IterableSqlDataSetDefinition();
        clone.setId(null);
        clone.setUuid(null);
        clone.setName(name);
        clone.setDescription(description);
        clone.setParameters(from.getParameters());
        clone.setSql(from.getSql());
        Context.getService(DataSetDefinitionService.class).saveDefinition(clone);
        request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
        return "redirect:iterableSqlDataSetEditor.form?uuid=" + clone.getUuid();
    }
}
