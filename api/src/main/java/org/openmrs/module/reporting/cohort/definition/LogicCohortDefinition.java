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
