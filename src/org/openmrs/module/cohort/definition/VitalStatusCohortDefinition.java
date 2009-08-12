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
import org.openmrs.module.cohort.definition.configuration.ConfigurationProperty;

public class VitalStatusCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    	
	@ConfigurationProperty(required=false)
	private Boolean aliveOnly;
	
	@ConfigurationProperty(required=false)
	private Boolean deadOnly;
	
	@ConfigurationProperty(required=false)
	private Date effectiveDate;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public VitalStatusCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Context.getLocale());
		
		if (aliveOnly == null && deadOnly == null) {
			return "All Patients";
		}
		
		StringBuffer ret = new StringBuffer("Patients");

		if (aliveOnly != null && aliveOnly) {
			ret.append(" who are alive");
		}
		if (deadOnly != null && deadOnly) {
			ret.append(" who are dead");
		}
		if (effectiveDate != null) {
			ret.append(" as of " + df.format(effectiveDate));
		}
		
		return ret.toString();
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
