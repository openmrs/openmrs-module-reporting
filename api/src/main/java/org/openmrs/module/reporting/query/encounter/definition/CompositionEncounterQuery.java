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
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.CompositionQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Supports the evaluation of a composition of encounter queries
 */
@Localized("reporting.CompositionEncounterQuery")
public class CompositionEncounterQuery extends CompositionQuery<EncounterQuery, Encounter> implements EncounterQuery {

	@ConfigurationProperty
	private Map<String, Mapped<EncounterQuery>> searches;

    /**
     * Default Constructor
     */
	public CompositionEncounterQuery() {
		super();
	}

	@Override
	public Map<String, Mapped<EncounterQuery>> getSearches() {
		if (searches == null) {
			searches = new HashMap<String, Mapped<EncounterQuery>>();
		}
		return searches;
	}

	@Override
	public void setSearches(Map<String, Mapped<EncounterQuery>> searches) {
		this.searches = searches;
	}
}
