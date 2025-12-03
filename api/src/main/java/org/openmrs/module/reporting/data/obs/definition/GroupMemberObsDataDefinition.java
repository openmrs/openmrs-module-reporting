/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.List;

/**
 *
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class GroupMemberObsDataDefinition extends BaseDataDefinition implements ObsDataDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(required=true)
    private Concept question;

    @ConfigurationProperty
    private boolean singleObs = true;

    public Concept getQuestion() {
        return question;
    }

    public void setQuestion(Concept question) {
        this.question = question;
    }

    public boolean isSingleObs() {
        return singleObs;
    }

    public void setSingleObs(boolean singleObs) {
        this.singleObs = singleObs;
    }

    @Override
    public Class<?> getDataType() {
        return singleObs ? Obs.class : List.class;
    }

}
