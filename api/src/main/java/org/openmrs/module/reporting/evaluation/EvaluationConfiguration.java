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
package org.openmrs.module.reporting.evaluation;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;

import java.util.Date;
import java.util.Map;

/**
 * Represents the configurable aspects of an EvaluationContext,
 * that may be set in order to control the results of an Evaluation
 */
public class EvaluationConfiguration {

	private Map<String, Object> parameterValues;
	private CohortDefinition baseCohortDefinition;
	private Date evaluationDate;

	/**
	 * Default Constructor
	 */
	public EvaluationConfiguration() { }

	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public CohortDefinition getBaseCohortDefinition() {
		return baseCohortDefinition;
	}

	public void setBaseCohortDefinition(CohortDefinition baseCohortDefinition) {
		this.baseCohortDefinition = baseCohortDefinition;
	}

	public Date getEvaluationDate() {
		return evaluationDate;
	}

	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}
}
