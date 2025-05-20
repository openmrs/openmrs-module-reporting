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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramStatesForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * ProgramStatesForPatientDataEvaluator test cases
 */
@SuppressWarnings("deprecation")
public class ProgramStatesForPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient states that are active on a given date
	 */
	@Test
	public void evaluate_shouldReturnPatientStatesThatAreActiveOnAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));
		
		def.setActiveOnDate(DateUtil.getDateTime(2008, 8, 11));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, pd.getData().size());
		
		def.setActiveOnDate(DateUtil.getDateTime(2008, 8, 9));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, pd.getData().size());
		
		PatientState ps = (PatientState) pd.getData().get(8);
		Assert.assertEquals(4, ps.getPatientStateId().intValue());
		
		def.setActiveOnDate(DateUtil.getDateTime(2010, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(0, pd.getData().size());
	}
	
	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient states started on or after a given date
	 */
	@Test
	public void evaluate_shouldReturnPatientStatesStartedOnOrAfterAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));
		
		def.setStartedOnOrAfter(DateUtil.getDateTime(2008, 8, 11));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, pd.getData().size());
		
		def.setStartedOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, pd.getData().size());
		
		def.setStartedOnOrAfter(DateUtil.getDateTime(2010, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(0, pd.getData().size());
	}
	
	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient states started on or before a given date
	 */
	@Test
	public void evaluate_shouldReturnPatientStatesStartedOnOrBeforeAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));
		
		def.setStartedOnOrBefore(DateUtil.getDateTime(2008, 1, 1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(0, pd.getData().size());
		
		def.setStartedOnOrBefore(DateUtil.getDateTime(2008, 8, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, pd.getData().size());
		
		def.setStartedOnOrBefore(DateUtil.getDateTime(2010, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, pd.getData().size());
	}
	
	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient states ended on or after a given date
	 */
	@Test
	public void evaluate_shouldReturnPatientStatesEndedOnOrAfterAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));
		
		def.setEndedOnOrAfter(DateUtil.getDateTime(2008, 1, 1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, pd.getData().size());
		
		def.setEndedOnOrAfter(DateUtil.getDateTime(2009, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, pd.getData().size());
		
		def.setEndedOnOrAfter(DateUtil.getDateTime(2010, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(0, pd.getData().size());
	}

	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient states ended on or before a given date
	 */
	@Test
	public void evaluate_shouldReturnPatientStatesEndedOnOrBeforeAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));
		
		def.setEndedOnOrBefore(DateUtil.getDateTime(2008, 1, 1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(0, pd.getData().size());
		
		def.setEndedOnOrBefore(DateUtil.getDateTime(2009, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, pd.getData().size());
		
		def.setEndedOnOrBefore(DateUtil.getDateTime(2010, 1, 1));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, pd.getData().size());
	}
	
	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the first patient state by start date
	 */
	@Test
	public void evaluate_shouldReturnTheFirstPatientStateByEnrollmentDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(4, ((PatientState)pd.getData().get(8)).getPatientStateId().intValue());
	}

	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the last patient state by start date
	 */
	@Test
	public void evaluate_shouldReturnTheLastPatientStateByEnrollmentDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("8"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(4, ((PatientState)pd.getData().get(8)).getPatientStateId().intValue());
	}

	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the first patient state by start date and program enrollment date
	 */
	@Test
	public void evaluate_shouldReturnTheLastPatientStateByStateStartDateAndEnrollmentDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("23"));

		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
        def.setState(Context.getProgramWorkflowService().getStateByUuid("92584cdc-6a20-4c84-a659-e035e45d36b0"));

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(8, ((PatientState)pd.getData().get(23)).getPatientStateId().intValue());
	}


	/**
	 * @see ProgramStatesForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return a list of patient states for each patient
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnAListOfPatientStatesForEachPatient() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("8"));
		
		ProgramStatesForPatientDataDefinition def = new ProgramStatesForPatientDataDefinition();
        def.setState(Context.getProgramWorkflowService().getStateByUuid("0d521bb4-2edb-4dd1-8d9f-34489bb4d9ea"));

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, ((List)pd.getData().get(8)).size());
	}
}