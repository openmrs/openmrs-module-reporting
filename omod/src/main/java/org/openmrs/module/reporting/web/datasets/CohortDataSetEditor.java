/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.datasets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CohortDataSetEditor {

	protected static Log log = LogFactory.getLog(CohortDataSetEditor.class);
	
	/**
	 * Default Constructor
	 */
	public CohortDataSetEditor() { }

    /**
     * Retrieves either an existing or new report to edit
     */
    @RequestMapping("/module/reporting/datasets/cohortDatasetEditor")
    public void editCohortDataSet(ModelMap model,
		    		@RequestParam(required=false, value="uuid") String uuid,
		            @RequestParam(required=false, value="type") Class<? extends CohortCrossTabDataSetDefinition> type) {
    	
    	DataSetDefinitionService svc = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dsd = svc.getDefinition(uuid, type);
    	model.addAttribute("dsd", dsd);
    }
}
