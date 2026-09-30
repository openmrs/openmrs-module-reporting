/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MappedParametersCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    CohortDefinitionService service;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        Date date = DateUtil.parseDate("2008-08-01", "yyyy-MM-dd");
        EncounterCohortDefinition original = new EncounterCohortDefinition();
        original.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        original.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));

        Map<String, String> renamedParameters = new HashMap<String, String>();
        renamedParameters.put("onOrAfter", "startDate");
        renamedParameters.put("onOrBefore", "endDate");
        MappedParametersCohortDefinition renamed = new MappedParametersCohortDefinition(original, renamedParameters);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", date);
        context.addParameterValue("endDate", date);
        EvaluatedCohort result = service.evaluate(renamed, context);

        assertThat(result.size(), is(1));
        assertTrue(result.contains(7));
    }

}
