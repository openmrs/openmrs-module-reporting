package org.openmrs.module.reporting.cohort.query.service;


import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CohortQueryServiceTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier, Concept, Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, RangeComparator, Object, RangeComparator, Object)}
	 */
	@Test
	@Verifies(value = "should get patients with any obs of a specified concept", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,Modifier,Double,Modifier,Double)")
	public void getPatientsHavingRangedObs_shouldGetPatientsWithAnyObsOfASpecifiedConcept() throws Exception {
		CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.ANY, new Concept(5089), null, null, null, null, null, null, null, null, null);
		Assert.assertEquals(4, cohort.size());
		Assert.assertTrue(cohort.contains(7));
		Assert.assertTrue(cohort.contains(20));
		Assert.assertTrue(cohort.contains(21));
		Assert.assertTrue(cohort.contains(22));
	}

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier, Concept, Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, RangeComparator, Object, RangeComparator, Object)}
     */
    @Test
    @Verifies(value = "should get patients whose first obs of a specified concept is in a range", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseFirstObsOfASpecifiedConceptIsInARange() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, new Concept(5089), null, null, null, null, null, RangeComparator.GREATER_THAN, 50d, RangeComparator.LESS_EQUAL, 80d);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(21));
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier, Concept, Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, RangeComparator, Object, RangeComparator, Object)}
     */
    @Test
    @Verifies(value = "should get patients whose maximum obs of a specified concept is equal to a specified value", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseMaximumObsOfASpecifiedConceptIsEqualToASpecifiedValue() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, new Concept(5089), null, null, null, null, null, RangeComparator.EQUAL, 180d, null, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(20));
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)}
     */
    @Test
    @Verifies(value = "should get patients with any obs of a specified concept in a specified encounter type", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWithAnyObsOfASpecifiedConceptInASpecifiedEncounterType() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
    	List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(1));
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.ANY, new Concept(5089), null, null, null, null, encTypeList, null, null, null, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)}
     */
    @Test
    @Verifies(value = "should get patients whose first obs of a specified concept in a specified encounter type is in a range", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseFirstObsOfASpecifiedConceptInASpecifiedEncounterTypeIsInARange() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
    	
    	List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(1));
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, new Concept(5089), null, null, null, null, encTypeList, RangeComparator.GREATER_THAN, 54d, RangeComparator.LESS_EQUAL, 56d);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
		
		encTypeList = Collections.singletonList(new EncounterType(2));
		cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, new Concept(5089), null, null, null, null, encTypeList, RangeComparator.GREATER_THAN, 49d, RangeComparator.LESS_EQUAL, 51d);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)}
     */
    @Test
    @Verifies(value = "should get patients whose maximum obs of a specified concept in a specified encounter type is equals to a specified value", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseMaximumObsOfASpecifiedConceptInASpecifiedEncounterTypeIsEqualsToASpecifiedValue() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
    	
    	List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(1));
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, new Concept(5089), null, null, null, null, encTypeList, RangeComparator.EQUAL, 61d, null, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
		
		encTypeList = Collections.singletonList(new EncounterType(2));
		cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, new Concept(5089), null, null, null, null, encTypeList, RangeComparator.EQUAL, 50d, null, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(7));
    }

}