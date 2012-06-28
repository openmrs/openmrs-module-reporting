package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test for the {@link PatientIdentifierCohortDefinitionEvaluator}
 */
public class PatientIdentifierCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see PatientIdentifierCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)
	 * @verifies return patients who have identifiers of the passed types
	 */
	@Test
	public void evaluate_shouldReturnPatientsWhoHaveIdentifiersOfThePassedTypes() throws Exception {
		{
			PatientIdentifierCohortDefinition picd = new PatientIdentifierCohortDefinition();
			picd.addTypeToMatch(new PatientIdentifierType(2));
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(8, c.getSize());
		}
		{
			PatientIdentifierCohortDefinition picd = new PatientIdentifierCohortDefinition();
			picd.addTypeToMatch(new PatientIdentifierType(1));
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(3, c.getSize());
		}
		{
			PatientIdentifierCohortDefinition picd = new PatientIdentifierCohortDefinition();
			picd.addTypeToMatch(new PatientIdentifierType(1));
			picd.addTypeToMatch(new PatientIdentifierType(2));
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(10, c.getSize());
		}
	}

	/**
	 * @see PatientIdentifierCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)
	 * @verifies return patients who have identifiers matching the passed locations
	 */
	@Test
	public void evaluate_shouldReturnPatientsWhoHaveIdentifiersMatchingThePassedLocations() throws Exception {
		PatientIdentifierCohortDefinition picd = new PatientIdentifierCohortDefinition();
		picd.addTypeToMatch(new PatientIdentifierType(2));
		Assert.assertEquals(8, Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext()).size());
		picd.addLocationToMatch(new Location(3));
		Assert.assertEquals(1, Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext()).size());
	}

	/**
	 * @see PatientIdentifierCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)
	 * @verifies return patients who have identifiers matching the passed text
	 */
	@Test
	public void evaluate_shouldReturnPatientsWhoHaveIdentifiersMatchingThePassedText() throws Exception {
		PatientIdentifierCohortDefinition picd = new PatientIdentifierCohortDefinition();
		{
			picd.setTextToMatch("TEST");
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(0, c.size());
		}
		{
			picd.setTextToMatch("TEST901");
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(1, c.size());
		}
		{
			picd.setTextToMatch("TEST%");
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(1, c.size());
			Assert.assertTrue(c.contains(20));
		}
		{
			picd.setTextToMatch("%TEST");
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(1, c.size());
			Assert.assertTrue(c.contains(21));
		}	
		{
			picd.setTextToMatch("%TEST%");
			EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
			Assert.assertEquals(2, c.size());
		}
	}

	/**
	 * @see PatientIdentifierCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)
	 * @verifies return patients who have identifiers matching the passed regular expression
	 */
	@Test
	public void evaluate_shouldReturnPatientsWhoHaveIdentifiersMatchingThePassedRegularExpression() throws Exception {
		PatientIdentifierCohortDefinition picd = new PatientIdentifierCohortDefinition();
		picd.setRegexToMatch(".*-.*"); // Match any identifier that contains a dash
		EvaluatedCohort c = Context.getService(CohortDefinitionService.class).evaluate(picd, new EvaluationContext());
		Assert.assertEquals(4, c.size());
	}
}