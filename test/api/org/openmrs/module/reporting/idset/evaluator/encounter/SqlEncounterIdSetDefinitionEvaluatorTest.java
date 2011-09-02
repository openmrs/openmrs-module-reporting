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
package org.openmrs.module.reporting.idset.evaluator.encounter;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.idset.EncounterIdSet;
import org.openmrs.module.reporting.idset.IdSet;
import org.openmrs.module.reporting.idset.definition.encounter.SqlEncounterIdSetDefinition;
import org.openmrs.module.reporting.idset.service.IdSetDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the SqlEncounterIdSetDefinition
 */
public class SqlEncounterIdSetDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(SqlEncounterIdSetDefinitionEvaluatorTest.class);

	@Test
	public void evaluate_shouldEvaluateASQLQueryIntoAnEncounterIdSet() throws Exception {
		SqlEncounterIdSetDefinition d = new SqlEncounterIdSetDefinition();
		d.setQuery("select encounter_id from encounter where location_id = 2");
		IdSet s = evaluate(d, new EvaluationContext());
		Assert.assertEquals(8, s.size());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseEncounterIdSetInAnEvaluationContext() throws Exception {
	
		EvaluationContext context = new EvaluationContext();
		EncounterIdSet baseEncounterIds = new EncounterIdSet();
		baseEncounterIds.add(3, 4, 5, 6, 7, 8);
		context.addIdSet(Encounter.class, baseEncounterIds);
		
		SqlEncounterIdSetDefinition d = new SqlEncounterIdSetDefinition();
		d.setQuery("select encounter_id from encounter where location_id = 2");
		Assert.assertEquals(4, evaluate(d, context).size());
	}
	
	@Test
	public void evaluate_shouldFilterResultsGivenABaseCohortInAnEvaluationContext() throws Exception {
	
		EvaluationContext context = new EvaluationContext();
		EncounterIdSet baseEncounterIds = new EncounterIdSet();
		baseEncounterIds.add(3, 4, 5, 6, 7, 8);
		context.addIdSet(Encounter.class, baseEncounterIds);
		
		Cohort baseCohort = new Cohort("20,21");
		context.setBaseCohort(baseCohort);
		
		SqlEncounterIdSetDefinition d = new SqlEncounterIdSetDefinition();
		d.setQuery("select encounter_id from encounter where location_id = 2");
		Assert.assertEquals(3, evaluate(d, context).size());

	}
	
	public IdSet evaluate(SqlEncounterIdSetDefinition definition, EvaluationContext context) throws Exception {
		return Context.getService(IdSetDefinitionService.class).evaluate(definition, context).getIdSet();
	}
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
}