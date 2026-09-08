/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator;

import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Base Implementation of Indicator
 */
public abstract class BaseIndicator extends BaseDefinition implements Indicator {
	
    public static final long serialVersionUID = 1920394873L;
    
    //***** PROPERTIES *****

    private Integer id;

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public BaseIndicator() {
    	super();
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