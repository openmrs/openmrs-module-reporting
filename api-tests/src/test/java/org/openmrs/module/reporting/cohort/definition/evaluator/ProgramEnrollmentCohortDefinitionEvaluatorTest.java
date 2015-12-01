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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.Collections;

public class ProgramEnrollmentCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	ProgramWorkflowService ps;
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		ps = Context.getProgramWorkflowService();
	}
	
	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs after the given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsAfterTheGivenDate() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setEnrolledOnOrAfter(DateUtil.getDateTime(2008, 8, 1, 11, 0, 0, 0));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
		
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(pp.getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs before the given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsBeforeTheGivenDate() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setEnrolledOnOrBefore(DateUtil.getDateTime(2008, 8, 1, 11, 0, 0, 0));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
		
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(pp.getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients that completed the given programs before the given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsThatCompletedTheGivenProgramsBeforeTheGivenDate() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateCompleted(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setCompletedOnOrBefore(DateUtil.getDateTime(2008, 8, 1, 11, 0, 0, 0));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
		
		pp.setDateCompleted(DateUtil.getDateTime(2008, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(pp.getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients that completed the given programs after the given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsThatCompletedTheGivenProgramsAfterTheGivenDate() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateCompleted(DateUtil.getDateTime(2008, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setCompletedOnOrAfter(DateUtil.getDateTime(2008, 8, 1, 11, 0, 0, 0));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
		
		pp.setDateCompleted(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(pp.getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients that completed the given programs on the given date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsThatCompletedTheGivenProgramsOnTheGivenDateIfPassedInTimeIsAtMidnight()
	    throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateCompleted(DateUtil.getDateTime(2008, 8, 1, 12, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setCompletedOnOrBefore(DateUtil.getDateTime(2008, 8, 1));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs on the given date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsOnTheGivenDateIfPassedInTimeIsAtMidnight()
	    throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 8, 1, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setEnrolledOnOrBefore(DateUtil.getDateTime(2008, 8, 1));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
	}

	/**
	 * @see {@link ProgramEnrollmentCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled at the given locations", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledAtTheGivenLocations() throws Exception {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setPrograms(Collections.singletonList(Context.getProgramWorkflowService().getProgram(1)));
		cd.setLocationList(Collections.singletonList(Context.getLocationService().getLocation(1)));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext());
		Assert.assertEquals(2, c.size());
	}
}
