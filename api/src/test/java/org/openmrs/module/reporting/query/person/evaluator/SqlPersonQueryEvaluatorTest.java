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
package org.openmrs.module.reporting.query.person.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.SqlPersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

/**
 * Test the evaluation of the SqlPersonQuery
 */
public class SqlPersonQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/" + new TestUtil().getTestDatasetFilename("ReportTestDataset"));
	}
	
	@Test
	public void evaluate_shouldEvaluateASQLQueryIntoPersonQuery() throws Exception {
		SqlPersonQuery d = new SqlPersonQuery();
		d.setQuery("select person_id from person where gender = 'F'");
		PersonQueryResult s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(6, s.getSize());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseFilterInAnEvaluationContext() throws Exception {
		
		PersonEvaluationContext context = new PersonEvaluationContext();
		PersonQueryResult basePersonIds = new PersonQueryResult();
		basePersonIds.add(2, 9, 20, 23);
		context.setBasePersons(basePersonIds);
		
		SqlPersonQuery d = new SqlPersonQuery();
		d.setQuery("select person_id from person where gender = 'F'");
		Assert.assertEquals(1, evaluate(d, context).getSize());
		
		d.setQuery("select person_id from person where gender in ('M','F')");
		Assert.assertEquals(3, evaluate(d, context).getSize());
		
		d.setQuery("select person_id from person where gender not in ('M', 'F')");
		Assert.assertEquals(1, evaluate(d, context).getSize());
	}
	
	public PersonQueryResult evaluate(SqlPersonQuery definition, EvaluationContext context) throws Exception {
		return Context.getService(PersonQueryService.class).evaluate(definition, context);
	}
}