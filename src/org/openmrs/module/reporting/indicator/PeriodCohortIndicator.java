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
package org.openmrs.module.reporting.indicator;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.openmrs.Location;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.aggregation.Aggregator;

/**
 * Period cohort-based indicator is a simple indicator that has three
 * default parameters (start date, end date, location).  
 */
public class PeriodCohortIndicator extends CohortIndicator {
	
    private static final long serialVersionUID = 1L;
        
    /**
     * Default constructor that just adds the default parameters.
     */
    public PeriodCohortIndicator() {
    	super();    	
    	// FIXME 
		addParameter(new Parameter("startDate", "Enter a start date", Date.class, null, true));
		addParameter(new Parameter("endDate", "Enter an end date", Date.class, null, true));
		addParameter(new Parameter("location", "Choose a location", Location.class, null, true));    	
    }
 
    /** 
     * Constructor that fully specifies the indicator.
     * 
     * @param cohortDefinition
     * @param mappings
     */
    public PeriodCohortIndicator(CohortDefinition cohortDefinition, Map<String,Object> mappings) { 
    	this();
    	setCohortDefinition(cohortDefinition, mappings);
    }
    
    

}