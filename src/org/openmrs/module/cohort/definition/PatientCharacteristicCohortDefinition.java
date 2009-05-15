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

import java.text.DateFormat;
import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.parameter.Param;

public class PatientCharacteristicCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@Param(required=false)
	private String gender;
	
	@Param(required=false)
	private Date minBirthdate;
	
	@Param(required=false)
	private Date maxBirthdate;
	
	@Param(required=false)
	private Integer minAge;
	
	@Param(required=false)
	private Integer maxAge;
	
	@Param(required=false)
	private Boolean aliveOnly;
	
	@Param(required=false)
	private Boolean deadOnly;
	
	@Param(required=false)
	private Date effectiveDate;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public PatientCharacteristicCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Context.getLocale());
		
		if (gender == null && minBirthdate == null && maxBirthdate == null && 
			minAge == null && maxAge == null && aliveOnly == null && deadOnly == null) {
			return "All Patients";
		}
		
		StringBuffer ret = new StringBuffer();
		if (gender != null) {
			if ("M".equalsIgnoreCase(gender)) { // TODO: Use an enumerated constant
				ret.append("Male");
			} else {
				ret.append("Female");
			}
			ret.append(" patients ");
		}
		else {
			ret.append("Patients ");
		}

		if (minBirthdate != null) {
			if (maxBirthdate != null) {
				ret.append(" born between " + df.format(minBirthdate) + " and " + df.format(maxBirthdate));
			} else {
				ret.append(" born after " + df.format(minBirthdate));
			}
		} else {
			if (maxBirthdate != null) {
				ret.append(" born before " + df.format(maxBirthdate));
			}
		}
		if (minAge != null) {
			if (maxAge != null) {
				ret.append(" between the ages of " + minAge + " and " + maxAge);
			} else {
				ret.append(" at least " + minAge + " years old");
			}
		} else {
			if (maxAge != null) {
				ret.append(" up to " + maxAge + " years old");
			}
		}
		if (effectiveDate != null) {
			ret.append(" as of " + df.format(effectiveDate));
		}
		if (aliveOnly != null && aliveOnly) {
			ret.append(" who are alive");
		}
		if (deadOnly != null && deadOnly) {
			ret.append(" who are dead");
		}
		return ret.toString();
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

    /**
     * @return the minBirthdate
     */
    public Date getMinBirthdate() {
    	return minBirthdate;
    }
	
    /**
     * @param minBirthdate the minBirthdate to set
     */
    public void setMinBirthdate(Date minBirthdate) {
    	this.minBirthdate = minBirthdate;
    }
	
    /**
     * @return the maxBirthdate
     */
    public Date getMaxBirthdate() {
    	return maxBirthdate;
    }
	
    /**
     * @param maxBirthdate the maxBirthdate to set
     */
    public void setMaxBirthdate(Date maxBirthdate) {
    	this.maxBirthdate = maxBirthdate;
    }

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
     * @return the aliveOnly
     */
    public Boolean getAliveOnly() {
    	return aliveOnly;
    }
	
    /**
     * @param aliveOnly the aliveOnly to set
     */
    public void setAliveOnly(Boolean aliveOnly) {
    	this.aliveOnly = aliveOnly;
    }

    /**
     * @return the deadOnly
     */
    public Boolean getDeadOnly() {
    	return deadOnly;
    }
	
    /**
     * @param deadOnly the deadOnly to set
     */
    public void setDeadOnly(Boolean deadOnly) {
    	this.deadOnly = deadOnly;
    }
    
    /**
     * @return the effectiveDate
     */
    public Date getEffectiveDate() {
    	return effectiveDate;
    }

    /**
     * @param effectiveDate the effectiveDate to set
     */
    public void setEffectiveDate(Date effectiveDate) {
    	this.effectiveDate = effectiveDate;
    }
}
