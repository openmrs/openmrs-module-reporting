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
package org.openmrs.module.reporting.data.person.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ProblemListPersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests ProblemListPersonDataEvaluator
 */
public class ProblemListPersonDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see ProblemListPersonDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnAllProblemLists() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,20,21,22"));
		
		ProblemListPersonDataDefinition d = new ProblemListPersonDataDefinition();
		
		List<Concept> problemsAdded = new ArrayList<Concept>();
		problemsAdded.add(Context.getConceptService().getConcept(5089));
		d.setProblemAddedConcepts(problemsAdded);
		
		List<Concept> problemsResolved = new ArrayList<Concept>();
		problemsResolved.add(Context.getConceptService().getConcept(5089));
		d.setProblemRemovedConcepts(problemsResolved);
		
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(3, ((List) pd.getData().get(7)).size());
		Assert.assertEquals(1, ((List) pd.getData().get(20)).size());
	}
}
