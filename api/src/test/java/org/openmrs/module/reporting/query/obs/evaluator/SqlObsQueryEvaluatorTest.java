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
package org.openmrs.module.reporting.query.obs.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.SqlObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the SqlObsQuery
 */
public class SqlObsQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(SqlObsQueryEvaluatorTest.class);

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/" + new TestUtil().getTestDatasetFilename("ReportTestDataset"));
	}

	@Test
	public void evaluate_shouldEvaluateASQLQueryIntoAnObsQuery() throws Exception {
		SqlObsQuery d = new SqlObsQuery();
		d.setQuery("select obs_id from obs where concept_id = 5089");
		ObsQueryResult s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(8, s.getSize());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseObsQueryInAnEvaluationContext() throws Exception {
		ObsEvaluationContext context = new ObsEvaluationContext();
		context.setBaseObs(new ObsIdSet(7, 9, 10, 11, 12));

		SqlObsQuery d = new SqlObsQuery();
		d.setQuery("select obs_id from obs where concept_id = 5089");
		ObsQueryResult s = evaluate(d, context);
		Assert.assertEquals(2, s.getSize());
	}

	@Test
	public void evaluate_shouldFilterResultsGivenABaseCohortInAnEvaluationContext() throws Exception {

		SqlObsQuery d = new SqlObsQuery();
		d.setQuery("select obs_id from obs where concept_id = 5089");

		ObsEvaluationContext context = new ObsEvaluationContext();
		context.setBaseObs(new ObsIdSet(7, 16, 18, 21, 24));
		Assert.assertEquals(5, evaluate(d, context).getSize());

		context.setBaseCohort(new Cohort("7,21"));
		Assert.assertEquals(4, evaluate(d, context).getSize());
	}


	public ObsQueryResult evaluate(SqlObsQuery definition, EvaluationContext context) throws Exception {
		return Context.getService(ObsQueryService.class).evaluate(definition, context);
	}

}