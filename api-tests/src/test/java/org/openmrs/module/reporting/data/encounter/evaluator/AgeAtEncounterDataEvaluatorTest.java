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
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AgeAtEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private EncounterDataService encounterDataService;

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(3));

        EvaluatedEncounterData result = encounterDataService.evaluate(new AgeAtEncounterDataDefinition(), context);
        assertThat(result.getData().size(), is(1));
        assertThat(((Age) result.getData().get(3)).getBirthDate().getTime(), is(DateUtil.parseYmd("1976-08-25").getTime()));
        assertThat(((Age) result.getData().get(3)).getCurrentDate().getTime(), is(DateUtil.parseYmd("2008-08-01").getTime()));
    }

}
