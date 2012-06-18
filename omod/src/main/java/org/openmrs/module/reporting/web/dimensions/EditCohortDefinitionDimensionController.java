package org.openmrs.module.reporting.web.dimensions;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditCohortDefinitionDimensionController {

	@RequestMapping("/module/reporting/indicators/editCohortDefinitionDimension")
    public void editReportDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
    		ModelMap model) {

		DimensionService service = Context.getService(DimensionService.class);
		CohortDefinitionDimension dim = (CohortDefinitionDimension) service.getDefinitionByUuid(uuid);
		model.addAttribute("dimension", dim);
	}

	@RequestMapping("/module/reporting/indicators/editCohortDefinitionDimensionRemoveOption")
    public String removeOption(
    		@RequestParam("uuid") String uuid,
    		@RequestParam("key") String keyToRemove) {

		DimensionService service = Context.getService(DimensionService.class);
		CohortDefinitionDimension dim = (CohortDefinitionDimension) service.getDefinitionByUuid(uuid);
		dim.getCohortDefinitions().remove(keyToRemove);
		service.saveDefinition(dim);
		
		return "redirect:editCohortDefinitionDimension.form?uuid=" + uuid;
	}
}
