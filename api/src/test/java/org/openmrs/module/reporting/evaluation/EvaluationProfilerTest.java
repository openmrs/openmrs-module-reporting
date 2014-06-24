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

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Tests for {@link EvaluationProfiler}
 */
public class EvaluationProfilerTest extends BaseModuleContextSensitiveTest {

	protected EvaluationProfiler profiler1, profiler2;

	protected Logger logger;
	protected Level startingLevel;
	protected List<Appender> startingAppenders = new ArrayList<Appender>();
	protected StringWriter logOutput;

	/**
	 * Setup each test by configuring AOP on the relevant services and logging for the profiler class
	 */
	@Before
	public void setup() {
		profiler1 = new EvaluationProfiler(new EvaluationContext());
		profiler2 = new EvaluationProfiler(new EvaluationContext());
		logOutput = new StringWriter();
		logger = LogManager.getLogger(EvaluationProfiler.class);
		startingLevel = logger.getLevel();
		logger.setLevel(Level.TRACE);
		for (Enumeration e = logger.getAllAppenders(); e.hasMoreElements();) {
			startingAppenders.add((Appender)e.nextElement());
		}
		logger.removeAllAppenders();
		logger.addAppender(new WriterAppender(new PatternLayout("%m%n"), logOutput));
	}

	/**
	 * Cleanup after tests by removing AOP and resetting logging
	 */
	@After
	public void cleanup() {
		logger.setLevel(startingLevel);
		for (Appender appender : startingAppenders) {
			logger.addAppender(appender);
		}
	}

	@Test
	public void integration() throws EvaluationException {
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("males");
		males.setMaleIncluded(true);

		CohortIndicator count = new CohortIndicator(); // No name, log message should use "?"
		count.setCohortDefinition(males, "");

		Context.getService(IndicatorService.class).evaluate(count, null);

		String[] split = logOutput.toString().split(System.getProperty("line.separator"));
		Assert.assertEquals(6, split.length);
		Assert.assertTrue(split[0].contains("EVALUATION_STARTED"));
		Assert.assertTrue(split[1].contains(">"));
		Assert.assertTrue(split[1].contains("CohortIndicator"));
		Assert.assertTrue(split[2].contains(">>"));
		Assert.assertTrue(split[2].contains("GenderCohortDefinition[males]"));
		Assert.assertTrue(split[5].contains("EVALUATION_COMPLETED"));
	}
}