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

import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class AgeCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=false)
	private Integer minAge;
	
	@ConfigurationProperty(required=false)
	private Integer maxAge;
	
	@ConfigurationProperty(required=false)
	private Date effectiveDate;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public AgeCohortDefinition() {
		super();
	}
	
	public AgeCohortDefinition(Integer minAge, Integer maxAge, Date effectiveDate) { 
		super();
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.effectiveDate = effectiveDate;
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("Patients ");
		if (minAge != null) {
			if (maxAge != null) {
				buffer.append(" between the ages of " + minAge + " and " + maxAge);
			} 
			else {
				buffer.append(" at least " + minAge + " years old");
			}
		} 
		else {
			if (maxAge != null) {
				buffer.append("| up to " + maxAge + " years old");
			}
		}	
		if (effectiveDate != null) { 
			buffer.append(" as of " + Context.getDateFormat().format(effectiveDate));
		}
		
		return buffer.toString();
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
