/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ScriptingLanguage;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ScriptedCompositionPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngineManager;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ScriptedCompositionPatientDataDefinitionController {
	
	protected transient Log log = LogFactory.getLog(getClass());
	
	@SuppressWarnings("unchecked")
    @RequestMapping("/module/reporting/definition/scriptedCompositionPatientDataDefinition")
	public void showForm(ModelMap model, @RequestParam(value = "uuid", required = false) String uuid,
	                     @RequestParam(value = "copyFromUuid", required = false) String copyFromUuid) {
		model.put("scriptTypes", TemplateEngineManager.getAvailableTemplateEngineNames());
		if (uuid == null) {
			model.addAttribute("definition", new ScriptedCompositionPatientDataDefinition());
		} 
		else {
			PatientDataDefinition def = Context.getService(PatientDataService.class).getDefinitionByUuid(uuid);
			if (def instanceof ScriptedCompositionPatientDataDefinition) {
				ScriptedCompositionPatientDataDefinition definition = (ScriptedCompositionPatientDataDefinition) def;
				model.addAttribute("definition", definition);
			} 
			else {
				throw new RuntimeException("This definition is not of the right class");
			}
		}
	}
	
	@RequestMapping("/module/reporting/definition/scriptedCompositionPatientDataDefinitionSetComposition")
	public String setScriptCode(@RequestParam("uuid") String uuid, @RequestParam("scriptCode") String scriptCode,
	                            @RequestParam("scriptType") String scriptType) {
		PatientDataDefinition def = Context.getService(PatientDataService.class).getDefinitionByUuid(uuid);
		ScriptedCompositionPatientDataDefinition definition = (ScriptedCompositionPatientDataDefinition) def;
		definition.setScriptCode(scriptCode);
		definition.setScriptType(new ScriptingLanguage(scriptType));
		Context.getService(PatientDataService.class).saveDefinition(definition);
		return "redirect:/module/reporting/definition/manageDefinitions.form?type=" + PatientDataDefinition.class.getName();
	}
	
	/**
	 * Copies the composition patient data definition with the given uuid into another one with the
	 * same parameters and contained definitions, but blank name/description and composition string
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/module/reporting/definition/scriptedCompositionPatientDataDefinitionClone")
	public String cloneDefinition(WebRequest request, @RequestParam("name") String name,
	                              @RequestParam(value = "description", required = false) String description,
	                              @RequestParam("copyFromUuid") String copyFromUuid) {
		PatientDataDefinition def = Context.getService(PatientDataService.class).getDefinitionByUuid(copyFromUuid);
		ScriptedCompositionPatientDataDefinition from = (ScriptedCompositionPatientDataDefinition) def;
		
		ScriptedCompositionPatientDataDefinition clone = new ScriptedCompositionPatientDataDefinition();
		clone.setName(name);
		clone.setDescription(description);
		clone.setParameters(from.getParameters());
		clone.setContainedDataDefinitions(from.getContainedDataDefinitions());
		clone.setScriptCode(from.getScriptCode());
		clone.setScriptType(from.getScriptType());
		Context.getService(PatientDataService.class).saveDefinition(clone);
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:scriptedCompositionPatientDataDefinition.form?uuid=" + clone.getUuid();
	}
	
}
