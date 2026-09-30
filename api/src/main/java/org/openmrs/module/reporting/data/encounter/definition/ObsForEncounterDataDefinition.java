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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Column of a specific obs or set of obs associated with an encounter based on the question concept
 * associated with the obs
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ObsForEncounterDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Concept question;

    @ConfigurationProperty
    private List<Concept> answers; // Only returns Obs with the following coded answer values

    @ConfigurationProperty
    private boolean singleObs = true;

    public ObsForEncounterDataDefinition() {
        super();
    }

    public ObsForEncounterDataDefinition(String name) {
        super(name);
    }

    public Concept getQuestion() {
        return question;
    }

    public void setQuestion(Concept question) {
        this.question = question;
    }

    public List<Concept> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Concept> answers) {
        this.answers = answers;
    }

    public void addAnswer(Concept answer) {
        if (answers == null) {
            answers = new ArrayList<Concept>();
        }
        answers.add(answer);
    }

    public boolean isSingleObs() {
        return singleObs;
    }

    public boolean getSingleObs() {
        return isSingleObs();
    }

    public void setSingleObs(boolean singleObs) {
        this.singleObs = singleObs;
    }

    @Override
    public Class<?> getDataType() {
        return singleObs ? Obs.class : List.class;
    }

}
