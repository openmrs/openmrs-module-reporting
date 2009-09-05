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
package org.openmrs.module.report;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class LabOrderReportTest extends BaseModuleContextSensitiveTest {
	
	private Log log = LogFactory.getLog(getClass());
	
	
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
		Location location = Context.getLocationService().getAllLocations().get(0);
			
			
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
		DataSet<Object> labDataSet = 
			Context.getService(
				DataSetDefinitionService.class).evaluate(
						labDataSetDefinition, context);	
		
        ReportData labReportData = new ReportData();
        Map<String, DataSet> labDataSets = new HashMap<String, DataSet>();
        labDataSets.put("encounter", labDataSet);
        labReportData.setDataSets(labDataSets);       
        
        List<String> displayColumns = new LinkedList<String>();
        List<String> ledsColumns = labDataSetDefinition.getColumnKeys();
        ledsColumns.remove(LabEncounterDataSetDefinition.ENCOUNTER_ID);
        ledsColumns.remove(LabEncounterDataSetDefinition.PATIENT_ID);
        displayColumns.addAll(ledsColumns);        
        
        CsvReportRenderer renderer = new CsvReportRenderer();
        //renderer.setDisplayColumns(displayColumns);  MS: No longer supported in renderer...move to DSD
        renderer.render(labReportData, null, System.out);			
		
	}
}
