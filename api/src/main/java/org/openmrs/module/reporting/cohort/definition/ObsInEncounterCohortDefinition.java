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

	@ConfigurationProperty(group = "where")
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

	@ConfigurationProperty(group = "where")
	private boolean includeChildLocations = false;

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

	public boolean isIncludeChildLocations() {
		return includeChildLocations;
	}

	public boolean getIncludeChildLocations() {
		return isIncludeChildLocations();
	}

	public void setIncludeChildLocations(boolean includeChildLocations) {
		this.includeChildLocations = includeChildLocations;
	}
}
