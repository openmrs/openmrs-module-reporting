package org.openmrs.module.reporting.web.reports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.report.util.ReportUtil.InitialDataElement;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Automatically creates initial cohort definitions, dimensions, and indicators
 */
@Controller
@RequestMapping("/module/reporting/reports/createInitial")
public class CreateInitialController {

	@RequestMapping(method=RequestMethod.GET)
	public void showOptions(ModelMap model) {
		Set<String> alreadyNames = new HashSet<String>();
		for (CohortDefinition def : Context.getService(CohortDefinitionService.class).getAllDefinitions(true)) {
			alreadyNames.add(CohortDefinition.class.getName() + " " + def.getName());
		}
		for (Dimension def : Context.getService(IndicatorService.class).getAllDimensions(true)) {
			alreadyNames.add(Dimension.class.getName() + " " + def.getName());
		}
		for (Indicator def : Context.getService(IndicatorService.class).getAllIndicators(true)) {
			alreadyNames.add(Indicator.class.getName() + " " + def.getName());
		}
		
		List<InitialDataElement> toCreate = ReportUtil.getInitialDataElements();
		List<InitialDataElement> already = new ArrayList<InitialDataElement>();
		for (Iterator<InitialDataElement> i = toCreate.iterator(); i.hasNext(); ) {
			InitialDataElement e = i.next();
			if (alreadyNames.contains(e.getClazz().getName() + " " + e.getName())) {
				i.remove();
				already.add(e);
			}
		}

		model.addAttribute("toCreate", toCreate);
		model.addAttribute("already", already);
	}
	
	
	@RequestMapping(method=RequestMethod.POST)
	public String handleSubmit(WebRequest request,
	                           @RequestParam(value="create", required=false) Set<String> toCreate) {
		List<String> errorMessages = new ArrayList<String>();
		for (InitialDataElement e : ReportUtil.getInitialDataElements()) {
			if (toCreate.contains(e.getClazz().getName() + " " + e.getName())) {
				try {
					e.apply();
				} catch (Exception ex) {
					errorMessages.add(ex.getMessage());
				}
			}
		}
		
		if (errorMessages.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String err : errorMessages) {
				sb.append(err).append("<br/>");
			}
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, sb.toString(), WebRequest.SCOPE_SESSION);
		}

		return "redirect:createInitial.form";
	}
		
}
