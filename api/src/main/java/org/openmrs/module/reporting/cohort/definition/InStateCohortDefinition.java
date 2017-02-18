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

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Query for whether the patient was in a state on a date or date range
 * (Using onDate is equivalent to setting onOrAfter==onOrBefore, but may be more efficient and readable
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.InStateCohortDefinition")
public class InStateCohortDefinition extends BaseCohortDefinition {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty(required=true, group="statesGroup")
	private List<ProgramWorkflowState> states;
	
	@ConfigurationProperty(group="dateRangeGroup")
	private Date onOrAfter;

	@ConfigurationProperty(group="dateRangeGroup")
	private Date onOrBefore;

	@ConfigurationProperty(group="onDateGroup")
	private Date onDate;

	@ConfigurationProperty(group="where")
	private List<Location> locations;

	@ConfigurationProperty(group = "where")
	private boolean includeChildLocations;

	/**
	 * Default constructor
	 */
	public InStateCohortDefinition() { }

    /**
     * @return the states
     */
    public List<ProgramWorkflowState> getStates() {
    	return states;
    }

    /**
     * @param states the states to set
     */
    public void setStates(List<ProgramWorkflowState> states) {
    	this.states = states;
    }

    /**
     * @param state the state to add
     */
    public void addState(ProgramWorkflowState state) {
    	if (states == null) {
    		states = new ArrayList<ProgramWorkflowState>();
    	}
    	states.add(state);
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
     * @return the onDate
     */
    public Date getOnDate() {
    	return onDate;
    }

	
    /**
     * @param onDate the onDate to set
     */
    public void setOnDate(Date onDate) {
    	this.onDate = onDate;
    }

	/**
	 * @return the locations
	 */
	public List<Location> getLocations() {
		return locations;
	}

	/**
	 * @param locations
	 */
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public void addLocation(Location location) {
		if (locations == null) {
			locations = new ArrayList<Location>();
		}
		locations.add(location);
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
