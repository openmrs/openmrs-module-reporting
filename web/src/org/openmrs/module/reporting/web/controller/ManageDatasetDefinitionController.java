package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageDatasetDefinitionController {

	protected Log log = LogFactory.getLog(this.getClass());
	
    @RequestMapping("/module/reporting/manageDatasets")
    public String managerDatasetDefinitions(
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	DataSetDefinitionService service = 
    		Context.getService(DataSetDefinitionService.class);
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	
    	log.info("Service: " + service);
    	
    	List<DataSetDefinition> datasetDefinitions = new ArrayList<DataSetDefinition>();
    	
    	try { 
    		datasetDefinitions =     		
        		service.getAllDataSetDefinitions(retired);
    	} catch (Exception e) { 
    		log.error("Could not fetch dataset definitions", e);
    	}
    	
    	model.addAttribute("datasetDefinitions", datasetDefinitions);
    	
        return "/module/reporting/datasets/datasetManager";
    }
    
    @RequestMapping("/module/reporting/editDataset")
    public String editDatasetDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam(required=false, value="returnUrl") String returnUrl,
    		ModelMap model
    ) {
    	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type);
     	model.addAttribute("dataSetDefinition", dataSetDefinition);
        return "/module/reporting/editDataset";
    }
    
    @RequestMapping("/module/reporting/saveDataset")
    public String saveDatasetDefinition(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
    		ModelMap model
    ) {
    	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type);
    	dataSetDefinition.setName(name);
    	dataSetDefinition.setDescription(description);
    	
    	dataSetDefinition = 
    		Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(dataSetDefinition);
    	
        return "redirect:/module/reporting/editDataset.form?uuid="+dataSetDefinition.getUuid();
    }
    
    /**
     * 
     */
    protected DataSetDefinition getDataSetDefinition(String uuid, Class<? extends DataSetDefinition> type) {
    	DataSetDefinition dataSetDefinition = null;
    	if (StringUtils.hasText(uuid)) {
	    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
	    	dataSetDefinition = service.getDataSetDefinitionByUuid(uuid);
    	}
    	else if (type != null) {
     		try {
     			dataSetDefinition = type.newInstance();
    		}
    		catch (Exception e) {
    			throw new IllegalArgumentException("Unable to instantiate a DataSetDefinition of type: " + type);
    		}
    	}
    	else {
    		throw new IllegalArgumentException("You must supply either a uuid or a type");
    	}
    	return dataSetDefinition;
    }
}
