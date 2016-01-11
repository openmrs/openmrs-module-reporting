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

import java.util.Date;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Query for patients based on ranges for birth date and death date
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.BirthAndDeathCohortDefinition")
public class BirthAndDeathCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
		
	@ConfigurationProperty(group="birth")
	private Date bornOnOrAfter;

	@ConfigurationProperty(group="birth")
	private Date bornOnOrBefore;
	
	@ConfigurationProperty(group="death")
	private Date diedOnOrAfter;

	@ConfigurationProperty(group="death")
	private Date diedOnOrBefore;

	public BirthAndDeathCohortDefinition() { }

	
    /**
     * @return the bornOnOrAfter
     */
    public Date getBornOnOrAfter() {
    	return bornOnOrAfter;
    }

	
    /**
     * @param bornOnOrAfter the bornOnOrAfter to set
     */
    public void setBornOnOrAfter(Date bornOnOrAfter) {
    	this.bornOnOrAfter = bornOnOrAfter;
    }

	
    /**
     * @return the bornOnOrBefore
     */
    public Date getBornOnOrBefore() {
    	return bornOnOrBefore;
    }

	
    /**
     * @param bornOnOrBefore the bornOnOrBefore to set
     */
    public void setBornOnOrBefore(Date bornOnOrBefore) {
    	this.bornOnOrBefore = bornOnOrBefore;
    }

	
    /**
     * @return the diedOnOrAfter
     */
    public Date getDiedOnOrAfter() {
    	return diedOnOrAfter;
    }

	
    /**
     * @param diedOnOrAfter the diedOnOrAfter to set
     */
    public void setDiedOnOrAfter(Date diedOnOrAfter) {
    	this.diedOnOrAfter = diedOnOrAfter;
    }

	
    /**
     * @return the diedOnOrBefore
     */
    public Date getDiedOnOrBefore() {
    	return diedOnOrBefore;
    }

	
    /**
     * @param diedOnOrBefore the diedOnOrBefore to set
     */
    public void setDiedOnOrBefore(Date diedOnOrBefore) {
    	this.diedOnOrBefore = diedOnOrBefore;
    }
	
}
