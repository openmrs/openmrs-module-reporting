/*
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
