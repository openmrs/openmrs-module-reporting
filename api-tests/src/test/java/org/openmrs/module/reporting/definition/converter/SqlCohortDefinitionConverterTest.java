/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.converter;

import java.util.List;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the SqlCohortDefinitionConverter
 */
public class SqlCohortDefinitionConverterTest extends BaseModuleContextSensitiveTest {
	
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
	
	@Test
	@Verifies(value = "convert legacy definitions to latest format", method = "convert")
	public void convert_shouldConvertLegacyDefinitionsToLatestFormat() throws Exception {

		SqlCohortDefinitionConverter converter = new SqlCohortDefinitionConverter();
		
		List<SerializedObject> before = converter.getInvalidDefinitions();
		Assert.assertEquals(1, before.size());
		
		for (SerializedObject so : before) {
			Assert.assertTrue(converter.convertDefinition(so));
		}
		
		Assert.assertEquals(0, converter.getInvalidDefinitions().size());
	}
}
