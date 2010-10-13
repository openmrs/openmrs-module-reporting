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
