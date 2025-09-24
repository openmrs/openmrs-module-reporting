/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Query for whether the patient was in a program on a date or date range
 * (Using onDate is equivalent to setting onOrAfter==onOrBefore, but may be more efficient and readable
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.InProgramCohortDefinition")
public class InProgramCohortDefinition extends BaseCohortDefinition {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty(required=true, group="programsGroup")
	private List<Program> programs;
	
	@ConfigurationProperty(group="dateRangeGroup")
	private Date onOrAfter;

	@ConfigurationProperty(group="dateRangeGroup")
	private Date onOrBefore;

	@ConfigurationProperty(group="onDateGroup")
	private Date onDate;

	@ConfigurationProperty(group="locationGroup")
	private List<Location> locations;

	/**
	 * Default constructor
	 */
	public InProgramCohortDefinition() { }
	
    /**
     * @return the programs
     */
    public List<Program> getPrograms() {
    	return programs;
    }

    /**
     * @param programs the programs to set
     */
    public void setPrograms(List<Program> programs) {
    	this.programs = programs;
    }
    
    /**
     * @param program the program to add
     */
    public void addProgram(Program program) {
    	if (programs == null) {
    		programs = new ArrayList<Program>();
    	}
    	programs.add(program);
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
}
