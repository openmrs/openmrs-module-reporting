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
package org.openmrs.module.reporting.data.patient.evaluator;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * ProgramEnrollmentsForPatientDataEvaluator test cases
 */
public class ProgramEnrollmentsForPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient programs that are active on a given date
	 */
	@Test
	public void evaluate_shouldReturnPatientProgramsThatAreActiveOnAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,7"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		
		def.setActiveOnDate(DateUtil.getDateTime(2008, 8, 4));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, pd.getData().size());
		PatientProgram pp = (PatientProgram) pd.getData().get(2);
		Assert.assertEquals("2008-08-01", DateUtil.formatDate(pp.getDateEnrolled(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-02-10", DateUtil.formatDate(pp.getDateCompleted(), "yyyy-MM-dd"));
	}
	
	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient programs started on or after a given date
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnPatientProgramsStartedOnOrAfterAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		
		def.setEnrolledOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, ((List)pd.getData().get(2)).size());

		def.setEnrolledOnOrAfter(DateUtil.getDateTime(2008, 8, 2));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, ((List)pd.getData().get(2)).size());
	}

	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient programs started on or before a given date
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnPatientProgramsStartedOnOrBeforeAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		
		def.setEnrolledOnOrBefore(DateUtil.getDateTime(2010, 3, 10));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, ((List)pd.getData().get(2)).size());

		def.setEnrolledOnOrBefore(DateUtil.getDateTime(2009, 3, 10));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, ((List)pd.getData().get(2)).size());
		
		def.setEnrolledOnOrBefore(DateUtil.getDateTime(2008, 3, 10));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertNull(pd.getData().get(2));
	}
	
	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient programs completed on or after a given date
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnPatientProgramsCompletedOnOrAfterAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		
		def.setCompletedOnOrAfter(DateUtil.getDateTime(2009, 2, 10));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, ((List)pd.getData().get(2)).size());
		
		def.setCompletedOnOrAfter(DateUtil.getDateTime(2010, 2, 10));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertNull(pd.getData().get(2));
	}

	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return patient programs completed on or before a given date
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnPatientProgramsCompletedOnOrBeforeAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		
		def.setCompletedOnOrBefore(DateUtil.getDateTime(2009, 2, 10));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, ((List)pd.getData().get(2)).size());
		
		def.setCompletedOnOrBefore(DateUtil.getDateTime(2008, 2, 10));
		pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertNull(pd.getData().get(2));
	}
	
	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the first patient program by enrollment date
	 */
	@Test
	public void evaluate_shouldReturnTheFirstPatientProgramByEnrollmentDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setWhichEnrollment(TimeQualifier.FIRST);
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(1, ((PatientProgram)pd.getData().get(2)).getPatientProgramId().intValue());
	}

	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the last patient program by enrollment date
	 */
	@Test
	public void evaluate_shouldReturnTheLastPatientProgramByEnrollmentDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setWhichEnrollment(TimeQualifier.LAST);
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(8, ((PatientProgram)pd.getData().get(2)).getPatientProgramId().intValue());
	}

	/**
	 * @see ProgramEnrollmentsForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return a list of patient programs for each patient
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnAListOfPatientProgramsForEachPatient() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));

		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition();
		def.setProgram(Context.getProgramWorkflowService().getProgram(1));
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
		Assert.assertEquals(2, ((List)pd.getData().get(2)).size());
	}
}