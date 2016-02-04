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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PreferredNameDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PreferredNameDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the most preferred name for each person in the passed context
	 */
	@Test
	public void evaluate_shouldReturnAllNamesForAllPersons() throws Exception {
		PreferredNameDataDefinition d = new PreferredNameDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals("Hornblower", ((PersonName)pd.getData().get(2)).getFamilyName());
		Assert.assertEquals("Johnny", ((PersonName)pd.getData().get(6)).getGivenName());
		Assert.assertEquals("Collet", ((PersonName)pd.getData().get(7)).getGivenName());
		Assert.assertEquals("Oloo", ((PersonName)pd.getData().get(8)).getFamilyName());
	}
	
	/**
	 * @see PreferredNameEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return empty result set for an empty base cohort
	 */
	@Test
	public void evaluate_shouldReturnEmptyResultSetForEmptyBaseCohort() throws Exception {
		PreferredNameDataDefinition d = new PreferredNameDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort());
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(0, pd.getData().size());
	}

	/**
	 * @verifies return the preferred name for all persons
	 * @see PreferredNameDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnThePreferredNameForAllPersons() throws Exception {
		PreferredNameDataDefinition d = new PreferredNameDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("6"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(1, pd.getData().size());
		PersonName pn = (PersonName) pd.getData().get(6);
		Assert.assertEquals(Boolean.TRUE, pn.getPreferred());
	}
}