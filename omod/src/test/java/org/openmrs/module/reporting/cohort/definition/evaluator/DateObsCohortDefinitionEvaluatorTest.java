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
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class DateObsCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see {@link DateObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should test any with many properties specified", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldTestAnyWithManyPropertiesSpecified() throws Exception {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(20));
		cd.setOnOrAfter(DateUtil.getDateTime(2008, 8, 15));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 15));
		cd.setLocationList(Collections.singletonList(new Location(1)));
		cd.setOperator1(RangeComparator.GREATER_THAN);
		cd.setValue1(DateUtil.getDateTime(2008, 8, 10));
		cd.setOperator2(RangeComparator.LESS_THAN);
		cd.setValue2(DateUtil.getDateTime(2008, 8, 17));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
	}

	/**
     * @see {@link DateObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     * 
     */
    @Test
    @Verifies(value = "should find nobody if no patients match", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldFindNobodyIfNoPatientsMatch() throws Exception {
    	DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(20));
		cd.setOnOrAfter(DateUtil.getDateTime(2008, 8, 15));
		cd.setOnOrBefore(DateUtil.getDateTime(2008, 8, 15));
		cd.setLocationList(Collections.singletonList(new Location(1)));
		cd.setOperator1(RangeComparator.GREATER_THAN);
		cd.setValue1(DateUtil.getDateTime(2008, 8, 20));
		cd.setOperator2(RangeComparator.LESS_THAN);
		cd.setValue2(DateUtil.getDateTime(2008, 8, 27));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(0, cohort.size());
    }
    /**
     * @see {@link DateObsCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should find patients with obs within the specified time frame", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnPatientsWithObsWithinTheSpecifiedTimeframe() throws Exception {
		
    	NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(new Concept(5089));
		
		// There should be 4 patients with observations on any date
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(4, c.size());
		
		// 3 patients have observations on or after 2009-08-19
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 19, 0, 0, 0, 0));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		
		// Only 2 patients have any observations on or after 2009-08-19 with a non zero time
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 8, 19, 0, 0, 0, 7));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, c.size());

		// All 4 patients have their observations on or before 2009-09-19
		cd.setOnOrAfter(null);
		cd.setOnOrBefore(DateUtil.getDateTime(2009, 9, 19, 0, 0, 0, 0));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(4, c.size());
		
		// One patient has an observation on 2009-09-19 between 6am and noon
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 9, 19, 6, 0, 0, 0));
		cd.setOnOrBefore(DateUtil.getDateTime(2009, 9, 19, 12, 0, 0, 0));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, c.size());
		
		// No patients have observations on 2009-09-19 between 6am and 9am
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 9, 19, 6, 0, 0, 0));
		cd.setOnOrBefore(DateUtil.getDateTime(2009, 9, 19, 9, 0, 0, 0));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(0, c.size());
		
		// No patients have observations on 2009-09-19 between 12pm and 6pm
		cd.setOnOrAfter(DateUtil.getDateTime(2009, 9, 19, 12, 0, 0, 0));
		cd.setOnOrBefore(DateUtil.getDateTime(2009, 9, 19, 18, 0, 0, 0));
		c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(0, c.size());
    }
}