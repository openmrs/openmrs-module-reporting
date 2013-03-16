package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class InStateCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return no patients if none have the given state", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnNoPatientsIfNoneHaveTheGivenState() throws Exception {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		List<ProgramWorkflowState> states = Collections.singletonList(Context.getProgramWorkflowService().getStateByUuid(
		    "92584cdc-6a20-4c84-a659-e035e45d36b0"));
		cd.setStates(states);
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(0, c.size());
	}
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in given state on given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInGivenStateOnGivenDate() throws Exception {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		List<ProgramWorkflowState> states = Collections.singletonList(Context.getProgramWorkflowService().getStateByUuid(
		    "e938129e-248a-482a-acea-f85127251472"));
		cd.setStates(states);
		cd.setOnDate(DateUtil.getDateTime(2009, 8, 15));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, c.size());
		Assert.assertTrue(c.contains(2));
	}
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients in a state on the onOrBefore date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsInAStateOnTheOnOrBeforeDateIfPassedInTimeIsAtMidnight() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		ProgramWorkflowService ps = Context.getProgramWorkflowService();
		
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37eaa");
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(Collections.singletonList(patientState.getState()));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in the given state on or before the given start date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInTheGivenStateOnOrBeforeTheGivenStartDate() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		
		ProgramWorkflowService ps = Context.getProgramWorkflowService();
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37eaa");
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 8, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(Collections.singletonList(patientState.getState()));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 1, 9, 0, 0, 0));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));

		//Check that a patient in the state after the specified date is excluded
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in the given state on or after the given end date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInTheGivenStateOnOrAfterTheGivenEndDate() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		
		ProgramWorkflowService ps = Context.getProgramWorkflowService();
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37eaa");
		patientState.setEndDate(DateUtil.getDateTime(2012, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.setStates(Collections.singletonList(patientState.getState()));
		cd.setOnOrAfter(DateUtil.getDateTime(2012, 8, 1, 11, 0, 0, 0));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
		
		patientState.setEndDate(DateUtil.getDateTime(2012, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
}
