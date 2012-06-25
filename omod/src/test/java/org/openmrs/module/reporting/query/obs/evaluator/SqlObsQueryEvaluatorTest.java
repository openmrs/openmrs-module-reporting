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

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.SqlObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the SqlObsQuery
 */
public class SqlObsQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(SqlObsQueryEvaluatorTest.class);
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	@Ignore
	public void evaluate_shouldEvaluateASQLQueryIntoAnObsQuery() throws Exception {
		SqlObsQuery d = new SqlObsQuery();
		d.setQuery("select obs_id from obs where concept_id = 5089");
		ObsQueryResult s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(8, s.getSize());
	}
	
	@Test
	@Ignore
	public void evaluate_shouldFilterResultsGivenABaseObsQueryInAnEvaluationContext() throws Exception {
	
		EvaluationContext context = new EvaluationContext();
		ObsQueryResult baseObsIds = new ObsQueryResult();
		baseObsIds.add(7, 9, 10, 11, 12);
		//context.addQueryResult(Obs.class, baseObsIds);
		
		SqlObsQuery d = new SqlObsQuery();
		d.setQuery("select obs_id from obs where concept_id = 5089");
		ObsQueryResult s = evaluate(d, context);
		Assert.assertEquals(2, s.getSize());
	}
	
	@Test
	@Ignore
	public void evaluate_shouldFilterResultsGivenABaseEncounterQueryInAnEvaluationContext() throws Exception {
		
		SqlObsQuery d = new SqlObsQuery();
		d.setQuery("select obs_id from obs where concept_id = 5089");
	
		EvaluationContext context = new EvaluationContext();
		
		ObsQueryResult baseObsIds = new ObsQueryResult();
		baseObsIds.add(7, 16, 18, 21, 24);
		//context.addQueryResult(Obs.class, baseObsIds);
		Assert.assertEquals(5, evaluate(d, context).getSize());
		
		EncounterQueryResult baseEncounterIds = new EncounterQueryResult();
		baseEncounterIds.add(5, 6, 7, 8);
		//context.addQueryResult(Encounter.class, baseEncounterIds);
		Assert.assertEquals(4, evaluate(d, context).getSize());
		
		Cohort baseCohort  = new Cohort("7, 21");
		context.setBaseCohort(baseCohort);
		Assert.assertEquals(3, evaluate(d, context).getSize());
	}

	
	public ObsQueryResult evaluate(SqlObsQuery definition, EvaluationContext context) throws Exception {
		return Context.getService(ObsQueryService.class).evaluate(definition, context);
	}
	
}