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

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class EncounterDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	/** Logger */
	private static Log log = LogFactory.getLog(EncounterDataSetEvaluatorTest.class);
	
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
		
		//executeDataSet("initialInMemoryTestDataSet.xml");
		executeDataSet("org/openmrs/include/standardTestDataset.xml");
		//executeDataSet("org/openmrs/api/include/EncounterServiceTest-initialData.xml");
		authenticate();
		
	}

	@Test
	public void shouldEvaluateDataExportDataSet() throws Exception {
		EvaluationContext evalContext = new EvaluationContext();
		
		DataSetDefinition dataSetDefinition = new EncounterDataSetDefinition();
		DataSet dataSet = Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, evalContext);
	
		StringBuilder datasetBuilder = new StringBuilder();
		datasetBuilder.append("\n");
		int columnCount = 0;
		
		
		for (DataSetColumn column : dataSet.getDefinition().getColumns()) { 
			datasetBuilder.append(column.getColumnKey());
			if (columnCount++ <= dataSet.getDefinition().getColumns().size())
				datasetBuilder.append(",");
		}
		datasetBuilder.append("\n");

		int rowCount = 0;
		for (DataSetRow row : dataSet) { 
			rowCount++;
			columnCount = 0;
			//Map<DataSetColumn, Object> columnSet = (Map<DataSetColumn, Object>) rowSet;			

			for (DataSetColumn column : dataSet.getDefinition().getColumns()) {
				datasetBuilder.append(column.getDisplayName() + "=" + row.getColumnValue(column));
				if (columnCount++ <= dataSet.getDefinition().getColumns().size()) {
					datasetBuilder.append(",");
				}
			}			
			datasetBuilder.append("\n");
		}
		log.info(datasetBuilder);

		
		Assert.assertEquals(1, rowCount);
		
	}
	
}
