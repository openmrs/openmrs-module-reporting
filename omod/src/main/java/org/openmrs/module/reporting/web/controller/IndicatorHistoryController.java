package org.openmrs.module.reporting.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.propertyeditor.IndicatorEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
		
		//register a customDateEditor to handle startDate and endDate
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true, 10)); 
	}
	
	@ModelAttribute("query")
	public Query formBackingObject(@RequestParam(value = "startDate", required = false) Date startDate,
	                               @RequestParam(value = "endDate", required = false) Date endDate,
	                               @RequestParam(value = "location", required = false) Location location,
	                               @RequestParam(value = "indicators", required = false) List<Indicator> indicators) {
		Query query = new Query();
		
		if (startDate != null)
			query.setStartDate(startDate);
		if (endDate != null)
			query.setEndDate(endDate);
		if (location != null)
			query.setLocation(location);
		if (indicators != null)
			query.setIndicators(indicators);
		return query;
	}
	
	/**
	 * Main controller--displays the options form and the results
	 * 
	 * @param model
	 * @param query
	 * @param result
	 * @throws EvaluationException
	 */
	@RequestMapping("/module/reporting/indicators/indicatorHistory")
	public String getIndicatorHistory(ModelMap model, @ModelAttribute("query") Query query, BindingResult result)
	                                                                                                             throws EvaluationException {
		
		model.addAttribute("locations", Context.getLocationService().getAllLocations());
		
		// only process if a query has been submitted
		
		if (CollectionUtils.isNotEmpty(query.getIndicators())) {
			Calendar cal = Calendar.getInstance();
			if (query.getStartDate() == null) {
				result.rejectValue("startDate", "reporting.startDate.required", "Start date is required");
				return null;
			} else if (query.getStartDate().after(cal.getTime())) {
				result.rejectValue("startDate", "reporting.error.startDateInFuture",
				    "The Start date shouldn't be in the future");
				return null;
			} else if (query.getEndDate() == null) {
				result.rejectValue("endDate", "reporting.endDate.required", "End date is required");
				return null;
			} else if (query.getEndDate().after(cal.getTime())) {
				result.rejectValue("endDate", "reporting.error.endDateInFuture", "The End date shouldn't be in the future");
				return null;
			} else if (query.getStartDate().after(query.getEndDate())) {
				result.rejectValue("endDate", "reporting.error.endDateBeforeStartDate",
				    "End date cannot be before Start date");
				return null;
			}
			
			//CohortIndicator indicator = (CohortIndicator) Context.getService(IndicatorService.class).getIndicatorByUuid(query.getIndicatorUuid());
			
			// determine which periods to do this for
			List<Iteration> iterations = new ArrayList<Iteration>();
			
			cal.setTime(query.getStartDate());
			//the report should run till midnight of the specified range
			query.setEndDate(DateUtil.getEndOfDay(query.getEndDate()));
			Date startOfPeriod;
			Date endOfPeriod;
			
			do {
				startOfPeriod = DateUtil.getStartOfDay(cal.getTime());
				
				//get only the remaining days in the current month if startDate wasn't at the start of the month
				//otherwise it will always run from beginning to end of the current month
				cal.setTime(DateUtil.getEndOfMonth(cal.getTime()));
				
				//in case the starDate and endDate are within the same month, we shouldn't go past endDate
				if (cal.getTime().after(query.getEndDate()))
					cal.setTime(query.getEndDate());
				
				endOfPeriod = DateUtil.getEndOfDay(cal.getTime());
				
				iterations.add(new MultiPeriodIndicatorDataSetDefinition.Iteration(startOfPeriod, endOfPeriod, query
				        .getLocation()));
				cal.setTime(endOfPeriod);
				
				//go to the next day which actually sends us to the next month
				cal.add(Calendar.MILLISECOND, 1);
			} while (cal.getTime().before(query.getEndDate()));
			
			CohortIndicatorDataSetDefinition indDSD = new CohortIndicatorDataSetDefinition();
			for (Indicator ind : query.getIndicators()) {
				
				// hack to remove any empty indicators created by List HTML Widget
				if (ind != null && ObjectUtil.notNull(ind.getUuid())) {
					
					// quick hack error check, could be improved
					if (ind.getParameter("startDate") == null || ind.getParameter("endDate") == null) {
						model.addAttribute("error", "Only indicators with start and end date parameters can be plotted");
						return null;
					}
					
					try {
						CohortIndicator indicator = (CohortIndicator) ind;
						
						indDSD.addColumn(indicator.getUuid(), indicator.getName(), new Mapped<CohortIndicator>(indicator,
						        IndicatorUtil.getDefaultParameterMappings()), "");
					}
					catch (ClassCastException ex) {
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
		
		private Location location;
		
		private List<Indicator> indicators;
		
		private Date startDate = null;
		
		private Date endDate = null;
		
		public Query() {
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
		
		public Date getStartDate() {
			return startDate;
		}
		
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		public Date getEndDate() {
			return endDate;
		}
		
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
	}
}
