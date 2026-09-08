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
		initializeInMemoryDatabase();
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
		cd.setConditionCoded(concept);
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(4, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithConceptAndNonCodedValue() throws Exception {
		cd.setConditionNonCoded("NON-CODED-CONDITION");
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(4));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(2, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithCreatedOnOrAfter() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setCreatedOnOrAfter(DateUtil.getDateTime(2014, 03, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertEquals(3, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithOnSetDateOnOrAfter() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setOnsetDateOnOrAfter(DateUtil.getDateTime(2014, 03, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertEquals(3, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithEndDateOnOrAfter() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setEndDateOnOrAfter(DateUtil.getDateTime(2016, 05, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertTrue(cohort.contains(3));
		Assert.assertEquals(3, cohort.size());
	}
	
	
	
	@Test
	public void evaluateShouldFilterPatientsWithCreatedOnOrBefore() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setCreatedOnOrBefore(DateUtil.getDateTime(2014, 03, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(2, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithOnSetDateOnOrBefore() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setOnsetDateOnOrBefore(DateUtil.getDateTime(2014, 03, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(2, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithEndDateOnOrBefore() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setEndDateOnOrBefore(DateUtil.getDateTime(2016, 05, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(1, cohort.size());
	}
	
	
	
	@Test
	public void evaluateShouldFilterPatientsBetweenDateRanges() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setCreatedOnOrAfter(DateUtil.getDateTime(2014, 02, 12));
		cd.setCreatedOnOrBefore(DateUtil.getDateTime(2014, 04, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(3));
		Assert.assertEquals(1, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithActiveOnDate() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setConditionCoded(concept);
		cd.setActiveOnDate(DateUtil.getDateTime(2014, 04, 12));
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(3));
		Assert.assertTrue(cohort.contains(4));
		Assert.assertEquals(2, cohort.size());
	}
	
	@Test
	public void evaluateShouldFilterPatientsWithAllParams() throws Exception {
		Concept concept = Context.getConceptService().getConcept(409);
		cd.setCreatedOnOrAfter(DateUtil.getDateTime(2015, 01, 10));
		cd.setCreatedOnOrBefore(DateUtil.getDateTime(2015, 01, 14));
		cd.setConditionCoded(concept);
		cd.setConditionNonCoded("NON-CODED-CONDITION2");
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(cohort.contains(1));
		Assert.assertTrue(cohort.contains(2));
		Assert.assertEquals(2, cohort.size());
	}
}
