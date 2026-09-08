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