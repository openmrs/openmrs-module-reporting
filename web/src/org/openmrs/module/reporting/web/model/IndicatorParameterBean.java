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

import java.util.Map;
import java.util.UUID;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;

/**
 * Cohort-based indicator
 */
public class IndicatorParameterBean {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    private CohortIndicator cohortIndicator = new CohortIndicator();
    
    private Parameter parameter = new Parameter();

    //***** CONSTRUCTORS *****
    public IndicatorParameterBean() { }

	public CohortIndicator getCohortIndicator() {
		return cohortIndicator;
	}

	public void setCohortIndicator(CohortIndicator cohortIndicator) {
		this.cohortIndicator = cohortIndicator;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	} 
}