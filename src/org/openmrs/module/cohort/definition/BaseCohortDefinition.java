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

import java.util.List;

import org.openmrs.module.cohort.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.cohort.definition.configuration.Property;
import org.openmrs.module.cohort.definition.util.CohortDefinitionUtil;
import org.openmrs.module.evaluation.caching.Caching;
import org.openmrs.module.evaluation.parameter.BaseParameterizable;

/**
 * Base Implementation of CohortDefinition which provides core method
 * implementations for handling Parameters and common Property values
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public abstract class BaseCohortDefinition extends BaseParameterizable implements CohortDefinition {
	
    private static final long serialVersionUID = 1920394873L;
    
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
	 * @see CohortDefinition#getConfigurationProperties()
	 */
	public List<Property> getConfigurationProperties() {
		return CohortDefinitionUtil.getConfigurationProperties(this);
	}
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CohortDefinition) {
			CohortDefinition p = (CohortDefinition) obj;
			if (this.getUuid() != null) {
				return (this.getUuid().equals(p.getUuid()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getUuid() == null ? 0 : 31 * getUuid().hashCode());
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
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