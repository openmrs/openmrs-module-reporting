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
package org.openmrs.module.cohort.definition;

import org.openmrs.Location;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.module.evaluation.parameter.Param;

/**
 * CohortDefinition implementation by Location
 */
public class LocationCohortDefinition extends BaseCohortDefinition {
	
    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@Param(required=true)
	private Location location;
	
	@Param(required=true)
	private PatientLocationMethod calculationMethod = PatientLocationMethod.PATIENT_HEALTH_CENTER;
	
    //***** CONSTRUCTORS *****
	
    /**
     * Default Constructor
     */
	public LocationCohortDefinition() {
		super();
	}

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the location
     */
    public Location getLocation() {
    	return location;
    }
	
    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
    	this.location = location;
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
