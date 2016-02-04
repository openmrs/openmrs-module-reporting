package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the OptionalParameterCohortDefinition
 */
public class ConditionalParameterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	BuiltInCohortDefinitionLibrary builtInCohortDefinitionLibrary;
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	/**
	 * @see {@link OptionalParameterCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)}
	 */
	@Test
	public void evaluate_shouldSupportIntegerParameter() throws Exception {

		Cohort females = cohortDefinitionService.evaluate(builtInCohortDefinitionLibrary.getFemales(), new EvaluationContext());
		Cohort males = cohortDefinitionService.evaluate(builtInCohortDefinitionLibrary.getMales(), new EvaluationContext());

		GenderCohortDefinition gender = new GenderCohortDefinition();
		gender.addParameter(new Parameter("gender", "Gender", String.class));

		ConditionalParameterCohortDefinition cd = new ConditionalParameterCohortDefinition();
		cd.setParameterToCheck("gender");
		cd.addConditionalCohortDefinition("M", Mapped.mapStraightThrough(builtInCohortDefinitionLibrary.getMales()));
		cd.addConditionalCohortDefinition("F", Mapped.mapStraightThrough(builtInCohortDefinitionLibrary.getFemales()));
		
		EvaluationContext context = new EvaluationContext();

		context.addParameterValue("gender", "M");
		Cohort test1 = cohortDefinitionService.evaluate(cd, context);
		Assert.assertEquals(males.getSize(), test1.getSize());
		Assert.assertTrue(males.getMemberIds().containsAll(test1.getMemberIds()));

		context.addParameterValue("gender", "F");
		Cohort test2 = cohortDefinitionService.evaluate(cd, context);
		Assert.assertEquals(females.getSize(), test2.getSize());
		Assert.assertTrue(females.getMemberIds().containsAll(test2.getMemberIds()));
	}
}
