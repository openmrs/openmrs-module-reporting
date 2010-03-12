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

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;

/**
 * Cohort-based indicator bean 
 */
public class IndicatorForm {
	
    private static final long serialVersionUID = 1L;
    
    // Basic Details 
    private String columnKey = null;
    private String displayName = null;
    private String indicatorType = null;  // count, fraction, custom
    private String dateRangeType = null;  // snapshot vs period (or range)

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
    
    // Parameter to parameter mapping (for adding a child to parent)
    private Map<String, Object> parameterMapping = new HashMap<String, Object>();

    // Parameter to value mapping (for evaluating a parameterizable)  
    private Map<String, String> parameterValues = new HashMap<String, String>();

    // Location filter 
    private CohortDefinition locationFilter = null;

    // Parameter to parameter mapping for the location cohort definition
    private Map<String, Object> locationFilterParameterMapping = new HashMap<String, Object>();

    // Parameter to value mapping (for evaluating a parameterizable)  
    private Map<String, String> locationFilterParameterValues = new HashMap<String, String>();
    

    
    
    //***** CONSTRUCTORS *****
    public IndicatorForm() { }

    
    
    // =====  Indicator Details  =====
    
    public String getUuid() { 
    	return this.uuid;
    }
    
    public void setUuid(String uuid) { 
    	this.uuid = uuid;
    }    

    public String getReportUuid() {
		return reportUuid;
	}

	public void setReportUuid(String reportUuid) {
		this.reportUuid = reportUuid;
	}	
    

	public String getCohortDefinitionUuid() {
		return cohortDefinitionUuid;
	}

	public void setCohortDefinitionUuid(String cohortDefinitionUuid) {
		this.cohortDefinitionUuid = cohortDefinitionUuid;
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

	public String getIndicatorType() {
		return indicatorType;
	}

	public void setIndicatorType(String indicatorType) {
		this.indicatorType = indicatorType;
	}
	
	public CohortIndicator getCohortIndicator() {
		return cohortIndicator;
	}
	
	// =====  Simple Count calculation  =====

	public void setCohortIndicator(CohortIndicator cohortIndicator) {
		this.cohortIndicator = cohortIndicator;
	}
	
	public CohortDefinition getCohortDefinition() {
		return cohortDefinition;
	}

	public void setCohortDefinition(CohortDefinition cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}	

	public Map<String, Object> getParameterMapping() {
		return parameterMapping;
	}

	public void setParameterMapping(Map<String, Object> parameterMapping) {
		this.parameterMapping = parameterMapping;
	}

	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, String> parameterValues) {
		this.parameterValues = parameterValues;
	}


	// =====  Logic Query calculation  =====

	public String getLogicQuery() {
		return logicQuery;
	}

	public void setLogicQuery(String logicQuery) {
		this.logicQuery = logicQuery;
	}

	public String getAggregator() {
		return aggregator;
	}

	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}

	// =====  Location Filter  =====
	
	public CohortDefinition getLocationFilter() {
		return locationFilter;
	}

	public void setLocationFilter(CohortDefinition locationFilter) {
		this.locationFilter = locationFilter;
	}

	public Map<String, Object> getLocationFilterParameterMapping() {
		return locationFilterParameterMapping;
	}

	public void setLocationFilterParameterMapping(Map<String, Object> locationFilterParameterMapping) {
		this.locationFilterParameterMapping = locationFilterParameterMapping;
	}
		
	public Map<String, String> getLocationFilterParameterValues() {
		return locationFilterParameterValues;
	}

	public void setLocationFilterParameterValues(Map<String, String> locationFilterParameterValues) {
		this.locationFilterParameterValues = locationFilterParameterValues;
	}	

	
}