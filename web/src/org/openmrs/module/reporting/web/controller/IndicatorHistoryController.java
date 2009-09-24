package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.indicator.util.IndicatorUtil;
import org.openmrs.module.propertyeditor.IndicatorEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("query")
public class IndicatorHistoryController {

    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Location.class, new LocationEditor());
    	binder.registerCustomEditor(Indicator.class, new IndicatorEditor());
    }
    
	@ModelAttribute("query")
	public Query formBackingObject(@RequestParam(value="lastMonths", required=false) Integer lastMonths,
	                               @RequestParam(value="location", required=false) Location location,
	                               @RequestParam(value="indicators", required=false) List<Indicator> indicators) {
		Query query = new Query();
		if (lastMonths != null)
			query.setLastMonths(lastMonths);
		if (location != null)
			query.setLocation(location);
		if (indicators != null )
			query.setIndicators(indicators);
		return query;
	}

	
	/**
	 * The full options form. Minor options changes can be done from the results page
	 * 
	 * @param model
	 */
	@RequestMapping("/module/reporting/indicators/indicatorHistoryOptions")
	public void showOptionsForm(ModelMap model) {
		model.addAttribute("locations", Context.getLocationService().getAllLocations());
		model.addAttribute("indicators", Context.getService(IndicatorService.class).getAllIndicators(false));
	}
	
	
	/**
	 * This actually does the calculations
	 * 
	 * @param model
	 * @param query
	 */
	@RequestMapping("/module/reporting/indicators/indicatorHistory")
	public String getIndicatorHistory(ModelMap model,
	                                @ModelAttribute("query") Query query) {
		if (query.getIndicators() == null || query.getIndicators().size() == 0) {
			return "redirect:indicatorHistoryOptions.form";
		}
		//CohortIndicator indicator = (CohortIndicator) Context.getService(IndicatorService.class).getIndicatorByUuid(query.getIndicatorUuid());

		// determine which periods to do this for
		List<Iteration> iterations = new ArrayList<Iteration>();
		{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR, 0);
			for (int i = 0; i < query.getLastMonths(); ++i) {
				cal.add(Calendar.DAY_OF_MONTH, -1);
				Date endOfMonth = cal.getTime();
				cal.set(Calendar.DAY_OF_MONTH, 1);
				Date startOfMonth = cal.getTime();
				iterations.add(new MultiPeriodIndicatorDataSetDefinition.Iteration(startOfMonth, endOfMonth, query.getLocation()));
			}
		}
			
		CohortIndicatorDataSetDefinition indDSD = new CohortIndicatorDataSetDefinition();
		for (Indicator ind : query.getIndicators()) {
			try {
				CohortIndicator indicator = (CohortIndicator) ind;
				indDSD.addColumn(
					indicator.getUuid(),
					indicator.getName(),
					new Mapped<CohortIndicator>(indicator, IndicatorUtil.getDefaultParameterMappings()),
					"");
			} catch (ClassCastException ex) {
				throw new RuntimeException("This feature only works for Cohort Indicators");
			}
		}
		MultiPeriodIndicatorDataSetDefinition dsd = new MultiPeriodIndicatorDataSetDefinition(indDSD);
		dsd.setIterations(iterations);
		
		DataSet<?> ds = Context.getService(DataSetDefinitionService.class).evaluate(dsd, null);
		model.addAttribute("dataSet", ds);
		return null;
	}

	public class Query {
		private Integer lastMonths = 6;
		private Location location;
		private List<Indicator> indicators;

		public Query() { }

		public Integer getLastMonths() {
        	return lastMonths;
        }
		
        public void setLastMonths(Integer lastMonths) {
        	this.lastMonths = lastMonths;
        }
		
        public Location getLocation() {
        	return location;
        }
		
        public void setLocation(Location location) {
        	this.location = location;
        }
		
        public List<Indicator> getIndicators() {
        	return indicators;
        }
		
        public void setIndicators(List<Indicator> indicators) {
        	this.indicators = indicators;
        }
		
	}
}
