/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
