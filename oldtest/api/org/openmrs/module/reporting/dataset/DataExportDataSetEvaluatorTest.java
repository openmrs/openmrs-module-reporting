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
package org.openmrs.module.reporting.dataset;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class DataExportDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	/** Logger */
	private static Log log = LogFactory.getLog(DataExportDataSetEvaluatorTest.class);
	
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
		//executeDataSet("org/openmrs/report/include/ReportTests-patients.xml");
		//executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");		
		authenticate();
		
	}

	

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldEvaluateDataExportDataSet() throws Exception {
		EvaluationContext evalContext = new EvaluationContext();
		DataSetDefinitionService dsds = Context.getService(DataSetDefinitionService.class);
		
		DataExportDataSetDefinition dataSetDefinition = dsds.getDefinition(DataExportDataSetDefinition.class, new Integer(45));
		
		DataSet dataSet = dsds.evaluate(dataSetDefinition, evalContext);
	
		for (DataSetColumn column : dataSet.getMetaData().getColumns()) { 
			log.info("column: " + column.getColumnKey() + " " + column.getDataType());		
		}
		Assert.fail("Need to add test conditions");
	}
	
}
