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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the SqlEncounterQuery
 */
public class SqlEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(SqlEncounterQueryEvaluatorTest.class);

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/" + new TestUtil().getTestDatasetFilename("ReportTestDataset"));
	}

	@Test
	public void evaluate_shouldEvaluateASQLQueryIntoAnEncounterQuery() throws Exception {
		SqlEncounterQuery d = new SqlEncounterQuery();
		d.setQuery("select encounter_id from encounter where location_id = 2");
		EncounterQueryResult s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(8, s.getSize());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseEncounterQueryInAnEvaluationContext() throws Exception {
	
		EncounterEvaluationContext context = new EncounterEvaluationContext();
		EncounterQueryResult baseEncounterIds = new EncounterQueryResult();
		baseEncounterIds.add(3, 4, 5, 6, 7, 8);
		context.setBaseEncounters(baseEncounterIds);
		
		SqlEncounterQuery d = new SqlEncounterQuery();
		d.setQuery("select encounter_id from encounter where location_id = 2");
		Assert.assertEquals(4, evaluate(d, context).getSize());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseCohortInAnEvaluationContext() throws Exception {
	
		EncounterEvaluationContext context = new EncounterEvaluationContext();
		EncounterQueryResult baseEncounterIds = new EncounterQueryResult();
		baseEncounterIds.add(3, 4, 5, 6, 7, 8);
		context.setBaseEncounters(baseEncounterIds);
		
		Cohort baseCohort = new Cohort("20,21");
		context.setBaseCohort(baseCohort);
		
		SqlEncounterQuery d = new SqlEncounterQuery();
		d.setQuery("select encounter_id from encounter where location_id = 2");
		Assert.assertEquals(3, evaluate(d, context).getSize());

	}
	
	public EncounterQueryResult evaluate(SqlEncounterQuery definition, EvaluationContext context) throws Exception {
		return Context.getService(EncounterQueryService.class).evaluate(definition, context);
	}
	
}