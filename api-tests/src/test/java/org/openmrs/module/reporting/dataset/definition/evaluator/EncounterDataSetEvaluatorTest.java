/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;

/**
 * Test the EncounterDataSetDefinition
 */
public class EncounterDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected Log log = LogFactory.getLog(getClass());
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.getDateTime(2010, 1, 1));
		context.addParameterValue("endDate", DateUtil.getDateTime(2010, 12, 31));
		
		EncounterDataSetDefinition d = new EncounterDataSetDefinition();
		d.addParameter(new Parameter("startDate", "Start Date", Date.class));
		d.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		d.addColumn("ENCOUNTER ID", new EncounterIdDataDefinition(), null);	// Test a basic encounter data item
		d.addColumn("EMR ID", new PatientIdDataDefinition(), null); 			// Test a basic patient data item
		d.addColumn("BIRTHDATE", new BirthdateDataDefinition(), null); 		// Test a basic person data item
		d.addColumn("ENCOUNTER DATE", new EncounterDatetimeDataDefinition(), null, new DateConverter("dd/MMM/yyyy"));  // Test a column with a converter
		
		AgeDataDefinition ageOnDateData = new AgeDataDefinition();
		ageOnDateData.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		
		d.addColumn("Age At Start", ageOnDateData, "effectiveDate=${startDate}"); // Test a column with a parameter
		d.addColumn("Age At End", ageOnDateData, "effectiveDate=${endDate}");  // Test a column with a different parameter mapping
		
		DataSet dataset = Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		DataSetUtil.printDataSet(dataset, System.out);
	}
}
