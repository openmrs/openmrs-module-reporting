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
