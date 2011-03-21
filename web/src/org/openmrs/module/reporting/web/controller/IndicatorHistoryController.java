package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.propertyeditor.IndicatorEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
    	binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
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
	 * 
	 * Main controller--displays the options form and the results
	 * 
	 * @param model
	 * @param query
	 */
	@RequestMapping("/module/reporting/indicators/indicatorHistory")
	public String getIndicatorHistory(ModelMap model,
	                                @ModelAttribute("query") Query query) {
		
		model.addAttribute("locations", Context.getLocationService().getAllLocations());
		
		// only process if a query has been submitted
		if (query.getIndicators() != null && query.getIndicators().size() != 0) {
	
			//CohortIndicator indicator = (CohortIndicator) Context.getService(IndicatorService.class).getIndicatorByUuid(query.getIndicatorUuid());

			// determine which periods to do this for
			List<Iteration> iterations = new ArrayList<Iteration>();
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
			
			CohortIndicatorDataSetDefinition indDSD = new CohortIndicatorDataSetDefinition();
			for (Indicator ind : query.getIndicators()) {				
				
				// hack to remove any empty indicators created by List HTML Widget
				if (ind != null && ObjectUtil.notNull(ind.getUuid())) {			
					
					// quick hack error check, could be improved
					if(ind.getParameter("startDate") == null || ind.getParameter("endDate") == null){
						model.addAttribute("error", "Only indicators with start and end date parameters can be plotted");
						return null;
					}
						
					
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
			}
			
			MultiPeriodIndicatorDataSetDefinition dsd = new MultiPeriodIndicatorDataSetDefinition(indDSD);
			dsd.setIterations(iterations);
		
			DataSet ds = Context.getService(DataSetDefinitionService.class).evaluate(dsd, null);
			model.addAttribute("dataSet", ds);
		}
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
