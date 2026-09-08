/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.visit.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

/**
 * Lets you query for encounters based on simple properties on Encounter
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class BasicVisitQuery extends BaseQuery<Visit> implements VisitQuery {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty
	public List<VisitType> visitTypes;

	@ConfigurationProperty
	private List<Location> locationList;

	@ConfigurationProperty
	private Date startedOnOrAfter;

	@ConfigurationProperty
	private Date startedOnOrBefore;

	@ConfigurationProperty
	private Date endedOnOrAfter;

	@ConfigurationProperty
	private Date endedOnOrBefore;

	public Date getStartedOnOrAfter() {
		return startedOnOrAfter;
	}

	public void addVisitType(VisitType visitType) {
		if (visitTypes == null) {
			visitTypes = new ArrayList<VisitType>();
		}
		visitTypes.add(visitType);
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
	
	public void setStartedOnOrAfter(Date startedOnOrAfter) {
		this.startedOnOrAfter = startedOnOrAfter;
	}

	public Date getStartedOnOrBefore() {
		return startedOnOrBefore;
	}

	public void setStartedOnOrBefore(Date startedOnOrBefore) {
		this.startedOnOrBefore = startedOnOrBefore;
	}

	public Date getEndedOnOrAfter() {
		return endedOnOrAfter;
	}

	public void setEndedOnOrAfter(Date endedOnOrAfter) {
		this.endedOnOrAfter = endedOnOrAfter;
	}

	public Date getEndedOnOrBefore() {
		return endedOnOrBefore;
	}

	public void setEndedOnOrBefore(Date endedOnOrBefore) {
		this.endedOnOrBefore = endedOnOrBefore;
	}

	public List<VisitType> getVisitTypes() {
		return visitTypes;
	}

	public void setVisitTypes(List<VisitType> visitTypes) {
		this.visitTypes = visitTypes;
	}
}
