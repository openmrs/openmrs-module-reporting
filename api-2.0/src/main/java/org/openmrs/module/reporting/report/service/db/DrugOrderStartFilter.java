package org.openmrs.module.reporting.report.service.db;

import java.util.Date;

public class DrugOrderStartFilter extends DrugOrderFilter {
	
	private Date onOrBefore;
	
	private Date onOrAfter;
	
	private Integer withinLastMonths;
	
	private Integer withinLastDays;
	
	//getters and setters
	public void setOnOrBefore(Date onOrBefore) {
		
		this.onOrBefore = onOrBefore;
	}
	
	public Date getOnOrBefore() {
		
		return onOrBefore;
	}
	
	public void setOnOrAfter(Date onOrAfter) {
		
		this.onOrAfter = onOrAfter;
	}
	
	public Date getOnOrAfter() {
		
		return onOrAfter;
	}
	
	public void setWithinLastDays(Integer withinLastDays) {
		
		this.withinLastDays = withinLastDays;
	}
	
	public Integer getWithinLastDays() {
		
		return withinLastDays;
	}
	
	public void setWithinLastMonths(Integer withinLastMonths) {
		
		this.withinLastMonths = withinLastMonths;
	}
	
	public Integer getWithinLastMonths() {
		
		return withinLastMonths;
	}
}//end DrugOrderStartFilter
