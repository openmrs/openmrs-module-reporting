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
package org.openmrs.module.reporting.definition.converter;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the SqlCohortDefinitionConverter
 */
public class SqlCohortDefinitionConverterTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
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
