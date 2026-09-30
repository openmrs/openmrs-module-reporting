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
