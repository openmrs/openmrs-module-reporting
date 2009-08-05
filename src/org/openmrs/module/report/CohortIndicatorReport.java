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
package org.openmrs.module.report;

import java.io.Serializable;
import java.util.List;

import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.indicator.CohortIndicator;

/**
 * Cohort-based indicator report.
 */
public class CohortIndicatorReport implements Serializable {
	
	private String name;
	private String description;
	private CohortIndicatorDataSetDefinition dataSetDefinition;
    private List<CohortIndicator> cohortIndicators;
    /**
     * Default Constructor
     */
    public CohortIndicatorReport() { }

    
    /**
     * Public constructor with arguments.
     * @param name
     * @param description
     * @param cohortDefinition
     * @param logicCriteria
     * @param aggregator
     */
    public CohortIndicatorReport(String name, String description) { 
    	this.name = name;
    	this.description = description;
    }


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public CohortIndicatorDataSetDefinition getDataSetDefinition() {
		return dataSetDefinition;
	}


	public void setDataSetDefinition(
			CohortIndicatorDataSetDefinition dataSetDefinition) {
		this.dataSetDefinition = dataSetDefinition;
	}


	public List<CohortIndicator> getCohortIndicators() {
		return cohortIndicators;
	}


	public void setCohortIndicators(List<CohortIndicator> cohortIndicators) {
		this.cohortIndicators = cohortIndicators;
	}
}