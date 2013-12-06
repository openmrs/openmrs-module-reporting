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

package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.DefinitionLibraryCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ReportingMatchers;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class DefinitionLibraryCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CohortDefinitionService service;

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluateWithNoParameters() throws Exception {
        DefinitionLibraryCohortDefinition cd = new DefinitionLibraryCohortDefinition();
        cd.setDefinitionKey(BuiltInCohortDefinitionLibrary.PREFIX + "males");

        EvaluatedCohort result = service.evaluate(cd, new EvaluationContext());
        assertThat(result, ReportingMatchers.isCohortWithExactlyIds(2, 6, 21));
    }

    @Test
    public void testEvaluateWithParameterValues() throws Exception {
        Map<String, Object> parameterValues = new HashMap<String, Object>();
        parameterValues.put("effectiveDate", DateUtil.parseYmd("2013-12-01"));
        parameterValues.put("maxAge", 35);

        DefinitionLibraryCohortDefinition cd = new DefinitionLibraryCohortDefinition();
        cd.setDefinitionKey(BuiltInCohortDefinitionLibrary.PREFIX + "upToAgeOnDate");
        cd.setParameterValues(parameterValues);

        EvaluatedCohort result = service.evaluate(cd, new EvaluationContext());
        assertThat(result, ReportingMatchers.isCohortWithExactlyIds(6, 22, 23, 24));
    }

}
