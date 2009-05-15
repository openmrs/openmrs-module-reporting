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
package org.openmrs.module.dataset;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class DataSetDefinitionServiceTest extends BaseContextSensitiveTest {
	
	private static Log log = LogFactory.getLog(DataSetDefinitionServiceTest.class);
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	@Override
    public Boolean useInMemoryDatabase() { return false; }	
   	 */
	
	/**
	 * Runs the basic stuff since we have SkipBaseSetup on the whole class
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		//executeDataSet("org/openmrs/include/standardTestDatabase.xml");
		executeDataSet("org/openmrs/report/include/ReportTests-reportObjects.xml");
		//executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");		
		authenticate();
	}

	

	
	@Test
	public void shouldGetAllDataSetDefinitions() throws Exception { 
		List<DataSetDefinition> definitions = 
			Context.getService(DataSetDefinitionService.class).getAllDataSetDefinitions();
		
		for (DataSetDefinition definition : definitions) { 
			log.info("Data set definition: " + definition.getName());
	}
	
		
	}
	
	@Test
	public void shouldEvaluateDataExportDataSet() throws Exception { 
		Assert.fail("Test needs to be implemented");		
	}
	
	
	@Test
	public void shouldSaveDataExportDataSet() throws Exception { 
		Assert.fail("Test needs to be implemented");
		}
	
	@Test
	public void shouldPurgeDataExportDataSet() throws Exception { 
		Assert.fail("Test needs to be implemented");	
	}
	
	@Test
	public void shouldAddDataExportDataSetColumn() throws Exception { 
		Assert.fail("Test needs to be implemented");
		}
		
	
}
