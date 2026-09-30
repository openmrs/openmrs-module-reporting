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

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.HashMap;
import java.util.Map;

/**
 * Primary use case is to support base Cohorts for report definitions that take in optional parameters
 * For the configured parameter(s), if these parameters are non-null, evaluate the Cohort Definition,
 * but if these parameters are null, return "All Patients"
 */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class ConditionalParameterCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private Map<Object, Mapped<? extends CohortDefinition>> conditionalCohortDefinitions;

	@ConfigurationProperty
	private Mapped<? extends CohortDefinition> defaultCohortDefinition;

	@ConfigurationProperty
	private String parameterToCheck;

    public ConditionalParameterCohortDefinition() { }

	public Map<Object, Mapped<? extends CohortDefinition>> getConditionalCohortDefinitions() {
		return conditionalCohortDefinitions;
	}

	public void setConditionalCohortDefinitions(Map<Object, Mapped<? extends CohortDefinition>> conditionalCohortDefinitions) {
		this.conditionalCohortDefinitions = conditionalCohortDefinitions;
	}

	public void addConditionalCohortDefinition(Object parameterValue, Mapped<? extends CohortDefinition> cohortDefinition) {
		if (conditionalCohortDefinitions == null) {
			conditionalCohortDefinitions = new HashMap<Object, Mapped<? extends CohortDefinition>>();
		}
		conditionalCohortDefinitions.put(parameterValue, cohortDefinition);
	}

	public Mapped<? extends CohortDefinition> getDefaultCohortDefinition() {
		return defaultCohortDefinition;
	}

	public void setDefaultCohortDefinition(Mapped<? extends CohortDefinition> defaultCohortDefinition) {
		this.defaultCohortDefinition = defaultCohortDefinition;
	}

	public String getParameterToCheck() {
		return parameterToCheck;
	}

	public void setParameterToCheck(String parameterToCheck) {
		this.parameterToCheck = parameterToCheck;
	}
}
