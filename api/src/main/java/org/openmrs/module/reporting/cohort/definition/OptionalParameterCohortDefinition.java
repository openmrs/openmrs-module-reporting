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
