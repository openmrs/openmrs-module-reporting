package org.openmrs.module.reporting.web.dimensions;

import org.openmrs.api.context.Context;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.service.IndicatorService;
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

		IndicatorService service = Context.getService(IndicatorService.class);
		CohortDefinitionDimension dim = (CohortDefinitionDimension) service.getDimensionByUuid(uuid);
		model.addAttribute("dimension", dim);
	}

	@RequestMapping("/module/reporting/indicators/editCohortDefinitionDimensionRemoveOption")
    public String removeOption(
    		@RequestParam("uuid") String uuid,
    		@RequestParam("key") String keyToRemove) {

		IndicatorService service = Context.getService(IndicatorService.class);
		CohortDefinitionDimension dim = (CohortDefinitionDimension) service.getDimensionByUuid(uuid);
		dim.getCohortDefinitions().remove(keyToRemove);
		service.saveDimension(dim);
		
		return "redirect:editCohortDefinitionDimension.form?uuid=" + uuid;
	}
}
