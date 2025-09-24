/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.obs.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.definition.SqlObsQuery;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the ObsQueryServiceImpl
 */
public class ObsQueryServiceImplTest extends BaseModuleContextSensitiveTest {
	
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
	
	/**
	 * @see ObsQueryServiceImpl#evaluate(ObsQuery,EvaluationContext)
	 * @verifies evaluate an obs query
	 */
	@Test
	@Ignore //TODO:  Un-ignore when we actually implement this
	public void evaluate_shouldEvaluateAnObsQuery() throws Exception {
		ObsQuery q = new SqlObsQuery("select obs_id from obs where voided = 0");
		ObsQueryResult r = Context.getService(ObsQueryService.class).evaluate(q, new EvaluationContext());
		Assert.assertNotNull(r);
	}
	
	/**
	 * @see ObsQueryServiceImpl#saveDefinition(ObsQuery)
	 * @verifies save an obs query
	 */
	@Test
	public void saveDefinition_shouldSaveAnObsQuery() throws Exception {
		ObsQuery q = new SqlObsQuery("select obs_id from obs where voided = 0");
		q.setName("Non voided obs");
		q = Context.getService(ObsQueryService.class).saveDefinition(q);
		Assert.assertNotNull(q.getId());
		Assert.assertNotNull(q.getUuid());
		ObsQuery loadedQuery = Context.getService(ObsQueryService.class).getDefinitionByUuid(q.getUuid());
		Assert.assertEquals(q, loadedQuery);
	}
	
}