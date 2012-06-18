package org.openmrs.module.reporting.web.indicator;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.CohortIndicator.IndicatorType;
import org.openmrs.module.reporting.indicator.aggregation.Aggregator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditCohortIndicatorController {
	
	@RequestMapping("/module/reporting/indicators/editCohortIndicator")
	public void editCohortIndicator(ModelMap model,
	                     			@RequestParam(value="uuid", required=false) String uuid) {
		if (uuid == null) {
			CohortIndicator indicator = new CohortIndicator();
			model.addAttribute("indicator", indicator);
		} else {
			Indicator indicator = Context.getService(IndicatorService.class).getDefinitionByUuid(uuid);
			model.addAttribute("indicator", indicator);
		}
	}
	
	@RequestMapping("/module/reporting/indicators/saveBaseCohortIndicator")
	public String saveBaseCohortIndicator(ModelMap model,
	                     				@RequestParam(value="uuid", required=false) String uuid,
	                     				@RequestParam(value="name", required=true) String name,
	                     				@RequestParam(value="description", required=false) String description,
	                     				@RequestParam(value="type", required=true) String type,
	                     				@RequestParam(value="parameters", required=false) String[] parameters) {
		
		IndicatorService svc = Context.getService(IndicatorService.class);
		CohortIndicator indicator = null;
		if (StringUtils.hasText(uuid)) {
			indicator = (CohortIndicator)svc.getDefinitionByUuid(uuid);
		} else {
			indicator = new CohortIndicator();
			indicator.getParameters().clear();
			if (parameters != null) {
				for (String s : parameters) {
					if (ReportingConstants.START_DATE_PARAMETER.getName().equals(s)) {
						indicator.addParameter(ReportingConstants.START_DATE_PARAMETER);
					}
					if (ReportingConstants.END_DATE_PARAMETER.getName().equals(s)) {
						indicator.addParameter(ReportingConstants.END_DATE_PARAMETER);
					}
					if (ReportingConstants.LOCATION_PARAMETER.getName().equals(s)) {
						indicator.addParameter(ReportingConstants.LOCATION_PARAMETER);
					}
				}
			}
		}
		indicator.setName(name);
		indicator.setDescription(description);
		indicator.setType(IndicatorType.valueOf(type));
		indicator = svc.saveDefinition(indicator);
		
		if (StringUtils.hasText(uuid)) {
			return "redirect:/module/reporting/closeWindow.htm";
		}
		else {
			return "redirect:editCohortIndicator.form?uuid="+indicator.getUuid();
		}
	}
	
	@RequestMapping("/module/reporting/indicators/saveLogicCohortIndicator")
	@SuppressWarnings("unchecked")
	public String saveLogicCohortIndicator(ModelMap model,
	                     				  @RequestParam(value="uuid", required=true) String uuid,
	                     				  @RequestParam(value="aggregator", required=true) String aggregator,
	                     				 @RequestParam(value="logicExpression", required=true) String logicExpression) {
		
		IndicatorService svc = Context.getService(IndicatorService.class);
		CohortIndicator indicator = (CohortIndicator)svc.getDefinitionByUuid(uuid);
		try {
			Class<? extends Aggregator> a = (Class<? extends Aggregator>) Context.loadClass(aggregator);
			indicator.setAggregator(a);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to find class for aggregator: " + aggregator);
		}
		indicator.setLogicExpression(logicExpression);
		svc.saveDefinition(indicator);
		return "redirect:/module/reporting/closeWindow.htm";
	}
}
