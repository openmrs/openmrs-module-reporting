package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class InverseCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link InverseCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return all patients who are not in the inner cohort definition", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWhoAreNotInTheInnerCohortDefinition() throws Exception {
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setMaleIncluded(true);
		InverseCohortDefinition nonMales = new InverseCohortDefinition(males);
		
		GenderCohortDefinition femaleOrUnknown = new GenderCohortDefinition();
		femaleOrUnknown.setFemaleIncluded(true);
		femaleOrUnknown.setUnknownGenderIncluded(true);
		
		Cohort nonMaleCohort = Context.getService(CohortDefinitionService.class).evaluate(nonMales, null);
		Cohort femaleOrUnknownCohort = Context.getService(CohortDefinitionService.class).evaluate(femaleOrUnknown, null);

		Assert.assertEquals(femaleOrUnknownCohort.size(), nonMaleCohort.getSize());
		Assert.assertTrue(Cohort.subtract(nonMaleCohort, femaleOrUnknownCohort).isEmpty());
	}

	/**
	 * @see {@link InverseCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should successfully use the context base cohort", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldSuccessfullyUseTheContextBaseCohort() throws Exception {
		
		// Set the base cohort to males only (3 patients born in 1959, 1975, 2007)
		EvaluationContext context = new EvaluationContext();
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setMaleIncluded(true);
		Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(males, null);
		context.setBaseCohort(baseCohort);
		Assert.assertEquals(3, baseCohort.size());
		
		// Children on 1/1/2010 (4)
		AgeCohortDefinition children = new AgeCohortDefinition();
		children.setMaxAge(15);
		children.setEffectiveDate(DateUtil.getDateTime(2010, 1, 1));
		Cohort childrenCohort = Context.getService(CohortDefinitionService.class).evaluate(children, null);
		Assert.assertEquals(4, childrenCohort.size());
		
		InverseCohortDefinition nonChildren = new InverseCohortDefinition(children);

		// Inverse Children, non base cohort
		Assert.assertEquals(5, Context.getService(CohortDefinitionService.class).evaluate(nonChildren, null).size());
		
		// Inverse Children, base cohort
		Assert.assertEquals(2, Context.getService(CohortDefinitionService.class).evaluate(nonChildren, context).size());

	}
}