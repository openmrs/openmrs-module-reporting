package org.openmrs.module.reporting.report.service.db;

import java.util.Date;
import java.util.List;

import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.module.reporting.data.patient.PatientData;

public class DrugOrderFilter extends PatientData {
	
	private List<Concept> drugConcepts;
	
	private List<Concept> drugSets;
	
	private Date activeOnOrBefore;
	
	private Date activeOnOrAfter;
	
	private Integer activeWithinLastMonths;
	
	private Integer activeWithinLastDays;
	
	private boolean onlyCurrentlyActive;
	
	private CareSetting careSetting;
	
	public DrugOrderFilter() {
		
	}
	
	//getters and setters
	public void setDrugConcepts(List<Concept> drugConcepts) {
		
		this.drugConcepts = drugConcepts;
	}
	
	public List<Concept> getDrugConcepts() {
		
		return drugConcepts;
	}
	
	public void setDrugSets(List<Concept> drugSets) {
		
		this.drugSets = drugSets;
	}
	
	public List<Concept> getDrugSets() {
		
		return drugSets;
	}
	
	public void setActiveOnOrBefore(Date activeOnOrBefore) {
		
		this.activeOnOrBefore = activeOnOrBefore;
	}
	
	public Date getActiveOnOrBefore() {
		
		return activeOnOrBefore;
	}
	
	public void setActiveOnOrAfter(Date activeOnOrAfter) {
		
		this.activeOnOrAfter = activeOnOrAfter;
	}
	
	public Date getActiveOnOrAfter() {
		
		return activeOnOrAfter;
	}
	
	public void setActiveWithinLastMonths(Integer activeWithinLastMonths) {
		
		this.activeWithinLastMonths = activeWithinLastMonths;
	}
	
	public Integer getActiveWithinLastMonths() {
		
		return activeWithinLastMonths;
	}
	
	public void setActiveWithinLastDays(Integer activeWithinLastDays) {
		
		this.activeWithinLastDays = activeWithinLastDays;
	}
	
	public Integer getActiveWithinLastDays() {
		
		return activeWithinLastDays;
	}
	
	public void setOnlyCurrentlyActive(boolean onlyCurrentlyActive) {
		
		this.onlyCurrentlyActive = onlyCurrentlyActive;
	}
	
	public boolean getOnlyCurrentlyActive() {
		
		return onlyCurrentlyActive;
	}
	
	public void setCareSetting(CareSetting careSetting) {
		
		this.careSetting = careSetting;
	}
	
	public CareSetting getCareSetting() {
		
		return careSetting;
	}
}//end DrugOrderFilter
