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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.Collections;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsUtil;

public class PatientStateCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	ProgramWorkflowService ps;
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		ps = Context.getProgramWorkflowService();
	}
	
	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in the specified states after the start date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInTheSpecifiedStatesAfterTheStartDate() throws Exception {
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37edd");
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStartedOnOrAfter(DateUtil.getDateTime(2008, 8, 1, 11, 0, 0, 0));
		cd.setStates(Collections.singletonList(patientState.getState()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
		
		//Check that a patient that started the state before is excluded
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in the specified states before the end date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInTheSpecifiedStatesBeforeTheEndDate() throws Exception {
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37edd");
		patientState.setEndDate(DateUtil.getDateTime(2008, 12, 15, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setEndedOnOrBefore(DateUtil.getDateTime(2008, 12, 15, 11, 0, 0, 0));
		cd.setStates(Collections.singletonList(patientState.getState()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
		
		patientState.setEndDate(DateUtil.getDateTime(2008, 12, 15, 12, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in the specified states after the end date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInTheSpecifiedStatesAfterTheEndDate() throws Exception {
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37edd");
		patientState.setEndDate(DateUtil.getDateTime(2008, 12, 15, 12, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setEndedOnOrAfter(DateUtil.getDateTime(2008, 12, 15, 11, 0, 0, 0));
		cd.setStates(Collections.singletonList(patientState.getState()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
		
		patientState.setEndDate(DateUtil.getDateTime(2008, 12, 15, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients in the specified states before the start date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsInTheSpecifiedStatesBeforeTheStartDate() throws Exception {
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37edd");
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStartedOnOrBefore(DateUtil.getDateTime(2008, 8, 1, 11, 0, 0, 0));
		cd.setStates(Collections.singletonList(patientState.getState()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
		
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 15, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients in specified states on the before end date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsInSpecifiedStatesOnTheBeforeEndDateIfPassedInTimeIsAtMidnight() throws Exception {
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37edd");
		patientState.setEndDate(DateUtil.getDateTime(2008, 12, 15, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setEndedOnOrBefore(DateUtil.getDateTime(2008, 12, 15));
		cd.setStates(Collections.singletonList(patientState.getState()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients in specified states on the before start date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsInSpecifiedStatesOnTheBeforeStartDateIfPassedInTimeIsAtMidnight()
	    throws Exception {
		PatientState patientState = ps.getPatientStateByUuid("ea89deaa-23cc-4840-92fe-63d199c37edd");
		patientState.setStartDate(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(patientState.getPatientProgram());
		Context.flushSession();
		
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.setStartedOnOrBefore(DateUtil.getDateTime(2008, 8, 1));
		cd.setStates(Collections.singletonList(patientState.getState()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(patientState.getPatientProgram().getPatient().getPatientId()));
	}

	/**
	 * @see {@link PatientStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients in specified states for the specified locations", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsInSpecifiedStatesForTheSpecifiedLocations() throws Exception {
		PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
		cd.addLocation(Context.getLocationService().getLocation(1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertEquals("2,7,23", OpenmrsUtil.join(new TreeSet<Integer>(c.getMemberIds()), ","));
	}
}
