package org.openmrs.module.reporting.cohort.query.service;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.api.PatientSetService.Modifier;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CohortQueryServiceTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see {@link CohortQueryService#getPatientsHavingNumericObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,Modifier,Double,Modifier,Double)}
	 */
	@Test
	@Verifies(value = "should get patients with any obs of a specified concept", method = "getPatientsHavingNumericObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,Modifier,Double,Modifier,Double)")
	public void getPatientsHavingNumericObs_shouldGetPatientsWithAnyObsOfASpecifiedConcept() throws Exception {
		CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.ANY, new Concept(5089), null, null, null, null, null, null, null, null, null);
		Assert.assertEquals(4, cohort.size());
		Assert.assertTrue(cohort.contains(7));
		Assert.assertTrue(cohort.contains(20));
		Assert.assertTrue(cohort.contains(21));
		Assert.assertTrue(cohort.contains(22));
	}

	/**
     * @see {@link CohortQueryService#getPatientsHavingNumericObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,Modifier,Double,Modifier,Double)}
     * 
     */
    @Test
    @Verifies(value = "should get patients whose first obs of a specified concept is in a range", method = "getPatientsHavingNumericObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,Modifier,Double,Modifier,Double)")
    public void getPatientsHavingNumericObs_shouldGetPatientsWhoseFirstObsOfASpecifiedConceptIsInARange() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.FIRST, new Concept(5089), null, null, null, null, null, Modifier.GREATER_THAN, 50d, Modifier.LESS_EQUAL, 80d);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(21));
    }

	/**
     * @see {@link CohortQueryService#getPatientsHavingNumericObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,Modifier,Double,Modifier,Double)}
     * 
     */
    @Test
    @Verifies(value = "should get patients whose maximum obs of a specified concept is equals to a specified value", method = "getPatientsHavingNumericObs(TimeModifier,Concept,Concept,Date,Date,List<QLocation;>,List<QEncounterType;>,Modifier,Double,Modifier,Double)")
    public void getPatientsHavingNumericObs_shouldGetPatientsWhoseMaximumObsOfASpecifiedConceptIsEqualsToASpecifiedValue() throws Exception {
    	CohortQueryService service = Context.getService(CohortQueryService.class);
		Cohort cohort = service.getPatientsHavingRangedObs(TimeModifier.MAX, new Concept(5089), null, null, null, null, null, Modifier.EQUAL, 180d, null, null);
		Assert.assertEquals(1, cohort.size());
		Assert.assertTrue(cohort.contains(20));
    }
}