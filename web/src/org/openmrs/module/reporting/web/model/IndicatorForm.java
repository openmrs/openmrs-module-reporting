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
package org.openmrs.module.reporting.web.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;

/**
 * Cohort-based indicator bean 
 */
public class IndicatorForm {
	
    private static final long serialVersionUID = 1L;
    
    // Basic Details 
    private String columnKey = null;
    private String displayName = null;
    private String indicatorType = null;  // supports CohortDefinition or LogicQuery

    // Action 
    private String action = null;
    
    // Logic-based indicator  
    private String logicQuery = null;
    private String aggregator = null;
    
    // Cohort-based indicator
    private String uuid = null; 		
    private String reportUuid = null;
    private String cohortDefinitionUuid = null;
    private CohortIndicator cohortIndicator = null;
    private CohortDefinition cohortDefinition = null;
    private Map<String, String> parameterMapping = new HashMap<String, String>();
    
    //***** CONSTRUCTORS *****
    public IndicatorForm() { }

    public String getUuid() { 
    	return this.uuid;
    }
    
    public void setUuid(String uuid) { 
    	this.uuid = uuid;
    }    
    
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}    
    
	public String getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(String key) {
		this.columnKey = key;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAggregator() {
		return aggregator;
	}

	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}

	public CohortIndicator getCohortIndicator() {
		return cohortIndicator;
	}

	public void setCohortIndicator(CohortIndicator cohortIndicator) {
		this.cohortIndicator = cohortIndicator;
	}

	public Map<String, String> getParameterMapping() {
		return parameterMapping;
	}

	public void setParameterMapping(Map<String, String> parameterMapping) {
		this.parameterMapping = parameterMapping;
	}

	public String getIndicatorType() {
		return indicatorType;
	}

	public void setIndicatorType(String indicatorType) {
		this.indicatorType = indicatorType;
	}

	public String getLogicQuery() {
		return logicQuery;
	}

	public void setLogicQuery(String logicQuery) {
		this.logicQuery = logicQuery;
	}

	public CohortDefinition getCohortDefinition() {
		return cohortDefinition;
	}

	public void setCohortDefinition(CohortDefinition cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}

	public String getCohortDefinitionUuid() {
		return cohortDefinitionUuid;
	}

	public void setCohortDefinitionUuid(String cohortDefinitionUuid) {
		this.cohortDefinitionUuid = cohortDefinitionUuid;
	}

	public String getReportUuid() {
		return reportUuid;
	}

	public void setReportUuid(String reportUuid) {
		this.reportUuid = reportUuid;
	}
	
}