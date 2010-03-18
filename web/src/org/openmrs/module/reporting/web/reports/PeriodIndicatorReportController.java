package org.openmrs.module.reporting.web.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.propertyeditor.IndicatorEditor;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class PeriodIndicatorReportController {

	@InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Indicator.class, new IndicatorEditor());
    }
	
	@ModelAttribute("indicators")
	public List<Indicator> getIndicators() {
		return Context.getService(IndicatorService.class).getAllDefinitions(false);
	}

	@RequestMapping("/module/reporting/reports/periodIndicatorReport")
	public void showForm(ModelMap model,
	                     @RequestParam(value="uuid", required=false) String uuid) {
		if (uuid == null) {
			model.addAttribute("report", new PeriodIndicatorReportDefinition());
		} else {
			ReportDefinition def = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
			if (def instanceof PeriodIndicatorReportDefinition) {
				PeriodIndicatorReportDefinition report = (PeriodIndicatorReportDefinition) def;
				PeriodIndicatorReportUtil.ensureDataSetDefinition(report);
				model.addAttribute("report", report);
			} else {
				throw new RuntimeException("This report is not of the right class");
			} 
		}
	}

	
	@RequestMapping("/module/reporting/reports/periodIndicatorReportAddColumn")
	public String addColumn(@RequestParam("uuid") String uuid,
	                        @RequestParam("key") String key,
	                        @RequestParam("displayName") String displayName,
	                        @RequestParam("indicator") Indicator indicator,
	                        WebRequest request) {
		
		PeriodIndicatorReportDefinition report = (PeriodIndicatorReportDefinition) Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
		
		// special code because I don't think I can do a RequestParam for: Map<String, String> dimensionOptions
		Map<String, String> dimensionOptions = new HashMap<String, String>();
		Map<String, String> params = (Map<String, String>) request.getParameterMap();
		for (String param : params.keySet()) {
			if (param.startsWith("dimensionOption_")) {
				String dimName = param.substring("dimensionOption_".length());
				String dimValue = request.getParameter(param);
				if (StringUtils.hasText(dimValue)) {
					dimensionOptions.put(dimName, dimValue);
				}
			}
		}
		
		PeriodIndicatorReportUtil.addColumn(report, key, displayName, (CohortIndicator) indicator, dimensionOptions);
		
		return "redirect:periodIndicatorReport.form?uuid=" + uuid;
	} 
	
	@RequestMapping("/module/reporting/reports/periodIndicatorReportRemoveColumn")
	public String removeColumn(@RequestParam("uuid") String uuid,
	                           @RequestParam("key") String key) {
		
		PeriodIndicatorReportDefinition report = (PeriodIndicatorReportDefinition) Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
		PeriodIndicatorReportUtil.removeColumn(report, key);
		
		return "redirect:periodIndicatorReport.form?uuid=" + uuid;
	}
}
