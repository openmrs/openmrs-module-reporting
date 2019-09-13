package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.ConditionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ConditionCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String CONDITION_TEST_DATASET = "org/openmrs/module/reporting/include/ConditionCohortDefinitionEvaluatorTestDataSet.xml";
	
	private ConditionCohortDefinition cd;
	
	@Before
	public void setup() throws Exception {
		cd = new ConditionCohortDefinition();
		executeDataSet(CONDITION_TEST_DATASET);
	}
	
	@After
	public void tearDown() {
		cd = null;
	}
	
	@Test
	public void evaluateShouldReturnAllPatients() throws Exception {
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertTrue(cohort.contains(5));
		Assert.assertEquals(5, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithConcept() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConcept(concept);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(4, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithConceptAndNonCodedValue() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConcept(concept);
		cd.setConditionNonCoded("NON-CODED-CONDITION");
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(1, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithOnOrBeforeDate() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConcept(concept);
		cd.setOnOrBefore(DateUtil.getDateTime(2014, 03, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(2, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithOnOrAftereDate() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConcept(concept);
		cd.setOnOrAfter(DateUtil.getDateTime(2014, 03, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertEquals(3, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsBetweenDateRanges() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConcept(concept);
		cd.setOnOrAfter(DateUtil.getDateTime(2014, 02, 12));
		cd.setOnOrBefore(DateUtil.getDateTime(2014, 04, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(3));
		Assert.assertEquals(1, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithAllParams() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setOnOrAfter(DateUtil.getDateTime(2015, 01, 10));
		cd.setOnOrBefore(DateUtil.getDateTime(2015, 01, 14));
		cd.setConcept(concept);
		cd.setConditionNonCoded("NON-CODED-CONDITION2");
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertEquals(2, cohort.size());
	}
}
