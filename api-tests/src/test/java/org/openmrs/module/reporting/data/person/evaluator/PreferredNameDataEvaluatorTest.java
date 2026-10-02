/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class PreferredNameDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PreferredNameDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the most preferred name for each person in the passed context
	 */
	@Test
	public void evaluate_shouldReturnAllNamesForAllPersons() throws Exception {
		PreferredNameDataDefinition d = new PreferredNameDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assertions.assertEquals("Hornblower", ((PersonName)pd.getData().get(2)).getFamilyName());
		Assertions.assertEquals("Johnny", ((PersonName)pd.getData().get(6)).getGivenName());
		Assertions.assertEquals("Collet", ((PersonName)pd.getData().get(7)).getGivenName());
		Assertions.assertEquals("Oloo", ((PersonName)pd.getData().get(8)).getFamilyName());
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
		Assertions.assertEquals(0, pd.getData().size());
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
		Assertions.assertEquals(1, pd.getData().size());
		PersonName pn = (PersonName) pd.getData().get(6);
		Assertions.assertEquals(Boolean.TRUE, pn.getPreferred());
	}
}