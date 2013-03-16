package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import java.util.Set;

public class BirthAndDeathCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients by birth range", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsByBirthRange() throws Exception {
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setBornOnOrAfter(DateUtil.getDateTime(1950, 1, 1));
		cd.setBornOnOrBefore(DateUtil.getDateTime(1999, 12, 31));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(4, cohort.size());
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(7));
		Assert.assertTrue(cohort.contains(21));
		Assert.assertTrue(cohort.contains(22));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients by death range", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsByDeathRange() throws Exception {
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setDiedOnOrAfter(DateUtil.getDateTime(2005, 1, 1));
		cd.setDiedOnOrBefore(DateUtil.getDateTime(2005, 12, 31));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(20));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients by birth range and death range", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsByBirthRangeAndDeathRange() throws Exception {
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setBornOnOrAfter(DateUtil.getDateTime(1900, 1, 1));
		cd.setBornOnOrBefore(DateUtil.getDateTime(1950, 12, 31));
		cd.setDiedOnOrAfter(DateUtil.getDateTime(2005, 1, 1));
		cd.setDiedOnOrBefore(DateUtil.getDateTime(2005, 12, 31));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(20));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients born on the onOrBefore date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsBornOnTheOnOrBeforeDateIfPassedInTimeIsAtMidnight() throws Exception {
		PatientService ps = Context.getPatientService();
		final Integer patientId = 6;
		Patient patient = ps.getPatient(patientId);
		patient.setBirthdate(DateUtil.getDateTime(1999, 8, 23, 11, 0, 0, 0));
		patient.getAttribute(8).setValue("value"); //1.9 is not happy with empty values
		ps.savePatient(patient);
		
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setBornOnOrBefore(DateUtil.getDateTime(1999, 8, 23));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(patientId));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients that died on the onOrBefore date if passed in time is at midnight", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsThatDiedOnTheOnOrBeforeDateIfPassedInTimeIsAtMidnight() throws Exception {
		PatientService ps = Context.getPatientService();
		final Integer patientId = 7;
		Patient patient = ps.getPatient(patientId);
		patient.setDead(true);
		patient.setDeathDate(DateUtil.getDateTime(2005, 12, 31, 11, 0, 0, 0));
		patient.setCauseOfDeath(new Concept(3));
		ps.savePatient(patient);
		
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setDiedOnOrBefore(DateUtil.getDateTime(2005, 12, 31));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(patientId));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients born after the specified date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsBornAfterTheSpecifiedDate() throws Exception {
		PatientService ps = Context.getPatientService();
		final Integer patientId = 6;
		Patient patient = ps.getPatient(patientId);
		patient.setBirthdate(DateUtil.getDateTime(1999, 8, 23));
		patient.getAttribute(8).setValue("value"); //1.9 is not happy with empty values
		ps.savePatient(patient);
		
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setBornOnOrAfter(DateUtil.getDateTime(1999, 8, 23, 11, 0, 0, 0));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(cohort.contains(patientId));
	}
	
	/**
	 * @see {@link BirthAndDeathCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should find patients that died after the specified date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldFindPatientsThatDiedAfterTheSpecifiedDate() throws Exception {
		PatientService ps = Context.getPatientService();
		final Integer patientId = 7;
		Patient patient = ps.getPatient(patientId);
		patient.setDead(true);
		patient.setDeathDate(DateUtil.getDateTime(2005, 12, 31));
		patient.setCauseOfDeath(new Concept(3));
		ps.savePatient(patient);
		
		BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
		cd.setDiedOnOrAfter(DateUtil.getDateTime(2005, 12, 31, 11, 0, 0, 0));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertFalse(cohort.contains(patientId));
	}
}
