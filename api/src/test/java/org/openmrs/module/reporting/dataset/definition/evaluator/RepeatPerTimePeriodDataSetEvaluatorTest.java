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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
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
        SqlDataSetDefinition baseDsd = new SqlDataSetDefinition();
        baseDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        baseDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        RepeatPerTimePeriodDataSetDefinition dsd = new RepeatPerTimePeriodDataSetDefinition();
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.setBaseDefinition(Mapped.mapStraightThrough(baseDsd));
        dsd.setRepeatPerTimePeriod(TimePeriod.WEEKLY);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.parseYmd("2013-12-01"));
        context.addParameterValue("endDate", DateUtils.addMilliseconds(DateUtil.parseYmd("2014-01-01"), -1));

        evaluator.evaluate(dsd, context);

        // set up the delegate DSD we expect to be evaluated

        final MultiParameterDataSetDefinition expectedDelegate = new MultiParameterDataSetDefinition();
        expectedDelegate.setBaseDefinition(baseDsd);

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

    @Test
    public void testEvaluateCoversWholeEndDay() throws Exception {
        SqlDataSetDefinition baseDsd = new SqlDataSetDefinition();
        baseDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        baseDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        RepeatPerTimePeriodDataSetDefinition dsd = new RepeatPerTimePeriodDataSetDefinition();
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.setBaseDefinition(Mapped.mapStraightThrough(baseDsd));
        dsd.setRepeatPerTimePeriod(TimePeriod.DAILY);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.parseYmd("2013-12-01"));
        context.addParameterValue("endDate", DateUtil.parseYmd("2013-12-01"));

        evaluator.evaluate(dsd, context);

        // set up the delegate DSD we expect to be evaluated

        final MultiParameterDataSetDefinition expectedDelegate = new MultiParameterDataSetDefinition();
        expectedDelegate.setBaseDefinition(baseDsd);

        Map<String, Object> iteration = new HashMap<String, Object>();
        iteration.put("startDate", DateUtil.parseYmd("2013-12-01"));
        iteration.put("endDate", DateUtil.getEndOfDay(DateUtil.parseYmd("2013-12-01")));
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

    @Test
    public void testEvaluateAddingTime() throws Exception {
        SqlDataSetDefinition baseDsd = new SqlDataSetDefinition();
        baseDsd.addParameter(new Parameter("start", "Start Date", Date.class));
        baseDsd.addParameter(new Parameter("end", "End Date", Date.class));

        RepeatPerTimePeriodDataSetDefinition dsd = new RepeatPerTimePeriodDataSetDefinition();
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.setBaseDefinition(Mapped.map(baseDsd, "start=${startDate+9h},end=${startDate+17h}"));
        dsd.setRepeatPerTimePeriod(TimePeriod.DAILY);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.parseYmd("2013-12-01"));
        context.addParameterValue("endDate", DateUtil.parseYmd("2013-12-02"));

        evaluator.evaluate(dsd, context);

        // set up the delegate DSD we expect to be evaluated

        final MultiParameterDataSetDefinition expectedDelegate = new MultiParameterDataSetDefinition();
        expectedDelegate.setBaseDefinition(baseDsd);

        Map<String, Object> iteration = new HashMap<String, Object>();
        iteration.put("start", DateUtil.parseYmdhms("2013-12-01 09:00:00"));
        iteration.put("end", DateUtil.parseYmdhms("2013-12-01 17:00:00"));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("start", DateUtil.parseYmdhms("2013-12-02 09:00:00"));
        iteration.put("end", DateUtil.parseYmdhms("2013-12-02 17:00:00"));
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

    @Test
    public void testEvaluateWithMoreParameters() throws Exception {
        Location rwinkwavu = new Location();

        SqlDataSetDefinition baseDsd = new SqlDataSetDefinition();
        baseDsd.addParameter(new Parameter("startOfPeriod", "Start Date", Date.class));
        baseDsd.addParameter(new Parameter("endOfPeriod", "End Date", Date.class));
        baseDsd.addParameter(new Parameter("hospital", "Hospital", Location.class));

        RepeatPerTimePeriodDataSetDefinition dsd = new RepeatPerTimePeriodDataSetDefinition();
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("location", "Location", Location.class));
        dsd.setBaseDefinition(Mapped.map(baseDsd, "startOfPeriod=${startDate},endOfPeriod=${endDate},hospital=${location}"));
        dsd.setRepeatPerTimePeriod(TimePeriod.DAILY);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.parseYmd("2013-12-01"));
        context.addParameterValue("endDate", DateUtil.parseYmd("2013-12-03"));
        context.addParameterValue("location", rwinkwavu);

        evaluator.evaluate(dsd, context);

        // set up the delegate DSD we expect to be evaluated

        final MultiParameterDataSetDefinition expectedDelegate = new MultiParameterDataSetDefinition();
        expectedDelegate.setBaseDefinition(baseDsd);

        Map<String, Object> iteration = new HashMap<String, Object>();
        iteration.put("startOfPeriod", DateUtil.parseYmd("2013-12-01"));
        iteration.put("endOfPeriod", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-02"), -1));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("startOfPeriod", DateUtil.parseYmd("2013-12-02"));
        iteration.put("endOfPeriod", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-03"), -1));
        expectedDelegate.addIteration(iteration);

        iteration = new HashMap<String, Object>();
        iteration.put("startOfPeriod", DateUtil.parseYmd("2013-12-03"));
        iteration.put("endOfPeriod", DateUtils.addMilliseconds(DateUtil.parseYmd("2013-12-04"), -1));
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
