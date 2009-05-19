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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.result.Result;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class LabEncounterDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	private static Log log = LogFactory.getLog(LabEncounterDataSetEvaluatorTest.class);
	
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
    public Boolean useInMemoryDatabase() {
	    return true;
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
	public void shouldGenerateLabOrderDataSet() throws Exception {			
		
		EvaluationContext evalContext = new EvaluationContext();
		
		DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);		
		
		Integer [] labTestIds = { 5497, 5089, 1019 };
		
		LabEncounterDataSetDefinition definition = 
			new LabEncounterDataSetDefinition(Arrays.asList(labTestIds));
		
		DataSet<Object> dataSet = service.evaluate(definition, evalContext);

		ReportData reportData = new ReportData();
		Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
		dataSets.put("labDataSet", dataSet);
		reportData.setDataSets(dataSets);
		
		new CsvReportRenderer().render(reportData, null, System.out);
					
	
		
	}
}
