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
    
    //***** PROPERTIES *****
    private String logicQuery = null;
    private String indicatorType = null;  // supports CohortDefinition and LogicQuery
    
    private String cohortDefinitionUuid = null;
    private CohortIndicator cohortIndicator = new CohortIndicator();
    private CohortDefinition cohortDefinition = null;
    private Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    
    //***** CONSTRUCTORS *****
    public IndicatorForm() { }

	public CohortIndicator getCohortIndicator() {
		return cohortIndicator;
	}

	public void setCohortIndicator(CohortIndicator cohortIndicator) {
		this.cohortIndicator = cohortIndicator;
	}

	public Map<String, Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Parameter> parameters) {
		this.parameters = parameters;
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

}