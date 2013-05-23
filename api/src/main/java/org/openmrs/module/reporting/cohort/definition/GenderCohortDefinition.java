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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.GenderCohortDefinition")
public class GenderCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty(group="genders")
	private Boolean maleIncluded = Boolean.FALSE;
	
	@ConfigurationProperty(group="genders")
	private Boolean femaleIncluded = Boolean.FALSE;
	
	@ConfigurationProperty(group="genders")
	private Boolean unknownGenderIncluded = Boolean.FALSE;
	
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
		if (isMaleIncluded() == Boolean.TRUE) {
			buffer.append("Male");
		}
		if (isFemaleIncluded() == Boolean.TRUE) {
			buffer.append((buffer.length() > 0 ? "," : "") + "Female");
		}
		if (isUnknownGenderIncluded() == Boolean.TRUE) {
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
	public Boolean getMaleIncluded() {
		return maleIncluded;
	}
	
	/**
	 * @return the maleIncluded
	 */
	public Boolean isMaleIncluded() {
		return maleIncluded;
	}

	/**
	 * @param maleIncluded the maleIncluded to set
	 */
	public void setMaleIncluded(Boolean maleIncluded) {
		this.maleIncluded = maleIncluded;
	}

	/**
	 * @return the femaleIncluded
	 */
	public Boolean getFemaleIncluded() {
		return femaleIncluded;
	}
	
	/**
	 * @return the femaleIncluded
	 */
	public Boolean isFemaleIncluded() {
		return femaleIncluded;
	}

	/**
	 * @param femaleIncluded the femaleIncluded to set
	 */
	public void setFemaleIncluded(Boolean femaleIncluded) {
		this.femaleIncluded = femaleIncluded;
	}

	/**
	 * @return the unknownGenderIncluded
	 */
	public Boolean getUnknownGenderIncluded() {
		return unknownGenderIncluded;
	}
	
	/**
	 * @return the unknownGenderIncluded
	 */
	public Boolean isUnknownGenderIncluded() {
		return unknownGenderIncluded;
	}

	/**
	 * @param unknownGenderIncluded the unknownGenderIncluded to set
	 */
	public void setUnknownGenderIncluded(Boolean unknownGenderIncluded) {
		this.unknownGenderIncluded = unknownGenderIncluded;
	}
}
