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

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;
import java.util.List;

/**
 * This class serves two purposes
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ObsInEncounterCohortDefinition")
public class ObsInEncounterCohortDefinition extends BaseCohortDefinition {

	//***** CONFIGURATION PROPERTIES *****

	@ConfigurationProperty
	private TimeQualifier whichEncounter;

	@ConfigurationProperty
	private List<EncounterType> encounterTypes;

	@ConfigurationProperty
	private Date encounterOnOrAfter;

	@ConfigurationProperty
	private Date encounterOnOrBefore;

	@ConfigurationProperty
	private List<Location> encounterLocations;

	@ConfigurationProperty
	private Concept question;

	@ConfigurationProperty
	private RangeComparator valueOperator1;

	@ConfigurationProperty
	private Date valueDatetime1;

	@ConfigurationProperty
	private RangeComparator valueOperator2;

	@ConfigurationProperty
	private Date valueDatetime2;

	// In the future, here we will add valueNumeric1/2 valueCoded1/2 if and when needed

	//***** PROPERTY ACCESS *****

	public TimeQualifier getWhichEncounter() {
		return whichEncounter;
	}

	public void setWhichEncounter(TimeQualifier whichEncounter) {
		this.whichEncounter = whichEncounter;
	}

	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}

	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
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

	public Concept getQuestion() {
		return question;
	}

	public void setQuestion(Concept question) {
		this.question = question;
	}

	public RangeComparator getValueOperator1() {
		return valueOperator1;
	}

	public void setValueOperator1(RangeComparator valueOperator1) {
		this.valueOperator1 = valueOperator1;
	}

	public Date getValueDatetime1() {
		return valueDatetime1;
	}

	public void setValueDatetime1(Date valueDatetime1) {
		this.valueDatetime1 = valueDatetime1;
	}

	public RangeComparator getValueOperator2() {
		return valueOperator2;
	}

	public void setValueOperator2(RangeComparator valueOperator2) {
		this.valueOperator2 = valueOperator2;
	}

	public Date getValueDatetime2() {
		return valueDatetime2;
	}

	public void setValueDatetime2(Date valueDatetime2) {
		this.valueDatetime2 = valueDatetime2;
	}
}
