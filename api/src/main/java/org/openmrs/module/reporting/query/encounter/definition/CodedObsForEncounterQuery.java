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

import org.openmrs.Concept;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Query that returns Encounters within which an Obs with the given Concept question is recorded
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class CodedObsForEncounterQuery extends ObsForEncounterQuery implements EncounterQuery {

	//***** PROPERTIES *****

	@ConfigurationProperty
	private List<Concept> conceptsToInclude;


	//***** PROPERTY ACCESS *****

	public List<Concept> getConceptsToInclude() {
		return conceptsToInclude;
	}

	public void setConceptsToInclude(List<Concept> conceptsToInclude) {
		this.conceptsToInclude = conceptsToInclude;
	}

	public void addConceptToInclude(Concept conceptToInclude) {
		if (conceptsToInclude == null) {
			conceptsToInclude = new ArrayList<Concept>();
		}
		conceptsToInclude.add(conceptToInclude);
	}
}
