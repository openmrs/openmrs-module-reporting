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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lets you query for encounters based on simple properties on Encounter
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class BasicEncounterQuery extends BaseQuery<Encounter> implements EncounterQuery {

    public static final long serialVersionUID = 1L;

	@ConfigurationProperty
	private TimeQualifier which;

	@ConfigurationProperty
	private Integer whichNumber;

	@ConfigurationProperty
	public List<EncounterType> encounterTypes;

	@ConfigurationProperty
	public List<Form> forms;

    @ConfigurationProperty
    public Date onOrAfter;

    @ConfigurationProperty
    public Date onOrBefore;

	@ConfigurationProperty
	private List<Location> locationList;

	public TimeQualifier getWhich() {
		return which;
	}

	public void setWhich(TimeQualifier which) {
		this.which = which;
	}

	public Integer getWhichNumber() {
		return whichNumber;
	}

	public void setWhichNumber(Integer whichNumber) {
		this.whichNumber = whichNumber;
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

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public void addForm(Form form) {
		if (forms == null) {
			forms = new ArrayList<Form>();
		}
		forms.add(form);
	}

	public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	public void addLocation(Location location) {
		if (locationList == null) {
			locationList = new ArrayList<Location>();
		}
		locationList.add(location);
	}
}
