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

import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * CohortDefinition implementation by Location
 */
public class LocationCohortDefinition extends BaseCohortDefinition {
	
    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private List<Location> locations;
	
	@ConfigurationProperty(required=true)
	private PatientLocationMethod calculationMethod = PatientLocationMethod.PATIENT_HEALTH_CENTER;
	
    //***** CONSTRUCTORS *****
	
    /**
     * Default Constructor
     */
	public LocationCohortDefinition() {
		super();
	}
	
	public LocationCohortDefinition(List<Location> locations) { 
		super();
		this.locations = locations;
	}
	

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the locations
     */
    public List<Location> getLocations() {
    	return locations;
    }
	
    /**
     * @param locations the locations to set
     */
    public void setLocations(List<Location> locations) {
    	this.locations = locations;
    }

    /**
     * @return the calculationMethod
     */
    public PatientLocationMethod getCalculationMethod() {
    	return calculationMethod;
    }

    /**
     * @param calculationMethod the calculationMethod to set
     */
    public void setCalculationMethod(PatientLocationMethod calculationMethod) {
    	this.calculationMethod = calculationMethod;
    }
}
