package org.openmrs.module.reporting.web.dimensions;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
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
    	
    	IndicatorService service = Context.getService(IndicatorService.class);
    	
    	// Get list of existing dimensions
    	boolean includeRet = (includeRetired == Boolean.TRUE);
    	List<Dimension> dimensions = service.getAllDimensions(includeRet);
    	model.addAttribute("dimensions", dimensions);
    	
    	// Get available dimension types
    	model.addAttribute("types", service.getDimensionTypes());
    }
    
    /**
     * Delete an existing dimension
     */
    @RequestMapping("/module/reporting/indicators/purgeDimension")
    public String purgeDimension(@RequestParam("uuid") String uuid) {
    	IndicatorService service = Context.getService(IndicatorService.class);
		Dimension dim = service.getDimensionByUuid(uuid);
		service.purgeDimension(dim);
		return "redirect:manageDimensions.form";
    }
}
