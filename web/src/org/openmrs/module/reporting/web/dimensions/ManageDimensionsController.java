package org.openmrs.module.reporting.web.dimensions;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.dimension.service.DimensionService;
import org.openmrs.module.indicator.dimension.Dimension;
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
    @RequestMapping("/module/reporting/dimensions/manageDimensions")
    public void manageDimensions(ModelMap model, 
    				@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
    	
    	DimensionService service = Context.getService(DimensionService.class);
    	
    	// Get list of existing dimensions
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<Dimension> dimensions = service.getAllDimensions(includeRet);
    	model.addAttribute("dimensions", dimensions);
    	
    	// Get available dimension types
    	model.addAttribute("types", service.getDimensionTypes());
    }
}
