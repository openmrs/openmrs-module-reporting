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
