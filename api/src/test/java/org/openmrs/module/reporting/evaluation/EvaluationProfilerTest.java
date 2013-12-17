/**
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

package org.openmrs.module.reporting.evaluation;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.StringWriter;

/**
 * Tests for {@link EvaluationProfiler}
 */
public class EvaluationProfilerTest extends BaseModuleContextSensitiveTest {

	protected EvaluationProfiler profiler1, profiler2;

	protected StringWriter logOutput;

	/**
	 * Setup each test by configuring AOP on the relevant services and logging for the profiler class
	 */
	@Before
	public void setup() {
		profiler1 = new EvaluationProfiler();
		profiler2 = new EvaluationProfiler();

		logOutput = new StringWriter();

		Context.addAdvice(CohortDefinitionService.class, profiler2);
		Context.addAdvice(IndicatorService.class, profiler1);

		LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);
		LogManager.getLogger(EvaluationProfiler.class).addAppender(new WriterAppender(new PatternLayout("%p %m"), logOutput));
	}

	/**
	 * Cleanup after tests by removing AOP and resetting logging
	 */
	@After
	public void cleanup() {
		Context.removeAdvice(CohortDefinitionService.class, profiler1);
		Context.removeAdvice(IndicatorService.class, profiler2);

		LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.INFO);
		LogManager.getLogger(EvaluationProfiler.class).removeAllAppenders();
	}

	@Test
	public void integration() throws EvaluationException {
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("males");
		males.setMaleIncluded(true);

		CohortIndicator count = new CohortIndicator(); // No name, log message should use "?"
		count.setCohortDefinition(males, "");

		Context.getService(IndicatorService.class).evaluate(count, null);

		Assert.assertTrue(logOutput.toString().matches(
				"TRACE >> \\d+ ms to evaluate GenderCohortDefinition \\[males\\]" +
				"TRACE > \\d+ ms to evaluate CohortIndicator \\[\\?\\]")
		);
	}
}