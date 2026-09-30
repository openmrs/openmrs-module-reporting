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
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Finds patients with encounters that include a given coded obs value
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class EncounterWithCodedObsCohortDefinition extends BaseCohortDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Date onOrAfter;

    @ConfigurationProperty
    private Date onOrBefore;

    @ConfigurationProperty
    private List<EncounterType> encounterTypeList;

    @ConfigurationProperty
    private List<Location> locationList;

    @ConfigurationProperty
    private Concept concept;

    @ConfigurationProperty
    private List<Concept> includeCodedValues;

    @ConfigurationProperty
    private List<Concept> excludeCodedValues;

    @ConfigurationProperty
    private boolean includeNoObsValue = false;

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

    public List<EncounterType> getEncounterTypeList() {
        return encounterTypeList;
    }

    public void setEncounterTypeList(List<EncounterType> encounterTypeList) {
        this.encounterTypeList = encounterTypeList;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public List<Concept> getIncludeCodedValues() {
        return includeCodedValues;
    }

    public void setIncludeCodedValues(List<Concept> includeCodedValues) {
        this.includeCodedValues = includeCodedValues;
    }

    public List<Concept> getExcludeCodedValues() {
        return excludeCodedValues;
    }

    public void setExcludeCodedValues(List<Concept> excludeCodedValues) {
        this.excludeCodedValues = excludeCodedValues;
    }

    public boolean isIncludeNoObsValue() {
        return includeNoObsValue;
    }

    public boolean getIncludeNoObsValue() {
        return isIncludeNoObsValue();
    }

    public void setIncludeNoObsValue(boolean includeNoObsValue) {
        this.includeNoObsValue = includeNoObsValue;
    }

    public void addIncludeCodedValue(Concept codedValue) {
        if (includeCodedValues == null) {
            includeCodedValues = new ArrayList<Concept>();
        }
        includeCodedValues.add(codedValue);
    }

    public void addExcludeCodedValue(Concept codedValue) {
        if (excludeCodedValues == null) {
            excludeCodedValues = new ArrayList<Concept>();
        }
        excludeCodedValues.add(codedValue);
    }

    public void addEncounterType(EncounterType encounterType) {
        if (encounterTypeList == null) {
            encounterTypeList = new ArrayList<EncounterType>();
        }
        encounterTypeList.add(encounterType);
    }

    public void addLocation(Location location) {
        if (locationList == null) {
            locationList = new ArrayList<Location>();
        }
        locationList.add(location);
    }

}
