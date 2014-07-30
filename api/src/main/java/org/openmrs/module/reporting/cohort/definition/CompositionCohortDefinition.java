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

import org.openmrs.Patient;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.CompositionQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Supports the evaluation of a composition of cohort definitions
 */
@Localized("reporting.CompositionCohortDefinition")
public class CompositionCohortDefinition extends CompositionQuery<CohortDefinition, Patient> implements CohortDefinition {

	@ConfigurationProperty
	private Map<String, Mapped<CohortDefinition>> searches;

    /**
     * Default Constructor
     */
	public CompositionCohortDefinition() {
		super();
	}

	@Override
	public Map<String, Mapped<CohortDefinition>> getSearches() {
		if (searches == null) {
			searches = new HashMap<String, Mapped<CohortDefinition>>();
		}
		return searches;
	}

	@Override
	public void setSearches(Map<String, Mapped<CohortDefinition>> searches) {
		this.searches = searches;
	}
}
