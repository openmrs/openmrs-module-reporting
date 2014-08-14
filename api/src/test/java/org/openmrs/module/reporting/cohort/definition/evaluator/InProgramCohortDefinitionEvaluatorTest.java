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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class InProgramCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	ProgramWorkflowService ps;
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		ps = Context.getProgramWorkflowService();
	}
	
	/**
	 * @see {@link InProgramCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs on or before the given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsOnOrBeforeTheGivenDate() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		Assert.assertNull(pp.getDateCompleted());
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 8, 1, 8, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 1, 9, 0, 0, 0));
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
	 * @see {@link InProgramCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients in a program on the onOrBefore date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsInAProgramOnTheOnOrBeforeDateIfPassedInTimeIsAtMidnight() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		Assert.assertNull(pp.getDateCompleted());
		pp.setDateEnrolled(DateUtil.getDateTime(2008, 7, 30, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 7, 30));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
	}
	
	/**
	 * @see {@link InProgramCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs on or after the given date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsOnOrAfterTheGivenDate() throws Exception {
		PatientProgram pp = ps.getPatientProgram(7);
		pp.setDateCompleted(DateUtil.getDateTime(2009, 11, 1, 12, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();//the patient program will be fetched from the database
		
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 11, 1, 11, 0, 0, 0));
		cd.setPrograms(Collections.singletonList(pp.getProgram()));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(pp.getPatient().getPatientId()));
		
		pp.setDateCompleted(DateUtil.getDateTime(2009, 11, 1, 10, 0, 0, 0));
		ps.savePatientProgram(pp);
		Context.flushSession();
		
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(c.contains(pp.getPatient().getPatientId()));
	}

	/**
	 * @see {@link InProgramCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients enrolled in the given programs at the given locations", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnPatientsEnrolledInTheGivenProgramsAtTheGivenLocations() throws Exception {
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.addProgram(Context.getProgramWorkflowService().getProgram(1));
		cd.setOnOrAfter(DateUtil.getDateTime(2000, 1, 1));
		cd.setOnOrBefore(DateUtil.getDateTime(2014, 1, 1));
		cd.addLocation(Context.getLocationService().getLocation(1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
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
		InProgramCohortDefinition cd = new InProgramCohortDefinition();
		cd.addProgram(Context.getProgramWorkflowService().getProgram(1));
		cd.addLocation(Context.getLocationService().getLocation(1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext(DateUtil.getDateTime(2000, 1, 1)));
		Assert.assertEquals(0, c.getSize());
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext(DateUtil.getDateTime(2009, 1, 1)));
		Assert.assertTrue(c.contains(2));
		Assert.assertEquals(1, c.getSize());
	}
}
