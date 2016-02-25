package org.openmrs.module.reporting.cohort.query.service;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CohortQueryServiceTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier, Concept, Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, RangeComparator, Object, RangeComparator, Object)}
	 */
	@Test
	@Verifies(value = "should get patients with any obs of a specified concept", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,Modifier,Double,Modifier,Double)")
	public void getPatientsHavingRangedObs_shouldGetPatientsWithAnyObsOfASpecifiedConcept() throws Exception {
		CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.ANY, new Concept(5089), null, null, null, null, null, null, null, null, null);
		assertCohort(cohort, 7, 20, 21, 22);
	}

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier, Concept, Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, RangeComparator, Object, RangeComparator, Object)}
     */
    @Test
    @Verifies(value = "should get patients whose first obs of a specified concept is in a range", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseFirstObsOfASpecifiedConceptIsInARange() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, new Concept(5089), null, null, null, null, null, RangeComparator.GREATER_THAN, 50d, RangeComparator.LESS_EQUAL, 80d);
		assertCohort(cohort, 21);
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier, Concept, Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, RangeComparator, Object, RangeComparator, Object)}
     */
    @Test
    @Verifies(value = "should get patients whose maximum obs of a specified concept is equal to a specified value", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWhoseMaximumObsOfASpecifiedConceptIsEqualToASpecifiedValue() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, new Concept(5089), null, null, null, null, null, RangeComparator.EQUAL, 180d, null, null);
		assertCohort(cohort, 20);
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
		assertCohort(cohort, 7);
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
		assertCohort(cohort, 7);
		
		encTypeList = Collections.singletonList(new EncounterType(2));
		cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, new Concept(5089), null, null, null, null, encTypeList, RangeComparator.GREATER_THAN, 49d, RangeComparator.LESS_EQUAL, 51d);
		assertCohort(cohort, 7);
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
		assertCohort(cohort, 7);
		
		encTypeList = Collections.singletonList(new EncounterType(2));
		cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, new Concept(5089), null, null, null, null, encTypeList, RangeComparator.EQUAL, 50d, null, null);
		assertCohort(cohort, 7);
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)}
     */
    @Test
    @Verifies(value = "should get patients with a query with all parameters", method = "getPatientsHavingRangedObs(TimeModifier,Concept,Concept,Date,Date,List<Location>,List<EncounterType>,RangeComparator,Object,RangeComparator,Object)")
    public void getPatientsHavingRangedObs_shouldGetPatientsWithAQueryWithAllParameters() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
    	List<EncounterType> encTypeList = Collections.singletonList(new EncounterType(6));
    	List<Location> locationList = Collections.singletonList(new Location(2));
    	Concept concept = new Concept(5089);
    	Date onOrAfter = new SimpleDateFormat("yyyy-MM-dd").parse("2009-08-01");
    	Date onOrBefore = new SimpleDateFormat("yyyy-MM-dd").parse("2009-09-30");
    	// TODO test grouping concept

    	Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.ANY, concept, null, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
    	assertCohort(cohort, 20, 22);
    	
    	cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, concept, null, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
    	assertCohort(cohort, 20, 22);
    	
    	cohort = service.getPatientsHavingRangedObs(TimeModifier.LAST, concept, null, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
    	assertCohort(cohort, 20);
    	
    	cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, concept, null, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
    	assertCohort(cohort, 20);
    	
    	cohort = service.getPatientsHavingRangedObs(TimeModifier.NO, concept, null, onOrAfter, onOrBefore, locationList, encTypeList, RangeComparator.GREATER_THAN, 175d, RangeComparator.LESS_THAN, 185d);
    	assertCohort(cohort, 2, 6, 7, 8, 21, 23, 24);
    }
    
    /**
	 * @see {@link CohortQueryService#getPatientsHavingEncounters(Date, Date, TimeQualifier, List<Location>, List<Person>, List<EncounterType>, List<Form>, Integer, Integer, User, Date, Date)}
	 */
	@Test
	@Verifies(value = "should get patients having encounters with a specified provider", method = "getPatientsHavingEncounters(Date, Date, TimeQualifier, List<Location>, List<Person>, List<EncounterType>, List<Form>, Integer, Integer, User, Date, Date)")
	public void getPatientsHavingEncounters_shouldGetPatientsHavingEncountersWithASpecifiedProvider() throws Exception {
		List<Person> providerList = Collections.singletonList(new Person(2));
		CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingEncounters(null, null, TimeQualifier.ANY, null, providerList, null, null, null, null, null, null, null);
		assertCohort(cohort, 23, 24);
	}

    /**
     * Asserts that the passed in cohort has exactly the specified member ids
     * @param cohort
     * @param memberIds
     */
	private void assertCohort(Cohort cohort, Integer... memberIds) {
	    Assert.assertEquals("Cohort was supposed to be: " + Arrays.asList(memberIds) + " but was instead: " + cohort.getCommaSeparatedPatientIds(), memberIds.length, cohort.size());
	    for (Integer memberId : memberIds)
	    	Assert.assertTrue("Cohort does not contain patient " + memberId, cohort.contains(memberId));
    }

}