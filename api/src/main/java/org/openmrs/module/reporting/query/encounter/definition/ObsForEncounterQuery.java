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
