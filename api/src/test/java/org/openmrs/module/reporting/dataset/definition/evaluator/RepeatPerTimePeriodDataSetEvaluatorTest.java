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

package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openmrs.Location;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TimePeriod;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RepeatPerTimePeriodDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.test.AuthenticatedUserTestHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RepeatPerTimePeriodDataSetEvaluatorTest extends AuthenticatedUserTestHelper {

    private DataSetDefinitionService service;
    private RepeatPerTimePeriodDataSetEvaluator evaluator;

    @Before
    public void setUp() throws Exception {
        service = mock(DataSetDefinitionService.class);

        evaluator = new RepeatPerTimePeriodDataSetEvaluator();
        evaluator.setDataSetDefinitionService(service);
    }

    @Test
    public void testEvaluate() throws Exception {
        Location aLocation = new Location();

        SqlDataSetDefinition baseDsd = new SqlDataSetDefinition();
        baseDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        baseDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        baseDsd.addParameter(new Parameter("location", "Location", Location.class));

        RepeatPerTimePeriodDataSetDefinition dsd = new RepeatPerTimePeriodDataSetDefinition();
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("location", "Location", Location.class));
        dsd.setBaseDefinition(baseDsd);
        dsd.setRepeatPerTimePeriod(TimePeriod.WEEKLY);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.parseYmd("2013-12-01"));
        context.addParameterValue("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2014-01-01"), -1));
        context.addParameterValue("location", aLocation);

        evaluator.evaluate(dsd, context);

        // set up the delegate DSD we expect to be evaluated

        final MultiParameterDataSetDefinition expectedDelegate = new MultiParameterDataSetDefinition();
        expectedDelegate.setBaseDefinition(baseDsd);
        expectedDelegate.addParameter(new Parameter("startDate", "Start Date", Date.class));
        expectedDelegate.addParameter(new Parameter("endDate", "End Date", Date.class));
        expectedDelegate.addParameter(new Parameter("location", "Location", Location.class));

        Map<String, Object> iteration = new HashMap<String, Object>();
        iteration.put("startDate", DateUtil.parseYmd("2013-12-01"));
        iteration.put("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-08"), -1));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("startDate", DateUtil.parseYmd("2013-12-08"));
        iteration.put("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-15"), -1));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("startDate", DateUtil.parseYmd("2013-12-15"));
        iteration.put("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-22"), -1));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("startDate", DateUtil.parseYmd("2013-12-22"));
        iteration.put("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-29"), -1));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("startDate", DateUtil.parseYmd("2013-12-29"));
        iteration.put("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2014-01-01"), -1));
        expectedDelegate.addIteration(iteration);

        // verify we delegated as expected

        verify(service).evaluate(argThat(new ArgumentMatcher<DataSetDefinition>() {
            @Override
            public boolean matches(Object argument) {
                MultiParameterDataSetDefinition actualDelegate = (MultiParameterDataSetDefinition) argument;
                return actualDelegate.getParameters().equals(expectedDelegate.getParameters())
                    && actualDelegate.getIterations().equals(expectedDelegate.getIterations());
            }
        }), eq(context));
    }

}
