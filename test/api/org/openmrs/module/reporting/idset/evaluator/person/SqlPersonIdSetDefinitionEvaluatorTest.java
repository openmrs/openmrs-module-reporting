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
package org.openmrs.module.reporting.idset.evaluator.person;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.idset.IdSet;
import org.openmrs.module.reporting.idset.PersonIdSet;
import org.openmrs.module.reporting.idset.definition.person.SqlPersonIdSetDefinition;
import org.openmrs.module.reporting.idset.service.IdSetDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the SqlPersonIdSetDefinition
 */
public class SqlPersonIdSetDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(SqlPersonIdSetDefinitionEvaluatorTest.class);
	
	@Test
	public void evaluate_shouldEvaluateASQLQueryIntoPersonIdSet() throws Exception {
		SqlPersonIdSetDefinition d = new SqlPersonIdSetDefinition();
		d.setQuery("select person_id from person where gender = 'F'");
		IdSet s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(6, s.size());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseFilterInAnEvaluationContext() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		PersonIdSet basePersonIds = new PersonIdSet();
		basePersonIds.add(2, 9, 20, 23);
		context.addIdSet(Person.class, basePersonIds);
		
		SqlPersonIdSetDefinition d = new SqlPersonIdSetDefinition();
		d.setQuery("select person_id from person where gender = 'F'");
		Assert.assertEquals(1, evaluate(d, context).size());
		
		d.setQuery("select person_id from person where gender in ('M','F')");
		Assert.assertEquals(3, evaluate(d, context).size());
		
		d.setQuery("select person_id from person where gender not in ('M', 'F')");
		Assert.assertEquals(1, evaluate(d, context).size());
	}
	
	public IdSet evaluate(SqlPersonIdSetDefinition definition, EvaluationContext context) throws Exception {
		return Context.getService(IdSetDefinitionService.class).evaluate(definition, context).getIdSet();
	}
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
}