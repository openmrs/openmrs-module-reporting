/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.PersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * Test the PersonDataServiceImpl
 */
public class PersonDataServiceImplTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PersonDataServiceImpl#evaluate(PersonData,EvaluationContext)
	 * @verifies evaluate a person query
	 */
	@Test
	public void evaluate_shouldEvaluateAnPersonData() throws Exception {
		PersonDataDefinition definition = new PersonIdDataDefinition();
		PersonData data = Context.getService(PersonDataService.class).evaluate(definition, new EvaluationContext());
		Assertions.assertNotNull(data);
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
		Assertions.assertNotNull(definition.getId());
		Assertions.assertNotNull(definition.getUuid());
		PersonDataDefinition loadedDefinition = Context.getService(PersonDataService.class).getDefinitionByUuid(definition.getUuid());
		Assertions.assertEquals(definition, loadedDefinition);
	}
	
}