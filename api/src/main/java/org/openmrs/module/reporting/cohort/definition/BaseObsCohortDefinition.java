package org.openmrs.module.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.BaseObsCohortDefinition")
public abstract class BaseObsCohortDefinition extends BaseCohortDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true, group="questionGroup")
	private TimeModifier timeModifier;
	
	@ConfigurationProperty(required=true, group="questionGroup")
	private Concept question;
	
	// temporarily comment this out since it's not yet implemented
	//@ConfigurationProperty(required=true, group="questionGroup")
	private Concept groupingConcept;
	
	@ConfigurationProperty(group="obsDatetimeGroup")
	private Date onOrAfter;

	@ConfigurationProperty(group="obsDatetimeGroup")
	private Date onOrBefore;
	
	@ConfigurationProperty(group="otherGroup")
	private List<Location> locationList;
	
	@ConfigurationProperty(group="otherGroup")
	private List<EncounterType> encounterTypeList;
	
	// ------ helper accessors -------
	
	public Integer getQuestionId() {
		return question == null ? null : question.getConceptId();
	}
	
	public Integer getGroupingConceptId() {
		return groupingConcept == null ? null : groupingConcept.getConceptId();
	}

	public List<Integer> getLocationIds() {
		if (locationList == null || locationList.size() == 0) {
			return null;
		}
		List<Integer> ret = new ArrayList<Integer>();
		for (Location l : locationList) {
			ret.add(l.getId());
		}
		return ret;
	}

	public List<Integer> getEncounterTypeIds() {
		if (encounterTypeList == null || encounterTypeList.size() == 0) {
			return null;
		}
		List<Integer> ret = new ArrayList<Integer>();
		for (EncounterType t : encounterTypeList) {
			ret.add(t.getId());
		}
		return ret;
	}

	// ------ property accessors -------
	
    /**
     * @return the question
     */
    public Concept getQuestion() {
    	return question;
    }
	
    /**
     * @param question the question to set
     */
    public void setQuestion(Concept question) {
    	this.question = question;
    }
	
    /**
     * @return the groupingConcept
     */
    public Concept getGroupingConcept() {
    	return groupingConcept;
    }
	
    /**
     * @param groupingConcept the groupingConcept to set
     */
    public void setGroupingConcept(Concept groupingConcept) {
    	this.groupingConcept = groupingConcept;
    }
	
    /**
     * @return the onOrAfter
     */
    public Date getOnOrAfter() {
    	return onOrAfter;
    }

    /**
     * @param onOrAfter the onOrAfter to set
     */
    public void setOnOrAfter(Date onOrAfter) {
    	this.onOrAfter = onOrAfter;
    }

    /**
     * @return the onOrBefore
     */
    public Date getOnOrBefore() {
    	return onOrBefore;
    }

    /**
     * @param onOrBefore the onOrBefore to set
     */
    public void setOnOrBefore(Date onOrBefore) {
    	this.onOrBefore = onOrBefore;
    }

    /**
     * @return the locationList
     */
    public List<Location> getLocationList() {
    	return locationList;
    }
	
    /**
     * @param locationList the locationList to set
     */
    public void setLocationList(List<Location> locationList) {
    	this.locationList = locationList;
    }
    
    /**
     * @param location the location to add to the list
     */
    public void addLocation(Location location) {
    	if (locationList == null) {
    		locationList = new ArrayList<Location>();
    	}
    	locationList.add(location);
    }
	
    /**
     * @return the encounterTypeList
     */
    public List<EncounterType> getEncounterTypeList() {
    	return encounterTypeList;
    }
	
    /**
     * @param encounterTypeList the encounterTypeList to set
     */
    public void setEncounterTypeList(List<EncounterType> encounterTypeList) {
    	this.encounterTypeList = encounterTypeList;
    }
    
    /**
     * @param encounterType the encounter type to add to the list
     */
    public void addEncounterType(EncounterType encounterType) {
    	if (encounterTypeList == null) {
    		encounterTypeList = new ArrayList<EncounterType>();
    	}
    	encounterTypeList.add(encounterType);
    }
	
    /**
     * @return the timeModifier
     */
    public TimeModifier getTimeModifier() {
    	return timeModifier;
    }

    /**
     * @param timeModifier the timeModifier to set
     */
    public void setTimeModifier(TimeModifier timeModifier) {
    	this.timeModifier = timeModifier;
    }	
}
