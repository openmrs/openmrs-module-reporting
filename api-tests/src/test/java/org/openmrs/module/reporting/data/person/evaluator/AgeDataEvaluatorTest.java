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
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class AgeDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see AgeDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the age for all persons
	 */
	@Test
	public void evaluate_shouldReturnAllAgesForAllPersons() throws Exception {
		AgeDataDefinition d = new AgeDataDefinition();
		d.setEffectiveDate(DateUtil.getDateTime(2011, 10, 7));
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(36, ((Age)pd.getData().get(2)).getFullYears().intValue());
		Assert.assertEquals(4, ((Age)pd.getData().get(6)).getFullYears().intValue());
		Assert.assertEquals(35, ((Age)pd.getData().get(7)).getFullYears().intValue());
	}

	@Test
	public void evaluate_shouldOnlyCalculateAgeUpToDeathDate() throws Exception {
		AgeDataDefinition d = new AgeDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("20"));

		d.setEffectiveDate(DateUtil.getDateTime(2014,3,1));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(80, ((Age) pd.getData().get(20)).getFullYears().intValue());

		d.setEffectiveDate(null);
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(80, ((Age) pd.getData().get(20)).getFullYears().intValue());

		d.setEffectiveDate(DateUtil.getDateTime(2000,3,1));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(75, ((Age) pd.getData().get(20)).getFullYears().intValue());
	}


}