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
