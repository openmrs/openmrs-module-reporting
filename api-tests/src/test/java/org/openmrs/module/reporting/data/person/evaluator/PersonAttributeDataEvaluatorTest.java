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
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonAttributeDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PersonAttributeDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the person attribute of the given type for each person
	 */
	@Test
	public void evaluate_shouldReturnAllBirthDatesForAllPersons() throws Exception {
		PersonAttributeDataDefinition d = new PersonAttributeDataDefinition();
		d.setPersonAttributeType(Context.getPersonService().getPersonAttributeType(2));
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("6,7,8"));
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals("Jamaica", ((PersonAttribute)pd.getData().get(6)).getHydratedObject());
		Assert.assertEquals("Paris, France", ((PersonAttribute)pd.getData().get(7)).getHydratedObject());
		Assert.assertEquals("Boston, MA", ((PersonAttribute)pd.getData().get(8)).getHydratedObject());
	}
}