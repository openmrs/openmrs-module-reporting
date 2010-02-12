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

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class GenderCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=false)
	private String gender;
	

	
	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public GenderCohortDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public GenderCohortDefinition(String gender) {
		super();
		setGender(gender);
	}
	
	
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();		
		if (gender != null) {
			if ("M".equalsIgnoreCase(gender)) { // TODO: Use an enumerated constant
				buffer.append("Male");
			} 
			else {
				buffer.append("Female");
			}
			buffer.append(" patients");
		}
		else {
			buffer.append("All Patients");
		}
		
		return buffer.toString();
	}


	//***** PROPERTY ACCESS *****
	
    /**
     * @return the gender
     */
    public String getGender() {
    	return gender;
    }
	
    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
    	this.gender = gender;
    }
}
