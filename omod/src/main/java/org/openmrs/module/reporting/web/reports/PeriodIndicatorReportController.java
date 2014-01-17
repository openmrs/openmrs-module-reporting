package org.openmrs.module.reporting.web.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.propertyeditor.CohortDefinitionEditor;
import org.openmrs.module.reporting.propertyeditor.IndicatorEditor;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
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
		binder.registerCustomEditor(CohortDefinition.class, new CohortDefinitionEditor());
	}

	@ModelAttribute("cohortQueries")
	public List<DefinitionSummary> getCohortQueries() {
		return DefinitionContext.getCohortDefinitionService().getAllDefinitionSummaries(false);
	}

	@ModelAttribute("indicators")
	public List<Indicator> getIndicators() {
		List<Indicator> ret = new ArrayList<Indicator>();
		for (Indicator i : DefinitionContext.getIndicatorService().getAllDefinitions(false)) {
			if (i.getParameter("startDate") != null && i.getParameter("endDate") != null && i.getParameter("location") != null) {
				ret.add(i);
			}
		}
		return ret;
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

	
	@RequestMapping("/module/reporting/reports/periodIndicatorReportSaveColumn")
	public String addColumn(@RequestParam("uuid") String uuid,
							@RequestParam(value="index", required=false) Integer index,
							@RequestParam("key") String key,
							@RequestParam("displayName") String displayName,
							@RequestParam("indicator") CohortIndicator indicator,
							@RequestParam("cohortQuery") CohortDefinition cohortDefinition,
							@RequestParam(value = "createFromCohortQuery", required = false) String createFromCohortQuery,
							WebRequest request) {
		
		PeriodIndicatorReportDefinition report = (PeriodIndicatorReportDefinition) Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
		PeriodIndicatorReportUtil.ensureDataSetDefinition(report);
		CohortIndicatorDataSetDefinition cidsd = report.getIndicatorDataSetDefinition();
		
		CohortIndicatorAndDimensionColumn column = null;
		if (ObjectUtil.notNull(index)) {
			column = cidsd.getColumns().get(index);
		}
		else {
			column = cidsd.new CohortIndicatorAndDimensionColumn();
			cidsd.addColumn(column);
		}
		column.setName(key);
		column.setLabel(displayName);

		CohortIndicator cohortIndicator;
		if (createFromCohortQuery == null) {
			cohortIndicator = indicator;
		} else {
			cohortIndicator = new CohortIndicator();
			cohortIndicator.setName(cohortDefinition.getName() + " indicator");
			cohortIndicator.setDescription("Automatically generated indicator for cohort: " + cohortDefinition.getName());
			cohortIndicator.setType(CohortIndicator.IndicatorType.COUNT);
			cohortIndicator.addParameter(new Parameter("startDate", "startDate", Date.class));
			cohortIndicator.addParameter(new Parameter("endDate", "endDate", Date.class));
			cohortIndicator.addParameter(new Parameter("location", "location", Location.class));
			cohortIndicator.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinition));
		}
		column.setIndicator(new Mapped<CohortIndicator>(cohortIndicator, IndicatorUtil.getDefaultParameterMappings()));
			
		// special code because I don't think I can do a RequestParam for: Map<String, String> dimensionOptions
		Map<String, String> dimensionOptions = new HashMap<String, String>();
		Set<String> paramKeys = request.getParameterMap().keySet();
		for (String param : paramKeys) {
			if (param.startsWith("dimensionOption_")) {
				String dimName = param.substring("dimensionOption_".length());
				String dimValue = request.getParameter(param);
				if (StringUtils.hasText(dimValue)) {
					dimensionOptions.put(dimName, dimValue);
				}
			}
		}
		column.setDimensionOptions(dimensionOptions);
		PeriodIndicatorReportUtil.saveDataSetDefinition(report);
		
		return "redirect:periodIndicatorReport.form?uuid=" + uuid;
	} 
	
	@RequestMapping("/module/reporting/reports/periodIndicatorReportRemoveColumn")
	public String removeColumn(@RequestParam("uuid") String uuid,
							   @RequestParam("key") String key) {
		
		PeriodIndicatorReportDefinition report = (PeriodIndicatorReportDefinition) Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
		PeriodIndicatorReportUtil.removeColumn(report, key);
		
		return "redirect:periodIndicatorReport.form?uuid=" + uuid;
	}
	
	@RequestMapping("/module/reporting/reports/periodIndicatorReportRemoveDimension")
	public String removeDimension(@RequestParam("uuid") String uuid,
								  @RequestParam("key") String key) {
		
		PeriodIndicatorReportDefinition report = (PeriodIndicatorReportDefinition) Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
		PeriodIndicatorReportUtil.removeDimension(report, key);
		
		return "redirect:periodIndicatorReport.form?uuid=" + uuid;
	}
}
