/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the EncounterDataServiceImpl
 */
public class EncounterDataServiceImplTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see EncounterDataServiceImpl#evaluate(EncounterData,EvaluationContext)
	 * @verifies evaluate an encounter query
	 */
	@Test
	public void evaluate_shouldEvaluateAnEncounterData() throws Exception {
		EncounterDataDefinition definition = new EncounterIdDataDefinition();
		EncounterData data = Context.getService(EncounterDataService.class).evaluate(definition, new EvaluationContext());
		Assert.assertNotNull(data);
	}
	
	/**
	 * @see EncounterDataServiceImpl#saveDefinition(EncounterData)
	 * @verifies save an encounter query
	 */
	@Test
	public void saveDefinition_shouldSaveAnEncounterData() throws Exception {
		EncounterDataDefinition definition = new EncounterIdDataDefinition();
		definition.setName("All Encounter Ids");
		definition = Context.getService(EncounterDataService.class).saveDefinition(definition);
		Assert.assertNotNull(definition.getId());
		Assert.assertNotNull(definition.getUuid());
		EncounterDataDefinition loadedDefinition = Context.getService(EncounterDataService.class).getDefinitionByUuid(definition.getUuid());
		Assert.assertEquals(definition, loadedDefinition);
	}
	
}