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
package org.openmrs.module.reporting.dataset.definition;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.column.LogicDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class PatientDataSetDefinitionTest extends BaseModuleContextSensitiveTest {
	
	/** Logger */
	private static Log log = LogFactory.getLog(PatientDataSetDefinitionTest.class);
	
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
		authenticate();
	}


	@Test
	public void shouldAddLogicColumn() throws Exception { 
		PatientDataSetDefinition instance = new PatientDataSetDefinition();
		instance.setName("Test Patient Data Set");
		instance.addLogicColumn(new LogicDataSetColumn("Test Column Name", String.class, "Test Logic Query"));
		DataSetDefinition dataSetDefinition = 
			Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(instance);		
		Assert.assertEquals("should return 6 columns", 6, dataSetDefinition.getColumns().size());
	}

	
	@Test
	public void shouldRemoveLogicColumn() throws Exception { 
		PatientDataSetDefinition instance = new PatientDataSetDefinition();
		instance.setName("Test Patient Data Set");
		instance.addLogicColumn(new LogicDataSetColumn("Test Column Name", String.class, "Test Logic Query"));		
		Assert.assertEquals("should return 6 columns", 6, instance.getColumns().size());	
		
		instance.removeLogicColumn("Test Column Name");
		instance =  (PatientDataSetDefinition) 
			Context.getService(DataSetDefinitionService.class).saveDataSetDefinition(instance);		
		Assert.assertEquals("should return 5 columns", 5, instance.getColumns().size());
	}
	
	
}
