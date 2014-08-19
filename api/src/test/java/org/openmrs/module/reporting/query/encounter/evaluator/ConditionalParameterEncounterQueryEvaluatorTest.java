package org.openmrs.module.reporting.query.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.EncounterService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.OptionalParameterCohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.ConditionalParameterEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the OptionalParameterCohortDefinition
 */
public class ConditionalParameterEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	EncounterQueryService encounterQueryService;

	@Autowired
	EncounterService encounterService;
	
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

		BasicEncounterQuery q1 = new BasicEncounterQuery();
		q1.addEncounterType(encounterService.getEncounterType(1));
		q1.addEncounterType(encounterService.getEncounterType(2));
		EncounterQueryResult r1 = encounterQueryService.evaluate(q1, new EvaluationContext());

		BasicEncounterQuery q2 = new BasicEncounterQuery();
		q2.addEncounterType(encounterService.getEncounterType(6));
		EncounterQueryResult r2 = encounterQueryService.evaluate(q2, new EvaluationContext());

		ConditionalParameterEncounterQuery cpq = new ConditionalParameterEncounterQuery();
		cpq.addConditionalQuery("visit", Mapped.mapStraightThrough(q1));
		cpq.addConditionalQuery("lab", Mapped.mapStraightThrough(q2));
		cpq.setParameterToCheck("type");
		
		EvaluationContext context = new EvaluationContext();

		context.addParameterValue("type", "visit");
		EncounterQueryResult test1 = encounterQueryService.evaluate(cpq, context);
		Assert.assertEquals(r1.getSize(), test1.getSize());
		Assert.assertTrue(r1.getMemberIds().containsAll(test1.getMemberIds()));

		context.addParameterValue("type", "lab");
		EncounterQueryResult test2 = encounterQueryService.evaluate(cpq, context);
		Assert.assertEquals(r2.getSize(), test2.getSize());
		Assert.assertTrue(r2.getMemberIds().containsAll(test2.getMemberIds()));
	}
}
