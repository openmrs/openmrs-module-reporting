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
import org.openmrs.module.reporting.cohort.definition.StartStopDrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.apache.commons.lang3.time.DateUtils;


public class StartStopDrugOrderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String TEST_DATA = "org/openmrs/module/reporting/include/DrugOrderCohortEvaluationData.xml";
	private StartStopDrugOrderCohortDefinition cohortDefinition;

  	@Before
  	public void setup() throws Exception {
  		executeDataSet(TEST_DATA);
  	}

  	@After
  	public void tearDown() {
  		cohortDefinition = null;
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsWhoStartedDrugs() throws Exception { 
  		cohortDefinition = new StartStopDrugOrderCohortDefinition();
  		cohortDefinition.setOnOrBefore(DateUtil.getDateTime(2018, 12, 2));
  		cohortDefinition.setOnOrBefore(DateUtil.getDateTime(2000, 1, 1));
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		
  		Assert.assertEquals(4, cohort.size());
  		Assert.assertTrue(cohort.contains(2));
  		Assert.assertTrue(cohort.contains(7));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(21));
  		
  	}
  	
  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveStoppedDrugsWithinDates() throws Exception {
  		cohortDefinition = new StartStopDrugOrderCohortDefinition();
  		cohortDefinition.setOnOrBefore(DateUtil.getDateTime(2008, 8, 9));
  		cohortDefinition.setOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
  		cohortDefinition.setState(Match.STOPPED);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);

  		// Does not inlude Patient(2) because their drugOrder is previousOrder to other order in StandardDataset.xml 
  		Assert.assertEquals(1, cohort.size());
  		Assert.assertTrue(cohort.contains(22));  				
  	}
 
  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveChangedDrugBetweenDates() throws Exception {
  		cohortDefinition = new StartStopDrugOrderCohortDefinition();
  		cohortDefinition.setOnOrAfter(DateUtil.getDateTime(2000, 1, 1));
  		cohortDefinition.setOnOrBefore(DateUtil.getDateTime(2008, 8, 16));
  		List drugs = new ArrayList<Drug>();
  		drugs.add(new Drug(11));
  		drugs.add(new Drug(2));
  		cohortDefinition.setDrugs(drugs);
  		cohortDefinition.setState(Match.CHANGED);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  
  		Assert.assertEquals(2, cohort.size());
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(2));
  
  		
  	}

  	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveChangedAnyListedDrugsByConcepts() throws Exception {
  		cohortDefinition = new StartStopDrugOrderCohortDefinition();
  		cohortDefinition.setOnOrBefore(DateUtil.getDateTime(2008, 8, 16));
  		cohortDefinition.setOnOrAfter(DateUtil.getDateTime(2000, 1, 1));
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(3));
  		drugSetList.add(new Concept(792));
  		cohortDefinition.setDrugSets(drugSetList);
  		cohortDefinition.setState(Match.STOPPED);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		
  		Assert.assertEquals(3, cohort.size());
  		Assert.assertTrue(cohort.contains(22));
  		Assert.assertTrue(cohort.contains(8));
  		Assert.assertTrue(cohort.contains(2));
  	}
  	
	@Test
  	public void evaluateShouldReturnAllPatientsThatHaveStoppedDrugBetweenDates() throws Exception {
		cohortDefinition = new StartStopDrugOrderCohortDefinition();
		cohortDefinition.setOnOrAfter(DateUtil.getDateTime(2012, 12, 31));
		cohortDefinition.setOnOrBefore(DateUtil.getDateTime(2018, 12, 31));
  		List drugSetList = new ArrayList<Concept>();
  		drugSetList.add(new Concept(3));
  		cohortDefinition.setDrugSets(drugSetList);
  		cohortDefinition.setState(Match.STOPPED);
  		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
  		
  		Assert.assertEquals(1, cohort.size());
  		Assert.assertTrue(cohort.contains(21));
  		
  	}
}