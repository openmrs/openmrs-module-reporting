/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset;

import java.util.List;

import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.persister.DataExportDataSetDefinitionPersister;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.StartModule;

/**
 * Test of the Data Export Data Set Persister
 */
@SuppressWarnings("deprecation")
@StartModule( { "org/openmrs/module/reporting/include/reportingcompatibility-1.5.3.omod" })
@Ignore
public class DataExportDataSetPersisterTest extends BaseModuleContextSensitiveTest {
	
	protected static Log log = LogFactory.getLog(DataExportDataSetPersisterTest.class);

	@Before
	public void setupInitialData() throws Exception {
		{
			DataExportReportObject ro = new DataExportReportObject();
			ro.setName("Test Last Weight");
			ro.setDescription("Description 1");
			ro.addSimpleColumn("patientId", "patientId");
			ro.addConceptColumn("weight", "LAST", 1, "5089", null);
			Context.getReportObjectService().saveReportObject(ro);
		}
		{
			DataExportReportObject ro = new DataExportReportObject();
			ro.setName("Test CD4");
			ro.setDescription("Description 2");
			ro.addSimpleColumn("patientId", "patientId");
			ro.addConceptColumn("cd4", "LAST", 1, "5497", null);
			Context.getReportObjectService().saveReportObject(ro);
		}
		{
			DataExportReportObject ro = new DataExportReportObject();
			ro.setName("Test Name");
			ro.setDescription("Description 3");
			ro.addSimpleColumn("patientId", "patientId");
			ro.addSimpleColumn("name", "personName");
			Context.getReportObjectService().saveReportObject(ro);
		}
		{
			DataExportReportObject ro = new DataExportReportObject();
			ro.setName("Test First 3 Weights");
			ro.setDescription("Description 4");
			ro.addSimpleColumn("patientId", "patientId");
			ro.addConceptColumn("weight", "FIRST", 3, "5089", null);
			Context.getReportObjectService().saveReportObject(ro);
		}
		{
			DataExportReportObject ro = new DataExportReportObject();
			ro.setName("Test Gender");
			ro.setDescription("Description 5");
			ro.addSimpleColumn("patientId", "patientId");
			ro.addSimpleColumn("gender", "patient.gender");
			Context.getReportObjectService().saveReportObject(ro);
		}
	}

	@Test
	public void shouldReturnDataSetDefinitionIfExists() throws Exception {
		// big time hack: this test only works if it's first, because I'm creating the Data Exports programmatically, and each run they get new autonumbered ids.
		// if the initial data gets set up via executing an xml dataset instead, then this test can be anywhere in the class
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataSetDefinition def = persister.getDefinition(1);
		Assert.assertNotNull("Should return dataset definition if exists", def); 		
	}
	
	/**
	 * Tests whether the persister returns all dataset definitions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllDataSetDefinitions() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> list = persister.getAllDefinitions(false);
		Assert.assertEquals("Should return all dataset definitions", 5, list.size()); 		
	}
	
	@Test
	public void shouldReturnAllDatasetDefinitionsIncludingRetired() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> list = persister.getAllDefinitions(true);
		Assert.assertEquals("Should return all dataset definitions, including retired", 5, list.size()); 		
	}
	
	@Test
	public void shouldReturnAllDatasetDefinitionsExcludingRetired() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> list = persister.getAllDefinitions(false);		
		Assert.assertEquals("Should return all dataset definitions, excluding retired", 5, list.size()); 		
	}
	
	@Test
	public void shouldReturnNullIfNotExist() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataSetDefinition def = persister.getDefinition(999);	
		Assert.assertNull("Should return null if not exists", def); 		
	}
	@Test
	public void shouldReturnNullIfNameDoesNotMatch() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> def = 
			persister.getDefinitions("Dataset definition that does not exist", true);
		Assert.assertEquals(0, def.size()); 		
	}
	
	@Test
	public void shouldReturnDataSetDefinitionIfNameMatches() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		List<DataSetDefinition> def = 
			persister.getDefinitions("Test First 3 Weights", true);
		Assert.assertEquals(1, def.size()); 		
	}

	@Test(expected=UnsupportedOperationException.class)
	public void shouldNotBeAbleToModifyUnderlyingDataExport() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataSetDefinition datasetDefinition = persister.getDefinitions("Test First 3 Weights", true).get(0);
		datasetDefinition.setName("My Data Set Definition");
	}
	
	@Test
	public void shouldSetIdentifierAfterSave() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataExportReportObject dataExport = (DataExportReportObject) Context.getService(ReportObjectService.class).getReportObject(45);
		dataExport.setReportObjectId(null);
		dataExport.setName("My Data Set Definition");
		// Save an existing data set definition
		DataSetDefinition datasetDefinition = new DataExportDataSetDefinition(dataExport);		
		datasetDefinition = persister.saveDefinition(datasetDefinition);
		Assert.assertNotNull("Should set identifier after saving dataset definition", datasetDefinition.getId());
	}

	@Test
	public void shouldRemoveDataSetDefintion() throws Exception { 
		DataSetDefinitionPersister persister = new DataExportDataSetDefinitionPersister();
		DataSetDefinition beforePurge = persister.getDefinitions("Test First 3 Weights", true).get(0);
		int idBefore = beforePurge.getId();
		persister.purgeDefinition(beforePurge);
		DataSetDefinition afterPurge = persister.getDefinition(idBefore);
		Assert.assertNull(afterPurge);
	}
}
