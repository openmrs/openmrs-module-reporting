/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.openmrs.module.reporting.query.obs.definition.MappedParametersObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MappedParametersObsQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        Date date = DateUtil.parseDate("2008-08-01", "yyyy-MM-dd");
        BasicObsQuery original = new BasicObsQuery();
        original.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        original.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));

        Map<String, String> renamedParameters = new HashMap<String, String>();
        renamedParameters.put("onOrAfter", "date");
        renamedParameters.put("onOrBefore", "date+1m");
        MappedParametersObsQuery renamed = new MappedParametersObsQuery(original, renamedParameters);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("date", date);
        ObsQueryResult result = Context.getService(ObsQueryService.class).evaluate(renamed, context);

        assertThat(result.getSize(), is(10));
        assertTrue(result.contains(6) && result.contains(7) && result.contains(9) && result.contains(10) && result.contains(11)
                && result.contains(12) && result.contains(13) && result.contains(14) && result.contains(15) && result.contains(16));
    }

}
