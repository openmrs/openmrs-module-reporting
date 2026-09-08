/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches other encounters with the same patient and encounterDatetime as the one in question
 * E.g. you could find any encounters of type "Admission" simultaneous to visit note encounters
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class SimultaneousEncountersDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private List<EncounterType> encounterTypeList;

    public void addEncounterType(EncounterType encounterType) {
        if (encounterTypeList == null) {
            encounterTypeList = new ArrayList<EncounterType>();
        }
        encounterTypeList.add(encounterType);
    }

    public List<EncounterType> getEncounterTypeList() {
        return encounterTypeList;
    }

    public void setEncounterTypeList(List<EncounterType> encounterTypeList) {
        this.encounterTypeList = encounterTypeList;
    }

    @Override
    public Class<?> getDataType() {
        return Encounter.class;
    }
}
