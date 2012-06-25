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
import org.openmrs.module.reporting.cohort.definition.TextObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class TextObsCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link TextObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test any with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestAnyWithManyPropertiesSpecified() throws Exception {
		TextObsCohortDefinition cd = new TextObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(19)); // favorite food, in the reporting test dataset
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Collections.singletonList("PB and J"));
		cd.setOnOrAfter(DateUtil.getDateTime(2008, 8, 14));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 16));
		cd.setLocationList(Collections.singletonList(new Location(1)));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
	}
	
	/**
	 * @see {@link TextObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test last with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestLastWithManyPropertiesSpecified() throws Exception {
		TextObsCohortDefinition cd = new TextObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.LAST);
		cd.setQuestion(new Concept(19)); // favorite food, in the reporting test dataset
		cd.setOperator(SetComparator.IN);
		cd.setValueList(Collections.singletonList("PB and J"));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
	}
}