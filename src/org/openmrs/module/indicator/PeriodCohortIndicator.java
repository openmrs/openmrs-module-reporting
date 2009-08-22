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
package org.openmrs.module.indicator;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.openmrs.Location;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.aggregation.Aggregator;

/**
 * Period cohort-based indicator
 */
public class PeriodCohortIndicator extends CohortIndicator {
	
    private static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public PeriodCohortIndicator() {
    	super();
		if (this.getUuid()==null) { 
			this.addParameter(new Parameter("startDate", "Enter a Start Date", Date.class, null, true));
			this.addParameter(new Parameter("endDate", "Enter an End Date", Date.class, null, true));
			this.addParameter(new Parameter("location", "Choose a Location", Location.class, null, true));			
		}
    	
    }

}