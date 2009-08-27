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

import org.openmrs.module.cohort.definition.configuration.ConfigurationProperty;

/**
 * Filter Implementation using Logic
 */
public class LogicCohortDefinition extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private String criteria;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default constructor
	 */
	public LogicCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return criteria == null ? "criteria==NULL" : criteria.toString();
	}

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the criteria
     */
    public String getCriteria() {
    	return criteria;
    }
	
    /**
     * @param criteria the criteria to set
     */
    public void setCriteria(String criteria) {
    	this.criteria = criteria;
    }
}
