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
import org.openmrs.module.reporting.cohort.definition.DefinitionLibraryCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ReportingMatchers;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class DefinitionLibraryCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private CohortDefinitionService service;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private AllDefinitionLibraries libraries;

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

    @Test
    public void testCachingDoesNotHappenIncorrectly() throws Exception {
        DefinitionLibraryCohortDefinition cd = libraries.cohortDefinition(BuiltInCohortDefinitionLibrary.PREFIX + "upToAgeOnDate", "maxAge", 35);

        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("effectiveDate", DateUtil.parseYmd("2013-12-01"));
        CohortIndicator ind1 = new CohortIndicator("one");
        ind1.setCohortDefinition(cd, params1);

        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("effectiveDate", DateUtil.parseYmd("1960-01-01"));
        CohortIndicator ind2 = new CohortIndicator("two");
        ind2.setCohortDefinition(cd, params2);

        EvaluationContext context = new EvaluationContext();
        IndicatorResult result1 = indicatorService.evaluate(ind1, context);
        IndicatorResult result2 = indicatorService.evaluate(ind2, context);

        assertThat(result1.getValue(), not(result2.getValue()));
    }

}
