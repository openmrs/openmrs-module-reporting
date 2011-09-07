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
package org.openmrs.module.reporting.query.evaluator.person;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.PersonQueryResult;
import org.openmrs.module.reporting.query.QueryResult;
import org.openmrs.module.reporting.query.definition.person.SqlPersonQuery;
import org.openmrs.module.reporting.query.service.QueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the SqlPersonQuery
 */
public class SqlPersonQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(SqlPersonQueryEvaluatorTest.class);
	
	@Test
	public void evaluate_shouldEvaluateASQLQueryIntoPersonQuery() throws Exception {
		SqlPersonQuery d = new SqlPersonQuery();
		d.setQuery("select person_id from person where gender = 'F'");
		QueryResult s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(6, s.size());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseFilterInAnEvaluationContext() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		PersonQueryResult basePersonIds = new PersonQueryResult();
		basePersonIds.add(2, 9, 20, 23);
		context.addQueryResult(Person.class, basePersonIds);
		
		SqlPersonQuery d = new SqlPersonQuery();
		d.setQuery("select person_id from person where gender = 'F'");
		Assert.assertEquals(1, evaluate(d, context).size());
		
		d.setQuery("select person_id from person where gender in ('M','F')");
		Assert.assertEquals(3, evaluate(d, context).size());
		
		d.setQuery("select person_id from person where gender not in ('M', 'F')");
		Assert.assertEquals(1, evaluate(d, context).size());
	}
	
	public QueryResult evaluate(SqlPersonQuery definition, EvaluationContext context) throws Exception {
		return Context.getService(QueryService.class).evaluate(definition, context).getQueryResult();
	}
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
}