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
