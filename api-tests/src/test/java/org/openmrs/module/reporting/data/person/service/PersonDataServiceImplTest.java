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
package org.openmrs.module.reporting.data.person.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.PersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the PersonDataServiceImpl
 */
public class PersonDataServiceImplTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PersonDataServiceImpl#evaluate(PersonData,EvaluationContext)
	 * @verifies evaluate a person query
	 */
	@Test
	public void evaluate_shouldEvaluateAnPersonData() throws Exception {
		PersonDataDefinition definition = new PersonIdDataDefinition();
		PersonData data = Context.getService(PersonDataService.class).evaluate(definition, new EvaluationContext());
		Assert.assertNotNull(data);
	}
	
	/**
	 * @see PersonDataServiceImpl#saveDefinition(PersonData)
	 * @verifies save a person query
	 */
	@Test
	public void saveDefinition_shouldSaveAnPersonData() throws Exception {
		PersonDataDefinition definition = new PersonIdDataDefinition();
		definition.setName("All Person Ids");
		definition = Context.getService(PersonDataService.class).saveDefinition(definition);
		Assert.assertNotNull(definition.getId());
		Assert.assertNotNull(definition.getUuid());
		PersonDataDefinition loadedDefinition = Context.getService(PersonDataService.class).getDefinitionByUuid(definition.getUuid());
		Assert.assertEquals(definition, loadedDefinition);
	}
	
}