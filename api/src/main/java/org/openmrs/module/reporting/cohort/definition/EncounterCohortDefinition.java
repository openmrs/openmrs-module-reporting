/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncounterCohortDefinition")
public class EncounterCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(group="which")
	private TimeQualifier timeQualifier = TimeQualifier.ANY;
	
	@ConfigurationProperty(group="which")
	private List<EncounterType> encounterTypeList;
	
	@ConfigurationProperty(group="which")
	private List<Form> formList;
	
	@ConfigurationProperty(group="where")
	private List<Location> locationList;
	
	@ConfigurationProperty(group="which")
	private List<Person> providerList;
	
	@ConfigurationProperty(group="when")
	private Date onOrAfter;

	@ConfigurationProperty(group="when")
	private Date onOrBefore;
	
	@ConfigurationProperty(group="howMany")
	private Integer atLeastCount;
	
	@ConfigurationProperty(group="howMany")
	private Integer atMostCount;
	
	@ConfigurationProperty(group="other")
	private Boolean returnInverse = Boolean.FALSE;
	
	@ConfigurationProperty(group="other")
	private User createdBy;

	@ConfigurationProperty(group="other")
	private Date createdOnOrBefore;
	
	@ConfigurationProperty(group="other")
	private Date createdOnOrAfter;

	@ConfigurationProperty(group = "where")
	private boolean includeChildLocations = false;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public EncounterCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		StringBuffer ret = new StringBuffer();
		ret.append("Patients with encounters");
		if (onOrAfter != null)
			ret.append(" on or after " + onOrAfter);
		if (onOrBefore != null)
			ret.append(" on or before " + onOrBefore);
		if (timeQualifier != null && timeQualifier != TimeQualifier.ANY)
			ret.append(" where the " + timeQualifier + " was ");
		if (locationList != null)
			ret.append(" at " + locationList);
		if (encounterTypeList != null)
			ret.append(" of type " + encounterTypeList);
		if (formList != null)
			ret.append(" from form " + formList);
		if (providerList != null)
			ret.append(" from provider " + providerList);
		if (atLeastCount != null)
			ret.append(" at least " + atLeastCount);
		if (atMostCount != null)
			ret.append(" at most " + atMostCount);
		if (returnInverse != null && returnInverse.booleanValue())
			ret.append(" AND INVERT THIS");
		if (createdBy != null)
			ret.append(" created by " + createdBy);
		return ret.toString();
	}

	/**
	 * @return the timeQualifier
	 */
	public TimeQualifier getTimeQualifier() {
		return timeQualifier;
	}

	/**
	 * @param timeQualifier the timeQualifier to set
	 */
	public void setTimeQualifier(TimeQualifier timeQualifier) {
		this.timeQualifier = timeQualifier;
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
     * @return the atLeastCount
     */
    public Integer getAtLeastCount() {
    	return atLeastCount;
    }
	
    /**
     * @param atLeastCount the atLeastCount to set
     */
    public void setAtLeastCount(Integer atLeastCount) {
    	this.atLeastCount = atLeastCount;
    }

    /**
     * @return the atMostCount
     */
    public Integer getAtMostCount() {
    	return atMostCount;
    }

    /**
     * @param atMostCount the atMostCount to set
     */
    public void setAtMostCount(Integer atMostCount) {
    	this.atMostCount = atMostCount;
    }

	/**
	 * @return the returnInverse
	 */
	public Boolean isReturnInverse() {
		return returnInverse;
	}

	/**
	 * @return the returnInverse
	 */
	public Boolean getReturnInverse() {
		return returnInverse;
	}

	/**
	 * @param returnInverse the returnInverse to set
	 */
	public void setReturnInverse(Boolean returnInverse) {
		this.returnInverse = returnInverse;
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
	 * @return the providerList
	 */
	public List<Person> getProviderList() {
		return providerList;
	}

	/**
	 * @param providerList the providerList to set
	 */
	public void setProviderList(List<Person> providerList) {
		this.providerList = providerList;
	}

    /**
     * @param provider the provider to add to the list
     */
    public void addProvider(Person provider) {
    	if (providerList == null) {
    		providerList = new ArrayList<Person>();
    	}
    	providerList.add(provider);
    }
    
	/**
     * @return the formList
     */
    public List<Form> getFormList() {
    	return formList;
    }
	
    /**
     * @param formList the formList to set
     */
    public void setFormList(List<Form> formList) {
    	this.formList = formList;
    }
    
    /**
     * @param Form the form to add to the list
     */
    public void addForm(Form Form) {
    	if (formList == null) {
    		formList = new ArrayList<Form>();
    	}
    	formList.add(Form);
    }

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdOnOrBefore
	 */
	public Date getCreatedOnOrBefore() {
		return createdOnOrBefore;
	}

	/**
	 * @param createdOnOrBefore the createdOnOrBefore to set
	 */
	public void setCreatedOnOrBefore(Date createdOnOrBefore) {
		this.createdOnOrBefore = createdOnOrBefore;
	}

	/**
	 * @return the createdOnOrAfter
	 */
	public Date getCreatedOnOrAfter() {
		return createdOnOrAfter;
	}

	/**
	 * @param createdOnOrAfter the createdOnOrAfter to set
	 */
	public void setCreatedOnOrAfter(Date createdOnOrAfter) {
		this.createdOnOrAfter = createdOnOrAfter;
	}

	public boolean isIncludeChildLocations() {
		return includeChildLocations;
	}

	public boolean getIncludeChildLocations() {
		return isIncludeChildLocations();
	}

	public void setIncludeChildLocations(boolean includeChildLocations) {
		this.includeChildLocations = includeChildLocations;
	}
}
