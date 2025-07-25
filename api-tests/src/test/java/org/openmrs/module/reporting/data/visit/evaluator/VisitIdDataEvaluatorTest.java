/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.evaluator;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitIdDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

@SkipBaseSetup
public class VisitIdDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    /**
     * Run this before each unit test in this class. The "@Before" method in
     * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
     *
     * @throws Exception
     */
    @BeforeEach
    public void setup() throws Exception {
        initializeInMemoryDatabase();
        authenticate();
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    /**
     * @see org.openmrs.module.reporting.data.visit.evaluator.VisitIdDataEvaluator#evaluate(org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * @verifies return visitIds for the patients given an EvaluationContext
     */
    @Test
    public void evaluate_shouldReturnVisitIdsForThePatientsGivenAnEvaluationContext() throws Exception {
        VisitIdDataDefinition d = new VisitIdDataDefinition();
        EvaluationContext context = new EvaluationContext();
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);
        Assert.assertEquals(5, ed.getData().size());  // one visit in the sample data has been voided
        for (Integer eId : ed.getData().keySet()) {
            Assert.assertEquals(eId, ed.getData().get(eId));
        }

        // Test for a limited base cohort of patients
        context.setBaseCohort(new Cohort("2"));
        ed = Context.getService(VisitDataService.class).evaluate(d, context);
        Assert.assertEquals(3, ed.getData().size());
        Assert.assertEquals(1, ed.getData().get(1));
        Assert.assertEquals(2, ed.getData().get(2));
        Assert.assertEquals(3, ed.getData().get(3));
    }

    /**
     * @see org.openmrs.module.reporting.data.visit.evaluator.VisitIdDataEvaluator#evaluate(org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition,EvaluationContext)
     * @verifies return visitIds for the visits given an VisitEvaluationContext
     */
    @Test
    public void evaluate_shouldReturnVisitIdsForTheVisitsGivenAnVisitEvaluationContext() throws Exception {
        VisitIdDataDefinition d = new VisitIdDataDefinition();
        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseCohort(new Cohort("2"));
        context.setBaseVisits(new VisitIdSet(2, 3, 4));
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        Assert.assertEquals(2, ed.getData().get(2));
        Assert.assertEquals(3, ed.getData().get(3));
    }
}
