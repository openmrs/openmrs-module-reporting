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

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the SqlCohortDefinitionConverter
 */
public class SqlCohortDefinitionConverterTest extends BaseModuleContextSensitiveTest {
	
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
	
	@Test
	@Verifies(value = "convert legacy definitions to latest format", method = "convert")
	public void convert_shouldConvertLegacyDefinitionsToLatestFormat() throws Exception {

		SqlCohortDefinitionConverter converter = new SqlCohortDefinitionConverter();
		
		List<SerializedObject> before = converter.getInvalidDefinitions();
		Assertions.assertEquals(1, before.size());
		
		for (SerializedObject so : before) {
			Assertions.assertTrue(converter.convertDefinition(so));
		}
		
		Assertions.assertEquals(0, converter.getInvalidDefinitions().size());
	}
}
