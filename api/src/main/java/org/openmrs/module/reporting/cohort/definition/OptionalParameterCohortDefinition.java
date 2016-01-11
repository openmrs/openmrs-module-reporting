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
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Arrays;
import java.util.List;

/**
 * Primary use case is to support base Cohorts for report definitions that take in optional parameters
 * For the configured parameter(s), if these parameters are non-null, evaluate the Cohort Definition,
 * but if these parameters are null, return "All Patients"
 */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class OptionalParameterCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private CohortDefinition wrappedCohortDefinition;

	@ConfigurationProperty
	private List<String> parametersToCheck;

    public OptionalParameterCohortDefinition() { }

	public OptionalParameterCohortDefinition(CohortDefinition wrappedCohortDefinition, String...parametersToCheck) {
		setWrappedCohortDefinition(wrappedCohortDefinition);
		if (parametersToCheck != null) {
			this.parametersToCheck = Arrays.asList(parametersToCheck);
		}
	}

	public CohortDefinition getWrappedCohortDefinition() {
		return wrappedCohortDefinition;
	}

	public void setWrappedCohortDefinition(CohortDefinition wrappedCohortDefinition) {
		this.wrappedCohortDefinition = wrappedCohortDefinition;
		setParameters(wrappedCohortDefinition.getParameters());
	}

	public List<String> getParametersToCheck() {
		return parametersToCheck;
	}

	public void setParametersToCheck(List<String> parametersToCheck) {
		this.parametersToCheck = parametersToCheck;
	}
}
