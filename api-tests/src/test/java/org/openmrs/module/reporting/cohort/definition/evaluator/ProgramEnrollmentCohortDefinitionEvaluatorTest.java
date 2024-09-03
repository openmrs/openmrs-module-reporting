/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
