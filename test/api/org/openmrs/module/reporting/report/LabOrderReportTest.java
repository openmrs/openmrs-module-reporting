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
package org.openmrs.module.reporting.report;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class LabOrderReportTest extends BaseModuleContextSensitiveTest {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
    public Boolean useInMemoryDatabase() {
	    return false;
	}

	/**
	 * Execute this before each test case.
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		if (useInMemoryDatabase()) { 
			initializeInMemoryDatabase();
			//executeDataSet("org/openmrs/include/standardTestDataset.xml");
			executeDataSet("org/openmrs/module/dataset/include/LabEncounterDataSetTest.xml");		
		}
		authenticate();
	}

	/**
	 * Should evaluate encounter dataset for the given dates
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldEvaluateEncounterDatasetWithinDates() throws Exception {			

		// FIXME This should be done within the HQL
		Date startDate = Context.getDateFormat().parse("04/01/2009");
		Date endDate = Context.getDateFormat().parse("04/30/2009");	
			
		Map<String,Object> parameterValues = new HashMap<String,Object>();
		parameterValues.put("startDate", startDate);
		parameterValues.put("endDate", endDate);
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(null);		
		context.setParameterValues(parameterValues);
		
		
		// Create, evaluate, and render the lab dataset
		Integer [] labTests = { 5497, 5089, 1019 };
		LabEncounterDataSetDefinition labDataSetDefinition = 
			new LabEncounterDataSetDefinition(Arrays.asList(labTests));				
		DataSet labDataSet = 
			Context.getService(
				DataSetDefinitionService.class).evaluate(
						labDataSetDefinition, context);	
		
        ReportData labReportData = new ReportData();
        Map<String, DataSet> labDataSets = new HashMap<String, DataSet>();
        labDataSets.put("encounter", labDataSet);
        labReportData.setDataSets(labDataSets);       
                
        CsvReportRenderer renderer = new CsvReportRenderer();
        //renderer.setDisplayColumns(displayColumns);  MS: No longer supported in renderer...move to DSD
        renderer.render(labReportData, null, System.out);			
		
	}
}
