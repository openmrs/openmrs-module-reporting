/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.CurrentPatientStateDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test of CurrentPatientStateDataEvaluator
 */
public class CurrentPatientStateDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	
	/**
	 * @see CurrentPatientStateDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the current state of the configured workflow for each patient in the passed context
	 */
	@Test
	public void evaluate_shouldReturnTheCurrentStateOfTheConfiguredWorkflowForEachPatientInThePassedContext() throws Exception {
		
		Program mdrtbProgram = Context.getProgramWorkflowService().getProgram(2);
		ProgramWorkflow mdrtbTreatmentStatusWorkflow = mdrtbProgram.getWorkflow(3);
		
		EvaluationContext context = new EvaluationContext();
		
		CurrentPatientStateDataDefinition mdrtbState = new CurrentPatientStateDataDefinition();
		mdrtbState.setWorkflow(mdrtbTreatmentStatusWorkflow);
		
		// No effective date set should return only one enrollment
		EvaluatedPatientData data = Context.getService(PatientDataService.class).evaluate(mdrtbState, context);
		Assert.assertEquals(1, data.getData().size());
		PatientState state = (PatientState)data.getData().get(7);
		Assert.assertEquals("2009-12-31", DateUtil.formatDate(state.getStartDate(), "yyyy-MM-dd"));
		
		// Effective date of 2008-12-15 should return 2
		mdrtbState.setEffectiveDate(DateUtil.getDateTime(2008, 12, 15));
		data = Context.getService(PatientDataService.class).evaluate(mdrtbState, context);
		Assert.assertEquals(2, data.getData().size());
		state = (PatientState)data.getData().get(7);
		Assert.assertEquals("2008-08-11", DateUtil.formatDate(state.getStartDate(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-12-31", DateUtil.formatDate(state.getEndDate(), "yyyy-MM-dd"));
		state = (PatientState)data.getData().get(8);
		Assert.assertEquals("2008-12-15", DateUtil.formatDate(state.getStartDate(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-11-01", DateUtil.formatDate(state.getEndDate(), "yyyy-MM-dd"));
		
		// Effective date (edge case) of 2009-11-01 should return 1
		mdrtbState.setEffectiveDate(DateUtil.getDateTime(2009, 11, 1));
		data = Context.getService(PatientDataService.class).evaluate(mdrtbState, context);
		Assert.assertEquals(1, data.getData().size());
		state = (PatientState)data.getData().get(7);
		Assert.assertEquals("2008-08-11", DateUtil.formatDate(state.getStartDate(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-12-31", DateUtil.formatDate(state.getEndDate(), "yyyy-MM-dd"));
	}
}