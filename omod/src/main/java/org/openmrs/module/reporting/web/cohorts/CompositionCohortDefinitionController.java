package org.openmrs.module.reporting.web.cohorts;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class CompositionCohortDefinitionController {
	
	@RequestMapping("/module/reporting/cohorts/compositionCohortDefinition")
	public void showForm(ModelMap model,
	                     @RequestParam(value="uuid", required=false) String uuid,
	                     @RequestParam(value="copyFromUuid", required=false) String copyFromUuid) {
		if (uuid == null) {
			model.addAttribute("definition", new CompositionCohortDefinition());
		} else {
			CohortDefinition def = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
			if (def instanceof CompositionCohortDefinition) {
				CompositionCohortDefinition definition = (CompositionCohortDefinition) def;
				model.addAttribute("definition", definition);
			} else {
				throw new RuntimeException("This definition is not of the right class");
			} 
		}
	}
	
	@RequestMapping("/module/reporting/cohorts/compositionCohortDefinitionSetComposition")
	public String setComposition(@RequestParam("uuid") String uuid,
	                           @RequestParam("compositionString") String compositionString) {
		CohortDefinition def = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
		CompositionCohortDefinition definition = (CompositionCohortDefinition) def;
		definition.setCompositionString(compositionString);
		Context.getService(CohortDefinitionService.class).saveDefinition(definition);
		return "redirect:/module/reporting/definition/manageDefinitions.form?type="+CohortDefinition.class.getName();
	}
	
	/**
	 * Copies the composition cohort definition with the given uuid into another one with the same
	 * parameters and searches, but blank name/description and composition string
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/module/reporting/cohorts/compositionCohortDefinitionClone")
	public String cloneDefinition(WebRequest request,
	                              @RequestParam("name") String name,
	                              @RequestParam(value="description", required=false) String description,
	                              @RequestParam("copyFromUuid") String copyFromUuid) {
		CohortDefinition def = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(copyFromUuid);
		CompositionCohortDefinition from = (CompositionCohortDefinition) def;

		CompositionCohortDefinition clone = new CompositionCohortDefinition();
		clone.setId(null);
		clone.setName(name);
		clone.setDescription(description);
		clone.setParameters(from.getParameters());
		clone.setSearches(from.getSearches());
		clone.setCompositionString(from.getCompositionString());
		Context.getService(CohortDefinitionService.class).saveDefinition(clone);
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved as a new copy", WebRequest.SCOPE_SESSION);
		return "redirect:compositionCohortDefinition.form?uuid=" + clone.getUuid();
	}

}
