package org.openmrs.module.reporting.web.dimensions;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the "Manage Indicators" page
 */
@Controller
public class ManageDimensionsController {

	/**
	 * List all dimensions
	 */
    @RequestMapping("/module/reporting/indicators/manageDimensions")
    public void manageDimensions(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	DimensionService service = Context.getService(DimensionService.class);
    	
    	// Get list of existing dimensions
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<Dimension> dimensions = service.getAllDefinitions(includeRet);
    	model.addAttribute("dimensions", dimensions);
    	
    	// Get available dimension types
    	model.addAttribute("types", service.getDefinitionTypes());
    }
    
    /**
     * Delete an existing dimension
     */
    @RequestMapping("/module/reporting/indicators/purgeDimension")
    public String purgeDimension(@RequestParam("uuid") String uuid) {
    	DimensionService service = Context.getService(DimensionService.class);
		Dimension dim = service.getDefinitionByUuid(uuid);
		service.purgeDefinition(dim);
		return "redirect:manageDimensions.form";
    }
}
