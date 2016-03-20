package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ManageIndicatorsController {

	protected Log log = LogFactory.getLog(this.getClass());

    @RequestMapping("/module/reporting/indicators/manageIndicators")
    public void manageIndicators(ModelMap model) {
    	List<Indicator> indicators = Context.getService(IndicatorService.class).getAllDefinitions(false);
    	model.addAttribute("indicators", indicators);
    }

    @RequestMapping("/module/reporting/indicators/purgeIndicator")
    public String purgeIndicator(@RequestParam(required=false, value="uuid") String uuid) {
    	Indicator indicator = Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
    	if (indicator != null) {     		
    		log.debug("Purging indicator: " + indicator);
    		Context.getService(IndicatorService.class).purgeDefinition(indicator);
    	}
        return "redirect:/module/reporting/indicators/manageIndicators.form";
    }
}
