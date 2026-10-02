/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.openmrs.module.reporting.query.person.definition.SqlPersonQuery;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * Test the PersonQueryServiceImpl
 */
public class PersonQueryServiceImplTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see PersonQueryServiceImpl#evaluate(PersonQuery,EvaluationContext)
	 * @verifies evaluate a person query
	 */
	@Test
	public void evaluate_shouldEvaluateAnPersonQuery() throws Exception {
		PersonQuery q = new SqlPersonQuery("select person_id from person where voided = 0");
		PersonQueryResult r = Context.getService(PersonQueryService.class).evaluate(q, new EvaluationContext());
		Assertions.assertNotNull(r);
	}
	
	/**
	 * @see PersonQueryServiceImpl#saveDefinition(PersonQuery)
	 * @verifies save a person query
	 */
	@Test
	public void saveDefinition_shouldSaveAnPersonQuery() throws Exception {
		PersonQuery q = new SqlPersonQuery("select person_id from person where voided = 0");
		q.setName("Non voided persons");
		q = Context.getService(PersonQueryService.class).saveDefinition(q);
		Assertions.assertNotNull(q.getId());
		Assertions.assertNotNull(q.getUuid());
		PersonQuery loadedQuery = Context.getService(PersonQueryService.class).getDefinitionByUuid(q.getUuid());
		Assertions.assertEquals(q, loadedQuery);
	}
	
}