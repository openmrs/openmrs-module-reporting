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

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import org.junit.After;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.CareSetting;
import org.openmrs.Drug;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Match;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.apache.commons.lang3.time.DateUtils;


public class DrugOrderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String TEST_DATA = "org/openmrs/module/reporting/include/DrugOrderCohortEvaluationData.xml";
	private DrugOrderCohortDefinition cohortDefinition;

  	@Before
  	public void setup() throws Exception {
  		cohortDefinition = new DrugOrderCohortDefinition();
  		executeDataSet(TEST_DATA);
  	}

  	@After
  	public void tearDown() {
  		cohortDefinition = null;
  	}

  	@Test
  	public void evaluateShouldReturnAllPatients() throws Exception {
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(5, cohort.size());
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsCurrentlyActiveOnDrugs() throws Exception { 
  		cohortDefinition.setActiveOnOrAfter(new Date());
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertEquals(3, cohort.size());
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsCurrentlyNotActiveOnDrugs() throws Exception { 
  		
  		cohortDefinition.setActiveOnOrBefore(DateUtils.addDays(new Date(2013, 12, 2), -1));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertEquals(3, cohort.size());
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveTakenAnyofListedDrugs() throws Exception {
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(88));
  		drugSetList.add(new Concept(792));
  		cohortDefinition.setDrugSets(drugSetList);
  		cohortDefinition.setWhich(Match.ANY);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertEquals(2, cohort.size());
  		
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveTakenAnyListedDrugByDefault() throws Exception {
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(3));
  		drugSetList.add(new Concept(792));
  		cohortDefinition.setDrugSets(drugSetList);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(4, cohort.size());
  		
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveTakenAnyofDrugs() throws Exception {
  		List drugs = new ArrayList<Drug>();
  		drugs.add(new Drug(3));
  		drugs.add(new Drug(2));
  		cohortDefinition.setDrugs(drugs);
  		cohortDefinition.setWhich(Match.ANY);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertEquals(2, cohort.size());
  		
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveTakenAnyDrugByDefault() throws Exception {
  		List drugs = new ArrayList<Drug>();
  		drugs.add(new Drug(11));
  		drugs.add(new Drug(2));
  		cohortDefinition.setDrugs(drugs);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(4, cohort.size());
  		
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveNeverTakenDrugs() throws Exception {
  		List drugs = new ArrayList<Concept>();
  		drugs.add(new Drug(3));
  		drugs.add(new Drug(2));
  		cohortDefinition.setDrugs(drugs);
  		cohortDefinition.setWhich(Match.NONE);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(3, cohort.size());
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveNeverTakenListedDrugs() throws Exception {
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(88));
  		drugSetList.add(new Concept(792));
  		cohortDefinition.setDrugSets(drugSetList);
  		cohortDefinition.setWhich(Match.NONE);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(3, cohort.size());
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveTakenAllListedDrugs() throws Exception { 
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(88));
  		drugSetList.add(new Concept(792));
  		cohortDefinition.setDrugSets(drugSetList);
  		cohortDefinition.setWhich(Match.ALL);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertEquals(1, cohort.size());
  		Assert.assertTrue(cohort.contains(2));
  	}
	
  	@Test
  	public void evaluateShouldReturnAllPatientsNotActiveOnDrugsAfterDate() throws Exception { 
  		cohortDefinition.setActiveOnOrBefore(DateUtil.getDateTime(2013, 12, 2));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertEquals(2, cohort.size());
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsCurrentlyActiveOnDrugsFromDate() throws Exception { 
  		cohortDefinition.setActiveOnOrAfter(DateUtil.getDateTime(2013, 12, 7));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertEquals(4, cohort.size());
  	}
  	@Test
  	public void evaluateShouldReturnAllPatientsWhoStartedTakingDrugsBeforeSpecifiedDate() throws Exception {
  		cohortDefinition.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 2));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(2, cohort.size());
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsWhoStartedTakingDrugsAfterSpecifiedDate() throws Exception {
  		cohortDefinition.setActivatedOnOrAfter(DateUtil.getDateTime(2008, 8, 10));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertEquals(2, cohort.size());
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsOnDrugsOnSpecifiedDate() throws Exception {
  		cohortDefinition.setActiveOnDate(DateUtil.getDateTime(2007, 12, 3));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertEquals(1, cohort.size());
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsTakingAnyDrugWithinADateRange() throws Exception {
  		cohortDefinition.setActivatedOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
  		cohortDefinition.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 8));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(5, cohort.size());
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsTakingSpecifiedDrugBeforeDate() throws Exception {
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(88));
  		cohortDefinition.setDrugSets(drugSetList);
  		cohortDefinition.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 2));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  	}

  	@Test
  	public void evaluateShouldReturnAllInSpecifiedCareSetting() throws Exception {    
  		CareSetting careSetting = Context.getService(OrderService.class).getCareSetting(1);
  		cohortDefinition.setCareSetting(careSetting);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertEquals(5, cohort.size());

  	}
}