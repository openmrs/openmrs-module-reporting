package org.openmrs.module.reporting.cohort.definition.evaluator;


import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;

public class DateObsCohortDefinitionEvaluatorTest extends BaseContextSensitiveTest {
	
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
}