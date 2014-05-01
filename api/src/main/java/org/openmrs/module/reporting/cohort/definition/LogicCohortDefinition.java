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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;


/**
 * A {@link CohortDefinition} based on a Logic query
 */
@Deprecated
@Caching(strategy=ConfigurationPropertyAndParameterCachingStrategy.class)
@Localized("reporting.LogicCohortDefinition")
public class LogicCohortDefinition extends BaseCohortDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true)
	private String logic;
	
	
    /**
     * Default constructor
     */
    public LogicCohortDefinition() {
    }
    
    /**
     * @param logic
     */
    public LogicCohortDefinition(String logic) {
    	this.logic = logic;
    }
    
    /**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Logic Cohort Query: [" + ObjectUtil.nvlStr(logic, "") + "]";
	}

	
    /**
     * @return the logic
     */
    public String getLogic() {
    	return logic;
    }

	
    /**
     * @param logic the logic to set
     */
    public void setLogic(String logic) {
    	this.logic = logic;
    }

    
}
