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
