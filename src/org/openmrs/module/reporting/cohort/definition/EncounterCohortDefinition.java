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

import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("reporting.EncounterCohortDefinition")
public class EncounterCohortDefinition extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(group="when")
	private Date onOrAfter;

	@ConfigurationProperty(group="when")
	private Date onOrBefore;
	
	@ConfigurationProperty(required=false, group="which")
	private List<Location> locationList;
	
	@ConfigurationProperty(required=false, group="which")
	private List<EncounterType> encounterTypeList;
	
	@ConfigurationProperty(required=false, group="which")
	private List<Form> formList;
	
	@ConfigurationProperty(required=false, group="howMany")
	private Integer atLeastCount;
	
	@ConfigurationProperty(required=false, group="howMany")
	private Integer atMostCount;
	
	@ConfigurationProperty(required=false, group="other")
	private Boolean returnInverse = Boolean.FALSE;
	
	@ConfigurationProperty(required=false, group="other")
	private User createdBy;

	@ConfigurationProperty(required=false, group="other")
	private Date createdOnOrBefore;
	
	@ConfigurationProperty(required=false, group="other")
	private Date createdOnOrAfter;
	
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
		if (locationList != null)
			ret.append(" at " + locationList);
		if (encounterTypeList != null)
			ret.append(" of type " + encounterTypeList);
		if (formList != null)
			ret.append(" from form " + formList);
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

    // purposely ignored javadoc, let me (cneumann) know if they are mandatory
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User creator) {
		this.createdBy = creator;
	}

	public Date getCreatedOnOrBefore() {
		return createdOnOrBefore;
	}

	public void setCreatedOnOrBefore(Date createdOnOrBefore) {
		this.createdOnOrBefore = createdOnOrBefore;
	}

	public Date getCreatedOnOrAfter() {
		return createdOnOrAfter;
	}

	public void setCreatedOnOrAfter(Date createdOnOrAfter) {
		this.createdOnOrAfter = createdOnOrAfter;
	}

}
