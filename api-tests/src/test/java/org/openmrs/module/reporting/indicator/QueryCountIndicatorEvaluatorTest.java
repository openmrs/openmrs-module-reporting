/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.query.person.definition.SqlPersonQuery;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashMap;

/**
 * test class for testing evaluation of QueryCountIndicators
 */
public class QueryCountIndicatorEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void evaluate_shouldSupportPersonQueries() throws Exception {
		SqlPersonQuery q = new SqlPersonQuery();
		q.setQuery("select person_id from person where gender = 'M' and person_id < 10");
		testResult(q, 3);
	}

	@Test
	public void evaluate_shouldSupportCohortQueries() throws Exception {
		GenderCohortDefinition q = new GenderCohortDefinition();
		q.setFemaleIncluded(true);
		testResult(q, 5);
	}

	@Test
	public void evaluate_shouldSupportEncounterQueries() throws Exception {
		SqlEncounterQuery q = new SqlEncounterQuery();
		q.setQuery("select encounter_id from encounter where encounter_id < 10");
		testResult(q, 7);
	}

	private void testResult(Query q, int expectedResult) throws Exception {
		QueryCountIndicator ci = new QueryCountIndicator();
		ci.setQuery(new Mapped<Query>(q, new HashMap<String, Object>()));
		IndicatorService is = Context.getService(IndicatorService.class);
		IndicatorResult result = is.evaluate(ci, new EvaluationContext());
		Assert.assertEquals(expectedResult, result.getValue().intValue());
	}
}
