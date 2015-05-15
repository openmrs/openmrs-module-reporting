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
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lets you query for encounters based on their audit information
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class AuditEncounterQuery extends BaseQuery<Encounter> implements EncounterQuery {

    public static final long serialVersionUID = 1L;

	@ConfigurationProperty
	public List<EncounterType> encounterTypes;

	@ConfigurationProperty
	public Date createdOnOrAfter;

	@ConfigurationProperty
	public Date createdOnOrBefore;

	@ConfigurationProperty
	public Integer latestCreatedNumber;

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

	public Date getCreatedOnOrAfter() {
		return createdOnOrAfter;
	}

	public void setCreatedOnOrAfter(Date createdOnOrAfter) {
		this.createdOnOrAfter = createdOnOrAfter;
	}

	public Date getCreatedOnOrBefore() {
		return createdOnOrBefore;
	}

	public void setCreatedOnOrBefore(Date createdOnOrBefore) {
		this.createdOnOrBefore = createdOnOrBefore;
	}

	public Integer getLatestCreatedNumber() {
		return latestCreatedNumber;
	}

	public void setLatestCreatedNumber(Integer latestCreatedNumber) {
		this.latestCreatedNumber = latestCreatedNumber;
	}
}
