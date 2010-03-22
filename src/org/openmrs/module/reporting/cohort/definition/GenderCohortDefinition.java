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

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class GenderCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty("reporting.genderCohortDefinition.maleIncluded")
	private boolean maleIncluded;
	
	@ConfigurationProperty("reporting.genderCohortDefinition.femaleIncluded")
	private boolean femaleIncluded;
	
	@ConfigurationProperty("reporting.genderCohortDefinition.unknownIncluded")
	private boolean unknownGenderIncluded;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public GenderCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();		
		if (isMaleIncluded()) {
			buffer.append("Male");
		}
		if (isFemaleIncluded()) {
			buffer.append((buffer.length() > 0 ? "," : "") + "Female");
		}
		if (isUnknownGenderIncluded()) {
			buffer.append((buffer.length() > 0 ? "," : "") + "Unknown Gender");
		}
		if (buffer.length() == 0) {
			return "No Patients";
		}
		return buffer.toString();
	}


	//***** PROPERTY ACCESS *****

	/**
	 * @return the maleIncluded
	 */
	public boolean getMaleIncluded() {
		return maleIncluded;
	}
	
	/**
	 * @return the maleIncluded
	 */
	public boolean isMaleIncluded() {
		return maleIncluded;
	}

	/**
	 * @param maleIncluded the maleIncluded to set
	 */
	public void setMaleIncluded(boolean maleIncluded) {
		this.maleIncluded = maleIncluded;
	}

	/**
	 * @return the femaleIncluded
	 */
	public boolean getFemaleIncluded() {
		return femaleIncluded;
	}
	
	/**
	 * @return the femaleIncluded
	 */
	public boolean isFemaleIncluded() {
		return femaleIncluded;
	}

	/**
	 * @param femaleIncluded the femaleIncluded to set
	 */
	public void setFemaleIncluded(boolean femaleIncluded) {
		this.femaleIncluded = femaleIncluded;
	}

	/**
	 * @return the unknownGenderIncluded
	 */
	public boolean getUnknownGenderIncluded() {
		return unknownGenderIncluded;
	}
	
	/**
	 * @return the unknownGenderIncluded
	 */
	public boolean isUnknownGenderIncluded() {
		return unknownGenderIncluded;
	}

	/**
	 * @param unknownGenderIncluded the unknownGenderIncluded to set
	 */
	public void setUnknownGenderIncluded(boolean unknownGenderIncluded) {
		this.unknownGenderIncluded = unknownGenderIncluded;
	}
}
