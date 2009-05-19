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
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class LabEncounterReportTest extends BaseModuleContextSensitiveTest {
	
	private static Log log = LogFactory.getLog(LabEncounterReportTest.class);
	
	
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
			//executeDataSet("org/openmrs/include/standardTestDataset.xml");
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
	public void shouldGenerateLabResultsReportTest() throws Exception {			
		
		EvaluationContext evalContext = new EvaluationContext();

		ReportService service = Context.getService(ReportService.class);

		ReportSchema reportSchema = new ReportSchema();		
		EvaluationContext context = new EvaluationContext();
		
		// Create, evaluate, and render the patient dataset
		PatientDataSetDefinition patientDataSetDefinition = 
			new PatientDataSetDefinition();
		DataSet patientDataSet = 
			Context.getService(
					DataSetDefinitionService.class).evaluate(
							patientDataSetDefinition, 
							evalContext);

        ReportData patientReportData = new ReportData();
        Map<String, DataSet> patientDataSets = new HashMap<String, DataSet>();
        patientDataSets.put("patient", patientDataSet);
        patientReportData.setDataSets(patientDataSets);        
        new CsvReportRenderer().render(patientReportData, null, System.out);		
		
		// Create, evaluate, and render the lab dataset
		Integer [] labTests = { 5497, 5089, 1019 };
		LabEncounterDataSetDefinition labDataSetDefinition = 
			new LabEncounterDataSetDefinition(Arrays.asList(labTests));				
		DataSet<Object> labDataSet = 
			Context.getService(
				DataSetDefinitionService.class).evaluate(labDataSetDefinition, evalContext);		
        ReportData labReportData = new ReportData();
        Map<String, DataSet> labDataSets = new HashMap<String, DataSet>();
        labDataSets.put("encounter", labDataSet);
        labReportData.setDataSets(labDataSets);        
        new CsvReportRenderer().render(labReportData, null, System.out);		
		
		
		// Create, evaluate, and render the joined dataset 
        JoinDataSetDefinition joinDataSetDefinition = 
        	new JoinDataSetDefinition(
        			patientDataSetDefinition, "patient.", "patient_id", 
        			labDataSetDefinition, "encounter.", "patient_id");

        // TODO Need to pass a Mapped<DataSetDefinition>
		//reportSchema.addDataSetDefinition(joinDataSetDefinition);

        DataSet joinDataSet = 
        	Context.getService(DataSetDefinitionService.class).evaluate(
        			joinDataSetDefinition, 
        			new EvaluationContext());
        
        ReportData reportData = new ReportData();
        Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
        dataSets.put("joinDataSet", joinDataSet);
        reportData.setDataSets(dataSets);
        
        new CsvReportRenderer().render(reportData, null, System.out);
		
		
		//service.evaluate(reportSchema, context);
		
		
	}
}
