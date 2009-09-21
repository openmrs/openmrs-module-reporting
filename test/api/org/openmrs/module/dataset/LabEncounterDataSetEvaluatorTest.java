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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.XlsReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * Test class that tries to run a portion of the
 */
public class LabEncounterDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	private Log log = LogFactory.getLog(getClass());
	
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
    public Boolean useInMemoryDatabase() {
	    return false;
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		if (useInMemoryDatabase()) { 
			initializeInMemoryDatabase();
			executeDataSet("org/openmrs/module/dataset/include/LabEncounterDataSetTest.xml");		
		}
		authenticate();
	}

	/**
	 * Auto generated method comment
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldGenerateLabOrderDataSet() throws Exception {			
		
		EvaluationContext evalContext = new EvaluationContext();
		
		DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);		
		
		Integer [] conceptIds = { 657, 6167, 6168 };
		
		LabEncounterDataSetDefinition definition = 
			new LabEncounterDataSetDefinition(Arrays.asList(conceptIds));
		
		DataSet<?> dataSet = service.evaluate(definition, evalContext);

		ReportData reportData = new ReportData();
		Map<String, DataSet<?>> dataSets = new HashMap<String, DataSet<?>>();
		dataSets.put("labDataSet", dataSet);
		reportData.setDataSets(dataSets);

		// Write to standard output
		new CsvReportRenderer().render(reportData, null, System.out);		
	}
}
