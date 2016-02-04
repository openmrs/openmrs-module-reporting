package org.openmrs.module.reporting.cohort.definition.evaluator;


import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class NumericObsCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link NumericObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should get patients with any obs of a specified concept", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldGetPatientsWithAnyObsOfASpecifiedConcept() throws Exception {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(5089));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(4, cohort.size());
		Assert.assertTrue(cohort.contains(7));
		Assert.assertTrue(cohort.contains(20));
		Assert.assertTrue(cohort.contains(21));
		Assert.assertTrue(cohort.contains(22));
	}
	
	/**
	 * @see {@link NumericObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test any with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestAnyWithManyPropertiesSpecified() throws Exception {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(5089));
		cd.setOnOrAfter(DateUtil.getDateTime(2008, 8, 18));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 20));
		cd.setLocationList(Collections.singletonList(new Location(2)));
		cd.setOperator1(RangeComparator.GREATER_THAN);
		cd.setValue1(60d);
		cd.setOperator2(RangeComparator.LESS_THAN);
		cd.setValue2(61.5d);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
	}
	
	/**
	 * @see {@link NumericObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test avg with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestAvgWithManyPropertiesSpecified() throws Exception {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.AVG);
		cd.setQuestion(new Concept(5089));
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 1, 1));
		cd.setOnOrBefore(DateUtil.getDateTime(2009, 12, 31));
		cd.setLocationList(Collections.singletonList(new Location(2)));
		cd.setOperator1(RangeComparator.GREATER_EQUAL);
		cd.setValue1(150d);
		cd.setOperator2(RangeComparator.LESS_EQUAL);
		cd.setValue2(200d);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, cohort.size());
		Assert.assertTrue(cohort.contains(20));
		Assert.assertTrue(cohort.contains(22));
	}
	
	/**
	 * @see {@link NumericObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test last with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestLastWithManyPropertiesSpecified() throws Exception {
		NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.LAST);
		cd.setQuestion(new Concept(5089));
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 1, 1));
		cd.setOnOrBefore(DateUtil.getDateTime(2009, 12, 31));
		cd.setLocationList(Collections.singletonList(new Location(2)));
		cd.setOperator1(RangeComparator.GREATER_EQUAL);
		cd.setValue1(190d);
		cd.setOperator2(RangeComparator.LESS_EQUAL);
		cd.setValue2(200d);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(22));
	}
}