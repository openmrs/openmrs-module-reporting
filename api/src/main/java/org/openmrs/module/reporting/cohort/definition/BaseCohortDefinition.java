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

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Base Implementation of CohortDefinition which provides core method
 * implementations for handling Parameters and common Property values
 */
public abstract class BaseCohortDefinition extends BaseDefinition implements CohortDefinition {
	
    public static final long serialVersionUID = 1920394873L;
    
    //***** PROPERTIES *****

    private Integer id;

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public BaseCohortDefinition() {
    	super();
    }
    
    //***** INSTANCE METHODS *****
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return ObjectUtil.nvlStr(getName(), getClass().getSimpleName());
	}
	
    //***** Property Access *****
	
    /**
     * @return the id
     */
    public Integer getId() {
    	return id;
    }

	/**
     * @param id the id to set
     */
    public void setId(Integer id) {
    	this.id = id;
    }
}