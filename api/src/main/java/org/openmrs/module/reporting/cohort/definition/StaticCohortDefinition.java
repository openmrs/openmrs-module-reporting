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

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 *  Simple CohortDefinition to restrict to a Static Cohort
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.StaticCohortDefinition")
public class StaticCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;
    
    //****************
    // Properties
    //****************

    @ConfigurationProperty(required=true)
    private Cohort cohort;
	
    //****************
    // Constructors
    //****************
	
    /**
     * Default Constructor
     */
	public StaticCohortDefinition() {
		super();
		cohort = new Cohort();
	}
	
	/**
	 * Full constructor
	 * @param cohort
	 */
	public StaticCohortDefinition(Cohort cohort) {
		this();
		this.cohort = cohort;
	}
	
    //****************
    // Property access
    //****************

	/**
     * @see BaseOpenmrsMetadata#getId()
     */
    public Integer getId() {
    	return cohort == null ? null : cohort.getCohortId();
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    public void setId(Integer id) {
    	cohort.setCohortId(id);
    }

	/** 
	 * @see BaseOpenmrsObject#getUuid()
	 */
	@Override
	public String getUuid() {
		return cohort == null ? null : cohort.getUuid();
	}

	/** 
	 * @see BaseOpenmrsObject#setUuid(String)
	 */
	@Override
	public void setUuid(String uuid) {
		cohort.setUuid(uuid);
	}

	/**
     * @see BaseOpenmrsMetadata#getName()
     */
    public String getName() {
    	return cohort == null ? null : cohort.getName();
    }
    
	/**
     * @see BaseOpenmrsMetadata#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
    	cohort.setName(name);
    }
    
	/**
     * @see BaseOpenmrsMetadata#getDescription()
     */
    public String getDescription() {
    	return cohort == null ? null : cohort.getDescription();
    }
    
	/**
     * @see BaseOpenmrsMetadata#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
    	cohort.setDescription(description);
    }

    /**
     * @return the cohort
     */
    public Cohort getCohort() {
    	return cohort;
    }

    /**
     * @param cohort the cohort to set
     */
    public void setCohort(Cohort cohort) {
    	this.cohort = cohort;
    }
}
