/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.library;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BuiltInEncounterDataLibraryTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private TestDataManager data;

    @Autowired
    private BuiltInEncounterDataLibrary library;

    @Autowired
    private EncounterDataService encounterDataService;

    private EncounterEvaluationContext context;

    private Encounter e1;

    private EncounterIdSet encounterIdSet;

    @Before
    public void setUp() throws Exception {
        e1 = data.encounter().patient(7)
                .encounterType("Scheduled")
                .location("Xanadu")
                .encounterDatetime("2013-10-02 09:15:00")
                .dateCreated("2013-10-03 00:00:00.0").creator(1).save();

        encounterIdSet = new EncounterIdSet(e1.getId());
        context = new EncounterEvaluationContext();
        context.setBaseEncounters(encounterIdSet);
    }

    @Test
    public void testEncounterId() throws EvaluationException {
        EncounterDataDefinition definition = library.getEncounterId();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Integer) data.getData().get(e1.getId()), is(e1.getId()));
    }

    @Test
    public void testPatientId() throws EvaluationException {
        EncounterDataDefinition definition = library.getPatientId();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Integer) data.getData().get(e1.getId()), is(7));
    }

    @Test
    public void testEncounterTypeName() throws EvaluationException {
        EncounterDataDefinition definition = library.getEncounterTypeName();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(e1.getId()), is("Scheduled"));
    }

    @Test
    public void testLocationName() throws EvaluationException {
        EncounterDataDefinition definition = library.getLocationName();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((String) data.getData().get(e1.getId()), is("Xanadu"));
    }

    @Test
    public void testEncounterDatetime() throws EvaluationException {
        EncounterDataDefinition definition = library.getEncounterDatetime();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Timestamp) data.getData().get(e1.getId()), is(new Timestamp(DateUtil.parseYmdhms("2013-10-02 09:15:00").getTime())));
    }

    @Test
    public void testEncounterDateCreated() throws EvaluationException {
        EncounterDataDefinition definition = library.getDateCreated();
        EvaluatedEncounterData data = encounterDataService.evaluate(definition, context);
        assertThat((Timestamp) data.getData().get(e1.getId()), is(new Timestamp(DateUtil.parseYmd("2013-10-03").getTime())));
    }

}
