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
package org.openmrs.module.reporting.common;

import org.openmrs.module.reporting.common.Age.Unit;

/**
 * Represents an Age Range, inclusive of end ages
 * For example, an Age Range of minAge = 0, maxAge = 14 would include anyone less than 15 years of age
 */
public class AgeRange {
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Integer minAge;
	private Unit minAgeUnit;
	private Integer maxAge;
	private Unit maxAgeUnit;
	private String label;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Range only Constructor
	 */
	public AgeRange(Integer minAgeYears, Integer maxAgeYears) {
		this(minAgeYears, Unit.YEARS, maxAgeYears, Unit.YEARS, null);
	}
	
	/**
	 * Full Constructor
	 */
	public AgeRange(Integer minAge, Unit minAgeUnit, Integer maxAge, Unit maxAgeUnit, String label) {
		this.minAge = minAge;
		this.minAgeUnit = minAgeUnit;
		this.maxAge = maxAge;
		this.maxAgeUnit = maxAgeUnit;
		this.label = label;
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************

	/**
	 * Returns true if an age is within the given range
	 */
	public Boolean isInRange(Age age) {
		Integer ageMonths = age.getFullMonths();
		Integer ageYears = age.getFullYears();
		if (minAge != null) {
			Unit minAgeUnit = ObjectUtil.nvl(getMinAgeUnit(), Unit.YEARS);
			if (minAgeUnit == Unit.MONTHS) {
				if (ageMonths < minAge) {
					return false;
				}
			} else {
				if (ageYears < minAge) {
					return false;
				}
			}
		}
		if (maxAge != null) {
			Unit maxAgeUnit = ObjectUtil.nvl(getMaxAgeUnit(), Unit.YEARS);
			if (maxAgeUnit == Unit.MONTHS) {
				if (ageMonths > maxAge) {
					return false;
				}
			} else {
				if (ageYears > maxAge) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ObjectUtil.nvlStr(minAge, 0) + ObjectUtil.decode(maxAge, "+", "-" + maxAge);
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the minAge
	 */
	public Integer getMinAge() {
		return minAge;
	}

	/**
	 * @param minAge the minAge to set
	 */
	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	/**
	 * @return the minAgeUnit
	 */
	public Unit getMinAgeUnit() {
		return minAgeUnit;
	}

	/**
	 * @param minAgeUnit the minAgeUnit to set
	 */
	public void setMinAgeUnit(Unit minAgeUnit) {
		this.minAgeUnit = minAgeUnit;
	}

	/**
	 * @return the maxAge
	 */
	public Integer getMaxAge() {
		return maxAge;
	}

	/**
	 * @param maxAge the maxAge to set
	 */
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return the maxAgeUnit
	 */
	public Unit getMaxAgeUnit() {
		return maxAgeUnit;
	}

	/**
	 * @param maxAgeUnit the maxAgeUnit to set
	 */
	public void setMaxAgeUnit(Unit maxAgeUnit) {
		this.maxAgeUnit = maxAgeUnit;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
