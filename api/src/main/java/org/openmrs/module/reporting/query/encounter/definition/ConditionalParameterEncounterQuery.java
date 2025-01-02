/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.definition;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.BaseQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Primary use case is to support base Cohorts for report definitions that take in optional parameters
 * For the configured parameter(s), if these parameters are non-null, evaluate the Cohort Definition,
 * but if these parameters are null, return "All Patients"
 */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class ConditionalParameterEncounterQuery extends BaseQuery<Encounter> implements EncounterQuery {

	@ConfigurationProperty
	private Map<Object, Mapped<? extends EncounterQuery>> conditionalQueries;

	@ConfigurationProperty
	private Mapped<? extends EncounterQuery> defaultQuery;

	@ConfigurationProperty
	private String parameterToCheck;

    public ConditionalParameterEncounterQuery() { }

	public Map<Object, Mapped<? extends EncounterQuery>> getConditionalQueries() {
		return conditionalQueries;
	}

	public void setConditionalQueries(Map<Object, Mapped<? extends EncounterQuery>> queriesToCheck) {
		this.conditionalQueries = queriesToCheck;
	}

	public void addConditionalQuery(Object parameterValue, Mapped<? extends EncounterQuery> query) {
		if (conditionalQueries == null) {
			conditionalQueries = new HashMap<Object, Mapped<? extends EncounterQuery>>();
		}
		conditionalQueries.put(parameterValue, query);
	}

	public Mapped<? extends EncounterQuery> getDefaultQuery() {
		return defaultQuery;
	}

	public void setDefaultQuery(Mapped<? extends EncounterQuery> defaultQuery) {
		this.defaultQuery = defaultQuery;
	}

	public String getParameterToCheck() {
		return parameterToCheck;
	}

	public void setParameterToCheck(String parameterToCheck) {
		this.parameterToCheck = parameterToCheck;
	}
}
