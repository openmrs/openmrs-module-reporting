package org.openmrs.module.reporting.web.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.propertyeditor.IndicatorEditor;
import org.openmrs.module.report.DariusPeriodIndicatorReportDefinition;
import org.openmrs.module.report.DariusPeriodIndicatorReportUtil;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
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
public class DariusPeriodIndicatorReportController {

	@InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Indicator.class, new IndicatorEditor());
    }
	
	@ModelAttribute("indicators")
	public List<Indicator> getIndicators() {
		return Context.getService(IndicatorService.class).getAllIndicators(false);
	}

	@RequestMapping("/module/reporting/reports/dariusPeriodIndicatorReport")
	public void showForm(ModelMap model,
	                     @RequestParam(value="uuid", required=false) String uuid) {
		if (uuid == null) {
			model.addAttribute("report", new DariusPeriodIndicatorReportDefinition());
		} else {
			ReportDefinition def = Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);
			if (def instanceof DariusPeriodIndicatorReportDefinition) {
				DariusPeriodIndicatorReportDefinition report = (DariusPeriodIndicatorReportDefinition) def;
				DariusPeriodIndicatorReportUtil.ensureDataSetDefinition(report);
				model.addAttribute("report", report);
			} else {
				throw new RuntimeException("This report is not of the right class");
			} 
		}
	}

	
	@RequestMapping("/module/reporting/reports/dariusPeriodIndicatorReportAddColumn")
	public String addColumn(@RequestParam("uuid") String uuid,
	                        @RequestParam("key") String key,
	                        @RequestParam("displayName") String displayName,
	                        @RequestParam("indicator") Indicator indicator,
	                        WebRequest request) {
		
		DariusPeriodIndicatorReportDefinition report = (DariusPeriodIndicatorReportDefinition) Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);
		
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
		
		DariusPeriodIndicatorReportUtil.addColumn(report, key, displayName, (CohortIndicator) indicator, dimensionOptions);
		
		return "redirect:dariusPeriodIndicatorReport.form?uuid=" + uuid;
	} 
	
	@RequestMapping("/module/reporting/reports/dariusPeriodIndicatorReportRemoveColumn")
	public String removeColumn(@RequestParam("uuid") String uuid,
	                           @RequestParam("key") String key) {
		
		DariusPeriodIndicatorReportDefinition report = (DariusPeriodIndicatorReportDefinition) Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);
		DariusPeriodIndicatorReportUtil.removeColumn(report, key);
		
		return "redirect:dariusPeriodIndicatorReport.form?uuid=" + uuid;
	}
}
