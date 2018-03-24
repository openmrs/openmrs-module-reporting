/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
