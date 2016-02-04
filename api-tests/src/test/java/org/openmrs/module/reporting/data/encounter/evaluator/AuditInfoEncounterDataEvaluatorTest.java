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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AuditInfoEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EncounterDataService encounterDataService;

    @Autowired
    TestDataManager td;

    private Encounter encounter;

    @Before
    public void setup() throws Exception {
        User user = Context.getUserService().getUser(1);
        Patient patient = td.randomPatient().save();
        encounter = td.randomEncounter().patient(patient).creator(user).dateCreated("2013-02-04 06:07:08").changedBy(user).dateChanged("2013-03-05 07:08:09").save();
    }

    @Test
    public void testEvaluate() throws Exception {
        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(encounter.getId()));

        EvaluatedEncounterData result = encounterDataService.evaluate(new AuditInfoEncounterDataDefinition(), context);
        assertThat(result.getData().size(), is(1));
        AuditInfo auditInfo = (AuditInfo) result.getData().get(encounter.getId());
        assertThat(auditInfo.getCreator(), is(encounter.getCreator()));
        assertThat(auditInfo.getDateCreated().getTime(), is(encounter.getDateCreated().getTime()));
        assertThat(auditInfo.getChangedBy(), is(encounter.getChangedBy()));
        assertThat(auditInfo.getDateChanged().getTime(), is(encounter.getDateChanged().getTime()));
        assertThat(auditInfo.getVoided(), is(encounter.getVoided()));
        assertThat(auditInfo.getVoidedBy(), is(encounter.getVoidedBy()));
        assertThat(auditInfo.getDateVoided(), is(encounter.getDateVoided()));
        assertThat(auditInfo.getVoidReason(), is(encounter.getVoidReason()));

    }

}
