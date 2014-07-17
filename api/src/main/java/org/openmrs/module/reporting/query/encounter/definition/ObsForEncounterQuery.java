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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Query that returns Encounters within which an Obs with the given Concept question is recorded
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ObsForEncounterQuery extends BaseQuery<Encounter> implements EncounterQuery {

	//***** PROPERTIES *****

	@ConfigurationProperty
	private Concept question;

    @ConfigurationProperty
    private Date encounterOnOrAfter;

    @ConfigurationProperty
    private Date encounterOnOrBefore;

	@ConfigurationProperty
	private List<Location> encounterLocations;

	@ConfigurationProperty
	private List<EncounterType> encounterTypes;

	//***** PROPERTY ACCESS *****

	public Concept getQuestion() {
		return question;
	}

	public void setQuestion(Concept question) {
		this.question = question;
	}

	public Date getEncounterOnOrAfter() {
		return encounterOnOrAfter;
	}

	public void setEncounterOnOrAfter(Date encounterOnOrAfter) {
		this.encounterOnOrAfter = encounterOnOrAfter;
	}

	public Date getEncounterOnOrBefore() {
		return encounterOnOrBefore;
	}

	public void setEncounterOnOrBefore(Date encounterOnOrBefore) {
		this.encounterOnOrBefore = encounterOnOrBefore;
	}

	public List<Location> getEncounterLocations() {
		return encounterLocations;
	}

	public void setEncounterLocations(List<Location> encounterLocations) {
		this.encounterLocations = encounterLocations;
	}

	public void addEncounterLocation(Location location) {
		if (encounterLocations == null) {
			encounterLocations = new ArrayList<Location>();
		}
		encounterLocations.add(location);
	}

	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}

	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}

	public void addEncounterType(EncounterType encounterType) {
		if (encounterTypes == null) {
			encounterTypes = new ArrayList<EncounterType>();
		}
		encounterTypes.add(encounterType);
	}
}
