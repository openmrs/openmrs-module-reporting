/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.SimultaneousEncountersDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SimultaneousEncountersDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    TestDataManager testData;

    @Autowired
    EncounterDataService encounterDataService;

    @Test
    public void testEvaluate() throws Exception {
        Patient p1 = testData.randomPatient().save();
        Patient p2 = testData.randomPatient().save();

        Encounter indexEncounter = testData.randomEncounter().patient(p1).save();
        Encounter associated = testData.randomEncounter().patient(p1).encounterDatetime(indexEncounter.getEncounterDatetime()).save();
        Encounter another = testData.randomEncounter().patient(p1).save();
        Encounter otherPatient = testData.randomEncounter().patient(p2).encounterDatetime(indexEncounter.getEncounterDatetime()).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(indexEncounter.getEncounterId()));

        SimultaneousEncountersDataDefinition def = new SimultaneousEncountersDataDefinition();
        def.addEncounterType(associated.getEncounterType());

        EvaluatedEncounterData result = encounterDataService.evaluate(def, context);
        assertThat(result.getData().size(), is(1));
        assertThat((Encounter) result.getData().values().iterator().next(), is(associated));
    }

    @Test
    public void testEvaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        Patient p1 = testData.randomPatient().save();

        Encounter indexEncounter = testData.randomEncounter().patient(p1).save();
        Encounter associated = testData.randomEncounter().patient(p1).encounterDatetime(indexEncounter.getEncounterDatetime()).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet());

        SimultaneousEncountersDataDefinition def = new SimultaneousEncountersDataDefinition();
        def.addEncounterType(associated.getEncounterType());

        EvaluatedEncounterData result = encounterDataService.evaluate(def, context);
        assertThat(result.getData().size(), is(0));
    }


}
