package org.openmrs.module.reporting.web.dimensions;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndicatorInfoController {

	@InitBinder
	public void addBinders(WebDataBinder binder) {
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping("/module/reporting/indicators/indicatorInfo")
	public void showIndicatorInfo(ModelMap model,
	                              @RequestParam("uuid") String uuid,
	                              @RequestParam("location") Location location) {
		model.addAttribute("indicator", Context.getService(IndicatorService.class).getDefinitionByUuid(uuid));
		model.addAttribute("location", location);
	}
	
}
