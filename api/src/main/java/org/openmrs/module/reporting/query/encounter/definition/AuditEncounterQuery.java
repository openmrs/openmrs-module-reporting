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
