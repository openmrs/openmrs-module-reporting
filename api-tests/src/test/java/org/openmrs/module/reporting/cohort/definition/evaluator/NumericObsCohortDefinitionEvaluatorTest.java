/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWithAnyObsOfASpecifiedConcept() throws Exception {
        Cohort cohort = getCohort(TimeModifier.ANY, new Concept(5089), null, null, null, null, null, null, null, null);
        assertCohort(cohort, 7, 20, 21, 22);
    }

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseFirstObsOfASpecifiedConceptIsInARange() throws Exception {
        Cohort cohort = getCohort(TimeModifier.FIRST, new Concept(5089), null, null, null, null, RangeComparator.GREATER_THAN, 50d, RangeComparator.LESS_EQUAL, 80d);
        assertCohort(cohort, 21);
    }

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseMaximumObsOfASpecifiedConceptIsEqualToASpecifiedValue() throws Exception {
        Cohort cohort = getCohort(TimeModifier.MAX, new Concept(5089), null, null, null, null, RangeComparator.EQUAL, 180d, null, null);
        assertCohort(cohort, 20);
    }

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWithAnyObsOfASpecifiedConceptInASpecifiedEncounterType() throws Exception {
        List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(1));
        Cohort cohort = getCohort(TimeModifier.ANY, new Concept(5089), null, null, null, encTypeList, null, null, null, null);
        assertCohort(cohort, 7);
    }

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseFirstObsOfASpecifiedConceptInASpecifiedEncounterTypeIsInARange() throws Exception {
        List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(1));
        Cohort cohort = getCohort(TimeModifier.FIRST, new Concept(5089), null, null, null, encTypeList, RangeComparator.GREATER_THAN, 54d, RangeComparator.LESS_EQUAL, 56d);
        assertCohort(cohort, 7);

        encTypeList = Collections.singletonList(new EncounterType(2));
        cohort = getCohort(TimeModifier.FIRST, new Concept(5089), null, null, null, encTypeList, RangeComparator.GREATER_THAN, 49d, RangeComparator.LESS_EQUAL, 51d);
        assertCohort(cohort, 7);
    }

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseMaximumObsOfASpecifiedConceptInASpecifiedEncounterTypeIsEqualsToASpecifiedValue() throws Exception {
        List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(1));
        Cohort cohort = getCohort(TimeModifier.MAX, new Concept(5089), null, null, null, encTypeList, RangeComparator.EQUAL, 61d, null, null);
        assertCohort(cohort, 7);

        encTypeList = Collections.singletonList(new EncounterType(2));
        cohort = getCohort(TimeModifier.MAX, new Concept(5089), null, null, null, encTypeList, RangeComparator.EQUAL, 50d, null, null);
        assertCohort(cohort, 7);
    }

    @Test
    public void getPatientsHavingRangedObs_shouldGetPatientsWithAQueryWithAllParameters() throws Exception {
        List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(6));
        List<Location> locationList = Collections.singletonList(new Location(2));
        Concept concept = new Concept(5089);
        Date onOrAfter = new SimpleDateFormat("yyyy-MM-dd").parse("2009-08-01");
        Date onOrBefore = new SimpleDateFormat("yyyy-MM-dd").parse("2009-09-30");
        // TODO test grouping concept

        Cohort cohort = getCohort(TimeModifier.ANY, concept, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
        assertCohort(cohort, 20, 22);

        cohort = getCohort(TimeModifier.FIRST, concept, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
        assertCohort(cohort, 20, 22);

        cohort = getCohort(TimeModifier.LAST, concept, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
        assertCohort(cohort, 20);

        cohort = getCohort(TimeModifier.MAX, concept, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
        assertCohort(cohort, 20);

        cohort = getCohort(TimeModifier.NO, concept, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
        assertCohort(cohort, 2, 6, 7, 8, 21, 23, 24);
    }

    protected Cohort getCohort(TimeModifier timeModifier, Concept question, Date onOrAfter, Date onOrBefore,
                               List<Location> locationList, List<EncounterType> encounterTypeList,
                               RangeComparator operator1, Double value1,
                               RangeComparator operator2, Double value2) throws EvaluationException {

        NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
        cd.setTimeModifier(timeModifier);
        cd.setQuestion(question);
        cd.setOnOrAfter(onOrAfter);
        cd.setOnOrBefore(onOrBefore);
        cd.setLocationList(locationList);
        cd.setEncounterTypeList(encounterTypeList);
        cd.setOperator1(operator1);
        cd.setValue1(value1);
        cd.setOperator2(operator2);
        cd.setValue2(value2);
        return Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext());
    }

    private void assertCohort(Cohort cohort, Integer... memberIds) {
        Assert.assertEquals("Cohort was supposed to be: " + Arrays.asList(memberIds) + " but was instead: " + cohort.getCommaSeparatedPatientIds(), memberIds.length, cohort.size());
        for (Integer memberId : memberIds)
            Assert.assertTrue("Cohort does not contain patient " + memberId, cohort.contains(memberId));
    }
}