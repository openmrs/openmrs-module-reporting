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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.dataset.column.LogicDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.util.CohortUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class PatientDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	/** Logger */
	private static Log log = LogFactory.getLog(PatientDataSetEvaluatorTest.class);
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
    public Boolean useInMemoryDatabase() { 
		return false; 
	}	
	
	/**
	 * Runs the basic stuff since we have SkipBaseSetup on the whole class
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		if (useInMemoryDatabase()) { 
			initializeInMemoryDatabase();
			executeDataSet("org/openmrs/module/dataset/include/LabEncounterDataSetTest.xml");
			//executeDataSet("org/openmrs/module/dataset/include/LabEncounterDataSet-concepts.xml");
		}
		authenticate();
		
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldEvaluatePatientDataSet() throws Exception {

		//EvaluationContext evalContext = new EvaluationContext();
		//evalContext.setBaseCohort(CohortUtil.limitCohort(Context.getPatientSetService().getAllPatients(), 1000));
		
		// Evaluate dataset
		//PatientDataSetDefinition dataSetDefinition = new PatientDataSetDefinition();
		//LogicDataSetColumn column = new LogicDataSetColumn("WEIGHT (KG)", String.class, "WEIGHT (KG)");
		//dataSetDefinition.addLogicColumn(column);
		
		//DataSet dataSet = Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, evalContext);
		LogicCriteria criteria = new LogicCriteria("WEIGHT (KG)").last();
		Result result = Context.getLogicService().eval(new Patient(1448), criteria);
		
		log.info("result: " + result.toString());
		// Build report
        //ReportData reportData = new ReportData();
        //Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
        //dataSets.put("patient", dataSet);
        //reportData.setDataSets(dataSets);
        
        // Render report
        //FileOutputStream fileOutputStream = new FileOutputStream(new File("/home/jmiranda/Workspace/module-reporting-core/patient.csv"));        
        //new CsvReportRenderer().render(reportData, null, fileOutputStream);
		
	}
	
}
