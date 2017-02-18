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
import org.openmrs.Program;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Query for whether the patient enrolled in or completed any of the specified programs in a date range
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ProgramEnrollmentCohortDefinition")
public class ProgramEnrollmentCohortDefinition extends BaseCohortDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true, group="programsGroup")
	private List<Program> programs;
	
	@ConfigurationProperty(group="enrollmentDate")
	private Date enrolledOnOrAfter;

	@ConfigurationProperty(group="enrollmentDate")
	private Date enrolledOnOrBefore;

	@ConfigurationProperty(group="completionDate")
	private Date completedOnOrAfter;

	@ConfigurationProperty(group="completionDate")
	private Date completedOnOrBefore;

	@ConfigurationProperty(group="where")
	private List<Location> locationList;

	@ConfigurationProperty(group = "where")
	private boolean includeChildLocations = false;
	
	/**
	 * Default constructor
	 */
	public ProgramEnrollmentCohortDefinition() { }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Patients ");
		if (enrolledOnOrAfter != null) {
			ret.append("who enrolled on or after " + enrolledOnOrAfter + " ");
		}
		if (enrolledOnOrBefore != null) {
			ret.append("who enrolled on or before " + enrolledOnOrBefore+ " ");
		}
		if (completedOnOrAfter != null) {
			ret.append("who completed on or after " + completedOnOrAfter + " ");
		}
		if (completedOnOrBefore != null) {
			ret.append("who completed on or before " + completedOnOrBefore + " ");
		}
			
		if (programs != null && programs.size() > 0) {
			ret.append(" in ");
			for (Program p : programs) {
				ret.append(p.getName() + " ");
			}
		}

		if (locationList != null && locationList.size() > 0) {
			ret.append(" at ");
			for (Location l : locationList) {
				ret.append(l.getName() + " ");
			}
		}
		return ret.toString();
	}
	
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
     * @return the enrolledOnOrAfter
     */
    public Date getEnrolledOnOrAfter() {
    	return enrolledOnOrAfter;
    }
	
    /**
     * @param enrolledOnOrAfter the enrolledOnOrAfter to set
     */
    public void setEnrolledOnOrAfter(Date enrolledOnOrAfter) {
    	this.enrolledOnOrAfter = enrolledOnOrAfter;
    }
	
    /**
     * @return the enrolledOnOrBefore
     */
    public Date getEnrolledOnOrBefore() {
    	return enrolledOnOrBefore;
    }
	
    /**
     * @param enrolledOnOrBefore the enrolledOnOrBefore to set
     */
    public void setEnrolledOnOrBefore(Date enrolledOnOrBefore) {
    	this.enrolledOnOrBefore = enrolledOnOrBefore;
    }
	
    /**
     * @return the completedOnOrAfter
     */
    public Date getCompletedOnOrAfter() {
    	return completedOnOrAfter;
    }

    /**
     * @param completedOnOrAfter the completedOnOrAfter to set
     */
    public void setCompletedOnOrAfter(Date completedOnOrAfter) {
    	this.completedOnOrAfter = completedOnOrAfter;
    }

    /**
     * @return the completedOnOrBefore
     */
    public Date getCompletedOnOrBefore() {
    	return completedOnOrBefore;
    }

    /**
     * @param completedOnOrBefore the completedOnOrBefore to set
     */
    public void setCompletedOnOrBefore(Date completedOnOrBefore) {
    	this.completedOnOrBefore = completedOnOrBefore;
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
