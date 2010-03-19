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
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.JoinDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test class that tries to run a portion of the
 */
public class LabEncounterReportTest extends BaseModuleContextSensitiveTest {
	
	protected Log log = LogFactory.getLog(getClass());
	
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
		
		EvaluationContext sharedContext = new EvaluationContext();
		Cohort baseCohort = new Cohort();

		// Add all patients
		// baseCohort = Context.getPatientSetService().getAllPatients();		

		// Add Malawi patients
		//baseCohort.addMember(35201);
		//baseCohort.addMember(35203);
		//baseCohort.addMember(35205);
		//baseCohort.addMember(35207);
		//baseCohort.addMember(35209);

		// Add Rwanda patients
		baseCohort.addMember(20447);
		baseCohort.addMember(15559);
		baseCohort.addMember(18429);
		baseCohort.addMember(13984);
		baseCohort.addMember(14552);
		baseCohort.addMember(14952);
		baseCohort.addMember(12591);
		baseCohort.addMember(13059);
		baseCohort.addMember(15447);
		
		sharedContext.setBaseCohort(baseCohort);
		
		// Date parameters
		Date startDate = Context.getDateFormat().parse("04/01/2009");
		Date endDate = Context.getDateFormat().parse("04/30/2009");
		

		// Set parameters for report
		Map<String,Object> parameterValues = new HashMap<String,Object>();

		// Location parameters (Rwinkwavu)
		Location location = Context.getLocationService().getLocation(new Integer(26));
		parameterValues.put("location", location);
		
		// Date parameters
		parameterValues.put("startDate", startDate);
		parameterValues.put("endDate", endDate);
		sharedContext.setParameterValues(parameterValues);
		
		// Create, evaluate, and render the patient dataset
		PatientDataSetDefinition patientDataSetDefinition = new PatientDataSetDefinition();

		EvaluationContext encounterDatasetContext = new EvaluationContext();
		encounterDatasetContext.setBaseCohort(baseCohort);		
		
		// Create, evaluate, and render the lab dataset
		Integer [] labTests = { 5497, 5089, 1019 };
		LabEncounterDataSetDefinition labDataSetDefinition = new LabEncounterDataSetDefinition(Arrays.asList(labTests));				

		
		// Create, evaluate, and render the joined dataset 
        JoinDataSetDefinition joinDataSetDefinition = new JoinDataSetDefinition(
        			patientDataSetDefinition, "patient.", "patient_id", 
        			labDataSetDefinition, "encounter.", "patient_id");

        // TODO Need to pass a Mapped<DataSetDefinition>
		//reportDefinition.addDataSetDefinition(joinDataSetDefinition);

        DataSet joinDataSet = 
        	Context.getService(DataSetDefinitionService.class).evaluate(
        			joinDataSetDefinition, 
        			sharedContext);
        
        ReportData reportData = new ReportData();
        Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
        dataSets.put("joinDataSet", joinDataSet);
        reportData.setDataSets(dataSets);
        
        new CsvReportRenderer().render(reportData, null, System.out);
		
		
		//service.evaluate(reportDefinition, context);
		
		
	}
}
