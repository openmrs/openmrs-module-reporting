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
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.persister.DataExportDataSetDefinitionPersister;
import org.openmrs.module.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class DataExportDataSetPersisterTest extends BaseModuleContextSensitiveTest {
	
	/** Logger */
	private static Log log = LogFactory.getLog(DataExportDataSetPersisterTest.class);
	
	/**
	 * Runs the basic stuff since we have SkipBaseSetup on the whole class
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/dataset/include/ReportTests-reportObjects.xml");
		authenticate();
	}
	

	/**
	 * Tests whether a class registered with this persister can be handled.
	 * 
	 * TODO This should be handled by the Handler annotation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldHandleRegisteredClass() throws Exception { 
		//Assert.fail("Not implemented");		
	}
	
	/**
	 * TODO This should be handled by the Handler annotation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldNotHandleUnregisteredClass() throws Exception { 
		//Assert.fail("Not implemented");		
	}
	
	/**
	 * Tests whether the persister returns all dataset definitions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllDataSetDefinitions() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> list = persister.getAllDataSetDefinitions(false);
		Assert.assertEquals("Should return all dataset definitions", 25, list.size()); 		
	}
	
	@Test
	public void shouldReturnAllDatasetDefinitionsIncludingRetired() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> list = persister.getAllDataSetDefinitions(true);
		Assert.assertEquals("Should return all dataset definitions, including retired", 25, list.size()); 		
	}
	
	@Test
	public void shouldReturnAllDatasetDefinitionsExcludingRetired() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> list = persister.getAllDataSetDefinitions(false);		
		Assert.assertEquals("Should return all dataset definitions, excluding retired", 25, list.size()); 		
	}
	
	@Test
	public void shouldReturnNullIfNotExist() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataSetDefinition def = persister.getDataSetDefinition(1);	
		Assert.assertNull("Should return null if not exists", def); 		
	}
	@Test
	public void shouldReturnDataSetDefinitionIfExists() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataSetDefinition def = persister.getDataSetDefinition(45);
		Assert.assertNotNull("Should return dataset definition if exists", def); 		
	}
	@Test
	public void shouldReturnNullIfNameDoesNotMatch() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> def = persister.getDataSetDefinitions("Dataset definition that does not exist", true);
		Assert.assertEquals(0, def.size()); 		
	}
	
	@Test
	public void shouldReturnDataSetDefinitionIfNameMatches() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> def = persister.getDataSetDefinitions("HIV Program Data", true);
		Assert.assertEquals(1, def.size()); 		
	}

	@Test
	public void shouldCreateNewDataSetDefinition() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataExportReportObject dataExport = (DataExportReportObject)
			Context.getService(ReportObjectService.class).getReportObject(45);
		dataExport.setReportObjectId(null);
		dataExport.setName("My Data Set Definition");
		DataSetDefinition datasetDefinition = new DataExportDataSetDefinition(dataExport);		
		datasetDefinition = persister.saveDataSetDefinition(datasetDefinition);
		DataSetDefinition def = 
			persister.getDataSetDefinition(45);
		log.info("newly created dataset definition: " + def.getId());
		Assert.assertNotNull("Should create new dataset definition", def.getId()); 	
	}
	@Test
	public void shouldUpdateExistingDataSetDefinition() throws Exception { 

		DataSetDefinitionPersister persister = 
			new DataExportDataSetDefinitionPersister();
		// Save an existing data set definition
		DataSetDefinition datasetDefinition = persister.getDataSetDefinition(45);
		datasetDefinition.setName("My Data Set Definition");
		datasetDefinition = persister.saveDataSetDefinition(datasetDefinition);
		datasetDefinition = persister.getDataSetDefinition(45);
		Assert.assertEquals("Should have same ID as before save", new Integer(45), datasetDefinition.getId());
		Assert.assertEquals("Should have new name", "My Data Set Definition", datasetDefinition.getName());

	}
	@Test
	public void shouldSetIdentifierAfterSave() throws Exception { 
		DataSetDefinitionPersister persister = 
			new DataExportDataSetDefinitionPersister();
		DataExportReportObject dataExport = (DataExportReportObject)
			Context.getService(ReportObjectService.class).getReportObject(45);
		dataExport.setReportObjectId(null);
		dataExport.setName("My Data Set Definition");
		// Save an existing data set definition
		DataSetDefinition datasetDefinition = 
			new DataExportDataSetDefinition(dataExport);		
		datasetDefinition = persister.saveDataSetDefinition(datasetDefinition);
		
		Assert.assertNotNull("Should set identifier after saving dataset definition", datasetDefinition.getId());

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldRemoveDataSetDefintion() throws Exception { 
		DataSetDefinitionPersister persister = 
			new DataExportDataSetDefinitionPersister();
		DataSetDefinition beforePurge = 
			persister.getDataSetDefinition(new Integer(45));
		persister.purgeDataSetDefinition(beforePurge);
		DataSetDefinition afterPurge = 
			persister.getDataSetDefinition(new Integer(45));
		Assert.assertNull(afterPurge);
	}
	
	
	/**
	 * org.hibernate.NonUniqueObjectException: 
	 * a different object with the same identifier value was already associated with the session: 
	 * [org.openmrs.reporting.ReportObjectWrapper#45]
	 * 
	 * Auto generated method comment
	 *
	 */
	public void shouldNotThrowNonUniqueObjectException() { 
		Assert.fail("Not sure how to implement this test, but purging causes this exception to be thrown");
		
	}
	

}
