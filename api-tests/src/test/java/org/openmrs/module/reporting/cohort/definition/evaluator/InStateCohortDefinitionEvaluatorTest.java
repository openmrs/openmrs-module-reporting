package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.Collections;
import java.util.List;

public class InStateCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return no patients if none have the given state", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnNoPatientsIfNoneHaveTheGivenState() throws Exception {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		List<ProgramWorkflowState> states = Collections.singletonList(Context.getProgramWorkflowService().getStateByUuid("0d5f1bb4-2edb-4dd1-8d9f-34489bb4d9ea"));
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
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.addState(Context.getProgramWorkflowService().getState(2));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 8));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(2));
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

	/**
	 * @see {@link InProgramCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs at the given locations", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsAtTheGivenLocations() throws Exception {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.addState(Context.getProgramWorkflowService().getState(2));
		cd.setOnOrAfter(DateUtil.getDateTime(2000, 1, 1));
		cd.setOnOrBefore(DateUtil.getDateTime(2014, 1, 1));
		cd.addLocation(Context.getLocationService().getLocation(1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		System.out.println("Cohort: " + c);
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(23));
		Assert.assertEquals(2, c.getSize());
	}

	/**
	 * @see {@link InProgramCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled at evaluation date if no other dates supplied", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledAtEvaluationDateIfNoOtherDatesSupplied() throws Exception {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		cd.addState(Context.getProgramWorkflowService().getState(2));
		cd.addLocation(Context.getLocationService().getLocation(1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext(DateUtil.getDateTime(2012, 5, 15)));
		Assert.assertEquals(2, c.getSize());
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(23));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext(DateUtil.getDateTime(2008, 1, 1)));
		Assert.assertEquals(0, c.getSize());
	}
}
